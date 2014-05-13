package com.cbuffer.zerorpc.common;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.handler.logging.LoggingHandler;

/**
 * fireflyc@icloud.com
 */
@ChannelHandler.Sharable
public class SimpleLoggingHandler extends LoggingHandler {
    @Override
    protected String formatByteBuf(String eventName, ByteBuf buf) {
        int length = buf.readableBytes();
        int simpleLen = 128;
        if (length > simpleLen) {
            byte simpleBytes[] = new byte[simpleLen];
            buf.getBytes(0, simpleBytes, 0, simpleLen);
            ByteBuf simpleBuf = Unpooled.wrappedBuffer(simpleBytes);
            return super.formatByteBuf(eventName, simpleBuf);
        }
        return super.formatByteBuf(eventName, buf);
    }
}
