package com.cbuffer.zerorpc.client.packet.collector;

import com.cbuffer.zerorpc.client.packet.filter.PacketFilter;
import com.cbuffer.zerorpc.packet.RpcPacket;

import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * 上海卓领通讯技术有限公司
 * User: xingsen@join-cn.com
 * Date: 13-8-22
 * Time: 下午3:09
 */
public class PacketCollector {
    private String packetCollectorId = UUID.randomUUID().toString();

    private PacketFilter packetFilter;
    private ArrayBlockingQueue<RpcPacket> resultQueue;

    public PacketCollector(PacketFilter packetFilter, int maxSize) {
        this.packetFilter = packetFilter;
        this.resultQueue = new ArrayBlockingQueue<RpcPacket>(maxSize);
    }

    public PacketCollector(PacketFilter packetFilter) {
        this(packetFilter, 1024);
    }

    public RpcPacket nextResultOrTimeOut() throws InterruptedException {
        return nextResultOrTimeOut(8000);
    }

    public RpcPacket nextResultOrTimeOut(long timeout) throws InterruptedException {
        return resultQueue.poll(timeout, TimeUnit.MILLISECONDS);
    }

    public String id() {
        return packetCollectorId;
    }

    public void processPacket(RpcPacket packet) {
        if (packet == null) {
            return;
        }
        if (packetFilter.accept(packet)) {
            while (!resultQueue.offer(packet)) {
                // Since we know the queue is full, this poll should never actually block.
                resultQueue.poll();
            }
        }
    }
}
