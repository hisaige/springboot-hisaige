package com.hisaige.core.task.delay;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * @author chenyj
 * 2020/10/16 - 14:01.
 **/
public class DelayedMsg<T> implements Delayed {

    private int id;
    private T msg; // 消息内容--文件名
    private long excuteTime;// 延迟时长，单位：毫秒

    public int getId() {
        return id;
    }

    public T getMsg() {
        return msg;
    }

    public long getExcuteTime() {
        return excuteTime;
    }

    public DelayedMsg(int id, T msg, long delayTime) {
        this.id = id;
        this.excuteTime = delayTime + System.currentTimeMillis();
        this.msg = msg;
    }

    // 自定义实现比较方法返回 1 0 -1三个参数
    @Override
    public int compareTo(Delayed delayed) {
        DelayedMsg msg = (DelayedMsg) delayed;
        return Integer.compare(this.id, msg.id);
    }

    // 延迟任务是否到时就是按照这个方法判断如果返回的是负数则说明到期否则还没到期
    @Override
    public long getDelay(TimeUnit unit) {
//        return unit.convert(this.excuteTime - System.nanoTime(), TimeUnit.NANOSECONDS);
        return unit.convert(this.excuteTime - System.currentTimeMillis(), TimeUnit.NANOSECONDS);
    }
}
