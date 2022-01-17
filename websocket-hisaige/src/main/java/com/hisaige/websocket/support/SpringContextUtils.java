package com.hisaige.websocket.support;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Map;
import java.util.Map.Entry;

public class SpringContextUtils implements ApplicationContextAware {
    private static ApplicationContext context;

    public static Object getBean(String name) {
        return context.getBean(name);
    }

    public static <T> T getBean(String name, Class<T> clazz) {
        return context.getBean(name, clazz);
    }

    public static ApplicationContext getContext() {
        return context;
    }

    /**
     * 根据类型获取所有的bean
     * 
     * @param clazz
     *            bean类型
     * @return
     */
    public static <T> Map<String, T> getBeansByType(Class<T> clazz) {
        return context.getBeansOfType(clazz);
    }
    
    /**
     * 根据bean类型获取第一个bean 使用前应确保该类型的bean只有一个
     * @param clazz
     * @return
     */
    public static <T> T getFirstBeansByType(Class<T> clazz) {
    	Map<String, T> model = context.getBeansOfType(clazz);
		assert model.size()>0: "bean type is not exit like " + clazz.getName();
    	T t = null;
		for(Entry<String, T> entry:model.entrySet()) {
    		if(null != entry.getValue()) {
    			t = entry.getValue();
    			break;
    		}
    	}
		return t;
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
    	SpringContextUtils.context = context;
    }
}