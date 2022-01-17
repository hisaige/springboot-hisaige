package com.hisaige.websocket.test;

import com.hisaige.websocket.handler.SocketServerHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

/**
 * @author chenyj
 * 2020/4/4 - 23:56.
 **/
@Controller
public class WebsocketTestController implements SocketServerHandler {

    private static final Logger logger = LoggerFactory.getLogger(WebsocketTestController.class);
    @Override
    public void onOpen(Channel channel, FullHttpRequest req) throws Exception {
        logger.info("connect success");
        channel.writeAndFlush("connection success");
    }

    @Override
    public void onClose(Channel channel) throws Exception {
        logger.info("connection close");
        channel.writeAndFlush(new TextWebSocketFrame("connection close"));
    }

    @Override
    public void onMessage(ChannelHandlerContext ctx, TextWebSocketFrame messageFrame) throws Exception {
        logger.info("onmessage:{}", messageFrame.text());
        ctx.writeAndFlush(new TextWebSocketFrame(("receive:" + messageFrame.text())));
    }

    @Override
    public void onBinary(ChannelHandlerContext ctx, byte[] bytes) throws Exception {

    }
}
