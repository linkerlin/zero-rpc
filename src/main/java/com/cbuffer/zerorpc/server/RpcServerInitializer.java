package com.cbuffer.zerorpc.server;

import com.cbuffer.zerorpc.RpcInitializer;
import com.cbuffer.zerorpc.handler.RequestHandler;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

/**
 * User: xingsen
 * Date: 14-3-5
 * Time: 下午2:56
 */
public class RpcServerInitializer extends RpcInitializer<SocketChannel> {
    private RequestHandler handler;

    public RpcServerInitializer(RequestHandler handler) {
        this.handler = handler;
    }

    @Override
    protected void initChannel0(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast("handlerRequest", handler);
    }
}
