package com.hisaige.websocket.handler;

import com.hisaige.websocket.support.Constants;
import com.hisaige.websocket.support.WebsocketSession;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * * 服务端websocket回调接口
 * @author chenyj
 */
public interface SocketServerHandler {

	Logger logger = LoggerFactory.getLogger(SocketServerHandler.class);

	/**
	 * 客户端socket连接上服务端后回调
	 * 不建议在onOpen方法做权限认证，用户信息赋值等操作处理，
	 * 如果需要用户信息添加到websocketSession，请在鉴权方法做处理
	 * @param channel
	 * @param req
	 * @throws IOException
	 */
	void onOpen(Channel channel, FullHttpRequest req) throws Exception;

	/**
	 * 关闭连接时回调
	 * 
	 * @param channel
	 * @return
	 * @throws IOException
	 */
	void onClose(Channel channel) throws Exception;

	/**
	 * 出现异常时回调
	 * 
	 * @param ctx
	 * @param throwable
	 * @throws IOException
	 */
	default void onError(ChannelHandlerContext ctx, Throwable throwable) throws Exception {

		logger.warn("websocket OnError channel id is ctx.channel().id()" + ctx.channel().id(), throwable);
//		ctx.disconnect(promise);
//		ctx.disconnect();
//		onClose(ctx.channel());
//		if (!promise.isDone()) {
//			promise.setFailure(throwable);
//		}
	}
	
	/**
	 * 接收消息回调
	 * 
	 * @param ctx
	 * @param messageFrame
	 * @throws IOException
	 */
	void onMessage(ChannelHandlerContext ctx, TextWebSocketFrame messageFrame) throws Exception;

	/**
	 * 接收消息回调
	 * 
	 * @param ctx
	 * @param bytes
	 * @throws IOException
	 */
	void onBinary(ChannelHandlerContext ctx, byte[] bytes) throws Exception;

	/**
	 * 处理连接超时事件回调
	 * 
	 * @param ctx
	 * @param evt
	 */
	default void onIdleStateEvent(ChannelHandlerContext ctx, Object evt) {

		if (evt instanceof IdleStateEvent) {
			Channel channel = ctx.channel();
			WebsocketSession websocketSession = channel.attr(Constants.SOCKET_SESSION).get();
			String id  = websocketSession.getSubscribeKey();
			IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
			switch (idleStateEvent.state()) {
			// 客户端收到心跳后应有所返回，在特定时间内没有收到客户端的消息，则说明客户端**
			case READER_IDLE: 
				if(logger.isDebugEnabled()) {
					//如果客户端不向服务端发送消息，则会触发刷屏，如果通过了Nginx代理，可以通过Nginx触发客户端心跳
					logger.debug("read idle, channel id is {}", id); 
				}
				break;
			case WRITER_IDLE:
				if(logger.isDebugEnabled()) {
					logger.debug("write idle, channel id is {}", id);
				}
				//给客户端发送一个ping消息当做心跳
				channel.writeAndFlush(Constants.pingFrame);
				break;
			case ALL_IDLE: //如果不在配置文件配置时间，这个事件不会触发
				if(logger.isDebugEnabled()) {
					logger.debug("readWrite idle, channel id is {}", id);
				}
				break;
			default:
				break;
			}
		}
	}

	/**
	 * 接收到ping消息，返回pong消息
	 * 
	 * @param channel
	 * @param pingFrame
	 */
	default void onPing(Channel channel, PingWebSocketFrame pingFrame) throws IOException {
		logger.info("WebSocket server received ping, channel id is {}", channel.id());
		channel.writeAndFlush(Constants.pongFrame);
	}

	/**
	 * 接收到pong消息
	 * 
	 * @param channel
	 * @param pongFrame
	 */
	default void onPong(Channel channel, PongWebSocketFrame pongFrame) throws IOException {
		logger.info("WebSocket  received pong, channel id is {}", channel.id());
	}

	/**
	 * 重写方法实现鉴权功能，默认创建连接不鉴权
	 * TCP握手之前回调，做握手前的权限认证
	 * @param ctx socket上下文对象
	 * @param request 创建连接请求方式为http
	 * @return true 则说明认证成功 允许TCP握手
	 */
	default boolean checkAuthentication(FullHttpRequest request, ChannelHandlerContext ctx) {

		/**
		 * //下面代码仅作为一个示例参考，根据websocket连接的token属性，去判断系统中是否存在这个token或sessionId
		String uri = request.uri();
		// uri = uri.split("\\?")[1];
		QueryStringDecoder uriDecoder = new QueryStringDecoder(uri);
		Map<String, List<String>> parameters = uriDecoder.parameters();
		List<String> tokens = parameters.get("token");
		if(!CollectionUtils.isEmpty(tokens)){
			//或者 userDetailsInfo = tokenService.getUserDetailsInfo(tokens.get(0));
			SessionInformation sessionInformation = sessionRegistry.getSessionInformation(tokens.get(0));
			Object principal = sessionInformation.getPrincipal();
			if (principal instanceof UserDetails) {
				UserDetailsInfo userInfo = (UserDetailsInfo) principal;
				Attribute<WebsocketSession> sessionAttribute = ctx.channel().attr(ChannelConstant.SOCKET_SESSION);
				WebsocketSession websocketSession = sessionAttribute.get();
				if(null == websocketSession) {
					websocketSession = new WebsocketSession();
				}
				websocketSession.setUserId(userInfo.getUserId());
				websocketSession.setUserName(userInfo.getUsername());
				return true;
			}
		}
		return false;
		 */
		return true;
	}

}
