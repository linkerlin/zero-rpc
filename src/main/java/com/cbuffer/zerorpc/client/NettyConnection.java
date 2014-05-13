package com.cbuffer.zerorpc.client;


import com.cbuffer.zerorpc.client.collector.PacketCollector;
import com.cbuffer.zerorpc.client.collector.PacketCollectorManager;
import com.cbuffer.zerorpc.client.collector.PacketListener;
import com.cbuffer.zerorpc.client.collector.filter.PacketAllFilter;
import com.cbuffer.zerorpc.client.collector.filter.PacketFilter;
import com.cbuffer.zerorpc.common.SimpleLoggingHandler;
import com.cbuffer.zerorpc.common.coder.RpcProtobufDecoder;
import com.cbuffer.zerorpc.common.coder.RpcProtobufEncoder;
import com.cbuffer.zerorpc.common.packet.RpcPacket;
import com.google.protobuf.MessageLite;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeoutException;

/**
 * fireflyc@icloud.com
 */
public class NettyConnection {
    private Logger logger = LoggerFactory.getLogger(NettyConnection.class);

    private final NioEventLoopGroup group;
    private final Bootstrap bootstrap;
    private final PacketCollectorManager packetCollectorManager;
    private String host;
    private int port;
    private Channel channel;

    public NettyConnection(String host, int port) {
        this(host, port, new PacketCollectorManager(), null);
    }

    public NettyConnection(String host, int port,
                           final PacketCollectorManager packetCollectorManager, ThreadFactory threadFactory) {
        this.host = host;
        this.port = port;
        if (threadFactory == null) {
            group = new NioEventLoopGroup(1);
        } else {
            group = new NioEventLoopGroup(1, threadFactory);
        }
        bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast("logging", new SimpleLoggingHandler());
                        pipeline.addLast("decoder", new RpcProtobufDecoder());
                        pipeline.addLast("encoder", new RpcProtobufEncoder());
                        pipeline.addLast("handler", new ClientHandler(packetCollectorManager));
                    }
                });
        this.packetCollectorManager = packetCollectorManager;
    }

    public ChannelFuture connection() throws InterruptedException, IOException {
        ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
        channelFuture.addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
        channel = channelFuture.channel();
        return channelFuture;
    }

    public void sendPacket(MessageLite payload) {
        channel.writeAndFlush(new RpcPacket(payload));
    }

    public MessageLite sendPacket(MessageLite payload, PacketFilter packetFilter)
            throws TimeoutException {
        PacketCollector packetCollector = packetCollectorManager.createPacketCollector(packetFilter);
        try {
            channel.writeAndFlush(new RpcPacket(payload));
            RpcPacket result = packetCollector.nextResultOrTimeOut();
            if (result == null) {
                throw new TimeoutException();
            }
            return result.getPayload();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            packetCollectorManager.remove(packetCollector);
        }
    }


    public void close() {
        if (channel != null) {
            channel.close();
        }
        if (group != null) {
            group.shutdownGracefully();
        }
    }

    public boolean isOpen() {
        if (channel == null) {
            return false;
        }
        return channel.isOpen();
    }

    public PacketCollectorManager getPacketCollectorManager() {
        return packetCollectorManager;
    }

    public void addListener(PacketListener listener) {
        packetCollectorManager.addListener(listener, new PacketAllFilter());
    }

    public Channel getChannel() {
        return channel;
    }
}
