package com.lwx.rpc.transport;

import com.lwx.rpc.serializer.CommonSerializer;

public interface RpcServer {
    int DEFAULT_SERIALIZER = CommonSerializer.KRYO_SERIALIZER;
    void start();
    <T> void publishService(T service,String serviceClass);
}
