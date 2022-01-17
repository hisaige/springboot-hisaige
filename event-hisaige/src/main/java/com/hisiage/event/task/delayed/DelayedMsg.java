package com.hisiage.event.task.delayed;

import com.hisiage.event.ievent.AbstractEvent;
import com.hisiage.event.ievent.IEvent;

import java.util.Objects;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * @author chenyj
 * 2020/10/16 - 14:01.
 **/
public class DelayedMsg<T extends AbstractEvent> implements Delayed {

    private int id;
    private T msg; // 消息内容--文件名
    private long executeTime;// 延迟时长，单位：毫秒

    public int getId() {
        return id;
    }

    public T getMsg() {
        return msg;
    }

    public long getExecuteTime() {
        return executeTime;
    }

    public DelayedMsg(int id, T msg, long delayTime) {
        this.id = id;
        this.executeTime = delayTime + System.currentTimeMillis();
        this.msg = msg;
    }

    // 自定义实现比较方法返回 1 0 -1三个参数
    @Override
    public int compareTo(Delayed delayed) {
        DelayedMsg msg = (DelayedMsg) delayed;
        return Integer.compare(this.id, msg.getId());
    }

    // 延迟任务是否到时就是按照这个方法判断如果返回的是负数则说明到期否则还没到期
    @Override
    public long getDelay(TimeUnit unit) {
//        return unit.convert(this.excuteTime - System.nanoTime(), TimeUnit.NANOSECONDS);
        return unit.convert(this.executeTime - System.currentTimeMillis(), TimeUnit.NANOSECONDS);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DelayedMsg<?> that = (DelayedMsg<?>) o;
        return id == that.id &&
                executeTime == that.executeTime &&
                Objects.equals(msg, that.msg);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, msg, executeTime);
    }
}
