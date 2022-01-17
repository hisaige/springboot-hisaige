package com.hisiage.event.config;

import com.hisiage.event.launcher.NotifyEventLauncher;
import com.hisiage.event.supper.notifyEvent.NotifyEventLauncherImpl;
import org.checkerframework.framework.qual.PostconditionAnnotation;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;

/**
 * @author chenyj
 * 2020/4/5 - 15:03.
 **/
public class IEventLauncherConfiguration {

    //添加默认的通知事件发射器
    @Bean
    @ConditionalOnMissingBean(NotifyEventLauncher.class)
    public NotifyEventLauncher notifyEventLauncher(){
        return new NotifyEventLauncherImpl();
    }
}
