package com.hisiage.event.processor;

import com.hisiage.event.listener.IEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 注意 此类的启动在注解上
 * 事件配置类
 * @author chenyj
 * 2019/12/7 - 19:27.
 **/

public class EventBeanDefinitionRegistrar implements BeanFactoryAware, ImportBeanDefinitionRegistrar, ResourceLoaderAware, EnvironmentAware {

    private static final Logger logger = LoggerFactory.getLogger(EventBeanDefinitionRegistrar.class);

    private BeanFactory beanFactory;
    private Environment environment;
    private ResourceLoader resourceLoader;


    @Override
    public void registerBeanDefinitions(@NonNull AnnotationMetadata importingClassMetadata, @NonNull BeanDefinitionRegistry registry) {

//        Set<String> annotationTypes = importingClassMetadata.getAnnotationTypes();
        //1.扫描并注入事件监听器到容器中
        ClassPathEventListenerScanner scanner = new ClassPathEventListenerScanner(registry);
        scanner.setEnvironment(environment);
        scanner.setResourceLoader(resourceLoader);

        try{
            List<String> packages = AutoConfigurationPackages.get(this.beanFactory);
            if (logger.isDebugEnabled()) {
                for (String pkg : packages) {
                    logger.debug("Using auto-configuration base package '{}'", pkg);
                }
            }

            Map<String, Object> basePackage = importingClassMetadata.getAnnotationAttributes("com.hisiage.event.annotation.EnableIEvent", true);

            if(null != basePackage){
                String[] basePackages = ((String[]) basePackage.get("basePackages"));

                if(null != basePackages && basePackages.length > 0){
                    packages.addAll(Arrays.asList(basePackages));
                }
            }
//            scanner.setAnnotationClass(IListener.class);
            scanner.setMarkerInterface(IEventListener.class);
            scanner.registerFilters();
            scanner.doScan(StringUtils.toStringArray(packages));
        } catch (Exception e){
            logger.error("register IEvent listener throw Exception", e);
        }
    }


    @Override
    public void setBeanFactory(@NonNull BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public void setEnvironment(@NonNull Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setResourceLoader(@NonNull ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
}
