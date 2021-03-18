package com.lwx.rpc.transport;

import com.lwx.rpc.enitity.RpcRequest;
import com.lwx.rpc.serializer.CommonSerializer;

public interface RpcClient {
    int DEFAULT_SERIALIZER = CommonSerializer.KRYO_SERIALIZER;
    Object sendRequest(RpcRequest rpcRequest);
}
