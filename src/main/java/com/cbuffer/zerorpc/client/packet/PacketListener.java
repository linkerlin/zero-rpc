package com.cbuffer.zerorpc.client.packet;

import com.cbuffer.zerorpc.packet.RpcPacket;

/**
 * 上海卓领通讯技术有限公司
 * User: xingsen@join-cn.com
 * Date: 13-8-22
 * Time: 下午10:11
 */
public interface PacketListener {
    public void processPacket(RpcPacket packet);
}
