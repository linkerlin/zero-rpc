package com.cbuffer.zerorpc.coder;

import com.cbuffer.zerorpc.protobuf.Rpc;
import com.google.protobuf.MessageLite;

import java.util.HashMap;
import java.util.Map;

/**
 * User: xingsen
 * Date: 14-3-6
 * Time: 上午9:34
 */
public class MessageLiteMapper {
    private static Map<Integer, MessageLite> messageLiteMap = new HashMap<Integer, MessageLite>();

    static {
        messageLiteMap.put(1, Rpc.Login.getDefaultInstance());
        messageLiteMap.put(2, Rpc.LoginResponse.getDefaultInstance());

        messageLiteMap.put(3, Rpc.RpcRequest.getDefaultInstance());
        messageLiteMap.put(4, Rpc.RpcResponse.getDefaultInstance());
    }

    public static MessageLite get(byte type) {
        return messageLiteMap.get(new Integer(type));
    }

    public static byte get(Class<? extends MessageLite> aClass) {
        for (Map.Entry<Integer, MessageLite> entry : messageLiteMap.entrySet()) {
            if (entry.getValue().getClass().isAssignableFrom(aClass)) {
                return entry.getKey().byteValue();
            }
        }
        return 0;
    }
}
