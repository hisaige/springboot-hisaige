package com.hisiage.event.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 监听器注解，用来标记一个事件监听者
 * 本注解运行时起作用，只能标记在 类和接口 上
 * @author chenyj
 * 2019/12/7 - 19:44.
 * @deprecated 暂时用不到这个注解 只要实现监听者接口即可
 **/
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface IListener {
}
