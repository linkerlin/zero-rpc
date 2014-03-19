package com.cbuffer.zerorpc.server;

import com.cbuffer.zerorpc.handler.RequestHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: xingsen
 * Date: 14-3-5
 * Time: 下午2:50
 */
public class NettyServer {
    private Logger logger = LoggerFactory.getLogger(NettyServer.class);

    private final int port;
    private final RequestHandler handler;

    public NettyServer(int port, RequestHandler handler) {
        this.port = port;
        this.handler = handler;
    }

    public void run() throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 100)
                    .childHandler(new RpcServerInitializer(handler));
            ChannelFuture f = b.bind(port).sync();
            logger.debug("启动服务器 {} 成功", f.channel().localAddress());

            f.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
