package com.cbuffer.zerorpc.coder;

import com.cbuffer.zerorpc.packet.RpcPacket;
import com.google.protobuf.MessageLite;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.CorruptedFrameException;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

/**
 * User: xingsen
 * Date: 14-3-5
 * Time: 下午9:58
 */
public class RpcProtobufDecoder extends MessageToMessageDecoder<ByteBuf> {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> out) throws Exception {
        byteBuf.markReaderIndex();
        if (byteBuf.readableBytes() < RpcPacket.HeadLength) {
            byteBuf.resetReaderIndex();
            return;
        }
        byte versionAndType = byteBuf.readByte();
        byte version = (byte) (versionAndType >> 5);
        byte type = (byte) (versionAndType & 31);

        if (version != RpcPacket.Version) {
            byteBuf.resetReaderIndex();
            throw new CorruptedFrameException("Don't Support Version");
        }
        MessageLite messageLite = MessageLiteMapper.get(type);
        if (messageLite == null) {
            byteBuf.resetReaderIndex();
            throw new CorruptedFrameException("Unknow Message Type");

        }
        //读取body
        int length = byteBuf.readUnsignedMedium(); //16bit 16M flow control
        if (byteBuf.readableBytes() < length) {
            byteBuf.resetReaderIndex();
            return;
        }
        byte payloadBuf[] = new byte[length];
        byteBuf.readBytes(payloadBuf);
        MessageLite payload = messageLite.getParserForType().parseFrom(payloadBuf);
        out.add(new RpcPacket(version, payload));
    }
}
