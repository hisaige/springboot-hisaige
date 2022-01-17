package com.hisiage.event.annotation;

import org.springframework.core.annotation.AliasFor;

/**
 * 事件监听器指定包扫描，如果不指定则默认扫描项目下所有类
 * 主要用来限事件listener的包范围
 * 用在Spring启动类或bean配置类中
 * @author chenyj
 * 2019/12/7 - 17:13.
 * @deprecated 暂时用不到注解扫描，目前仅根据listener接口扫描
 **/
public @interface IEventScanner {

    /**
     * 针对指定包名扫描事件监听器， 同 basePackages
     * @return String[]
     */
    @AliasFor("basePackages")
    String[] value() default {};


    @AliasFor("value")
    String[] basePackages() default {};
}
