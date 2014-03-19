package com.cbuffer.zerorpc.packet;

import com.google.protobuf.MessageLite;

/**
 * User: xingsen
 */
public class RpcPacket {
    public static final byte Version = 1;
    /**
     * #----------------------------------------------------------------------------
     * # version:3 type:5        |       payload length(N)    |     data.........
     * #-------1 Byte(8-byte)------------3 Byte(24-byte)-----------N Byte-----------
     * <p/>
     * version = 0-7
     * type = 0-31
     */
    public static final byte HeadLength = 4; //version+msgtype(1)+length(3)

    protected byte version;
    protected MessageLite payload;

    public RpcPacket(byte version, MessageLite payload) {
        this.version = version;
        this.payload = payload;
    }

    public RpcPacket(MessageLite payload) {
        this(Version, payload);
    }

    public byte getVersion() {
        return version;
    }

    public MessageLite getPayload() {
        return payload;
    }
}
