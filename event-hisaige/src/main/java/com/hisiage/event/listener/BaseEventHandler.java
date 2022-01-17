package com.hisiage.event.listener;

import com.hisiage.event.ievent.AbstractEvent;

/**
 * 事件处理的基础类
 * @param <T>
 */
public abstract class BaseEventHandler<T extends AbstractEvent> implements IEventListener<T> {

    private String name;

    @Override
    public String getEventName() {
        if(null == name){
            name = getEntityClass().getName();
        }
        return name;
    }

//    private Class<T> getEntityClass() {
//        @SuppressWarnings("unchecked")
//        Class<T> entityClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
//        return entityClass;
//    }

//    public static void main(String[] args) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
//        String name = TestEvent2.class.getName();
//        Class<?> aClass = Class.forName(name);
//        Object o = aClass.newInstance();
//        if(o instanceof AbstractNotifyEvent){
//            System.out.println(((AbstractNotifyEvent) o).getEventPath());
//        }
//    }
}
