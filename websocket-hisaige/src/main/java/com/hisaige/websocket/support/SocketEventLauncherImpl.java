package com.hisaige.websocket.support;

import com.alibaba.fastjson.JSON;
import com.hisaige.core.util.CollectionUtils;
import com.hisaige.core.util.StringUtils;
import com.hisaige.websocket.filter.EventChannelMatcherFilter;
import com.hisaige.websocket.listener.MessagePubFutureListener;
import com.hisaige.websocket.service.WebsocketService;
import com.hisiage.event.ievent.AbstractEvent;
import com.hisiage.event.ievent.AbstractNotifyEvent;
import com.hisiage.event.ievent.EventNotification;
import com.hisiage.event.launcher.NotifyEventLauncher;
import com.hisiage.event.supper.notifyEvent.EventSubscription;
import com.hisiage.event.supper.notifyEvent.manager.SubscriberManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * 默认的通知事件实现方法
 * 使用时仅需要添加到bean即可
 * @author chenyj
 * 2020/4/4 - 17:11.
 **/
public class SocketEventLauncherImpl implements NotifyEventLauncher {

    private static final Logger logger = LoggerFactory.getLogger(SocketEventLauncherImpl.class);

    private WebsocketService websocketService;

    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    private LinkedBlockingQueue<EventNotification> eventQueue = new LinkedBlockingQueue<>(Integer.MAX_VALUE/30);

    private static boolean isRun = true;

    public SocketEventLauncherImpl(WebsocketService websocketService, ThreadPoolTaskExecutor threadPoolTaskExecutor){
        this.websocketService = websocketService;
        this.threadPoolTaskExecutor = threadPoolTaskExecutor;
    }

    @Override
    public void pubEvent(AbstractEvent event) {
        EventNotification eventInfo = ((AbstractNotifyEvent) event).getEventInfo();
        EventChannelMatcherFilter eventChannelMatcherFilter = new EventChannelMatcherFilter(eventInfo);
        //如果sessionId不为空，则为点对点,否则为点对多
        if(!StringUtils.isEmpty(eventInfo.getSessionId())){
            for (EventSubscription eventSubscription : SubscriberManager.getEventSubscriptionMap().values()) {
                //一对一
                if(eventInfo.getSessionId().equals(eventSubscription.getSessionId())){
                    eventInfo.addEventSubscription(eventSubscription); //添加当前消息的点对点监听者
                }
            }
        }
        eventQueue.add(eventInfo); //添加到消息队列
    }

    @Override
    public void pubEvent(List<AbstractEvent> events) {
        if(!CollectionUtils.isEmpty(events)){
            events.forEach(this::pubEvent);
        }
    }

    @PostConstruct
    public void consumer(){
        //消费事件
        new Thread(this::eventQueueConsumer, "eventQueue-consumer-thread").start();//总事件队列消费

        new Thread(this::eventSubscriptionConsumer, "eventSubscription-consumer-thread").start();//用户队列消费
    }

    @PreDestroy
    public void destroy(){
        isRun = false;
    }

    private void eventQueueConsumer(){
        //对事件队列进行消费
        while (isRun){
            try{
                EventNotification eventNotification = eventQueue.take();

                threadPoolTaskExecutor.submit(() -> {
                    //消费
                    if(StringUtils.isEmpty(eventNotification.getSessionId())){
                        //点对多,点对多则不设置sessionId
                        websocketService.sendMessage2All(JSON.toJSONString(eventNotification), new EventChannelMatcherFilter(eventNotification));
                    } else {
                        @SuppressWarnings("unchecked")
                        List<EventSubscription> eventSubscriptions = eventNotification.getEventSubscriptions();
                        for(EventSubscription eventSubscription : eventSubscriptions){
                            //点对点
                            websocketService.send2One(JSON.toJSONString(eventNotification), eventSubscription.getSessionId() + "." + eventSubscription.getClientId(), new EventChannelMatcherFilter(eventNotification));
                        }
                    }
                });
            } catch (Exception e){
                logger.error("eventQueueConsumer throw Exception", e);
            }
        }
    }

    private void eventSubscriptionConsumer(){
        //消费用户事件队列
        while (isRun){
            try {
                if(SubscriberManager.getEventSize() == 0){
                    TimeUnit.SECONDS.sleep(5);
                    return;
                }
                Map<Long, EventSubscription> subscriptionsMap = SubscriberManager.getEventSubscriptionMap();
                for(EventSubscription entry : subscriptionsMap.values()){
                    if(entry.getEventSize() > 0){
                        EventNotification eventNotification = entry.takeEvent();
                        websocketService.send2One(JSON.toJSONString(eventNotification), entry.getSessionId() + "." + entry.getSubscribeId(), new MessagePubFutureListener(eventNotification));
                    }
                }
            } catch (Exception e) {
                logger.error("eventSubscriptionConsumer throw Exception", e);
            }
        }
    }
}
