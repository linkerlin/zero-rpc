package com.cbuffer.zerorpc.server;

import com.cbuffer.zerorpc.common.protobuf.Rpc;
import com.google.protobuf.ByteString;
import org.bson.BSON;
import org.bson.BSONObject;
import org.bson.BasicBSONObject;
import org.bson.types.BasicBSONList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * fireflyc@icloud.com
 */
public class RemoteExecutorService {
    private Logger logger = LoggerFactory.getLogger(RemoteExecutorService.class);
    private Map<String, Map<String, RpcInvoker>> invokerMap = new HashMap<String, Map<String, RpcInvoker>>();

    public void addRpcInvoker(RpcInvoker invoker) {
        Map<String, RpcInvoker> serviceMap = invokerMap.get(invoker.serviceName);
        if (serviceMap == null) {
            invokerMap.put(invoker.serviceName, new HashMap<String, RpcInvoker>());
            serviceMap = invokerMap.get(invoker.serviceName);
        }
        serviceMap.put(invoker.methodName, invoker);
        logger.debug("{}", invoker);
    }

    public Rpc.RpcResponse call(Rpc.RpcRequest rpcRequest) {
        Rpc.RpcResponse.Builder builder = Rpc.RpcResponse.newBuilder();
        builder.setRequestId(rpcRequest.getRequestId());
        builder.setSuccess(false);
        String serviceName = rpcRequest.getServiceName();
        String methodName = rpcRequest.getMethod();

        try {
            BSONObject bsonParams = new BasicBSONList();
            if (rpcRequest.hasParameter()) {
                ByteString bytesParams = rpcRequest.getParameter();
                bsonParams = BSON.decode(bytesParams.toByteArray());
            }
            Map<String, RpcInvoker> serviceMap = invokerMap.get(serviceName);
            if (serviceMap == null) {
                return error(rpcRequest.getRequestId(), String.format("service '%s' not found.", serviceName));
            }
            RpcInvoker rpcInvoker = serviceMap.get(methodName);
            if (rpcInvoker == null) {
                return error(rpcRequest.getRequestId(), String.format("%s not found.", endPoint(serviceName, methodName)));
            }
            Object obj = executeMethod(rpcInvoker, bsonParams);
            if (obj != null && !(obj instanceof BasicBSONObject)) {
                return error(rpcRequest.getRequestId(), String.format("%s must return 'BSONObject' type.",
                        endPoint(serviceName, methodName)));
            }
            builder.setSuccess(true);
            builder.clearErrorMsg();
            if (obj != null) {
                byte data[] = BSON.encode((BSONObject) obj);
                builder.setData(ByteString.copyFrom(data));
            }
        } catch (NoSuchMethodException e) {
            return error(rpcRequest.getRequestId(), String.format("%s not found.", endPoint(serviceName, methodName)));
        } catch (Exception e) {
            logger.error(e.toString(), e);
            return error(rpcRequest.getRequestId(), e.toString());
        }

        return builder.build();
    }

    protected String endPoint(String serviceName, String methodName) {
        return serviceName + "@" + methodName;
    }

    protected Rpc.RpcResponse error(long reqId, String msg) {
        Rpc.RpcResponse.Builder builder = Rpc.RpcResponse.newBuilder();
        builder.setSuccess(false);
        builder.setErrorMsg(msg);
        builder.setRequestId(reqId);
        return builder.build();
    }

    protected Object executeMethod(final RpcInvoker rpcInvoker, BSONObject bsonParams)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        List<Object> requestParams = new ArrayList<Object>();
        for (String key : bsonParams.keySet()) {
            Object p = bsonParams.get(key);
            requestParams.add(p);
        }
        return rpcInvoker.execute(requestParams);
    }

    public static RpcInvoker createInvoker(String methodName, Class clazz, Object instance) {
        RpcInvoker invoker = new RpcInvoker();
        invoker.serviceName = clazz.getSimpleName();
        invoker.methodName = methodName;
        invoker.clazz = clazz;
        invoker.instance = instance;
        return invoker;
    }

    static class RpcInvoker {
        public String serviceName;
        public String methodName;
        public Class clazz;
        public Object instance;

        public Object execute(List<Object> requestParams)
                throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
            Class<?> methodClazz[] = new Class<?>[requestParams.size()];
            for (int i = 0; i < requestParams.size(); i++) {
                methodClazz[i] = requestParams.get(i).getClass();
            }
            return clazz.getMethod(methodName, methodClazz)
                    .invoke(instance, requestParams.toArray());
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("serviceName=%s ", serviceName))
                    .append(String.format("methodName=%s ", methodName))
                    .append(String.format("clazz=%s ", clazz))
                    .append(String.format("instance=%s ", instance));
            return sb.toString();
        }
    }
}

