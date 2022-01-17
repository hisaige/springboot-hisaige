package com.hisaige.core.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
/**
 *  httpClient连接池配置属性类，
 *  注意` -- 所有时间单位都是 毫秒
 */
@ConfigurationProperties(prefix = "system.config.rest-template")
@Data
public class RestTemplateProperty {

	// 连接池的最大连接数，此处配置400
	private Integer maxTotalConnect = 400;
	// 单个主机的最大连接数
	private Integer maxConnectPerRoute = 100;
	// 连接超时默认20s
	private Integer connectTimeout = 5000;
	// 读取超时默认30s
	private Integer readTimeout = 5000;
	// 连接不够用等待时间20s
	private Integer ConnectionRequestTimeout = 5000;
	// 默认重连次数 3
	private Integer RequestRetry = 3;

}