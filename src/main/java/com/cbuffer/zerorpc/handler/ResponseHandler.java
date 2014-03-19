package com.cbuffer.zerorpc.handler;

import com.cbuffer.zerorpc.client.packet.collector.PacketCollectorManager;
import com.cbuffer.zerorpc.packet.RpcPacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: xingsen
 * Date: 14-3-5
 * Time: 下午5:20
 */
public class ResponseHandler extends SimpleChannelInboundHandler<RpcPacket> {
    private final PacketCollectorManager collectorManager;
    private Logger logger = LoggerFactory.getLogger(ResponseHandler.class);

    public ResponseHandler(PacketCollectorManager collectorManager) {
        this.collectorManager = collectorManager;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcPacket packet) throws Exception {
        collectorManager.processPacket(packet);
    }
}
