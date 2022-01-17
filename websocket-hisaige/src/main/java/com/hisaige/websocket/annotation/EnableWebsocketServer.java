package com.hisaige.websocket.annotation;

import com.hisiage.event.annotation.EnableIEvent;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 启动websocket自动装配、事件模块自动装配
 * @author chenyj
 * 2020/4/1 - 22:06.
 **/
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@EnableAutoConfiguration
@EnableWebsocket
@EnableIEvent
public @interface EnableWebsocketServer {
}
