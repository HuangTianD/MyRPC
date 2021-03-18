package com.lwx.rpc.nettyserver;

import com.lwx.rpc.annotation.Service;
import com.lwx.rpc.api.HelloService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.lwx.rpc.api.HelloObject;

@Service
public class HelloServiceImpl implements HelloService {

    private static final Logger logger = LoggerFactory.getLogger(HelloServiceImpl.class);

    @Override
    public String hello(HelloObject object) {
        logger.info("接收到：{}", object.getMessage());
        return "这是hello的返回值，id=" + object.getId();
    }

}
