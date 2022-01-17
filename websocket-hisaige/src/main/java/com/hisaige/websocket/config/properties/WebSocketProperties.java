package com.hisaige.websocket.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * websocket配置参数
 * @author chenyj
 * 2019年3月21日 下午2:24:33
 */
@ConfigurationProperties(prefix = "system.config.websocket")
public class WebSocketProperties {

	/**
	 * 以下为业务相关配置
	 */
	//每次消费最大事件数,暂时用不到
	private Integer maxFailedNum = 5;
	
	//netty积压最大事件数， 很重要，等同于io.netty.eventexecutor.maxPendingTasks
	private Integer maxPendingTasks = 30000;
	
	/**
	 * 一些任务线程池配置
	 */
	private Integer taskCorePoolSize = 35;
	private Integer taskMaxPoolSize = 50;
	private Integer taskCorePoolIncrement = 5;
	private Integer taskSchedulerCorePoolSize = 25;
	private Integer taskKeepAliveTime = 60;
	private Integer taskParallelKeepAliveTime = 10;

	/**
	 * 以下为netty服务相关的配置
	 */
	
	// 客户端配置
	private ClientProperties clientProperties = new ClientProperties();

	// 服务端配置
	private ServerProperties serverProperties = new ServerProperties();

	// ********* 公共配置 *******************

	private String host = "0.0.0.0";

	private Integer websocketVersion = 13;

	// 内容的最大长度（字节） 针对http消息
	private Integer maxContentLength = 8192;

	private Boolean tcpNotDelay = true;

	// 日志等级配置
	private String logLevel = "debug";

	private Boolean isKeepalive = Boolean.FALSE;

	// 连接超时时间，单位毫秒
	private Integer timeoutMillis = 5000;

	// 最大允许帧有效载荷长度 需要发送超长json字符串时，需要设大这个值，默认65536
	private Integer maxFramePayloadLength = 65536;

	public Boolean getIsKeepalive() {
		return isKeepalive;
	}

	public void setIsKeepalive(Boolean isKeepalive) {
		this.isKeepalive = isKeepalive;
	}

	public Integer getTimeoutMillis() {
		return timeoutMillis;
	}

	public void setTimeoutMillis(Integer timeoutmillis) {
		this.timeoutMillis = timeoutmillis;
	}

	public Integer getMaxFramePayloadLength() {
		return maxFramePayloadLength;
	}

	public void setMaxFramePayloadLength(Integer maxFramePayloadLength) {
		this.maxFramePayloadLength = maxFramePayloadLength;
	}

	public String getLogLevel() {
		return logLevel;
	}

	public void setLogLevel(String logLevel) {
		this.logLevel = logLevel;
	}

	public Boolean getTcpNotDelay() {
		return tcpNotDelay;
	}

	public void setTcpNotDelay(Boolean tcpNotDelay) {
		this.tcpNotDelay = tcpNotDelay;
	}

	public Integer getMaxContentLength() {
		return maxContentLength;
	}

	public void setMaxContentLength(Integer maxContentLength) {
		this.maxContentLength = maxContentLength;
	}

	public Integer getWebsocketVersion() {
		return websocketVersion;
	}

	public void setWebsocketVersion(Integer websocketVersion) {
		this.websocketVersion = websocketVersion;
	}

	public ClientProperties getClientProperties() {
		return clientProperties;
	}

	public void setClientProperties(ClientProperties clientProperties) {
		this.clientProperties = clientProperties;
	}

	public ServerProperties getServerProperties() {
		return serverProperties;
	}

	public void setServerProperties(ServerProperties serverProperties) {
		this.serverProperties = serverProperties;
	}

	public Integer getMaxFailedNum() {
		return maxFailedNum;
	}

	public void setMaxFailedNum(Integer maxFailedNum) {
		this.maxFailedNum = maxFailedNum;
	}

	public Integer getTaskCorePoolSize() {
		return taskCorePoolSize;
	}

	public void setTaskCorePoolSize(Integer taskCorePoolSize) {
		this.taskCorePoolSize = taskCorePoolSize;
	}

	public Integer getTaskMaxPoolSize() {
		return taskMaxPoolSize;
	}

	public void setTaskMaxPoolSize(Integer taskMaxPoolSize) {
		this.taskMaxPoolSize = taskMaxPoolSize;
	}

	public Integer getTaskCorePoolIncrement() {
		return taskCorePoolIncrement;
	}

	public void setTaskCorePoolIncrement(Integer taskCorePoolIncrement) {
		this.taskCorePoolIncrement = taskCorePoolIncrement;
	}

	public Integer getTaskSchedulerCorePoolSize() {
		return taskSchedulerCorePoolSize;
	}

	public void setTaskSchedulerCorePoolSize(Integer taskSchedulerCorePoolSize) {
		this.taskSchedulerCorePoolSize = taskSchedulerCorePoolSize;
	}

	public Integer getTaskKeepAliveTime() {
		return taskKeepAliveTime;
	}

	public void setTaskKeepAliveTime(Integer taskKeepAliveTime) {
		this.taskKeepAliveTime = taskKeepAliveTime;
	}

	public Integer getTaskParallelKeepAliveTime() {
		return taskParallelKeepAliveTime;
	}

	public void setTaskParallelKeepAliveTime(Integer taskParallelKeepAliveTime) {
		this.taskParallelKeepAliveTime = taskParallelKeepAliveTime;
	}

	public Integer getMaxPendingTasks() {
		return maxPendingTasks;
	}

	public void setMaxPendingTasks(Integer maxPendingTasks) {
		this.maxPendingTasks = maxPendingTasks;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}
}
