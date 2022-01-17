package com.hisaige.websocket.service;

import com.hisaige.websocket.support.WebsocketSession;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelMatcher;

import java.util.List;

/**
 * 提供一些webscket常用方法 目前消息发送仅支持文本消息
 * @author chenyj
 */
public interface WebsocketService {

	/**
	 * 给所有客户端发送消息
	 * 
	 * @param message 消息
	 */
	void sendMessage2All(String message);
	
	/**
	 * 给所有客户端发消息 消息发送成功后，每一个客户端消息发送成功都会通知ChannelFutureListener监听者
	 * 可以封装一个类实现ChannelMatcher的方法传入，具体步骤可以参考ChannelMatcherFilter
	 * 
	 * @param message               事件消息
	 * @param matcher               判断当前消息是否允许发送 ，接口方法返回true则允许
	 */
	void sendMessage2All(String message, ChannelMatcher matcher);

	/**
	 * 给所有客户端发送消息，消息发送成功后，每一个客户端消息发送成功都会通知ChannelFutureListener监听者
	 * 可以封装一个类实现ChannelFutureListener的方法传入，具体步骤可以参考MessagePubFutureListener
	 * 
	 * @param message 消息
	 * @param channelFutureListener 回调监听者
	 */
	void sendMessage2All(String message, ChannelFutureListener channelFutureListener);

	/**
	 * 给所有客户端发消息 消息发送成功后，每一个客户端消息发送成功都会通知ChannelFutureListener监听者
	 * 可以封装一个类实现ChannelFutureListener的方法传入，具体步骤可以参考MessagePubFutureListener
	 * 可以封装一个类实现ChannelMatcher的方法传入，具体步骤可以参考ChannelMatcherFilter
	 * 
	 * @param message               事件消息
	 * @param channelFutureListener 方法回调
	 * @param matcher               判断当前消息是否允许发送 ，接口方法返回true则允许
	 */
	void sendMessage2All(String message, ChannelFutureListener channelFutureListener, ChannelMatcher matcher);

	/**
	 * 将对象转化成字符串并推送给客户端 内部会调用 JSON.toJSONString(message) 将对象转化成JSON字符串
	 * 
	 * @param message               最终会转化成字符串发出去的对象
	 * @param channelFutureListener 方法回调
	 * @param matcher               判断当前消息是否允许发送 ，接口方法返回true则允许
	 */
	void sendMessage2All(Object message, ChannelFutureListener channelFutureListener, ChannelMatcher matcher);

	/**
	 * 给某个客户端发送消息
	 * 
	 * @param message  消息
	 * @param subscribeKey 客户端订阅信息,字符串组合 token.clientId 或sessionId.clientId
	 */
	void send2One(String message, String subscribeKey);

	/**
	 * 消息发送成功后，每一个客户端消息发送成功都会通知ChannelFutureListener监听者
	 * 可以封装一个类实现ChannelFutureListener的方法传入，具体步骤可以参考MessagePubFutureListener
	 * 
	 * @param message               消息
	 * @param subscribeKey           客户端订阅信息,字符串组合 token.clientId
	 * @param channelFutureListener 消息成功后回调
	 */
	void send2One(String message, String subscribeKey, ChannelFutureListener channelFutureListener);

	/**
	 * 消息发送成功后，每一个客户端消息发送成功都会通知ChannelFutureListener监听者
	 * 将对象转化成字符串并推送给客户端 内部会调用 JSON.toJSONString(message) 将对象转化成JSON字符串
	 * 
	 * @param message               字符串消息
	 * @param subscribeKey           客户端订阅信息,字符串组合 token.clientId
	 * @param matcher               判断当前消息是否允许发送 ，接口方法返回true则允许
	 */
	void send2One(String message, String subscribeKey, ChannelMatcher matcher);
	
