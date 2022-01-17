package com.hisaige.websocket.factory;

import com.hisaige.websocket.config.properties.WebSocketProperties;
import com.hisaige.websocket.handler.HttpServerHandler;
import com.hisaige.websocket.support.Constants;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

/**
 * @author chenyj
 * 2020/4/1 - 22:34.
 **/
public class SocketFactory {

    private static final Logger logger = LoggerFactory.getLogger(SocketFactory.class);

    private final WebSocketProperties webSocketProperties;
    private final EventLoopGroup boss;
    private final EventLoopGroup worker;
    private final HttpServerHandler httpServerHandler;

    public SocketFactory(WebSocketProperties webSocketProperties, HttpServerHandler httpServerHandler){
        this.webSocketProperties = webSocketProperties;
        this.httpServerHandler = httpServerHandler;

        this.boss = new NioEventLoopGroup(webSocketProperties.getServerProperties().getBossGroupNum(), new CustomizableThreadFactory("netty-server-bossGroup-"));
        this.worker = new NioEventLoopGroup(webSocketProperties.getServerProperties().getWorkerGroupNum(), new CustomizableThreadFactory("netty-server-workerGroup-"));
    }

    @PostConstruct
    public void init(){
        System.setProperty("io.netty.eventexecutor.maxPendingTasks", String.valueOf(webSocketProperties.getMaxPendingTasks()));

        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(boss, worker).channel(NioServerSocketChannel.class)
                //连接超时配置
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, webSocketProperties.getTimeoutMillis())
                //BACKLOG用于构造服务端套接字ServerSocket对象，标识当服务器请求处理线程全满时，用于临时存放已完成三次握手的请求的队列的最大长度。如果未设置或所设置的值小于1，Java将使用默认值50
                .option(ChannelOption.SO_BACKLOG, webSocketProperties.getServerProperties().getSoBacklog())
                //WRITE_SPIN_COUNT 一个Loop写操作执行的最大次数，默认值为16。也就是说，对于大数据量的写操作至多进行16次，如果16次仍没有全部写完数据，此时会提交一个新的写任务给EventLoop，任务将在下次调度继续执行。
                //这样，其他的写请求才能被响应不会因为单个大数据量写请求而耽误。
                .childOption(ChannelOption.WRITE_SPIN_COUNT, webSocketProperties.getServerProperties().getWriteSpinCount())
                //通过 WRITE_BUFFER_WATER_MARK 设置某个连接上可以暂存的最大最小 Buffer 之后，如果该连接的等待发送的数据量大于设置的值时，则 isWritable 会返回不可写。这样，客户端可以不再发送，防止这个量不断的积压，最终可能让客户端挂掉。
                //如果发生这种情况，一般是服务端处理缓慢导致。这个值可以有效的保护客户端。此时数据并没有发送出去。
                .childOption(ChannelOption.WRITE_BUFFER_WATER_MARK,
                        new WriteBufferWaterMark(webSocketProperties.getServerProperties().getWriteBufferLowWaterMark(),
                                webSocketProperties.getServerProperties().getWriteBufferHighWaterMark()))
                .childOption(ChannelOption.TCP_NODELAY, webSocketProperties.getTcpNotDelay())
                .childOption(ChannelOption.SO_KEEPALIVE, webSocketProperties.getIsKeepalive())
                .childOption(ChannelOption.ALLOW_HALF_CLOSURE, webSocketProperties.getServerProperties().getAllowHalfClosure())
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new HttpServerCodec());
                        // 分块向客户端写数据，防止发送大文件时导致内存溢出， channel.write(new ChunkedFile(new File("bigFile.mkv")))
                        pipeline.addLast(new ChunkedWriteHandler());
                        //http数据在传输过程中是分段,HttpObjectAggregator就是可以将多个段聚合
                        //这就是为什么当浏览器发送大量数据时，就会发出多次http请求
                        pipeline.addLast(new HttpObjectAggregator(webSocketProperties.getMaxContentLength()));
//                        if(null != webSocketProperties.getLogLevel()) {//这样写好像日志不打印，还需要再验证
//                            pipeline.addLast(new LoggingHandler(webSocketProperties.getLogLevel()));
//                        }
                        pipeline.addLast(httpServerHandler);
                    }

                });
        if(null != webSocketProperties.getLogLevel()) {
            bootstrap.handler(new LoggingHandler(webSocketProperties.getLogLevel()));
        }
        if (webSocketProperties.getServerProperties().getSoRcvbuf() != -1) {
            bootstrap.childOption(ChannelOption.SO_RCVBUF, webSocketProperties.getServerProperties().getSoRcvbuf());
        }
        if (webSocketProperties.getServerProperties().getSoSndbuf() != -1) {
            bootstrap.childOption(ChannelOption.SO_SNDBUF, webSocketProperties.getServerProperties().getSoSndbuf());
        }
        if(webSocketProperties.getServerProperties().getSoLinger() != -1) {
            bootstrap.childOption(ChannelOption.SO_LINGER, webSocketProperties.getServerProperties().getSoLinger());
        }

        String host = webSocketProperties.getHost();
        int port = webSocketProperties.getServerProperties().getPort();
        ChannelFuture channelFuture = null;
        if ("0.0.0.0".equals(host)) {
            channelFuture = bootstrap.bind(port);
        } else {
            try {
                channelFuture = bootstrap.bind(new InetSocketAddress(InetAddress.getByName(host), port));
            } catch (UnknownHostException e) {
                logger.error(e.getMessage(), e);
            }
        }
        if(null == channelFuture) {
            return;
        }

        channelFuture.addListener(future -> {
            if (!future.isSuccess()) {
                logger.error("bootstrap.bind failed, cause:", future.cause());
            }
        });

        Constants.channelGroup.add(channelFuture.channel());


        //destroy方法 调用 shutdownGracefully 即可
//        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
//            Thread.currentThread().setName("netty-group-ShutdownHook");
//            boss.shutdownGracefully().syncUninterruptibly();
//            worker.shutdownGracefully().syncUninterruptibly();
//        }));
    }

    // 关闭websocket服务
    @PreDestroy
    public void destroy() {
        //关闭所有客户端连接
        Constants.channelGroup.close();
        Constants.pushCtxMap.clear();

        boss.shutdownGracefully();
        worker.shutdownGracefully();
    }
}
