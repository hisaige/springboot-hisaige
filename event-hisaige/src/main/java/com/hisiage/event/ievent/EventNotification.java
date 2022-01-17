package com.hisiage.event.ievent;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hisiage.event.supper.notifyEvent.EventSubscription;

import java.util.ArrayList;
import java.util.List;

/**
 * 通知事件事件实体
 * @author chenyj
 * 2020/4/4 - 18:27.
 **/
public class EventNotification<T> {

    private String eventPath;
    private String sessionId;
    private String subEventPath;
    private T msg;

    @JsonIgnore
    @JSONField(serialize = false)
    private volatile List<EventSubscription> eventSubscriptions;

    @JsonIgnore
    @JSONField(serialize = false)
    private long timestamp; //先不开启本字段的序列化，作为扩展使用

    @JsonIgnore
    @JSONField(serialize = false)
    private String eventType; //先不开启本字段的序列化，作为扩展使用

    public EventNotification(AbstractNotifyEvent<T> abstractEvent){
        eventPath = abstractEvent.getEventPath();
        msg = abstractEvent.getMsg();
        sessionId = abstractEvent.getSessionId();
        subEventPath = abstractEvent.getSubEventPath();
        this.timestamp = abstractEvent.getTimestamp();
        this.eventType = abstractEvent.getEventType();
    }

    public String getEventPath() {
        return eventPath;
    }

    public void setEventPath(String eventPath) {
        this.eventPath = eventPath;
    }

    public T getMsg() {
        return msg;
    }

    public void setMsg(T msg) {
        this.msg = msg;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getSubEventPath() {
        return subEventPath;
    }

    public void setSubEventPath(String subEventPath) {
        this.subEventPath = subEventPath;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public void addEventSubscription(EventSubscription eventSubscription){
        if(null == eventSubscriptions){
            synchronized (EventNotification.class){
                if(null == eventSubscriptions){
                    eventSubscriptions = new ArrayList<>();
                }
            }
        }
        eventSubscriptions.add(eventSubscription);
    }

    public List<EventSubscription> getEventSubscriptions(){
        return eventSubscriptions;
    }

    @Override
    public String toString() {
        return "EventNotification{" +
                "eventPath='" + eventPath + '\'' +
                ", msg=" + msg +
                ", sessionId='" + sessionId + '\'' +
                ", subEventPath='" + subEventPath + '\'' +
                ", timestamp=" + timestamp +
                ", eventType='" + eventType + '\'' +
                '}';
    }


}
