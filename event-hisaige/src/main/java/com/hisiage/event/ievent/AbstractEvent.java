package com.hisiage.event.ievent;

/**
 * 内部事件抽象类,不持久化 也不触发任何通知
 * @author chenyj
 * 2019/12/7 - 15:59.
 **/
public abstract class AbstractEvent<T> implements InternalEvent<T> {

    //产生当前事件的时间戳
    private final long timestamp;
    //事件详情
    private T msg;


    public AbstractEvent(T t) {
        this.msg = t;
        timestamp = System.currentTimeMillis();
    }

    public AbstractEvent() {
        timestamp = System.currentTimeMillis();
    }

    @Override
    public void setMsg(T t) {
        this.msg = t;
    }

    @Override
    public T getMsg() {
        return msg;
    }

    public final long getTimestamp() {
        return this.timestamp;
    }

}
