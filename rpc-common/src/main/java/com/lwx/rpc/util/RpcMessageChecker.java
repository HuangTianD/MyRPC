package com.lwx.rpc.util;

import com.lwx.rpc.enitity.RpcRequest;
import com.lwx.rpc.enitity.RpcResponse;
import com.lwx.rpc.enumeration.ResponseCode;
import com.lwx.rpc.enumeration.RpcError;
import com.lwx.rpc.exception.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RpcMessageChecker {

    public static final String INTERFACE_NAME = "interfaceName";
    private static final Logger logger = LoggerFactory.getLogger(RpcMessageChecker.class);

    private RpcMessageChecker(){
    }

    public static void check(RpcRequest rpcRequest,RpcResponse rpcResponse) {
        if(rpcResponse == null){
            logger.error("invoke service {} fail:",rpcRequest.getInterfaceName());
            throw new RpcException(RpcError.SERVICE_INVOCATION_FAILURE,INTERFACE_NAME+":"+rpcRequest.getInterfaceName());
        }

        if(!rpcRequest.getRequestId().equals(rpcResponse.getRequestId())){
            throw new RpcException(RpcError.RESPONSE_NOT_MATCH,INTERFACE_NAME+":"+rpcRequest.getInterfaceName());
        }

        if(rpcResponse.getStatusCode() ==null||!rpcResponse.getStatusCode().equals(ResponseCode.SUCCESS.getCode())){
            logger.error("invoke service {} failed,RpcResponse:{}",rpcRequest.getInterfaceName(),rpcResponse);
            throw new RpcException(RpcError.SERVICE_INVOCATION_FAILURE,INTERFACE_NAME+":"+rpcRequest.getInterfaceName());
        }
    }
}
