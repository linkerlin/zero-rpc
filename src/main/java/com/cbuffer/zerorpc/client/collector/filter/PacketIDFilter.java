package com.cbuffer.zerorpc.client.collector.filter;


import com.cbuffer.zerorpc.common.packet.RpcPacket;
import com.cbuffer.zerorpc.common.protobuf.Rpc;

/**
 * fireflyc@icloud.com
 */
public class PacketIDFilter implements PacketFilter {
    private long requestId;

    public PacketIDFilter(long requestId) {
        if (requestId == 0) {
            throw new IllegalArgumentException("Packet ID cannot be null.");
        }
        this.requestId = requestId;
    }

    public boolean accept(RpcPacket packet) {
        if (packet.getPayload() instanceof Rpc.RpcResponse) {
            Rpc.RpcResponse response = (Rpc.RpcResponse) packet.getPayload();
            if (response.getRequestId() == requestId) {
                return true;
            }
        }
        return false;
    }

    public String toString() {
        return "PacketIDFilter by id: " + requestId;
    }

    public long getRequestId() {
        return requestId;
    }
}
