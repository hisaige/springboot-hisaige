package com.hisiage.event.processor;

import com.hisiage.event.factory.EventRegister;
import com.hisiage.event.listener.IEventListener;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * @author chenyj
 * 2020/1/15 - 17:21.
 **/
public class IEventListenerPostProcessor implements BeanPostProcessor {

    private EventRegister eventRegister;

    public IEventListenerPostProcessor(EventRegister eventRegister){
        this.eventRegister = eventRegister;
    }

    @Override
    public Object postProcessAfterInitialization(@NotNull Object bean, String beanName) throws BeansException {

        IEventListener listener;
        if(bean instanceof IEventListener){
            listener = (IEventListener)bean;
            eventRegister.register(listener);
        }
        return bean;
    }
}
