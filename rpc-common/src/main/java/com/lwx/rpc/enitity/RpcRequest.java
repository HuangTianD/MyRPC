package com.lwx.rpc.enitity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
/*
    rpc服务端向客户端请求服务的格式
 */
public class RpcRequest implements Serializable {

    private String requestId;

    private String interfaceName;

    private String methodName;

    private Object[] parameters;

    private Class<?>[] paramTypes;

    private boolean heartBeat;
}
