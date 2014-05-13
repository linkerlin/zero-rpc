package com.cbuffer.zerorpc.client.collector.filter;


import com.cbuffer.zerorpc.common.packet.RpcPacket;

/**
 * fireflyc@icloud.com
 */
public interface PacketFilter {
    public boolean accept(RpcPacket packet);
}
