package com.lwx.rpc.nettyclient;

import com.lwx.rpc.api.ByeService;
import com.lwx.rpc.serializer.CommonSerializer;
import com.lwx.rpc.transport.RpcClient;
import com.lwx.rpc.transport.RpcClientProxy;
import com.lwx.rpc.api.HelloObject;
import com.lwx.rpc.api.HelloService;
import com.lwx.rpc.transport.netty.client.NettyClient;
import com.lwx.rpc.serializer.ProtobufSerializer;

public class NettyTestClient {
    public static void main(String[] args) {
        //建立客户端，传入序列器
        RpcClient client = new NettyClient(CommonSerializer.PROTOBUF_SERIALIZER);
        //动态代理
        RpcClientProxy rpcClientProxy = new RpcClientProxy(client);
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
        HelloObject object = new HelloObject(12, "This is a message");
        //调用动态代理示例的invoke方法
        String res = helloService.hello(object);
        System.out.println(res);
        ByeService byeService = rpcClientProxy.getProxy(ByeService.class);
        System.out.println(byeService.bye("Netty"));
    }
}