	/**
	 * 消息发送成功后，每一个客户端消息发送成功都会通知ChannelFutureListener监听者
	 * 可以封装一个类实现ChannelFutureListener的方法传入，具体步骤可以参考MessagePubFutureListener
	 * 将对象转化成字符串并推送给客户端 内部会调用 JSON.toJSONString(message) 将对象转化成JSON字符串
	 * 
	 * @param message               字符串消息
	 * @param subscribeKey           客户端订阅信息,字符串组合 token.clientId
	 * @param channelFutureListener 消息成功后回调
	 * @param matcher               判断当前消息是否允许发送 ，接口方法返回true则允许
	 */
	void send2One(String message, String subscribeKey, ChannelFutureListener channelFutureListener,
                         ChannelMatcher matcher);

	/**
	 * 消息发送成功后，每一个客户端消息发送成功都会通知ChannelFutureListener监听者
	 * 可以封装一个类实现ChannelFutureListener的方法传入，具体步骤可以参考MessagePubFutureListener
	 * 将对象转化成字符串并推送给客户端 内部会调用 JSON.toJSONString(message) 将对象转化成JSON字符串
	 *
	 * @param message               消息，内部会调用 JSON.toJSONString(message) 将对象转化成JSON字符串
	 * @param subscribeKey           客户端订阅信息,字符串组合 token.clientId
	 * @param channelFutureListener 消息成功后回调
	 * @param matcher               判断当前消息是否允许发送 ，接口方法返回true则允许
	 */
	void send2One(Object message, String subscribeKey, ChannelFutureListener channelFutureListener,
                         ChannelMatcher matcher);

	/**
	 * 获取连接客户端数量
	 * 
	 * @return 连接客户端数量
	 */
	int getClientSize();

	/**
	 * 获取所有websocket客户端的连接信息
	 * 
	 * @return List<WebsocketSession>
	 */
	List<WebsocketSession> getAllClient();

	/**
	 * 根据websocket关联的token获取客户端
	 * 
	 * @param token 客户端连接websocket服务时url中指定的token
	 * @return List<WebsocketSession>
	 */
	List<WebsocketSession> getClientByToken(String token);

	/**
	 * 根据订阅信息获取某个客户端信息
	 * 
	 * @param subscribeKey 客户端订阅信息,字符串组合 token.clientId
	 * @return WebsocketSession
	 */
	WebsocketSession getClientBySubscribeKey(String subscribeKey);

	/**
	 * 判断缓冲区是否高于高水位
	 * 
	 * @param subscribeKey 客户端订阅信息,字符串组合 token.clientId
	 * @return boolean
	 */
	boolean isWritable(String subscribeKey);

	/**
	 * 
	 * @param subscribeKey 客户端订阅信息,字符串组合 token.clientId
	 * @return ChannelHandlerContext
	 */
	ChannelHandlerContext getChannelContextBySubscribeKey(String subscribeKey);

	/**
	 * 根据websocket关联的token获取客户端上下文信息
	 * 
	 * @param token 客户端连接websocket服务时url中指定的token
	 * @return List<ChannelHandlerContext>
	 */
	List<ChannelHandlerContext> getAllChannelContextByToken(String token);

	/**
	 * 获取所有 socket客户端的上下文对象
	 * 
	 * @return List<ChannelHandlerContext>
	 */
	List<ChannelHandlerContext> getAllChannelContext();

	/**
	 * 判断客户端中是否包含当前token的订阅
	 * 
	 * @param token 客户端连接websocket服务时url中指定的token
	 * @return true if contains
	 */
	boolean isClientsContainsToken(String token);

	/**
	 * 移除某个客户端
	 * 
	 * @param subscribeKey 客户端订阅信息,字符串组合 token.clientId
	 */
	void removeClient(String subscribeKey);

	/*
	 * 希望 根据用户ID一级事件path获取事件配置
	 *
	 * @param userId    用户id
	 * @param eventPath 事件path
	 * @return 用户配置
	 */
}
