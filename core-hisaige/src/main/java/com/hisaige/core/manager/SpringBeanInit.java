package com.hisaige.core.manager;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
@Component
public class SpringBeanInit implements BeanPostProcessor {

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		// TODO Auto-generated method stub
		Object obj = BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
//		System.out.println(beanName + "-->" + bean);
//		if (bean instanceof CacheLoader) {
//			CacheLoader cacheLoader = (CacheLoader) bean;
//		}
		return obj;
	}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		// TODO Auto-generated method stub
		Object obj = BeanPostProcessor.super.postProcessBeforeInitialization(bean, beanName);
//		System.out.println(beanName + "-->" + bean);
		return obj;
	}

	
}
