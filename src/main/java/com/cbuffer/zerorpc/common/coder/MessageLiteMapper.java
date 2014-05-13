package com.cbuffer.zerorpc.common.coder;

import com.cbuffer.zerorpc.common.protobuf.Rpc;
import com.google.protobuf.MessageLite;

import java.util.HashMap;
import java.util.Map;

/**
 * fireflyc@icloud.com
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
