package com.lwx.rpc.transport.netty.client;

import com.lwx.rpc.factory.SingletonFactory;
import com.lwx.rpc.loadbalancer.LoadBalancer;
import com.lwx.rpc.loadbalancer.RandomLoadBalancer;
import com.lwx.rpc.registry.NacosServiceDiscovery;
import com.lwx.rpc.registry.ServiceDiscovery;
import com.lwx.rpc.serializer.CommonSerializer;
import com.lwx.rpc.transport.RpcClient;
import com.lwx.rpc.enitity.RpcRequest;
import com.lwx.rpc.enitity.RpcResponse;
import com.lwx.rpc.enumeration.RpcError;
import com.lwx.rpc.exception.RpcException;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;

public class NettyClient implements RpcClient {
    private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);
    private static final Bootstrap bootstrap;
    private final ServiceDiscovery serviceDiscovery;
    private CommonSerializer serializer;
    private static final EventLoopGroup group;

    static{
        //netty客户端配置
        group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE,true);
    }
    //
    private final UnprocessedRequests unprocessedRequests;

    public NettyClient(){this(DEFAULT_SERIALIZER,new RandomLoadBalancer());}
    public NettyClient(LoadBalancer loadBalancer){this(DEFAULT_SERIALIZER,loadBalancer);}
    public NettyClient(Integer serializer){this(serializer,new RandomLoadBalancer());}
    public NettyClient(Integer serializer,LoadBalancer loadBalancer){
        //服务发现，通过负载均衡算法选取合适的服务
        this.serviceDiscovery = new NacosServiceDiscovery(loadBalancer);
        this.serializer = CommonSerializer.getByCode(serializer);
        //用uncompletabelfuture保存已发送的请求
        this.unprocessedRequests = SingletonFactory.getInstance(UnprocessedRequests.class);
    }

    @Override
    public CompletableFuture<RpcResponse> sendRequest(RpcRequest rpcRequest){
        if(serializer==null){
            logger.error("serializer not set");
            throw new RpcException(RpcError.SERIALIZER_NOT_FOUND);
        }
        CompletableFuture<RpcResponse> resultFuture = new CompletableFuture<>();
        try{
            InetSocketAddress inetSocketAddress = serviceDiscovery.lookupService(rpcRequest.getInterfaceName());
            Channel channel = ChannelProvider.get(inetSocketAddress,serializer);
            if(!channel.isActive()){
                group.shutdownGracefully();
                return null;
            }
            //发送请求并监听，使用unprocessedRequsets储存已发送过的请求及其对应的返回
            unprocessedRequests.put(rpcRequest.getRequestId(),resultFuture);
            channel.writeAndFlush(rpcRequest).addListener(((ChannelFutureListener)future1->{
                if(future1.isSuccess()){
                    logger.info(String.format("client send message:%s",rpcRequest.toString()));
                }else{
                    future1.channel().close();
                    resultFuture.completeExceptionally(future1.cause());
                    logger.error("error when send message",future1.cause());
                }
            }));
        } catch (InterruptedException e) {
            unprocessedRequests.remove(rpcRequest.getRequestId());
            logger.error("error when send message:",e);
            Thread.currentThread().interrupt();
        }
        return resultFuture;
    }
}
