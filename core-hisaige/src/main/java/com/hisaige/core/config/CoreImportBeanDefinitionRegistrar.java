package com.hisaige.core.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.stereotype.Component;

/**
 * @author chenyj
 * 2019/11/30 - 17:26.
 **/

public class CoreImportBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {

    private static final Logger logger  = LoggerFactory.getLogger(CoreImportBeanDefinitionRegistrar.class);

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        logger.info(importingClassMetadata.toString());
    }
}
