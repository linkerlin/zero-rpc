package com.cbuffer.zerorpc.client.packet.collector;

import com.cbuffer.zerorpc.client.packet.PacketListener;
import com.cbuffer.zerorpc.client.packet.filter.PacketFilter;
import com.cbuffer.zerorpc.packet.RpcPacket;
import io.netty.util.internal.chmv8.ConcurrentHashMapV8;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 上海卓领通讯技术有限公司
 * User: xingsen@join-cn.com
 * Date: 13-8-22
 * Time: 下午3:29
 */
public class PacketCollectorManager {
    private Map<String, PacketCollector> packetCollectorMap = new ConcurrentHashMapV8<String, PacketCollector>();
    private List<ListenerWrapper> listenerList = new CopyOnWriteArrayList<ListenerWrapper>();

    public PacketCollector createPacketCollector(PacketFilter packetFilter) {
        PacketCollector packetCollector = new PacketCollector(packetFilter);
        packetCollectorMap.put(packetCollector.id(), packetCollector);
        return packetCollector;
    }


    public void addListener(PacketListener listener, PacketFilter packetFilter) {
        listenerList.add(new ListenerWrapper(listener, packetFilter));
    }

    public void remove(PacketCollector packetCollector) {
        packetCollectorMap.remove(packetCollector.id());
    }

    public void processPacket(RpcPacket packet) {
        for (Map.Entry<String, PacketCollector> entry : packetCollectorMap.entrySet()) {
            entry.getValue().processPacket(packet);
        }
    }

    public void notifyListener(RpcPacket packet) {
        for (ListenerWrapper listenerWrapper : listenerList) {
            listenerWrapper.notifyListener(packet);
        }
    }

    protected static class ListenerWrapper {

        private PacketListener packetListener;
        private PacketFilter packetFilter;

        public ListenerWrapper(PacketListener packetListener, PacketFilter packetFilter) {
            this.packetListener = packetListener;
            this.packetFilter = packetFilter;
        }

        public void notifyListener(RpcPacket packet) {
            if (packetFilter == null || packetFilter.accept(packet)) {
                packetListener.processPacket(packet);
            }
        }
    }
}
