package com.cbuffer.zerorpc.client;

import com.cbuffer.zerorpc.common.protobuf.Rpc;
import org.bson.BSON;
import org.bson.BSONObject;
import org.bson.BasicBSONObject;

/**
 * fireflyc@icloud.com
 */
public class ResponseWrapper {

    private final Rpc.RpcResponse response;

    public ResponseWrapper(Rpc.RpcResponse response) {
        this.response = response;
    }

    public long getRequestId() {
        return response.getRequestId();
    }

    public boolean getSuccess() {
        return response.getSuccess();
    }

    public String getErrorMsg() {
        return response.getErrorMsg();
    }

    public BSONObject getData() {
        if (response.getData().isEmpty()) {
            return new BasicBSONObject();
        }
        return BSON.decode(response.getData().toByteArray());
    }
}

