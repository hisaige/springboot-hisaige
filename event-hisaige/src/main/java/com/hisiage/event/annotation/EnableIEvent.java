package com.hisiage.event.annotation;


import com.hisiage.event.config.IEventConfiguration;
import com.hisiage.event.config.IEventLauncherConfiguration;
import com.hisiage.event.factory.EventRegister;
import com.hisiage.event.processor.EventBeanDefinitionRegistrar;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 事件开关，用在Spring启动类或bean配置类中
 * @author chenyj
 * 2019/12/7 - 16:44.
 **/
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@EnableAutoConfiguration
@Import({IEventConfiguration.class, EventBeanDefinitionRegistrar.class, EventRegister.class, IEventLauncherConfiguration.class})
public @interface EnableIEvent {

    /**
     * 定义事件的基础包
     * @return Stringp[]
     */
    @AliasFor("basePackages")
    String[] value() default {};

    /**
     * 定义事件的基础包
     * @return Stringp[]
     */
    @AliasFor("value")
    String[] basePackages() default {};
}
