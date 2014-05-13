package com.cbuffer.zerorpc.client.collector;

import com.cbuffer.zerorpc.client.collector.filter.PacketFilter;
import com.cbuffer.zerorpc.common.packet.RpcPacket;
import io.netty.util.internal.chmv8.ConcurrentHashMapV8;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * fireflyc@icloud.com
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
