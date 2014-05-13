package com.cbuffer.zerorpc.common.coder;

import com.cbuffer.zerorpc.common.packet.RpcPacket;
import com.google.protobuf.MessageLite;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * fireflyc@icloud.com
 */
public class RpcProtobufDecoder extends ByteToMessageDecoder {
    private Logger logger = LoggerFactory.getLogger(RpcProtobufDecoder.class);

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> out) throws Exception {
        byteBuf.markReaderIndex();
        try {
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
            int length = byteBuf.readInt(); //2147483647 2g
            if (byteBuf.readableBytes() < length) {
                byteBuf.resetReaderIndex();
                return;
            }
            byte payloadBuf[] = new byte[length];
            byteBuf.readBytes(payloadBuf);
            MessageLite payload = messageLite.getParserForType().parseFrom(payloadBuf);
            out.add(new RpcPacket(version, payload));
        } catch (Exception e) {
            byteBuf.resetReaderIndex();
            logger.error(e.getMessage(), e);
        }
    }

}
