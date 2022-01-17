package com.hisaige.core.util;

/**
 * @author chenyj
 * 2020/10/20 - 15:12.
 **/
public class ClassUtils {
    private static <T> T getEntity(Class<T> clz, Object o){
        if(clz.isInstance(o)){
            return clz.cast(o);
        }
        return null;
    }
}
