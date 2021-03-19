package com.lwx.rpc.transport;

import com.lwx.rpc.annotation.Service;
import com.lwx.rpc.annotation.ServiceScan;
import com.lwx.rpc.enumeration.RpcError;
import com.lwx.rpc.exception.RpcException;
import com.lwx.rpc.provider.ServiceProvider;
import com.lwx.rpc.registry.ServiceRegistry;
import com.lwx.rpc.util.ReflectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.net.InetSocketAddress;
import java.util.Set;

public abstract class AbstractRpcServer implements RpcServer{
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    protected String host;
    protected int port;

    protected ServiceRegistry serviceRegistry;
    protected ServiceProvider serviceProvider;

    public void scanService(){
        String mainClassName = ReflectUtil.getStackTrace();
        Class<?> startClass;
        try{
            startClass = Class.forName(mainClassName);
            //寻找注解@ServiceScan
            if(!startClass.isAnnotationPresent(ServiceScan.class)){
                logger.error("the class is lack of @ServiceScan annotation!");
                throw new RpcException(RpcError.SERVICE_SCAN_PACKAGE_NOT_FOUND);
            }
        } catch (ClassNotFoundException e) {
            logger.error("unknown error");
            throw new RpcException(RpcError.UNKNOWN_ERROR);
        }

        //该注解的value属性，默认为空
        String basePackage = startClass.getAnnotation(ServiceScan.class).value();
        if("".equals(basePackage)){
            //获取包名
            basePackage = mainClassName.substring(0,mainClassName.lastIndexOf("."));
        }
        //找到包内的所有类
        Set<Class<?>> classSet = ReflectUtil.getClasses(basePackage);

        for(Class<?> clazz:classSet){
            //寻找@Service注解
            if(clazz.isAnnotationPresent(Service.class)){
                String serviceName = clazz.getAnnotation(Service.class).name();
                Object obj;
                try{
                    //此处有改动
                    obj = clazz.getDeclaredConstructor().newInstance();
                }catch(InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e){
                    logger.error("error when build {}",clazz);
                    continue;
                }
                if("".equals(serviceName)){
                    //注册每个方法
                    Class<?>[] interfaces = clazz.getInterfaces();
                    for(Class<?> oneInterface:interfaces){
                        publishService(obj,oneInterface.getCanonicalName());
                    }
                }else{
                    publishService(obj,serviceName);
                }
            }
        }
    }

    @Override
    public <T> void publishService(T service,String serviceName){
        //保存到本地注册表，同时发布至Nacos
        serviceProvider.addServiceProvider(service,serviceName);
        serviceRegistry.register(serviceName,new InetSocketAddress(host,port));
    }
}
