package com.hisaige.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 将字符串转化成Date对象
 * 也可以使用 @JSONField(serializeUsing = UtcDateTimeSerializer.class) UtcDateTimeSerializer是一个自定义序列化工具类
 * @author chenyj
 * 2019/12/8 - 23:11.
 **/
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
public @interface String2DateFormat {
    /**
     * 能识别的字符串格式，如果为空，则默认源为数字字符串， 如 longTime，转换成 new Date(longTime)
     * @return String[]
     */
    String pattern() default "";

    String timeZone() default "";
}
