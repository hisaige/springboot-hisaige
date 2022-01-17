package com.hisaige.websocket.config.properties;

import java.util.HashSet;
import java.util.Set;

public class ServerProperties extends NettyProperties {

	// 服务端bossGroup 线程数，默认是CPU核数的两倍
	private Integer bossGroupNum = 2;

	// 服务端workerGroup 线程数， 默认是CPU核数的两倍
	private Integer workerGroupNum = 8;

	private Integer port = 19402;

	private Set<String> eventPathSet = new HashSet<>();

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

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public Set<String> getEventPath() {
		return eventPathSet;
	}

	public void setEventPath(Set<String> eventPathSet) {
		this.eventPathSet = eventPathSet;
	}
}
