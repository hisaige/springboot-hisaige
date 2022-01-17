package com.hisaige.core.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

/**
 * @author chenyj
 * 2019/11/30 - 17:07.
 **/
@Component
@Slf4j
public class AopPostProcess implements BeanPostProcessor {

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
//        log.info(beanName + "======");
        return bean;
    }
}
