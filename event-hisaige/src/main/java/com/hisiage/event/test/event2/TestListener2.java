package com.hisiage.event.test.event2;

import com.alibaba.fastjson.JSON;
import com.hisiage.event.listener.BaseEventHandler;
import com.hisiage.event.test.TestEvent2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author chenyj
 * 2020/1/15 - 16:36.
 **/
public class TestListener2 extends BaseEventHandler<TestEvent2> {

    private static final Logger logger = LoggerFactory.getLogger(TestListener2.class);
    @Override
    public void process(TestEvent2 event) {
        logger.info("receive event:{}", JSON.toJSONString(event));
    }
}
