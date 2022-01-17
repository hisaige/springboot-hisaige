package com.hisaige.websocket.support;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.ImmediateEventExecutor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author chenyj
 * 2020/4/1 - 23:25.
 **/
public interface Constants {

    //存放所有的ChannelHandlerContext的订阅映射，将用户订阅信息和channel关联起来，实现点对点通信
    Map<String, ChannelHandlerContext> pushCtxMap = new ConcurrentHashMap<>() ;

    //保存所有用户信息 可以根据需要自己设定群组，实现一对多通信
    ChannelGroup channelGroup = new DefaultChannelGroup(ImmediateEventExecutor.INSTANCE);

    //超时配置
    AttributeKey<Integer> CHANNEL_CONNECT_IDLE = AttributeKey.valueOf("channel.tomout");

    //连接者信息
    AttributeKey<WebsocketSession> SOCKET_SESSION = AttributeKey.valueOf("socket.session");

    PingWebSocketFrame pingFrame = new PingWebSocketFrame();

    PongWebSocketFrame pongFrame = new PongWebSocketFrame();

}
