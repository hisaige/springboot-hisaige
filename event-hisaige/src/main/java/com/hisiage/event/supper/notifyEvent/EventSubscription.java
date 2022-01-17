package com.hisiage.event.supper.notifyEvent;

import com.hisaige.core.util.CollectionUtils;
import com.hisiage.event.ievent.EventNotification;
import com.hisiage.event.supper.EventPathMatcher;
import com.hisiage.event.supper.notifyEvent.manager.SubscriberManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author chenyj
 * 2020/4/4 - 18:17.
 **/
public class EventSubscription {

    private static final Logger logger = LoggerFactory.getLogger(EventSubscription.class);

    private static final TimeUnit timeUnit = TimeUnit.MILLISECONDS;
    private long lastAccessTime = System.currentTimeMillis(); //最后访问时间

    private int subscribeId; //订阅ID
    private String clientId;

    //用户信息
    private String sessionId;
    private String userName;
    private String subscribeIp; //订阅IP

    private Iterator<String> eventPathPrefixSet;
    private Set<String> eventPathSet;

    public EventSubscription(String subscribeIp, String sessionId, String clientId, Iterable<String> eventPathPrefixSet) throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        this.subscribeIp = subscribeIp;
        this.sessionId = sessionId;
        this.clientId = clientId;
        if(null != eventPathPrefixSet){
            this.eventPathPrefixSet = eventPathPrefixSet.iterator();
        }
        EventPathMatcher eventPathMatcher = new EventPathMatcher(this.eventPathPrefixSet);
        this.eventPathSet = eventPathMatcher.getMatcherEventPath();
    }

    private EventLinkedBlockingDeque eventLinkedBlockingDeque = new EventLinkedBlockingDeque(SubscriberManager.getEventCapacity());

    public boolean addEvent(EventNotification eventNotification) {
        return eventLinkedBlockingDeque.addEvent(eventNotification);
    }

    public boolean addEvent(List<EventNotification> eventNotifications) {
        return eventLinkedBlockingDeque.addEvents(eventNotifications);
    }

    public EventNotification takeEvent() throws InterruptedException {
        lastAccessTime = System.currentTimeMillis();
        return eventLinkedBlockingDeque.takeEvent();
    }

    public boolean removeEvent(EventNotification eventNotification){
        lastAccessTime = System.currentTimeMillis();
        return eventLinkedBlockingDeque.remove(eventNotification);
    }

    /**
     * 获取单个事件
     * @param timeout 超时时间
     * @return EventNotification
     * @throws InterruptedException 阻塞打断异常
     */
    public EventNotification pollEvent(long timeout) throws InterruptedException {
        lastAccessTime = System.currentTimeMillis();
        return eventLinkedBlockingDeque.pollEvent(timeout);
    }

    /**
     * 根据参数批量获取事件
     * 这是一个阻塞方法
     * @param eventNum 每次获取事件的最大个数
     * @param timeout 获取超时时间 单位 ms
     * @return List<EventNotification>
     * @throws InterruptedException 阻塞异常
     */
    public List<EventNotification> pollEvents(int eventNum, long timeout) throws InterruptedException {
        lastAccessTime = System.currentTimeMillis();
        return eventLinkedBlockingDeque.pollEvents(eventNum, timeout);
    }

    /**
     * 更新最后访问时间
     */
    public void updateLastAccessTime() {
        lastAccessTime = System.currentTimeMillis();
    }

    /**
     * 内部私有类 避免开放出去用错
     * 事件消息队列
     *
     * @author chenyj
     * 2020/4/4 - 18:59.
     **/
    private class EventLinkedBlockingDeque extends LinkedBlockingDeque<EventNotification> {


        EventLinkedBlockingDeque(int capital){
            super(capital);
        }

        private final ReentrantLock lock = new ReentrantLock();

        private final Condition notEmpty = lock.newCondition(); //非空条件 注意 仅仅在调用当前类的方法有效，如果调用父类方法，则会扰乱锁


        boolean addEvent(EventNotification eventNotification) {
            lock.lock();
            try {
                if (offerLast(eventNotification)) {
                    notEmpty.signal();
                    return true;
                }
                return false;
            } finally {
                lock.unlock();
            }
        }

        /**
         * 批量添加事件
         *
         * @param eventNotifications 批量事件
         * @return 如果全部添加成功 返回true 否则返回false
         */
        boolean addEvents(Collection<EventNotification> eventNotifications) {
            boolean fullAdd = true;
            if (!CollectionUtils.isEmpty(eventNotifications)) {
                lock.lock();
                try {
                    for (EventNotification eventNotification : eventNotifications) {
                        if (!offerLast(eventNotification)) {
                            fullAdd = false;
                            break;
                        }
                    }
                    notEmpty.signal();
                } finally {
                    lock.unlock();
                }
            }
            return fullAdd;
        }

        /**
         * 获取一个事件
         * 这是一个阻塞方法
         *
         * @return EventNotification
         * @throws InterruptedException 打断阻塞
         */
        EventNotification takeEvent() throws InterruptedException {
            return takeFirst();
        }

        /**
         * 获取一个事件
         * 这是一个根据参数阻塞方法
         *
         * @param timeout 阻塞超时
         * @return EventNotification
         * @throws InterruptedException 打断阻塞
         */
        EventNotification pollEvent(long timeout) throws InterruptedException {
            return poll(timeout, timeUnit);
        }

        /**
         * 根据参数批量获取事件
         * 这是一个阻塞方法
         * @param eventNum 每次获取事件的最大个数
         * @param timeout 获取超时时间 单位 ms
         * @return List<EventNotification>
         * @throws InterruptedException 阻塞异常
         */
        List<EventNotification> pollEvents(int eventNum, long timeout) throws InterruptedException {
            long nanos = timeUnit.toNanos(timeout);
            final ReentrantLock lock = this.lock;
            lock.lockInterruptibly();
            try {
                List<EventNotification> eventNotifications = new ArrayList<>();
                while (eventNotifications.size() == 0) {
                    drainTo(eventNotifications, eventNum);
                    if (nanos <= 0)
                        return eventNotifications;
                    nanos = notEmpty.awaitNanos(nanos);
                }
                return eventNotifications;
            } finally {
                lock.unlock();
            }
        }
    }

    public int getSubscribeId() {
        return subscribeId;
    }

    public void setSubscribeId(int subscribeId) {
        this.subscribeId = subscribeId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getSubscribeIp() {
        return subscribeIp;
    }

    public void setSubscribeIp(String subscribeIp) {
        this.subscribeIp = subscribeIp;
    }

    public long getLastAccessTime() {
        return lastAccessTime;
    }

    public int getEventSize(){
        return eventLinkedBlockingDeque.size();
    }

    public Iterator<String> getEventPathPrefixSet() {
        return eventPathPrefixSet;
    }

    public void setEventPathPrefixSet(Iterator<String> eventPathPrefixSet) {
        this.eventPathPrefixSet = eventPathPrefixSet;
    }

    public Set<String> getEventPathSet() {
        return eventPathSet;
    }

    public void setEventPathSet(Set<String> eventPathSet) {
        this.eventPathSet = eventPathSet;
    }

    public EventLinkedBlockingDeque getEventLinkedBlockingDeque() {
        return eventLinkedBlockingDeque;
    }

    public void setEventLinkedBlockingDeque(EventLinkedBlockingDeque eventLinkedBlockingDeque) {
        this.eventLinkedBlockingDeque = eventLinkedBlockingDeque;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    //    public static void main(String[] args) {
//        EventSubscription strings = new EventSubscription();
//        for (int i = 0; i < 5; i++) {
//            new Thread(() -> {
//                while (true) {
//                    List<EventNotification> strings1 = new ArrayList<>();
//                    try {
//                        strings1 = strings.pollEvents(5, 7000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    logger.info(Thread.currentThread().getName() + " " + strings1);
//                    strings1.clear();
////                try {
////                    TimeUnit.SECONDS.sleep(5);
////                } catch (InterruptedException e) {
////                    e.printStackTrace();
////                }
//                }
//            }, "获取线程-" + i).start();
//        }
//
//        AtomicInteger atomicInteger = new AtomicInteger();
//        for (int j = 0; j < 5; j++) {
//            new Thread(() -> {
//                while (true) {
//                    int andIncrement = atomicInteger.getAndIncrement();
//                    ArrayList<EventNotification> objects = new ArrayList<>();
//                    objects.add(new EventNotification(andIncrement));
//                    objects.add(new EventNotification(-andIncrement));
//                    strings.addEvents(objects);
//                    try {
//                        int i = (int) (Math.random() * 1);
//                        logger.info(Thread.currentThread().getName() + " 停止 " + i + "s , 添加 " + andIncrement);
//                        TimeUnit.SECONDS.sleep(i);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }, "添加线程" + j).start();
//        }
//    }
}
