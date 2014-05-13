package com.cbuffer.zerorpc.client.collector;


import com.cbuffer.zerorpc.common.packet.RpcPacket;

/**
 * fireflyc@icloud.com
 */
public interface PacketListener {
    public void processPacket(RpcPacket packet);
}
