package com.hisiage.event.ievent;

/**
 * 内部非持久化事件接口
 * 内部事件都绑定一个listener，事件发出后，handler将会根据事件详情进行处理
 * @author chenyj
 * 2019/12/7 - 15:31.
 **/
public interface InternalEvent<T> extends IEvent {
    /**
     * 设置事件详情
     * @param t 事件详情
     */
    void setMsg(T t);

    /**
     * 获取事件详情
     * @return 事件详情
     */
    T getMsg();
}
