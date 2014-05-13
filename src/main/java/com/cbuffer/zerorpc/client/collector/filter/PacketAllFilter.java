package com.cbuffer.zerorpc.client.collector.filter;

import com.cbuffer.zerorpc.common.packet.RpcPacket;

/**
 * fireflyc@icloud.com
 */
public class PacketAllFilter implements PacketFilter {
    @Override
    public boolean accept(RpcPacket packet) {
        return true;
    }
}
