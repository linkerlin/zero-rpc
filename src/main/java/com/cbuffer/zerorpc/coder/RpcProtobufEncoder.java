package com.cbuffer.zerorpc.coder;

import com.cbuffer.zerorpc.packet.RpcPacket;
import com.google.protobuf.MessageLite;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * User: xingsen
 * Date: 14-3-6
 * Time: 上午10:21
 */
public class RpcProtobufEncoder extends MessageToByteEncoder<RpcPacket> {
    @Override
    protected void encode(ChannelHandlerContext ctx, RpcPacket msg, ByteBuf out) throws Exception {
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
        out.writeMedium(bytes.length);
        out.writeBytes(bytes);
    }
}
