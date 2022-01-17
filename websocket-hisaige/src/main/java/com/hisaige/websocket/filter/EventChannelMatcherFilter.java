package com.hisaige.websocket.filter;

import com.hisaige.websocket.support.Constants;
import com.hisaige.websocket.support.WebsocketSession;
import com.hisiage.event.ievent.EventNotification;
import com.hisiage.event.supper.notifyEvent.EventSubscription;
import com.hisiage.event.supper.notifyEvent.manager.SubscriberManager;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelMatcher;
import io.netty.util.Attribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.util.Map;

/**
 * 自定义消息发送过滤器
 * @author chenyj
 */
public class EventChannelMatcherFilter implements ChannelMatcher {

	private static final Logger logger = LoggerFactory.getLogger(EventChannelMatcherFilter.class);

	private EventNotification eventNotification;

	public EventChannelMatcherFilter(EventNotification eventNotification) {
		Assert.notNull(eventNotification, "eventNotification IS NULL");
		this.eventNotification = eventNotification;
	}

	@Override
	public boolean matches(Channel channel) {

		Attribute<WebsocketSession> sessionAttribute = channel.attr(Constants.SOCKET_SESSION);
		WebsocketSession websocketSession = sessionAttribute.get();
		if (null != websocketSession) {
			Map<Long, EventSubscription> subscriptionsMap = SubscriberManager.getEventSubscriptionMap();
			if (null != websocketSession.getSubscribeId()) {

				Long subscribeId = websocketSession.getSubscribeId();
				if(null == subscribeId){
					logger.warn("subscribeId is null, websocketSession:{}", websocketSession);
					channel.close(); //没有订阅id，关闭连接
					return false;
				}
				EventSubscription eventSubscription = subscriptionsMap.get(subscribeId);
				if (null != eventSubscription && null != eventSubscription.getEventPathSet()
						&& eventSubscription.getEventPathSet().contains(eventNotification.getEventPath())) {
					if (!channel.isWritable()) {
						// 缓冲区高于高水位，将事件插入队列中慢慢消费
						logger.warn("channel is not writable, websocketSession:{}", websocketSession);
						// eventSubscription.increFailure(); //这里不做失败计数
						eventSubscription.addEvent(eventNotification);

						if (eventSubscription.getEventSize() > SubscriberManager.getEventCapacity()) {
							logger.warn(
									"Subscription:{} has gone over the allowed queue limit with {} events,client ip:{}, user:{}, websocketSession:{}",
									eventSubscription.getSubscribeId(), eventSubscription.getEventSize(),
									eventSubscription.getSubscribeIp(), eventSubscription.getUserName(), websocketSession);
							channel.close();
						}
						return false;
					}
					return true;
				}
			}
		} else {
			logger.warn("websocketSession Is null, remove client:{}", channel);
			channel.close();
		}
		return false;
	}
}
