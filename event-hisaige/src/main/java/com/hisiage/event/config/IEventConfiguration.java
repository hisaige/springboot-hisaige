package com.hisiage.event.config;

import com.hisiage.event.factory.EventRegister;
import com.hisiage.event.launcher.CustomEventLauncher;
import com.hisiage.event.launcher.NotifyEventLauncher;
import com.hisiage.event.processor.IEventListenerPostProcessor;
import com.hisiage.event.serevice.EventService;
import com.hisiage.event.serevice.EventServiceImpl;
import com.hisiage.event.task.delayed.DelayMsgDispatcher;
import com.hisiage.event.task.delayed.DelayMsgService;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;

import java.util.List;

/**
 * @author chenyj
 * 2020/1/15 - 14:48.
 **/
public class IEventConfiguration {

    private List<CustomEventLauncher> customEventLaunchers;
    private List<NotifyEventLauncher> notifyEventLaunchers;
    private EventRegister eventRegister;

    /**
     * ObjectProvider<List<CustomEventLauncher>> customEventLaunchers,
     *                                   ObjectProvider<List<NotifyEventLauncher>> notifyEventLaunchers,
     * @param eventRegister 事件注册器
     */
    public IEventConfiguration(ObjectProvider<List<CustomEventLauncher>> customEventLaunchers,
                                        ObjectProvider<List<NotifyEventLauncher>> notifyEventLaunchers, ObjectProvider<EventRegister> eventRegister){
        this.customEventLaunchers = customEventLaunchers.getIfAvailable();
        this.notifyEventLaunchers = notifyEventLaunchers.getIfAvailable();
        this.eventRegister = eventRegister.getIfAvailable();
    }

    /**
     * 初始化事件发射器
     * @return IEventLauncher
     */
    @Bean
    public EventService iEventService(){
        return new EventServiceImpl(customEventLaunchers, notifyEventLaunchers, eventRegister);
    }

    @Bean
    public DelayMsgDispatcher delayMsgDispatcher(){
        return new DelayMsgDispatcher(iEventService());
    }

    @Bean
    public DelayMsgService delayMsgService(){
        return new DelayMsgService();
    }

    @Bean
    public IEventListenerPostProcessor IEventListenerPostProcessor(){
        return new IEventListenerPostProcessor(eventRegister);
    }

}
