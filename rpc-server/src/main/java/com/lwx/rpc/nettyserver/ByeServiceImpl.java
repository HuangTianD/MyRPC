package com.lwx.rpc.nettyserver;

import com.lwx.rpc.annotation.Service;
import com.lwx.rpc.api.ByeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ByeServiceImpl implements ByeService {
    private static final Logger logger = LoggerFactory.getLogger(ByeServiceImpl.class);

    @Override
    public String bye(String name){
        return "bye, "+name;
    }
}
