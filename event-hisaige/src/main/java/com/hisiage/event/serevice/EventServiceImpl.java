package com.hisiage.event.serevice;

import com.hisaige.core.util.CollectionUtils;
import com.hisiage.event.factory.EventRegister;
import com.hisiage.event.ievent.AbstractEvent;
import com.hisiage.event.ievent.AbstractNotifyEvent;
import com.hisiage.event.ievent.AbstractPersistenceEvent;
import com.hisiage.event.launcher.NotifyEventLauncher;
import com.hisiage.event.launcher.CustomEventLauncher;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author chenyj
 * 2020/3/19 - 14:54.
 **/
public class EventServiceImpl implements EventService {

    private List<CustomEventLauncher> customEventLaunchers;
    private List<NotifyEventLauncher> notifyEventLaunchers;
    private EventRegister eventRegister;

    public EventServiceImpl(@Nullable List<CustomEventLauncher> customEventLaunchers, @Nullable List<NotifyEventLauncher> notifyEventLaunchers, EventRegister eventRegister){
        this.customEventLaunchers = customEventLaunchers;
        this.notifyEventLaunchers = notifyEventLaunchers;
        this.eventRegister = eventRegister;
    }

    @Override
    public void pubEvent(AbstractEvent event) {
        //先处理内部事件监听
        eventRegister.process(event);

        if(event instanceof AbstractNotifyEvent){
            if(!CollectionUtils.isEmpty(notifyEventLaunchers)){
                notifyEventLaunchers.forEach(action -> action.pubEvent(event));
            }
        } else if(event instanceof AbstractPersistenceEvent){
            if(!CollectionUtils.isEmpty(customEventLaunchers)){
                customEventLaunchers.forEach(action -> action.pubEvent(event));
            }
        }
    }
}
