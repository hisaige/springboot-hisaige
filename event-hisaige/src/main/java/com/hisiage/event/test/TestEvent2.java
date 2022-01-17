package com.hisiage.event.test;

import com.hisiage.event.ievent.AbstractNotifyEvent;

/**
 * @author chenyj
 * 2020/1/15 - 16:37.
 **/
public class TestEvent2 extends AbstractNotifyEvent<String> {
    @Override
    public String getEventPath() {
        return "event.test.no2";
    }
}
