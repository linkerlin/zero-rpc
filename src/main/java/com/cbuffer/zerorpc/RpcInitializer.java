package com.cbuffer.zerorpc;

import com.cbuffer.zerorpc.coder.RpcProtobufDecoder;
import com.cbuffer.zerorpc.coder.RpcProtobufEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LoggingHandler;

/**
 * User: xingsen
 * Date: 14-3-5
 * Time: 下午5:24
 */
public abstract class RpcInitializer<T extends SocketChannel> extends ChannelInitializer<T> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast("logging", new LoggingHandler());
        //pipeline.addLast("zlibDecode", new JZlibDecoder());
        //pipeline.addLast("zlibEncoder", new JZlibEncoder());
        pipeline.addLast("decoder", new RpcProtobufDecoder());
        pipeline.addLast("encoder", new RpcProtobufEncoder());
        initChannel0(ch);
    }

    protected abstract void initChannel0(SocketChannel ch);
}
