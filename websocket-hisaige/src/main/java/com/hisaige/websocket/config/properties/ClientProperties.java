package com.hisaige.websocket.config.properties;

/**
 * @author chenyj
 * 2020/4/1 - 22:21.
 **/
public class ClientProperties extends NettyProperties {

    // 服务端bossGroup 线程数，默认是CPU核数的两倍
    private Integer bossGroupNum = 2;

    // 服务端workerGroup 线程数， 默认是CPU核数的两倍
    private Integer workerGroupNum = 4;

    public Integer getBossGroupNum() {
        return bossGroupNum;
    }

    public void setBossGroupNum(Integer bossGroupNum) {
        this.bossGroupNum = bossGroupNum;
    }

    public Integer getWorkerGroupNum() {
        return workerGroupNum;
    }

    public void setWorkerGroupNum(Integer workerGroupNum) {
        this.workerGroupNum = workerGroupNum;
    }
}
