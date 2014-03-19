package com.cbuffer.zerorpc.client;


import com.cbuffer.zerorpc.client.packet.collector.PacketCollector;
import com.cbuffer.zerorpc.client.packet.collector.PacketCollectorManager;
import com.cbuffer.zerorpc.client.packet.filter.PacketFilter;
import com.cbuffer.zerorpc.packet.RpcPacket;
import com.cbuffer.zerorpc.protobuf.Rpc;
import com.google.protobuf.MessageLite;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.concurrent.ThreadFactory;

/**
 * 上海卓领通讯技术有限公司
 * User: xingsen@join-cn.com
 * Date: 13-8-22
 * Time: 下午12:32
 */
public class NettyConnection {
    private final NioEventLoopGroup group;
    private final Bootstrap bootstrap;
    private final PacketCollectorManager packetCollectorManager;
    private String host;
    private int port;
    private Channel channel;

    public NettyConnection(String host, int port, ChannelHandler channelHandler, PacketCollectorManager packetCollectorManager) {
        this(host, port, channelHandler, packetCollectorManager, null);
    }

    public NettyConnection(String host, int port, ChannelHandler channelHandler, PacketCollectorManager packetCollectorManager, ThreadFactory threadFactory) {
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
                .handler(channelHandler);
        this.packetCollectorManager = packetCollectorManager;
    }

    public void connection() throws InterruptedException, IOException {
        ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
        channelFuture.addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
        channel = channelFuture.channel();
    }

    public void sendPacket(MessageLite payload) {
        channel.writeAndFlush(new RpcPacket(payload));
    }

    public MessageLite sendPacket(MessageLite payload, PacketFilter packetFilter) throws SocketTimeoutException {
        PacketCollector packetCollector = packetCollectorManager.createPacketCollector(packetFilter);
        try {
            channel.writeAndFlush(new RpcPacket(payload));
            RpcPacket result = packetCollector.nextResultOrTimeOut();
            if (result == null) {
                throw new SocketTimeoutException();
            }
            if (result.getPayload() instanceof Rpc.RpcResponse) {
                Rpc.RpcResponse response = (Rpc.RpcResponse) result.getPayload();
                if (!response.getSuccess()) {
                    throw new RuntimeException(response.getErrorMsg());
                }
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
}
