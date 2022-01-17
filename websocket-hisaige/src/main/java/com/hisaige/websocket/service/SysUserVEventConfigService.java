package com.hisaige.websocket.service;

/**
 * 一个获取用户配置信息的接口,包含对用户配置的增删改查
 * 推荐优先将用户配置存到缓存中
 * @author chenyj
 * 2020/2/5 - 11:35.
 **/
public interface SysUserVEventConfigService {

    /**
     * 获取配置信息
     * @param userId 用户id
     * @param eventPath 事件path
     * @return 配置信息
     */
    String getSysUserVEventConfig(String userId, String eventPath);

    /**
     * 保存配置信息
     * @param userId 用户id
     * @param eventPath 事件path 即事件类型
     * @param configValue 配置值
     * @return 配置信息
     */
    String addSysUserVEventConfig(String userId, String eventPath, String configValue);

    /**
     * 更新配置信息
     * @param userId 用户id
     * @param eventPath 事件path 即事件类型
     * @param configValue 配置值
     * @return 配置信息
     */
    String updateSysUserVEventConfig(String userId, String eventPath, String configValue);

    /**
     * 删除
     * @param userId 用户id
     * @param eventPath 事件path 即事件类型
     */
    void deleteSysUserVEventConfig(String userId, String eventPath);
    
    /**
     * 根据userId或者token 获取userId
     * @param token 用户标识
     * @return String
     */
    String getUserIdByToken(String token);
}

