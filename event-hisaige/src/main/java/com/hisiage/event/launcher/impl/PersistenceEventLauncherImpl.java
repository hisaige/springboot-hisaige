package com.hisiage.event.launcher.impl;

import com.alibaba.fastjson.JSON;
import com.hisiage.event.ievent.AbstractEvent;
import com.hisiage.event.launcher.CustomEventLauncher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 持久化事件发射器
 * @author chenyj
 * 2019/12/16 - 23:03.
 **/
public class PersistenceEventLauncherImpl implements CustomEventLauncher {

    private static final Logger logger = LoggerFactory.getLogger(PersistenceEventLauncherImpl.class);

    @Override
    public void pubEvent(AbstractEvent event) {
        //通知事件发射具体方式
        if(logger.isDebugEnabled()){
            logger.debug("process event:{}", JSON.toJSONString(event));
        }
        //...
    }
}
