package com.hisaige.websocket.annotation;

import com.hisaige.websocket.config.WebsocketServerConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 启动websocket相关装配
 * 本注解不会激活事件模块的自动装配，因此事件相关功能将无法使用
 * @author chenyj
 * 2020/6/16
 **/
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@EnableAutoConfiguration
@EnableAsync
@Import({WebsocketServerConfiguration.class})
public @interface EnableWebsocket {
}
