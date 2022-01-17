package com.hisaige.websocket.config;

import com.hisaige.websocket.config.properties.WebSocketProperties;
import com.hisaige.websocket.factory.SocketFactory;
import com.hisaige.websocket.handler.HttpServerHandler;
import com.hisaige.websocket.handler.ServerHandlerEndpoint;
import com.hisaige.websocket.handler.SocketServerHandler;
import com.hisaige.websocket.service.WebsocketService;
import com.hisaige.websocket.service.WebsocketServiceImpl;
import com.hisaige.websocket.support.SocketEventLauncherImpl;
import com.hisaige.websocket.support.SpringContextUtils;
import com.hisiage.event.launcher.NotifyEventLauncher;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author chenyj
 * 2020/4/1 - 22:10.
 **/
@EnableConfigurationProperties({WebSocketProperties.class})
@ConditionalOnBean(SocketServerHandler.class)
public class WebsocketServerConfiguration {

    private WebSocketProperties webSocketProperties;
    private SocketServerHandler socketServerHandler;

    @Bean
    public SpringContextUtils springContextUtils(){
        return new SpringContextUtils();
    }

    public WebsocketServerConfiguration(WebSocketProperties webSocketProperties, SocketServerHandler socketServerHandler){
        this.webSocketProperties = webSocketProperties;
        this.socketServerHandler = socketServerHandler;
    }

    @Bean
    public HttpServerHandler httpServerHandler(){
        return new HttpServerHandler(serverHandlerEndpoint(), webSocketProperties);
    }

    @Bean
    public ServerHandlerEndpoint serverHandlerEndpoint(){
        return new ServerHandlerEndpoint(webSocketProperties, socketServerHandler);
    }

    @Bean
    public SocketFactory SocketFactory(){
        return new SocketFactory(webSocketProperties, httpServerHandler());
    }

    @Bean
    public WebsocketService websocketService(){
        return new WebsocketServiceImpl();
    }

    @Bean
    public NotifyEventLauncher socketEventLauncher(){
        return new SocketEventLauncherImpl(websocketService(), taskExecutor());
    }

    @Bean("eventConsumerExecutor")
    public ThreadPoolTaskExecutor taskExecutor() {

        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        // 设置核心线程数
        threadPoolTaskExecutor.setCorePoolSize(4);
        // 设置最大线程数
        threadPoolTaskExecutor.setMaxPoolSize(12);
        // 设置队列容量
        threadPoolTaskExecutor.setQueueCapacity(1);
        // 设置线程活跃时间（秒）
        threadPoolTaskExecutor.setKeepAliveSeconds(60);
        // 设置默认线程名称
        threadPoolTaskExecutor.setThreadNamePrefix("event-publish-thread-");
        /*
         * 核心线程超时会关闭,这里默认设置成false,因为核心线程只有两条
         * 如果 调用了 setCoreThreadSize 方法处理大任务， 处理完毕后应该重置线程池
         */
        threadPoolTaskExecutor.setAllowCoreThreadTimeOut(false);
        // 设置拒绝策略--重试添加当前的任务，他会自动重复调用execute()方法
        threadPoolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务结束后再关闭线程池
        //executor.setWaitForTasksToCompleteOnShutdown(true);
        //执行初始化
        threadPoolTaskExecutor.initialize();
        return threadPoolTaskExecutor;
    }
}
