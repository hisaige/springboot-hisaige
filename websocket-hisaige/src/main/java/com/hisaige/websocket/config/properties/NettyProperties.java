package com.hisaige.websocket.config.properties;

/**
 * @author chenyj
 * 2020/4/1 - 22:20.
 **/
public class NettyProperties {

    private Boolean isServerHearBeat = Boolean.TRUE;

    // 读超时 单位 秒
    private Integer readIdleTime = 500;

    // 写超时 单位 秒
    private Integer writeIdleTime = 120;

    // 读写超时，默认 0 不设置 单位 秒
    private Integer readWriteIdleTime = 0;

    // 同ChannelOption.SO_BACKLOG一致
    private Integer soBacklog = 128;

    // 同ChannelOption.WRITE_SPIN_COUNT一致
    private Integer writeSpinCount = 16;

    // 同WriteBufferWaterMark的low一致
    private Integer writeBufferLowWaterMark = 32*1024;

    // 同WriteBufferWaterMark的hight一致
    private Integer writeBufferHighWaterMark = 64*1024;

    // 同ChannelOption.SO_LINGER一致
    private Integer soLinger = -1;

    private Integer soRcvbuf = -1;

    private Integer soSndbuf = -1;

    // 同ChannelOption.ALLOW_HALF_CLOSURE一致
    private Boolean allowHalfClosure = false;

    private Boolean isUseCompressionHandler = Boolean.TRUE;


    public Boolean getServerHearBeat() {
        return isServerHearBeat;
    }

    public void setServerHearBeat(Boolean serverHearBeat) {
        isServerHearBeat = serverHearBeat;
    }

    public Boolean getUseCompressionHandler() {
        return isUseCompressionHandler;
    }

    public void setUseCompressionHandler(Boolean useCompressionHandler) {
        isUseCompressionHandler = useCompressionHandler;
    }

    public Integer getSoRcvbuf() {
        return soRcvbuf;
    }

    public void setSoRcvbuf(Integer soRcvbuf) {
        this.soRcvbuf = soRcvbuf;
    }

    public Integer getSoSndbuf() {
        return soSndbuf;
    }

    public void setSoSndbuf(Integer soSndbuf) {
        this.soSndbuf = soSndbuf;
    }

    public Boolean getIsServerHearBeat() {
        return isServerHearBeat;
    }

    public void setIsServerHearBeat(Boolean isServerHearBeat) {
        this.isServerHearBeat = isServerHearBeat;
    }

    public Integer getReadIdleTime() {
        return readIdleTime;
    }

    public void setReadIdleTime(Integer readIdleTime) {
        this.readIdleTime = readIdleTime;
    }

    public Integer getWriteIdleTime() {
        return writeIdleTime;
    }

    public void setWriteIdleTime(Integer writeIdleTime) {
        this.writeIdleTime = writeIdleTime;
    }

    public Integer getReadWriteIdleTime() {
        return readWriteIdleTime;
    }

    public void setReadWriteIdleTime(Integer readWriteIdleTime) {
        this.readWriteIdleTime = readWriteIdleTime;
    }

    public Integer getSoBacklog() {
        return soBacklog;
    }

    public void setSoBacklog(Integer soBacklog) {
        this.soBacklog = soBacklog;
    }

    public Integer getWriteSpinCount() {
        return writeSpinCount;
    }

    public void setWriteSpinCount(Integer writeSpinCount) {
        this.writeSpinCount = writeSpinCount;
    }

    public Integer getWriteBufferLowWaterMark() {
        return writeBufferLowWaterMark;
    }

    public void setWriteBufferLowWaterMark(Integer writeBufferLowWaterMark) {
        this.writeBufferLowWaterMark = writeBufferLowWaterMark;
    }

    public Integer getWriteBufferHighWaterMark() {
        return writeBufferHighWaterMark;
    }

    public void setWriteBufferHighWaterMark(Integer writeBufferHighWaterMark) {
        this.writeBufferHighWaterMark = writeBufferHighWaterMark;
    }

    public Integer getSoLinger() {
        return soLinger;
    }

    public void setSoLinger(Integer soLinger) {
        this.soLinger = soLinger;
    }

    public Boolean getAllowHalfClosure() {
        return allowHalfClosure;
    }

    public void setAllowHalfClosure(Boolean allowHalfClosure) {
        this.allowHalfClosure = allowHalfClosure;
    }
}
