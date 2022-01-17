package com.hisaige.core.task.delay;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author chenyj
 * 2020/10/16 - 14:05.
 **/
@Slf4j
@Component
public class DelayMsgDispatcher implements ApplicationRunner {

    private static volatile boolean isStop = false;
    // 启动消费线程 消费添加到延时队列中的消息，前提是任务到了延期时间
    private final ExecutorService exec = Executors.newFixedThreadPool(1);
    // 创建延时队列
    private static final DelayQueue<DelayedMsg> queue = new DelayQueue<>();

    protected static boolean offerTask(DelayedMsg message){
        return queue.offer(message);
    }
//    public static void main(String[] args) {
//
//        // 添加延时消息,m1 延时3s
//        DelayedMsg<String> m1 = new DelayedMsg<>(1, "world", 3000);
//
//        // 添加延时消息,m2 延时10s
//        DelayedMsg<String> m2 = new DelayedMsg<>(2, "hello", 10000);
//
//        //将延时消息放到延时队列中
//        log.info("queue.offer(m2):{}", offerTask(m2));
//        log.info("queue.offer(m1):{}", offerTask(m1));
//
//
//    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        exec.execute(() -> {
            Thread.currentThread().setName("DelayMsgDispatcher-delayTask");
            while (!isStop) {
                try {
                    DelayedMsg take = queue.poll(1, TimeUnit.SECONDS);
                    if(null != take){
                        log.debug("check msg:{}", take.getMsg());
                        Thread.yield();
                    }
                } catch (InterruptedException e) {
                    log.error("job throw InterruptedException", e);
                }
            }
        });
    }

    @PreDestroy
    public void destroy(){
        isStop = true;
        exec.shutdown();
    }
}
