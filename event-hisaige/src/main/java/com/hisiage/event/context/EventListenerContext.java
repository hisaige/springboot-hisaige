package com.hisiage.event.context;

import com.google.common.collect.Lists;
import com.hisiage.event.listener.IEventListener;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 事件处理器上下文对象
 * @author chenyj
 * 2019/12/16 - 22:40.
 **/
public class EventListenerContext implements BeanFactoryAware {

    private static final Logger logger = LoggerFactory.getLogger(EventListenerContext.class);

    private BeanFactory beanFactory;
    private Map<String, List<IEventListener>> listenerMap = new HashMap<>();

    public List<IEventListener> getListeners(String type){
        return listenerMap.get(type);
    }

    public void register(@NotNull IEventListener listener){
        String type = listener.getEventName();
        if(null == type){
            logger.warn("IEventListener:{}' type is null...", listener.getClass().getName());
        } else {
            if(listenerMap.containsKey(type)){
                listenerMap.put(type, Lists.newArrayList(listener));
            } else{
                listenerMap.get(type).add(listener);
            }
        }
    }

    public void registers(@NotNull List<IEventListener> listeners){
        listeners.forEach(this::register);
    }

    @Override
    public void setBeanFactory(@NotNull BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

}
