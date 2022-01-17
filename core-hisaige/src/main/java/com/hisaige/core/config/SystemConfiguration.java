package com.hisaige.core.config;


import com.hisaige.core.config.properties.SystemProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author chenyj
 * 2019/9/21 - 15:33.
 **/
@Configuration
@EnableConfigurationProperties({SystemProperties.class})
public class SystemConfiguration {

}
