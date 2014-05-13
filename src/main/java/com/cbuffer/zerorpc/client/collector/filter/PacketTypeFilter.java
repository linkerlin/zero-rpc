package com.cbuffer.zerorpc.client.collector.filter;

import com.cbuffer.zerorpc.common.packet.RpcPacket;
import com.google.protobuf.MessageLite;

import java.util.ArrayList;
import java.util.List;

/**
 * fireflyc@icloud.com
 */
public class PacketTypeFilter implements PacketFilter {
    private final List<Class<? extends MessageLite>> typesClass = new ArrayList<Class<? extends MessageLite>>();

    public PacketTypeFilter(Class<? extends MessageLite>... types) {
        for (Class<? extends MessageLite> type : types) {
            typesClass.add(type);
        }
    }

    @Override
    public boolean accept(RpcPacket packet) {
        for (Class<? extends MessageLite> clazz : typesClass) {
            if (packet.getPayload().getClass().isAssignableFrom(clazz)) {
                return true;
            }
        }
        return false;
    }
}
