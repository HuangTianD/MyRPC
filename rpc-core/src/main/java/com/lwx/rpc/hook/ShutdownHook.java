package com.lwx.rpc.hook;

import com.lwx.rpc.factory.ThreadPoolFactory;
import com.lwx.rpc.util.NacosUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class ShutdownHook {
    private static final Logger logger = LoggerFactory.getLogger(ShutdownHook.class);

    private static final ShutdownHook shutdownHook = new ShutdownHook();

    public static ShutdownHook getShutdownHook(){return shutdownHook;}

    public void addClearAllHook(){
        //当jvm关闭时，注销nacos所有服务，关闭线程池中所有线程
        logger.info("clear all service after shutdown");
        Runtime.getRuntime().addShutdownHook(new Thread(()->{
            NacosUtil.clearRegistry();
            ThreadPoolFactory.shutDownAll();
        }));
    }
}
