package com.hisaige.core.support;

import org.springframework.core.NamedThreadLocal;
import org.springframework.lang.Nullable;

/**
 * 模仿RequestContextHolder，如果需要在子线程中使用父线程的属性，可以新增NamedInheritableThreadLocal
 * @author chenyj
 * 2020/6/29 - 22:33.
 **/
public class CustomContectHolder {
    private static final ThreadLocal<String> contextAttributesHolder =
            new NamedThreadLocal<>("context attributes");

    public static void resetContextAttributes() {
        contextAttributesHolder.remove();
    }

    public static void setContextAttributes(@Nullable String attributes) {
        if(null == attributes){
            resetContextAttributes();
        } else {
            contextAttributesHolder.set(attributes);
        }
    }
}
