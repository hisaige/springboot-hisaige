package com.hisaige.websocket.handler;

import com.hisaige.websocket.config.properties.WebSocketProperties;
import com.hisaige.websocket.service.SysUserVEventConfigService;
import com.hisaige.websocket.support.Constants;
import com.hisaige.websocket.support.SpringContextUtils;
import com.hisaige.websocket.support.WebsocketSession;

import com.hisiage.event.supper.notifyEvent.manager.SubscriberManager;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.util.Attribute;
import io.netty.util.internal.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;

/**
 * websocket常用方法回调桥点
 * @author chenyj
 */
public class ServerHandlerEndpoint {
	
	private static final Logger logger = LoggerFactory.getLogger(ServerHandlerEndpoint.class);

	private final SocketServerHandler socketServerHandler;
	private final WebSocketProperties webSocketProperties;

	public ServerHandlerEndpoint(WebSocketProperties webSocketProperties, SocketServerHandler socketServerHandler) {
		this.socketServerHandler = socketServerHandler;
		this.webSocketProperties = webSocketProperties;
	}
	
	/**
	 * 走到这一步说明已经握手成功，websocket客户端即将准备就绪
	 * 在启动客户端连接前做一些初始化配置
	 * @param ctx 客户端连接上写文
	 * @param req 连接请求为http
	 * @param path 客户端连接url
	 * @throws Exception 这里方法如果抛出异常，会导致连接创建失败
	 */
	public void doOnOpen(ChannelHandlerContext ctx, FullHttpRequest req, String path) throws Exception {
		
		Attribute<WebsocketSession> sessionAttribute = ctx.channel().attr(Constants.SOCKET_SESSION);
		WebsocketSession websocketSession = sessionAttribute.get();
		
		//添加到客户端用户组，主要用于一对多推送
		Constants.channelGroup.add(ctx.channel());
		
		String uri = req.uri();
	    QueryStringDecoder uriDecoder = new QueryStringDecoder(uri);
	    Map<String, List<String>> parameters = uriDecoder.parameters();
	    //token与clientId不可能为空，在握手之前就已经做非空判断
		String token = parameters.get("token").get(0);
        String clientId = parameters.get("clientId").get(0);
        String subscribeKey = token + "." + clientId;
        List<String> eventTypes = parameters.get("eventTypes");
        String socketClientIp = "127.0.0.1";
        List<String> maxFailedNum = parameters.get("maxFailedNum");

		//TODO uri转成订阅list
        HttpHeaders headers = req.headers();
        
        //配置连接属性
        if(null == websocketSession) {
			websocketSession = new WebsocketSession();
		}
        websocketSession.setParameters(parameters);
    	websocketSession.setChannel(ctx.channel());
    	websocketSession.setId(ctx.channel().id().asLongText());
    	websocketSession.setPath(uriDecoder.path());
    	websocketSession.setHttpHeaders(headers);
    	websocketSession.setToken(token);
    	websocketSession.setClientId(clientId);
    	websocketSession.setLocalAddress(ctx.channel().localAddress());
    	websocketSession.setSubscribeKey(subscribeKey);
    	websocketSession.setSubscribeEvents(eventTypes);

		SysUserVEventConfigService sysUserVEventConfigService = SpringContextUtils.getFirstBeansByType(SysUserVEventConfigService.class);
    	if(null != sysUserVEventConfigService) {
			//注意 有多个bean只取第一个
			if(logger.isInfoEnabled()){
				logger.info("use sysUserVEventConfigService...");
			}
			String actionUserId = sysUserVEventConfigService.getUserIdByToken(token);
			websocketSession.setActionUserId(actionUserId);
		}
    	
    	//设置消费失败最大个数，超过指定个数，将会触发关闭客户端操作，客户端注意有断线重连功能
    	//或者在客户端指定连接参数maxFailedNum参数值设置为-1将不触发失败个数最大限制检查
    	if(!CollectionUtils.isEmpty(maxFailedNum)) {
    		websocketSession.setMaxFailedNum(Integer.valueOf(maxFailedNum.get(0)));
    	} else {
    		Integer socketMaxFailedNum = webSocketProperties.getMaxFailedNum();
    		if(null != socketMaxFailedNum) {
    			websocketSession.setMaxFailedNum(socketMaxFailedNum);
    		}
    	}
    	
    	//获取客户端IP
    	InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
        String clientIp = insocket.getAddress().getHostAddress();
        if(!StringUtil.isNullOrEmpty(clientIp) && !"0.0.0.0".equals(clientIp)) {
        	socketClientIp = clientIp;
        }
        websocketSession.setClientIp(socketClientIp);
        
        //设置客户端连接属性
        sessionAttribute.setIfAbsent(websocketSession);

		//订阅事件
		Long subscribeId = SubscriberManager.subscribe2Event(socketClientIp, token, clientId, eventTypes);

		websocketSession.setSubscribeId(subscribeId);

		//添加订阅客户端
		Constants.pushCtxMap.put(subscribeKey, ctx);

		try{
			//websocket连接成功回调
			socketServerHandler.onOpen(ctx.channel(), req);
		} catch (Exception e){
			//回调方法失败，移除订阅
			SubscriberManager.cancelSubscribe(subscribeId);

			//移除客户端
			Constants.pushCtxMap.remove(subscribeKey);
		}
	}

	public void doOnClose(ChannelHandlerContext ctx) throws Exception {
		
		Attribute<WebsocketSession> sessionAttribute = ctx.channel().attr(Constants.SOCKET_SESSION);
		try {
			socketServerHandler.onClose(ctx.channel());
		} catch (Exception e) {
			//捕获回调异常，避免回调方法里的业务处理影响客户端正常关闭
			logger.error("socketServerHandler.onClose throw exception", e);
		}
		WebsocketSession websocketSession = sessionAttribute.get();
		if (null == websocketSession) {
			return;
		}
		
		try {
			//取消订阅
			SubscriberManager.cancelSubscribe(websocketSession.getSubscribeId());
		} catch (Exception e) {
			logger.error("cancelSubscription throw exception", e);
		}
		//移除订阅客户端,解除当前clientId的订阅限制
		Constants.pushCtxMap.remove(websocketSession.getSubscribeKey());
		sessionAttribute.set(null);
	}

	public void doOnError(ChannelHandlerContext ctx, Throwable throwable) throws Exception {
		socketServerHandler.onError(ctx, throwable);
		//异常关闭也要执行一些清除缓存数据，取消订阅的操作
		doOnClose(ctx);
	}

	public void doOnMessage(ChannelHandlerContext ctx, WebSocketFrame frame) throws Exception {
		
		if(frame instanceof TextWebSocketFrame) {
			TextWebSocketFrame textFrame = (TextWebSocketFrame) frame;
			socketServerHandler.onMessage(ctx, textFrame);
		}
	}

	public void doOnBinary(ChannelHandlerContext ctx, WebSocketFrame frame) throws Exception {
		
		if(frame instanceof BinaryWebSocketFrame) {
			BinaryWebSocketFrame binaryWebSocketFrame = (BinaryWebSocketFrame) frame;
          ByteBuf content = binaryWebSocketFrame.content();
          byte[] bytes = new byte[content.readableBytes()];
			socketServerHandler.onBinary(ctx, bytes);
		}
	}

	public void doOnEvent(ChannelHandlerContext ctx, Object evt) throws Exception{
		socketServerHandler.onIdleStateEvent(ctx, evt);
	}

	public Boolean checkAuthentication(FullHttpRequest req, ChannelHandlerContext ctx) throws Exception{
		return socketServerHandler.checkAuthentication(req, ctx);
	}

}