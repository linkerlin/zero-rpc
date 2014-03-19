package com.cbuffer.zerorpc.handler;

import com.cbuffer.zerorpc.packet.RpcPacket;
import com.cbuffer.zerorpc.protobuf.Rpc;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: xingsen
 * Date: 14-3-5
 * Time: 下午5:17
 */
public abstract class RequestHandler extends SimpleChannelInboundHandler<RpcPacket> {
    private Logger logger = LoggerFactory.getLogger(RequestHandler.class);

    protected void channelRead0(ChannelHandlerContext ctx, RpcPacket msg) throws Exception {
        if (msg.getPayload() instanceof Rpc.RpcRequest) {
            logger.debug("rpc调用");
            Rpc.RpcRequest request = (Rpc.RpcRequest) msg.getPayload();
            ctx.writeAndFlush(new RpcPacket(this.onCall(request)));
        }
        if (msg.getPayload() instanceof Rpc.Login) {
            logger.debug("登录验证");
            Rpc.Login login = (Rpc.Login) msg.getPayload();
            ctx.writeAndFlush(new RpcPacket(this.onLogin(login)));
        }
    }

    protected abstract Rpc.LoginResponse onLogin(Rpc.Login payload);

    protected abstract Rpc.RpcResponse onCall(Rpc.RpcRequest payload);
}
