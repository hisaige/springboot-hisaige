package com.hisiage.event.factory;

import com.hisiage.event.ievent.AbstractEvent;
import com.hisiage.event.ievent.AbstractNotifyEvent;
import com.hisiage.event.listener.IEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author chenyj
 * 2020/1/15 - 19:30.
 **/
public class EventRegister {

    private static final Logger logger = LoggerFactory.getLogger(EventRegister.class);

    //存放事件监听器，key为事件类型
    private static final Map<String, List<IEventListener>> registerMap = new ConcurrentHashMap<>();


    /**
     * 注册事件，将事件类型与监听器关联起来
     *
     * @param iEventListener 监听器
     */
    public void register(IEventListener iEventListener) {

        Assert.notNull(iEventListener, "iEventListener is null");

        String eventType = iEventListener.getEventName();
        if (null == eventType) {
            throw new NullPointerException("get eventName throw NullPointerException");
        }
        if (registerMap.containsKey(eventType)) {
            registerMap.get(eventType).add(iEventListener);
        } else {
            List<IEventListener> iEventListeners = new ArrayList<>();
            iEventListeners.add(iEventListener);
            registerMap.put(eventType, iEventListeners);
        }
    }

    @Nullable
    private List<IEventListener> getListener(String eventType) {
        return registerMap.get(eventType);
    }

    @SuppressWarnings("unchecked")
    public void process(AbstractEvent abstractEvent) {
        if (null != abstractEvent) {
            List<IEventListener> listener = getListener(abstractEvent.getClass().getName());
            if (!CollectionUtils.isEmpty(listener)) {
                listener.forEach(action -> {
                    try {
                        action.process(abstractEvent);
                    } catch (Exception e) {
                        logger.error("process ievent throw Exception, event:{}, listener:{}", action.getEventName(), action.getListenerName());
                    }
                });
            }
        }
    }

    /**
     * 获取事件类型，这个方法主要用在事件订阅时，根据订阅前缀订阅具体事件使用
     *
     * @return List<String>
     */
    public static List<String> getAllEventTypes() {
        return new ArrayList<>(registerMap.keySet());
    }

    public static List<String> getNotifyEventTypes() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        Set<String> eventClassSet = registerMap.keySet();
        ArrayList<String> retList = new ArrayList<>();
        for (String eventClass : eventClassSet) {
            Class<?> aClass = Class.forName(eventClass);
            Object o = aClass.newInstance();
            if (o instanceof AbstractNotifyEvent) {
                retList.add(((AbstractNotifyEvent) o).getEventPath());
            }
        }
        return retList;
    }


    @PreDestroy
    public void destroy() {
        unRegister();
    }

    /**
     * 清空事件注册缓存
     */
    private void unRegister() {
        registerMap.values().forEach(List::clear);
        registerMap.clear();
    }

}
