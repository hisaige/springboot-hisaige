package com.hisiage.event.ievent;

/**
 * 持久化事件接口
 * 实现了本接口的所有事件，都将被持久化，不论外部或者内部事件
 * 这里的持久化指的是存入数据库、redis或MQ等外部应用，事件将不存于JVM缓存中
 * @author chenyj
 * 2019/12/7 - 16:05.
 **/
public interface PersistenceEvent<T> extends PathEvent<T> {

}
