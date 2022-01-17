package com.hisiage.event.ievent;

/**
 * 带有path的事件
 * @param <T>
 */
public interface PathEvent<T> extends InternalEvent<T> {

    /**
     * 获取事件标识符
     * @return String
     */
    String getEventPath();

    /**
     * 用于时间拓展使用 如 同一种报警事件，有时候需要弹窗，有时候又不需要弹窗仅做业务更新。
     * @return subEventPath
     */
    default String getSubEventPath(){
        return null;
    }
}
