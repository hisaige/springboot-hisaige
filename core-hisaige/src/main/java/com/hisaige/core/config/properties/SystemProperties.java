package com.hisaige.core.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "hisaige.system.config")
@Data
public class SystemProperties {

	//http代理配置
	private Boolean enableProxy = false;
	private String proxyHost = "localhost";
	private String proxyPort = "8888";

	//swagger 配置
	private Boolean enableSwagger2 = true;

}
