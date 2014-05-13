package com.cbuffer.zerorpc.server;

import com.cbuffer.zerorpc.common.SimpleLoggingHandler;
import com.cbuffer.zerorpc.common.coder.RpcProtobufDecoder;
import com.cbuffer.zerorpc.common.coder.RpcProtobufEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * fireflyc@icloud.com
 */
public class NettyServer {
    private final NettyServerConfig serverConfig;
    private Logger logger = LoggerFactory.getLogger(NettyServer.class);

    private ChannelFuture serverFuture;
    private ServerBootstrap serverBootstrap;
    private final EventLoopGroup bossGroup;
    private final EventLoopGroup workerGroup;
    private DefaultEventExecutorGroup eventExecutorGroup;

    public NettyServer(NettyServerConfig config) {
        this.serverConfig = config;
        this.bossGroup = new NioEventLoopGroup();
        this.workerGroup = new NioEventLoopGroup();
    }

    public void run() throws InterruptedException {
        this.eventExecutorGroup = new DefaultEventExecutorGroup(//
                this.serverConfig.getExecuteThread(), //
                new ThreadFactory() {
                    private AtomicInteger threadIndex = new AtomicInteger(0);

                    @Override
                    public Thread newThread(Runnable r) {
                        return new Thread(r, "ExecuteThread_" + this.threadIndex.incrementAndGet());
                    }
                });
        this.serverBootstrap = new ServerBootstrap();
        this.serverBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 512)
                .option(ChannelOption.TCP_NODELAY, true)
                .localAddress(new InetSocketAddress(this.serverConfig.getListenPort()))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast("logging", new SimpleLoggingHandler());
                        pipeline.addLast("decoder", new RpcProtobufDecoder());
                        pipeline.addLast("encoder", new RpcProtobufEncoder());
                        pipeline.addLast(eventExecutorGroup, "handlerRequest", NettyServer.this.serverConfig.getHandler());
                    }
                });
        serverFuture = this.serverBootstrap.bind().sync();
        logger.debug("start server {} success", serverFuture.channel().localAddress());

    }

    public void shutdown() {
        try {
            this.bossGroup.shutdownGracefully();
            this.workerGroup.shutdownGracefully();
            if (this.eventExecutorGroup != null) {
                this.eventExecutorGroup.shutdownGracefully();
            }
        } catch (Exception e) {
            logger.error("Netty Server Shutdown", e);
        }
    }
}
