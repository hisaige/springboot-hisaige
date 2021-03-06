package com.hisaige.websocket.handler;

import com.hisaige.websocket.config.properties.WebSocketProperties;
import com.hisaige.websocket.support.Constants;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static io.netty.handler.codec.http.HttpHeaderNames.SEC_WEBSOCKET_KEY;
import static io.netty.handler.codec.http.HttpHeaderNames.SEC_WEBSOCKET_VERSION;
import static io.netty.handler.codec.http.HttpHeaderNames.UPGRADE;
import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.FORBIDDEN;
import static io.netty.handler.codec.http.HttpResponseStatus.INTERNAL_SERVER_ERROR;
import static io.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpResponseStatus.UNAUTHORIZED;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 *
 * @author chenyj
 * 2019???5???24???
 *
 */
@ChannelHandler.Sharable
public class HttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

	private static final Logger logger = LoggerFactory.getLogger(HttpServerHandler.class);


	private final ServerHandlerEndpoint handlerEndpoint;
	private final WebSocketProperties webSocketProperties;

	public HttpServerHandler(ServerHandlerEndpoint handlerEndpoint, WebSocketProperties webSocketProperties) {
		this.handlerEndpoint = handlerEndpoint;
		this.webSocketProperties = webSocketProperties;
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) {
		try {
			handleHttpRequest(ctx, msg);
		} catch (Exception e) {
			FullHttpResponse res;
			res = new DefaultFullHttpResponse(HTTP_1_1, INTERNAL_SERVER_ERROR);
			sendHttpResponse(ctx, msg, res);
			logger.error(e.getMessage(), e);
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		try {
			handlerEndpoint.doOnError(ctx, cause);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		handlerEndpoint.doOnClose(ctx);
		super.channelInactive(ctx);
	}

	private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req) {

		FullHttpResponse res;
		String uri = req.uri();
		// uri = uri.split("\\?")[1];
		QueryStringDecoder uriDecoder = new QueryStringDecoder(uri);
		Map<String, List<String>> parameters = uriDecoder.parameters();
		String path = uriDecoder.path();
		HttpHeaders headers = req.headers();
		String host = headers.get(HttpHeaderNames.HOST);
		if (path.startsWith("//")) {
			path = path.substring(1);
		}
		if (!req.decoderResult().isSuccess()) {
			res = new DefaultFullHttpResponse(HTTP_1_1, BAD_REQUEST);
			sendHttpResponse(ctx, req, res);
			return;
		}
		if (req.method() != GET) {
			res = new DefaultFullHttpResponse(HTTP_1_1, FORBIDDEN);
			sendHttpResponse(ctx, req, res);
			return;
		}
		if (StringUtils.isEmpty(host)) {
			res = new DefaultFullHttpResponse(HTTP_1_1, FORBIDDEN);
			sendHttpResponse(ctx, req, res);
			return;
		}
//		if (!StringUtils.isEmpty(handlerEndpoint.getHost()) && !handlerEndpoint.getHost().equals("0.0.0.0")
//				&& !handlerEndpoint.getHost().equals(host.split(":")[0])) {
//			res = new DefaultFullHttpResponse(HTTP_1_1, FORBIDDEN);
//			sendHttpResponse(ctx, req, res);
//			return;
//		}

		if ("/favicon.ico".equals(path)) {
			res = new DefaultFullHttpResponse(HTTP_1_1, NOT_FOUND);
			sendHttpResponse(ctx, req, res);
			return;
		}

		Set<String> pathSet = webSocketProperties.getServerProperties().getEventPath();
		if (pathSet != null && pathSet.size() > 0 && !pathSet.contains(path)) {
			res = new DefaultFullHttpResponse(HTTP_1_1, NOT_FOUND);
			sendHttpResponse(ctx, req, res);
			return;
		}

		if (!req.headers().contains(UPGRADE) || !req.headers().contains(SEC_WEBSOCKET_KEY)
				|| !req.headers().contains(SEC_WEBSOCKET_VERSION)) {
			res = new DefaultFullHttpResponse(HTTP_1_1, FORBIDDEN);
			sendHttpResponse(ctx, req, res);
			return;
		}
		List<String> tokens = parameters.get("token");
		List<String> clientIds = parameters.get("clientId");
		if (tokens == null || tokens.isEmpty() || clientIds == null || clientIds.isEmpty()) {
			res = new DefaultFullHttpResponse(HTTP_1_1, BAD_REQUEST);
			sendHttpResponse(ctx, req, res);
			return;
		}
		// ???????????????token????????????
		try {
			if (!handlerEndpoint.checkAuthentication(req, ctx)) {
				logger.info("checkAuthentication failed");
				res = new DefaultFullHttpResponse(HTTP_1_1, UNAUTHORIZED);
				sendHttpResponse(ctx, req, res);
				return;
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			res = new DefaultFullHttpResponse(HTTP_1_1, FORBIDDEN);
			sendHttpResponse(ctx, req, res);
			return;
		}
		String token = tokens.get(0);
		String clientId = clientIds.get(0);
		String subscribeKey = token + "." + clientId;

		// ??????websocket, ????????????????????????subscribeKey??????????????????????????????????????????
		//?????????http????????????????????????????????????websocket????????????????????????????????? ServerHandlerEndpoint
		if (Constants.pushCtxMap.containsKey(subscribeKey)) {
			logger.error("bad request ,the clientId is already subscribe, subscribeKey :" + subscribeKey);
			res = new DefaultFullHttpResponse(HTTP_1_1, BAD_REQUEST);
			sendHttpResponse(ctx, req, res);
			return;
		}

		// TCP??????
		try{
			Channel channel = ctx.channel();
			//???????????????true-????????????handler????????????webSocketServerCompressionHandler????????????????????????????????????????????????????????????
			WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(getWebSocketLocation(req),
					null, true, webSocketProperties.getMaxFramePayloadLength());
			//??????????????????????????????ws?????????
			WebSocketServerHandshaker handshaker = wsFactory.newHandshaker(req);
			if (handshaker == null) {
				WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(channel);
			} else {
				ChannelPipeline pipeline = ctx.pipeline();
				//??????httpServerHandler?????????????????????????????????httpServerHandler????????????????????????
				pipeline.remove(ctx.name());
				if (webSocketProperties.getServerProperties().getReadIdleTime() != 0 || webSocketProperties.getServerProperties().getWriteIdleTime() != 0 || webSocketProperties.getServerProperties().getReadWriteIdleTime() != 0) {
					pipeline.addLast(new IdleStateHandler(webSocketProperties.getServerProperties().getReadIdleTime(), webSocketProperties.getServerProperties().getWriteIdleTime(),
							webSocketProperties.getServerProperties().getReadWriteIdleTime()));
				}
				if (webSocketProperties.getServerProperties().getUseCompressionHandler()) {
					// webSocket ?????????????????????????????????????????????WebSocketServerProtocolHandler?????????????????????????????????true
					pipeline.addLast("webSocketServerCompressionHandler", new WebSocketServerCompressionHandler());
					// ??????????????????????????? web socket ???????????????????????????????????????????????????maxFrameSize????????????????????????
					//WebSocketServerProtocolHandler??????????????????????????????http?????????ws???????????????????????????????????????????????????????????????????????????
//				pipeline.addLast(new WebSocketServerProtocolHandler("/enpointXxx", null, true, 10485760));
				}
				pipeline.addLast(new WebSocketServerHandler(handlerEndpoint));
				final String finalPath = path;
				handshaker.handshake(channel, req).addListener(future -> {
					if (future.isSuccess()) {
						try {
							handlerEndpoint.doOnOpen(ctx, req, finalPath);
						} catch (Exception e) {
							logger.error("handlerEndpoint.doOnOpen throw exception", e);
							handshaker.close(channel, new CloseWebSocketFrame());
						}
					} else {
						handshaker.close(channel, new CloseWebSocketFrame());
					}
				});
			}
		} finally {
			req.release();
		}
	}

	private static void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest req, FullHttpResponse res) {

		int statusCode = res.status().code();
		if (statusCode != OK.code() && res.content().readableBytes() == 0) {
			ByteBuf buf = Unpooled.copiedBuffer(res.status().toString(), CharsetUtil.UTF_8);
			res.content().writeBytes(buf);
			buf.release();
		}
		HttpUtil.setContentLength(res, res.content().readableBytes());

		// ????????????????????????????????????????????????????????????
		ChannelFuture f = ctx.channel().writeAndFlush(res);
		if (!HttpUtil.isKeepAlive(req) || statusCode != 200) {
			f.addListener(ChannelFutureListener.CLOSE);
		}
	}

	private static String getWebSocketLocation(FullHttpRequest req) {
		String location = req.headers().get(HttpHeaderNames.HOST) + req.uri();
		return "ws://" + location;
	}

}
