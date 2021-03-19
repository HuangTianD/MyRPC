package com.lwx.rpc.provider;

import com.lwx.rpc.enumeration.RpcError;
import com.lwx.rpc.exception.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

//服务注册表
public class ServiceProviderImpl implements ServiceProvider {
    private static final Logger logger = LoggerFactory.getLogger(ServiceProviderImpl.class);

    //建立哈希图和哈希表，以储存注册的服务及其对应的服务名
    private static final Map<String,Object> serviceMap = new ConcurrentHashMap<>();
    private static final Set<String> registeredService = ConcurrentHashMap.newKeySet();

    @Override
    public synchronized <T> void addServiceProvider(T service,String serviceName){
        //储存服务注册信息
        if(registeredService.contains(serviceName)) {
            logger.error("already contains this service");
            return ;
        }
        registeredService.add(serviceName);
        serviceMap.put(serviceName,service);
        logger.info("register service {} to interface {}",serviceName,service.getClass().getInterfaces());
    }

    @Override
    public synchronized Object getServiceProvider(String serviceName){
        //获取服务
        Object service = serviceMap.get(serviceName);
        if(service==null){
            logger.error("no such service");
            throw new RpcException(RpcError.SERVICE_NOT_FOUND);
        }
        return service;
    }
}
