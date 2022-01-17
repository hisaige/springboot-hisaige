package com.hisiage.event.ievent;

/**
 * 持久化事件
 * @author chenyj
 * 2020/3/19 - 19:06.
 **/
public abstract class AbstractPersistenceEvent<T> extends AbstractEvent<T> implements PersistenceEvent<T> {

}
