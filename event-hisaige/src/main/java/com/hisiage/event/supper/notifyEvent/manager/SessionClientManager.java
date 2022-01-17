package com.hisiage.event.supper.notifyEvent.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 用户会话与订阅管理
 * @author chenyj
 * 2020/4/4 - 17:17.
 **/
public class SessionClientManager {
    private static final Logger logger = LoggerFactory.getLogger(SessionClientManager.class);

    /**
     * 存储所有会话的订阅sessionId, clientId, subscriptionId哈希表
     */
    private static final ConcurrentHashMap<String, ConcurrentHashMap<String, Long>> SESSION_SUBSCRIBE_MAP = new ConcurrentHashMap<>();

    public static ConcurrentHashMap<String, Long> getOrCreateSubscribeMap(String sessionId) {
        if (sessionId != null) {
            ConcurrentHashMap<String, Long> ret = SESSION_SUBSCRIBE_MAP.get(sessionId);
            if (ret == null) {
                ret = new ConcurrentHashMap<>();
                SESSION_SUBSCRIBE_MAP.put(sessionId, ret);
            }
            return ret;
        } else {
            return null;
        }
    }

    /**
     * 移除用户的所有订阅
     * 根据sessionId 即 token 移除登录用户的说有订阅，如果用户退出登录，
     * 应该调用本方法移除，避免退出登录的用户继续订阅事件 这里只是暴露方法，应该参考具体情况调用
     * @param sessionId 用户登录的身份标识
     * @return ConcurrentHashMap
     */
    public static ConcurrentHashMap<String, Long> removeSubscribeMap(String sessionId) {
        if (sessionId != null) {
            return SESSION_SUBSCRIBE_MAP.remove(sessionId);
        } else {
            return null;
        }
    }

    @Nullable
    public static Long getSubscribeId(String sessionId, String clientId){
        Assert.notNull(sessionId, "sessionId cannot be null");
        Assert.notNull(clientId, "clientId cannot be null");
        synchronized (SESSION_SUBSCRIBE_MAP){
            ConcurrentHashMap<String, Long> sessionClientIdMap = SESSION_SUBSCRIBE_MAP.get(sessionId);
            if(null == sessionClientIdMap){
                return null;
            }
            return sessionClientIdMap.get(clientId);
        }
    }

    /**
     * 根据订阅信息移除用户的某个订阅
     * @param sessionId  用户登录的身份标识
     * @param clientId 订阅标识
     */
    public static void removeSubscribeId(String sessionId, String clientId) {
        if (sessionId != null && clientId != null) {
            ConcurrentHashMap<String, Long> subscribeMap = SESSION_SUBSCRIBE_MAP.get(sessionId);
            if (subscribeMap != null && !subscribeMap.isEmpty()) {
                Long subscribeId = subscribeMap.remove(clientId);
                if (logger.isDebugEnabled()) {
                    logger.debug("removeSubscribeId sessionId: {} clientId: {} subscribeId: {}", sessionId, clientId, subscribeId);
                }
                if(subscribeMap.isEmpty()){
                    SESSION_SUBSCRIBE_MAP.remove(sessionId);
                    if (logger.isDebugEnabled()) {
                        //移除当前session的最后一个订阅
                        logger.debug("remove session subscribe, sessionId: {} clientId: {} subscribeId: {}", sessionId, clientId, subscribeId);
                    }
                }
            }
        }
    }
}
