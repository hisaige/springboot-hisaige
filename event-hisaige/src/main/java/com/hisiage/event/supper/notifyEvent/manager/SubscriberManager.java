package com.hisiage.event.supper.notifyEvent.manager;

import com.hisiage.event.supper.notifyEvent.EventSubscription;
import com.hisiage.event.supper.notifyEvent.exception.RepeatSubscriptionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 用户订阅与订阅对象管理
 * @author chenyj
 * 2020/4/4 - 17:16.
 **/
public class SubscriberManager {

    private static final Logger logger = LoggerFactory.getLogger(SubscriberManager.class);

    private static final int EVENT_CAPACITY = 30000; //消息队列中的事件数量最大值为30000

    //订阅ID生成器
    private static AtomicLong atomicSubscribeId = new AtomicLong(0);
    private static Map<Long, EventSubscription> eventSubscriptionMap = new ConcurrentHashMap<>();

    /**
     * 用户订阅
     * @param subscribeIp 订阅IP
     * @param sessionId 订阅
     * @param clientId 客户端ID
     * @param paths 事件path规则，可使用 *(表示多个) ?(表示一个) 等特殊符号订阅特定事件
     * @return 订阅ID
     */
    public static synchronized Long subscribe2Event(String subscribeIp, String sessionId, String clientId, @Nullable Iterable<String> paths) throws IllegalAccessException, ClassNotFoundException, InstantiationException {

        Assert.notNull(sessionId, "sessionId cannot be null");
        Assert.notNull(clientId, "clientId cannot be null");

        ConcurrentHashMap<String, Long> clientSubscriptionMap = SessionClientManager.getOrCreateSubscribeMap(sessionId);
        Long subscribeId = clientSubscriptionMap.get(clientId);

        if(null == subscribeId){
            subscribeId = atomicSubscribeId.incrementAndGet();
            clientSubscriptionMap.put(clientId, subscribeId);
            EventSubscription eventSubscription = new EventSubscription(subscribeIp, sessionId, clientId, paths);
            eventSubscriptionMap.put(subscribeId, eventSubscription);
            if(logger.isInfoEnabled()){
                logger.info("subscribe2Event, sessionId:{}, clientId:{}", sessionId,clientId);
            }
            return subscribeId;
        } else {
            //重复订阅异常
            throw new RepeatSubscriptionException();
        }
    }

    /**
     * 取消订阅
     * @param subscribeId 订阅ID
     */
    public static synchronized void cancelSubscribe(long subscribeId){
        EventSubscription eventSubscription = eventSubscriptionMap.get(subscribeId);
        if(null != eventSubscription){
            if(logger.isDebugEnabled()){
                logger.debug("cancelSubscribe, subscribeId:{}, eventSubscription:{}", subscribeId, eventSubscription);
            }
            String sessionId = eventSubscription.getSessionId();
            String clientId = eventSubscription.getClientId();
            SessionClientManager.removeSubscribeId(sessionId, clientId);
            eventSubscriptionMap.remove(subscribeId);
        }
    }

    public static EventSubscription getEventSubscription(Long subscribeId){
        return eventSubscriptionMap.get(subscribeId);
    }

    public static Map<Long, EventSubscription> getEventSubscriptionMap(){
        return eventSubscriptionMap;
    }

    public static int getEventCapacity() {
        return EVENT_CAPACITY;
    }

    public static long getEventSize(){
        int sum = 0;
        Collection<EventSubscription> values = eventSubscriptionMap.values();
        if(!CollectionUtils.isEmpty(values)){
            for (EventSubscription eventSubscription : values){
                sum += eventSubscription.getEventSize();
            }
        }
        return sum;
    }
}
