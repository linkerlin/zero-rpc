package com.cbuffer.zerorpc.client;

import com.cbuffer.zerorpc.RpcInitializer;
import com.cbuffer.zerorpc.client.packet.collector.PacketCollectorManager;
import com.cbuffer.zerorpc.handler.ResponseHandler;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

/**
 * User: xingsen
 * Date: 14-3-5
 * Time: 下午3:31
 */
public class RpcClientInitializer extends RpcInitializer<SocketChannel> {
    private final PacketCollectorManager collectorManager;

    public RpcClientInitializer(PacketCollectorManager collectorManager) {
        this.collectorManager = collectorManager;
    }

    @Override
    protected void initChannel0(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();

        pipeline.addLast("handlerResponse", new ResponseHandler(collectorManager));
    }
}
