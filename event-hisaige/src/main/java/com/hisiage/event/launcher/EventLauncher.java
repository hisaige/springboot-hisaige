package com.hisiage.event.launcher;

import com.hisiage.event.ievent.AbstractEvent;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * 事件发射装置接口
 * @author chenyj
 * 2019/12/16 - 22:55.
 **/
public interface EventLauncher {

    /**
     * 事件发射方法
     * @param event 具体事件
     */
    void pubEvent(AbstractEvent event);

    /**
     * 发生多个事件
     * @param events 事件列表
     */
    default void pubEvent(List<AbstractEvent> events){
        if(!CollectionUtils.isEmpty(events)){
            events.forEach(this::pubEvent);
        }
    }
}
