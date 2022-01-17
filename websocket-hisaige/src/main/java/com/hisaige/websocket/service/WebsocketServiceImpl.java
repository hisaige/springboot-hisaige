package com.hisaige.websocket.service;

import com.alibaba.fastjson.JSON;
import com.hisaige.websocket.support.Constants;
import com.hisaige.websocket.support.WebsocketSession;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroupFuture;
import io.netty.channel.group.ChannelMatcher;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.Attribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * websocket常用方法封装
 * 
 * @author chenyj
 */
public class WebsocketServiceImpl implements WebsocketService {

	private static final Logger logger = LoggerFactory.getLogger(WebsocketServiceImpl.class);

	@Override
	public void sendMessage2All(String message) {
		Constants.channelGroup.writeAndFlush(new TextWebSocketFrame(message));
	}

	@Override
	public void sendMessage2All(String message, ChannelFutureListener channelFutureListener) {
		ChannelGroupFuture writeAndFlush = Constants.channelGroup.writeAndFlush(new TextWebSocketFrame(message));
		if (null != channelFutureListener) {
			for (ChannelFuture channelFuture : writeAndFlush) {
				channelFuture.addListener(channelFutureListener);
			}
		}
	}

	@Override
	public void sendMessage2All(String message, ChannelMatcher matcher) {
		Constants.channelGroup.writeAndFlush(new TextWebSocketFrame(message), matcher, true);
	}

	@Override
	public void sendMessage2All(String message, ChannelFutureListener channelFutureListener, ChannelMatcher matcher) {

		ChannelGroupFuture channelGroupFuture = Constants.channelGroup.writeAndFlush(new TextWebSocketFrame(message),
				matcher);
		if (null != channelGroupFuture && null != channelFutureListener) {
			for (ChannelFuture channelFuture : channelGroupFuture) {
				channelFuture.addListener(channelFutureListener);
			}
		}
	}

	@Override
	public void send2One(String message, String subcribeKey) {
		ChannelHandlerContext channelHandlerContext = Constants.pushCtxMap.get(subcribeKey);
		if (null != channelHandlerContext) {
			channelHandlerContext.channel().writeAndFlush(new TextWebSocketFrame(message));
		} else {
			logger.warn("channelHandlerContext is null");
		}
	}

	@Override
	public void send2One(String message, String subcribeKey, ChannelFutureListener channelFutureListener) {
		ChannelHandlerContext channelHandlerContext = Constants.pushCtxMap.get(subcribeKey);
		if (null != channelHandlerContext) {
			ChannelFuture channelFuture = channelHandlerContext.channel()
					.writeAndFlush(new TextWebSocketFrame(message));
			channelFuture.addListener(channelFutureListener);
		} else {
			logger.warn("channelHandlerContext is null");
		}
	}

	@Override
	public void send2One(String message, String subscribeKey, ChannelMatcher matcher) {
		ChannelHandlerContext channelHandlerContext = Constants.pushCtxMap.get(subscribeKey);
		if (null != channelHandlerContext) {
			if (matcher.matches(channelHandlerContext.channel())) {
				channelHandlerContext.channel().writeAndFlush(new TextWebSocketFrame(message),
						channelHandlerContext.voidPromise());
			}
		} else {
			logger.warn("channelHandlerContext is null, subscribeKey:{}, clientContextMap:{}", subscribeKey, Constants.pushCtxMap);
		}
	}

	@Override
	public void send2One(String message, String subscribeKey, ChannelFutureListener channelFutureListener,
			ChannelMatcher matcher) {

		ChannelHandlerContext channelHandlerContext = Constants.pushCtxMap.get(subscribeKey);
		if (null != channelHandlerContext) {
			if (matcher.matches(channelHandlerContext.channel())) {
				ChannelFuture channelFuture = channelHandlerContext.channel().writeAndFlush(message);
				channelFuture.addListener(channelFutureListener);
			}
		} else {
			logger.warn("channelHandlerContext is null");
		}
	}

	@Override
	public void send2One(Object message, String subscribeKey, ChannelFutureListener channelFutureListener,
			ChannelMatcher matcher) {
		ChannelHandlerContext channelHandlerContext = Constants.pushCtxMap.get(subscribeKey);
		if (null != channelHandlerContext) {
			if (matcher.matches(channelHandlerContext.channel())) {
				ChannelFuture channelFuture = channelHandlerContext.channel()
						.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(message)));
				channelFuture.addListener(channelFutureListener);
			}
		} else {
			logger.warn("channelHandlerContext is null");
		}
	}

	@Override
	public int getClientSize() {
		return Constants.channelGroup.size() - 1;
	}

	@Override
	public List<WebsocketSession> getAllClient() {

		return Constants.pushCtxMap.values().stream().map(chc -> chc.channel().attr(Constants.SOCKET_SESSION).get()).collect(Collectors.toList());
	}

	@Override
	public List<WebsocketSession> getClientByToken(String token) {
		if (StringUtils.isEmpty(token)) {
			return new ArrayList<>();
		}
		return Constants.pushCtxMap.keySet().stream().filter(key -> key.contains(".") && token.equals(key.substring(0, key.indexOf(".")))).map(Constants.pushCtxMap::get).map(chc -> chc.channel().attr(Constants.SOCKET_SESSION).get()).collect(Collectors.toList());
	}

	@Override
	public WebsocketSession getClientBySubscribeKey(String subcribeKey) {
		ChannelHandlerContext chc = getChannelContextBySubscribeKey(subcribeKey);
		Attribute<WebsocketSession> sessionAttribute = chc.channel().attr(Constants.SOCKET_SESSION);
		return sessionAttribute.get();
	}

	@Override
	public ChannelHandlerContext getChannelContextBySubscribeKey(String subcribeKey) {
		if (StringUtils.isEmpty(subcribeKey)) {
			return null;
		}
		return Constants.pushCtxMap.get(subcribeKey);
	}

	@Override
	public List<ChannelHandlerContext> getAllChannelContextByToken(String token) {

		if (StringUtils.isEmpty(token)) {
			return new ArrayList<>();
		}
		return Constants.pushCtxMap.keySet().stream().filter(key -> key.contains(".") && token.equals(key.substring(0, key.indexOf(".")))).map(Constants.pushCtxMap::get).collect(Collectors.toList());
	}

	@Override
	public List<ChannelHandlerContext> getAllChannelContext() {

		return new ArrayList<>(Constants.pushCtxMap.values());
	}

	@Override
	public boolean isClientsContainsToken(String token) {

		if (StringUtils.isEmpty(token)) {
			return false;
		}
		Set<String> keySet = Constants.pushCtxMap.keySet();
		for (String key : keySet) {
			if (key.contains(".") && token.equals(key.substring(0, key.indexOf(".")))) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void removeClient(String subscribeKey) {
		ChannelHandlerContext channelHandlerContext = getChannelContextBySubscribeKey(subscribeKey);
		if (null != channelHandlerContext) {
			logger.info("remove client, subscribeKey:{}", subscribeKey);
			channelHandlerContext.close();
		}
	}

	@Override
	public boolean isWritable(String subscribeKey) {

		ChannelHandlerContext channelContextBySubscribeKey = getChannelContextBySubscribeKey(subscribeKey);
		if (null != channelContextBySubscribeKey) {
			return channelContextBySubscribeKey.channel().isWritable();
		}
		return false;
	}

	@Override
	public void sendMessage2All(Object message, ChannelFutureListener channelFutureListener, ChannelMatcher matcher) {

		sendMessage2All(JSON.toJSONString(message), channelFutureListener, matcher);
	}

}
