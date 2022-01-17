package com.hisiage.event.supper.notifyEvent;

import com.hisaige.core.util.CollectionUtils;
import com.hisiage.event.ievent.AbstractEvent;
import com.hisiage.event.ievent.AbstractNotifyEvent;
import com.hisiage.event.ievent.EventNotification;
import com.hisiage.event.launcher.NotifyEventLauncher;
import com.hisiage.event.supper.notifyEvent.manager.SubscriberManager;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 默认的通知事件实现方法
 * 使用时仅需要添加到bean即可
 * @author chenyj
 * 2020/4/4 - 17:11.
 **/
public class NotifyEventLauncherImpl implements NotifyEventLauncher {

//    private String

    @Override
    public void pubEvent(AbstractEvent event) {
        for (EventSubscription eventSubscription : SubscriberManager.getEventSubscriptionMap().values()) {
            eventSubscription.addEvent(((AbstractNotifyEvent) event).getEventInfo());
        }
    }

    @Override
    public void pubEvent(List<AbstractEvent> events) {
        if(CollectionUtils.isEmpty(events)){
            return;
        }
        List<EventNotification> eventNotifications = events.stream().map(event -> ((AbstractNotifyEvent) event).getEventInfo()).collect(Collectors.toList());
        for (EventSubscription eventSubscription : SubscriberManager.getEventSubscriptionMap().values()) {
            eventSubscription.addEvent(eventNotifications);
        }
    }
}
