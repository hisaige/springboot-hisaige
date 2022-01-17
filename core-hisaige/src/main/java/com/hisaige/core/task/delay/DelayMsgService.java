package com.hisaige.core.task.delay;

import org.springframework.stereotype.Service;

/**
 * @author chenyj
 * 2020/10/16 - 18:48.
 **/
@Service
public class DelayMsgService {

    public boolean pubDelayMsg(DelayedMsg delayedMsg){
        return DelayMsgDispatcher.offerTask(delayedMsg);
    }
}
