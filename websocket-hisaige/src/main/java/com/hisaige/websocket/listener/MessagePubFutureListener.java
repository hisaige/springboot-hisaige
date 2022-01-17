package com.hisaige.websocket.listener;

import com.hisaige.websocket.support.Constants;
import com.hisaige.websocket.support.WebsocketSession;
import com.hisiage.event.ievent.EventNotification;
import com.hisiage.event.supper.notifyEvent.EventSubscription;
import com.hisiage.event.supper.notifyEvent.manager.SubscriberManager;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.util.Attribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * 发送事件成功后，从消息队列中移除事件，并更新订阅对象最后访问时间
 *  事件消息发送成功监听,用于从消息队列里获取事件后消费
 * 使用时，需要用构造器进行实例化传入websocketService方法
 * @author chenyj
 */
public class MessagePubFutureListener implements ChannelFutureListener {

	private static final Logger logger = LoggerFactory.getLogger(MessagePubFutureListener.class);
	
	private EventNotification event;
	
	public MessagePubFutureListener(EventNotification event) {
		this.event = event;
	}
	
	@Override
	public void operationComplete(ChannelFuture future) {
		// TODO websocket事件推送成功后回调
		Map<Long, EventSubscription> subscriptionsMap = SubscriberManager.getEventSubscriptionMap();
		Attribute<WebsocketSession> sessionAttribute = future.channel().attr(Constants.SOCKET_SESSION);
		WebsocketSession websocketSession = sessionAttribute.get();
		if(null != websocketSession) {
			//订阅id
			Long subscribeID = websocketSession.getSubscribeId();
			
			//根据订阅id移除对应的队列中的一条消息
			//订阅事件
			if(null != subscribeID) {
				EventSubscription eventSubscription = subscriptionsMap.get(subscribeID);
//
//				if(null != eventSubscription){
//					//甭管成功或失败都要移除
//					eventSubscription.removeEvent(event);
//				}

				if(null == eventSubscription || !future.isSuccess()) {
					logger.info("pub Event Failed, websocketSession:{}, event:{}", websocketSession, event);
//					eventSubscription.increFailure(); //todo 可以考虑做失败计数
				}
			}
		}
	}
}
