package com.cbuffer.zerorpc.client;

import com.cbuffer.zerorpc.client.collector.filter.PacketIDFilter;
import com.cbuffer.zerorpc.client.collector.filter.PacketTypeFilter;
import com.cbuffer.zerorpc.common.protobuf.Rpc;
import com.google.protobuf.ByteString;
import org.bson.BSON;
import org.bson.types.BasicBSONList;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicLong;

/**
 * fireflyc@icloud.com
 */
public class ZeroRpc {
    private AtomicLong atomRequestId = new AtomicLong();
    private final NettyConnection conn;
    private String token;

    public ZeroRpc(String host, int port) {
        this.conn = new NettyConnection(host, port);
    }

    public Rpc.LoginResponse connection(String username, String password) throws IOException, InterruptedException,
            TimeoutException {
        this.conn.connection();
        Rpc.Login login = Rpc.Login.newBuilder()
                .setUsername(username)
                .setPassword(password)
                .build();
        Rpc.LoginResponse response = (Rpc.LoginResponse) this.conn.sendPacket(login,
                new PacketTypeFilter(Rpc.LoginResponse.class));
        if (response.getSuccess()) {
            this.token = response.getToken();
        }
        return response;
    }

    public ResponseWrapper executeArgs(boolean isAsync, String service, String method, Object... params) throws TimeoutException {
        long requestId = atomRequestId.incrementAndGet();

        Rpc.RpcRequest.Builder requestBuilder = Rpc.RpcRequest.newBuilder();
        requestBuilder.setAsync(isAsync)
                .setRequestId(requestId)
                .setServiceName(service)
                .setMethod(method)
                .setToken(token);
        if (params != null) {
            BasicBSONList list = new BasicBSONList();
            for (Object param : params) {
                list.add(param);
            }
            byte paramBytes[] = BSON.encode(list);
            requestBuilder.setParameter(ByteString.copyFrom(paramBytes));
        }

        Rpc.RpcResponse response = (Rpc.RpcResponse) this.conn.sendPacket(requestBuilder.build(), new PacketIDFilter(requestId));
        return new ResponseWrapper(response);
    }

    public ResponseWrapper execute(boolean isAsync, String service, String method) throws TimeoutException {
        return this.executeArgs(isAsync, service, method);
    }

    public ResponseWrapper asyncExecute(String service, String method) throws TimeoutException {
        return this.asyncExecuteArgs(service, method);
    }

    public ResponseWrapper asyncExecuteArgs(String service, String method, Object... params) throws TimeoutException {
        return this.executeArgs(true, service, method, params);
    }

    public ResponseWrapper syncExecute(String service, String method) throws TimeoutException {
        return this.syncExecuteArgs(service, method);
    }

    public ResponseWrapper syncExecuteArgs(String service, String method, Object... params) throws TimeoutException {
        return this.executeArgs(false, service, method, params);
    }

    public void close() {
        this.conn.close();
    }
}