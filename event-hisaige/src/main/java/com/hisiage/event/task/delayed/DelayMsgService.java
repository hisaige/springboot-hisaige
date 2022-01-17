package com.hisiage.event.task.delayed;

/**
 * @author chenyj
 * 2020/10/16 - 18:48.
 **/
public class DelayMsgService {

    /**
     * 发送延迟队列消息
     * @param delayedMsg 消息负载
     * @return true if publish success
     */
    public boolean pubDelayMsg(DelayedMsg delayedMsg){
        return DelayMsgDispatcher.offerTask(delayedMsg);
    }

    /**
     * 判断延迟队列消息是否已经存在
     * @param delayedMsg 消息负载
     * @return true if exist
     */
    public boolean contains(DelayedMsg delayedMsg){return DelayMsgDispatcher.contains(delayedMsg);}

    /**
     * 移除延迟队列中的延迟任务
     * @param delayedMsg 消息负载
     * @return true if remove success
     */
    public boolean remove(DelayedMsg delayedMsg) {
        return DelayMsgDispatcher.contains(delayedMsg);
    }
}
