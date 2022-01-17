package com.hisaige.core.initialization;

import com.hisaige.core.manager.SystemManager;
import com.hisaige.core.util.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * ContextRefreshedEvent 事件是在程序初始化完毕后才发出的 而不是单个bean初始化完后才发
 * 注意 先判断  event.getApplicationContext().getParent();为null再进行初始化 ，否则当存在父子容器时可能会初始化两次
 * @author chenyj
 * 2019/11/20 - 11:53.
 **/
public class Initialization implements ApplicationListener<ContextRefreshedEvent> {

    private static Logger logger = LoggerFactory.getLogger(Initialization.class);

    @Autowired(required = false)
    private List<SystemManager> systemManagers;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        ApplicationContext parent = event.getApplicationContext().getParent();
        if(null == parent){
            //不存在父容器时才会进行初始化操作
            if(!CollectionUtils.isEmpty(systemManagers)){
                try{
                    systemManagers.forEach(systemManager -> {
                        try {
                            systemManager.init();
                            logger.debug("{} init success.", systemManager.getClass().getSimpleName());
                        } catch (Exception e) {
                            logger.error("init SystemManager throw exception, beanClassName: " + systemManager.getClass().getSimpleName(), e);
                        }
                    });
                }catch (Exception e){
                    logger.error("init SystemManager throw exception", e);
                }
            }
        }
    }
}
