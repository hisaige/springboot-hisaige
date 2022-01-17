package com.hisiage.event.ievent;

/**
 * 事件顶级接口
 * @author chenyj
 * 2019/12/7 - 15:31.
 **/
public interface IEvent extends java.io.Serializable {

    /**
     * 获取事件类型,即事件类的全类名
     * @return String 事件类型
     */
    default String getEventType(){
        return this.getClass().getName();
    }

}
