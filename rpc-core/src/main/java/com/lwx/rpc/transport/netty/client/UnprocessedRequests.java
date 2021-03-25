package com.lwx.rpc.transport.netty.client;

import com.lwx.rpc.enitity.RpcRequest;
import com.lwx.rpc.enitity.RpcResponse;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

//用于记录请求的完成情况及其对应的请求id
public class UnprocessedRequests {
    private static ConcurrentHashMap<String, CompletableFuture<RpcResponse>> unprocessedResponseFutures = new ConcurrentHashMap<>();

    public void put(String requestId,CompletableFuture<RpcResponse> future){
        unprocessedResponseFutures.put(requestId,future);
    }

    public void remove(String requestId){unprocessedResponseFutures.remove(requestId);}

    public void complete(RpcResponse rpcResponse){
        CompletableFuture<RpcResponse> future = unprocessedResponseFutures.remove(rpcResponse.getRequestId());
        if(null!=future){
            future.complete(rpcResponse);
        }else{
            throw new IllegalStateException();
        }
    }
}
