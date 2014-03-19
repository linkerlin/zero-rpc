package com.cbuffer.zerorpc.client.packet.filter;

import com.cbuffer.zerorpc.packet.RpcPacket;
import com.google.protobuf.MessageLite;

import java.util.ArrayList;
import java.util.List;

/**
 * 上海卓领通讯技术有限公司
 * User: xingsen@join-cn.com
 * Date: 13-8-22
 * Time: 下午10:21
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
