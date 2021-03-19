package com.lwx.rpc.nettyserver;

import com.lwx.rpc.annotation.ServiceScan;
import com.lwx.rpc.api.HelloService;
import com.lwx.rpc.serializer.CommonSerializer;
import com.lwx.rpc.transport.netty.server.NettyServer;
import com.lwx.rpc.serializer.ProtobufSerializer;

@ServiceScan
public class NettyTestServer {

    public static void main(String[] args) {
        //创建服务器，参数为ip地址、端口号以及默认的序列化器
        NettyServer server = new NettyServer("127.0.0.1",4397, CommonSerializer.PROTOBUF_SERIALIZER);
        server.start();
    }

}
