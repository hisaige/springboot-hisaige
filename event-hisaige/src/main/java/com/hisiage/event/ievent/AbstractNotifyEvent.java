package com.hisiage.event.ievent;

/**
 * 外部事件抽象类，定义可订阅事件
 * 注：包含内部事件的所有功能，默认实现是加入到被订阅的外部事件队列中
 * @author chenyj
 * 2019/12/7 - 16:05.
 **/
public abstract class AbstractNotifyEvent<T> extends AbstractEvent<T> implements NotifyEvent<T> {

    private String sessionId;

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public EventNotification getEventInfo(){

        return new EventNotification<>(this);
    }
}
