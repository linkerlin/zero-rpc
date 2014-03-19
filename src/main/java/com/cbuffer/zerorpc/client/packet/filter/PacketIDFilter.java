package com.cbuffer.zerorpc.client.packet.filter;

import com.cbuffer.zerorpc.packet.RpcPacket;
import com.cbuffer.zerorpc.protobuf.Rpc;

/**
 * 上海卓领通讯技术有限公司
 * User: xingsen@join-cn.com
 * Date: 13-8-22
 * Time: 下午3:34
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
