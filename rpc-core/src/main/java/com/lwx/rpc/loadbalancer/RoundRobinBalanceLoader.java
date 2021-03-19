package com.lwx.rpc.loadbalancer;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;
//轮转
public class RoundRobinBalanceLoader implements LoadBalancer{
    private int index = 0;
    @Override
    public Instance select(List<Instance> instances){
        if(index>=instances.size()){
            index%=instances.size();
        }
        return instances.get(index++);
    }
}
