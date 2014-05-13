package com.cbuffer.zerorpc.server;

import com.cbuffer.zerorpc.common.packet.RpcPacket;
import com.cbuffer.zerorpc.common.protobuf.Rpc;
import com.google.protobuf.MessageLite;
import io.netty.channel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketAddress;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * fireflyc@icloud.com
 */
@ChannelHandler.Sharable
public class ServerHandler extends SimpleChannelInboundHandler<RpcPacket> {
    private Logger logger = LoggerFactory.getLogger(ServerHandler.class);

    protected RemoteExecutorService rpcService;
    protected ConcurrentHashMap<String, Channel> clientChannelMap = new ConcurrentHashMap<String, Channel>();
    protected final ChannelFutureListener remover = new ChannelFutureListener() {
        @Override
        public void operationComplete(ChannelFuture future) throws Exception {
            remove(future.channel());
        }
    };

    public ServerHandler(RemoteExecutorService rpcService) {
        this.rpcService = rpcService;
    }

    protected String parseChannelRemoteAddr(final Channel channel) {
        if (null == channel) {
            return "";
        }
        final SocketAddress remote = channel.remoteAddress();
        final String addr = remote != null ? remote.toString() : "";
        if (addr.length() > 0) {
            int index = addr.lastIndexOf("/");
            if (index >= 0) {
                return addr.substring(index + 1).split(":")[0];
            }

            return addr.split(":")[0];
        }

        return "";
    }

    protected boolean remove(Channel channel) {
        String addr = parseChannelRemoteAddr(channel);
        Channel r = clientChannelMap.remove(addr);
        if (r != null) {
            return true;
        }
        channel.closeFuture().removeListener(remover);
        return true;
    }

    public Set<String> clients() {
        return Collections.unmodifiableSet(new HashSet<String>(clientChannelMap.keySet()));
    }

    public boolean sendClient(MessageLite messageLite, String addr) {
        Channel channel = clientChannelMap.get(addr);
        if (channel == null) {
            return false;
        }
        channel.writeAndFlush(messageLite);
        return true;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        channel.closeFuture().addListener(remover);
        String addr = parseChannelRemoteAddr(channel);
        logger.debug("{}", addr);
        clientChannelMap.put(addr, channel);
    }

    protected void channelRead0(ChannelHandlerContext ctx, RpcPacket msg) throws Exception {
        if (msg.getPayload() instanceof Rpc.RpcRequest) {
            logger.debug("call rpc");
            Rpc.RpcRequest request = (Rpc.RpcRequest) msg.getPayload();
            if (request != null) {
                ctx.writeAndFlush(new RpcPacket(this.onCall(request, ctx)));
            }
        }
        if (msg.getPayload() instanceof Rpc.Login) {
            logger.debug("rpc login");
            Rpc.Login login = (Rpc.Login) msg.getPayload();
            ctx.writeAndFlush(new RpcPacket(this.onLogin(login, ctx)));
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        logger.error(cause.getMessage(), cause);
    }

    protected Rpc.LoginResponse onLogin(Rpc.Login rpcRequest, ChannelHandlerContext ctx) {
        String uuid = UUID.randomUUID().toString();
        return Rpc.LoginResponse.newBuilder().setSuccess(true).setToken(uuid).build();
    }

    protected Rpc.RpcResponse onCall(Rpc.RpcRequest rpcRequest, ChannelHandlerContext ctx) {
        Rpc.RpcResponse rpcResponse = rpcService.call(rpcRequest);
        return rpcResponse;
    }
}
