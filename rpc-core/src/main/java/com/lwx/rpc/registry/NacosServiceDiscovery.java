package com.lwx.rpc.registry;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.lwx.rpc.loadbalancer.LoadBalancer;
import com.lwx.rpc.loadbalancer.RandomLoadBalancer;
import com.lwx.rpc.util.NacosUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.List;

public class NacosServiceDiscovery implements ServiceDiscovery{
    //服务获取
    private static final Logger logger = LoggerFactory.getLogger(NacosServiceDiscovery.class);
    //负载均衡策略
    private final LoadBalancer loadBalancer;

    public NacosServiceDiscovery(LoadBalancer loadBalancer){
        if(loadBalancer==null) this.loadBalancer = new RandomLoadBalancer();
        else this.loadBalancer = loadBalancer;
    }

    @Override
    public InetSocketAddress lookupService(String serviceName){
        //通过负载均衡算法，找出适用的服务
        try{
            List<Instance> instances = NacosUtil.getAllInstance(serviceName);
            Instance instance = loadBalancer.select(instances);
            return new InetSocketAddress(instance.getIp(),instance.getPort());
        }catch(NacosException e){
            logger.error("error when lookup service:",e);
        }
        return null;
    }
}
