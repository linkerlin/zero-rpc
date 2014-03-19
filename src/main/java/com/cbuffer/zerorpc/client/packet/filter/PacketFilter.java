package com.cbuffer.zerorpc.client.packet.filter;


import com.cbuffer.zerorpc.packet.RpcPacket;

/**
 * 上海卓领通讯技术有限公司
 * User: xingsen@join-cn.com
 * Date: 13-8-22
 * Time: 下午3:45
 */
public interface PacketFilter {
    public boolean accept(RpcPacket packet);
}
