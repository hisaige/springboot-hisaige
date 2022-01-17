package com.hisiage.event.listener;

import com.hisiage.event.ievent.IEvent;

import java.lang.reflect.ParameterizedType;

/**
 * 事件监听者接口
 * <T> 事件类型
 * @author chenyj
 * 2019/12/16 - 18:26.
 **/
public interface IEventListener<T extends IEvent> {

    /**
     * 获取监听者全类名
     * @return String 监听者全类名
     */
    default String getListenerName(){
        return this.getClass().getName();
    }

    /**
     * 获取当前接口的泛型类型
     * ps: 获取父类的泛型类型
     *  ParameterizedType genericSuperclass = (ParameterizedType)this.getClass().getGenericSuperclass();
     *  (Class<T>) genericSuperclass.getActualTypeArguments()[0];
     * @return Class<T> or null 获取失败返回null
     */
    default Class<T> getEntityClass() {
        @SuppressWarnings("unchecked")
        Class<T> entityClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        return entityClass;
    }

    /**
     * 异步监听并处理事件（本方法与发出事件的方法不在同一个线程上）
     * 可在当前方法
     * @param event 事件
     */
    void process(T event);

    String getEventName();
}
