package com.lwx.rpc.handler;

import com.lwx.rpc.enitity.RpcRequest;
import com.lwx.rpc.enitity.RpcResponse;
import com.lwx.rpc.enumeration.ResponseCode;
import com.lwx.rpc.provider.ServiceProvider;
import com.lwx.rpc.provider.ServiceProviderImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class RequestHandler {
    public static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);

    private static final ServiceProvider serviceProvicer;

    static{
        serviceProvicer = new ServiceProviderImpl();
    }
    public Object handle(RpcRequest rpcRequest){
        Object result = null;
        //获取目标方法的类的一个实例
        Object service = serviceProvicer.getServiceProvider(rpcRequest.getInterfaceName());
        try{
            //调用方法，获取结果
            result = invokeTargetMethod(rpcRequest,service);
            logger.info("service {} successfully invoked method {}",rpcRequest.getInterfaceName(),rpcRequest.getMethodName());
        }catch(IllegalAccessException| InvocationTargetException e){
            logger.error("something wrong when invoking or sending",e);
        }
        return result;
    }
    private Object invokeTargetMethod(RpcRequest rpcRequest,Object service) throws InvocationTargetException, IllegalAccessException {
        Method method;
        try{
            //通过反射机制找到对应名称和参数类型的方法
            method = service.getClass().getMethod(rpcRequest.getMethodName(),rpcRequest.getParamTypes());
        }catch (NoSuchMethodException e){
            return RpcResponse.fail(ResponseCode.METHOD_NOT_FOUND,rpcRequest.getRequestId());
        }
        return method.invoke(service,rpcRequest.getParameters());
    }
}
