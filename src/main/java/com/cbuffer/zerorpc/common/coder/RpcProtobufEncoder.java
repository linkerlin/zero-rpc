package com.cbuffer.zerorpc.common.coder;

import com.cbuffer.zerorpc.common.packet.RpcPacket;
import com.google.protobuf.MessageLite;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * fireflyc@icloud.com
 */
public class RpcProtobufEncoder extends MessageToByteEncoder<RpcPacket> {
    private Logger logger = LoggerFactory.getLogger(RpcProtobufEncoder.class);

    protected void encode(ChannelHandlerContext ctx, RpcPacket msg, ByteBuf out) throws Exception {
        try {
            MessageLite lite = msg.getPayload();
            byte type = MessageLiteMapper.get(lite.getClass());
            if (type == 0) {
                throw new RuntimeException("Unknow MessageLite");
            }
            byte bytes[] = lite.toByteArray();

            out.ensureWritable(RpcPacket.HeadLength + bytes.length);
            byte version = msg.getVersion();

            byte versionAndType = (byte) ((version << 5) | (type & 31));
            out.writeByte(versionAndType);
            out.writeInt(bytes.length);
            out.writeBytes(bytes);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}
