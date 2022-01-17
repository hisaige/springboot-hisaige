package com.hisaige.websocket.handler;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

class WebSocketServerHandler extends SimpleChannelInboundHandler<WebSocketFrame> {


	
    private final ServerHandlerEndpoint serverHandlerEndpoint;

    public WebSocketServerHandler(ServerHandlerEndpoint serverHandlerEndpoint) {
        this.serverHandlerEndpoint = serverHandlerEndpoint;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame msg) throws Exception {
    	handleWebSocketFrame(ctx, msg);
//        super.channelRead(ctx, msg); 
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        serverHandlerEndpoint.doOnError(ctx, cause);
        super.exceptionCaught(ctx, cause);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        serverHandlerEndpoint.doOnClose(ctx);
        super.channelInactive(ctx);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        serverHandlerEndpoint.doOnEvent(ctx, evt);
//        super.userEventTriggered(ctx, evt);
    }

    private void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) throws Exception {
        if (frame instanceof TextWebSocketFrame) {
        	TextWebSocketFrame textFrame = (TextWebSocketFrame) frame;
	        if ("PING".equals(textFrame.text())) {
	            ctx.writeAndFlush(new TextWebSocketFrame("PONG"));
	            return;
	        }
	        serverHandlerEndpoint.doOnMessage(ctx, frame);
	        return;
	    }
        if (frame instanceof PingWebSocketFrame) {
            ctx.writeAndFlush(new PongWebSocketFrame(frame.content().retain()));
            return;
        }
        if (frame instanceof CloseWebSocketFrame) {
            ctx.writeAndFlush(frame.retainedDuplicate()).addListener(ChannelFutureListener.CLOSE);
            return;
        }
        if (frame instanceof BinaryWebSocketFrame) {
            serverHandlerEndpoint.doOnBinary(ctx, frame);
            return;
        }
        if (frame instanceof PongWebSocketFrame) {
//        	ctx.writeAndFlush(new PingWebSocketFrame(frame.content().retain()));
        }
        ctx.flush();
    }
}