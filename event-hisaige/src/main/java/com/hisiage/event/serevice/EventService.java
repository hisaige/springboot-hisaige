package com.hisiage.event.serevice;

import com.hisiage.event.ievent.EventNotification;
import com.hisiage.event.launcher.EventLauncher;
import com.hisiage.event.supper.notifyEvent.EventSubscription;
import com.hisiage.event.supper.notifyEvent.exception.Subscribe2EventFailedException;
import com.hisiage.event.supper.notifyEvent.manager.SubscriberManager;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.Set;

/**
 * 事件服务
 * @author chenyj
 * 2020/3/19 - 14:53.
 **/
public interface EventService extends EventLauncher {

    /**
     * 事件订阅
     *
     * @param sessionId 用户身份标识
     * @param clientId  当前客户端标识
     * @return 订阅ID
     */
    default Long subscribe2Event(String subscribeIp, String sessionId, String clientId, @Nullable Set<String>paths) throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        return SubscriberManager.subscribe2Event(subscribeIp, sessionId, clientId, paths);
    }

    /**
     * 取消订阅
     */
    default void cancelSubscribe(long subscribeId) {
        SubscriberManager.cancelSubscribe(subscribeId);
    }

    default List<EventNotification> getEvents(long subscribeId, Integer eventSize, Long timeout) throws InterruptedException {
        EventSubscription eventSubscription = SubscriberManager.getEventSubscription(subscribeId);

        if (null == eventSubscription) {
            //当前订阅ID未订阅 抛出订阅失败异常
            throw new Subscribe2EventFailedException("current subscribeId is not subscribe2Event yet");
        }
        eventSize = null == eventSize ? 5 : eventSize;
        timeout = null == timeout ? 20000 : timeout;
        return eventSubscription.pollEvents(eventSize, timeout);
    }

}
