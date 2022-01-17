package com.hisiage.event.test;

import com.hisiage.event.serevice.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * 关闭这里的测试方法
 * 测试方法挪移到test模块 详见 package com.hisaige.test.moduleEvent;
 * @author chenyj
 * 2020/3/19 - 19:38.
 **/
//@Component
public class OnStart implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private EventService eventService;

//    @PostConstruct
//    public void init(){
//        //下面这种发送事件的方式不行，因为容器还没有初始化完成，监听者还未到位
//        TestEvent testEvent = new TestEvent();
//        testEvent.setMsg("adsadadsdasd");
//        testEvent.setSessionId("sessionId");
//        eventService.pubEvent(testEvent);
//    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
//        TestEvent testEvent = new TestEvent();
//        testEvent.setMsg("adsadadsdasd");
//        testEvent.setSessionId("sessionId");
//        eventService.pubEvent(testEvent); //不触发事件
//
//        TestEvent2 testEvent2 = new TestEvent2();
//        testEvent.setMsg("22222222222222");
//        testEvent.setSessionId("22222222222222222");
////        eventService.pubEvent(testEvent2);
    }
}
