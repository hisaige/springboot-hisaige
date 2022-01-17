package com.hisaige.core.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author chenyj
 * 2019/11/30 - 17:07.
 **/
@Configuration
public class AopConfiguration {

    @Bean
    @ConditionalOnMissingBean(name="aopPostProcess")
    public AopPostProcess aopPostProcess(){
        return new AopPostProcess();
    }
}
