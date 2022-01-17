package com.hisiage.event.processor;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

/**
 * 事件初始化处理
 * @author chenyj
 * 2019/12/7 - 16:49.
 * @deprecated 目前不要这个
 **/
public class EventListenerProcessor implements BeanFactoryPostProcessor {

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
//        System.out.println(beanFactory);

    }
}
