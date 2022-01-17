package com.hisaige.websocket;

import com.hisaige.websocket.annotation.EnableWebsocketServer;
import com.hisiage.event.annotation.EnableIEvent;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author chenyj
 * 2020/4/1 - 21:56.
 **/
@SpringBootApplication
@EnableWebsocketServer
//@EnableIEvent
public class SocketApplication {
    public static void main(String[] args) {
        SpringApplication.run(SocketApplication.class, args);
    }
}
