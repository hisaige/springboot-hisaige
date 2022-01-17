package com.hisaige.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 如果添加在类上，那么类中的所有方法都会被增强
 * 如果添加在方法上，那么当前方法被增强
 * @author chenyj
 * @date 2019年5月10日
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Enhance {
	String value() default "";
}
