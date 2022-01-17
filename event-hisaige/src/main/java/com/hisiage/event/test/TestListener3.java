package com.hisiage.event.test;

import com.alibaba.fastjson.JSON;
import com.hisiage.event.listener.BaseEventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author chenyj
 * 2020/1/15 - 16:36.
 **/
public class TestListener3 extends BaseEventHandler<TestEvent> {

    private static final Logger logger = LoggerFactory.getLogger(TestListener3.class);
    @Override
    public void process(TestEvent event) {
        logger.info("receive event:{}", JSON.toJSONString(event));
    }
}
