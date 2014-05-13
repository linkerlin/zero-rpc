package com.cbuffer.zerorpc.client;

import com.cbuffer.zerorpc.client.collector.PacketCollectorManager;
import com.cbuffer.zerorpc.common.packet.RpcPacket;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * fireflyc@icloud.com
 */
@ChannelHandler.Sharable
public class ClientHandler extends SimpleChannelInboundHandler<RpcPacket> {
    private Logger logger = LoggerFactory.getLogger(ClientHandler.class);
    private final PacketCollectorManager collectorManager;

    ClientHandler(PacketCollectorManager collectorManager) {
        this.collectorManager = collectorManager;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcPacket packet) throws Exception {
        collectorManager.processPacket(packet);
        collectorManager.notifyListener(packet);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        logger.error(cause.getMessage(), cause);
    }
}
