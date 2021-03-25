package com.lwx.rpc.transport.netty.client;

import com.lwx.rpc.codec.CommonDecoder;
import com.lwx.rpc.codec.CommonEncoder;
import com.lwx.rpc.enumeration.RpcError;
import com.lwx.rpc.exception.RpcException;
import com.lwx.rpc.serializer.CommonSerializer;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.*;

public class ChannelProvider {
    private static final Logger logger = LoggerFactory.getLogger(ChannelProvider.class);
    private static EventLoopGroup eventLoopGroup;
    private static Bootstrap bootstrap = initializeBootstrap();

    private static Map<String,Channel> channels = new ConcurrentHashMap<>();


    public static Channel get(InetSocketAddress inetSocketAddress, CommonSerializer serializer) throws InterruptedException{
        //获取对应地址的channel
        String key = inetSocketAddress.toString()+serializer.getCode();
        if(channels.containsKey(key)){
            Channel channel = channels.get(key);
            if(channels!=null&&channel.isActive()){
                return channel;
            }else{
                channels.remove(key);
            }
        }
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) {
                /*自定义序列化编解码器*/
                // RpcResponse -> ByteBuf
                ch.pipeline().addLast(new CommonEncoder(serializer))
                        .addLast(new IdleStateHandler(0, 5, 0, TimeUnit.SECONDS))
                        .addLast(new CommonDecoder())
                        .addLast(new NettyClientHandler());
            }
        });
        Channel channel = null;
        try{
            channel = connect(bootstrap,inetSocketAddress);
        }catch(ExecutionException e){
            logger.error("error when use client:",e);
            return null;
        }
        channels.put(key,channel);
        return channel;
    }

    private static Channel connect(Bootstrap bootstrap,InetSocketAddress inetSocketAddress) throws ExecutionException,InterruptedException{
        //连接目标地址，使用监听器监听channel内处理情况
        CompletableFuture<Channel> completableFuture = new CompletableFuture<>();
        bootstrap.connect(inetSocketAddress).addListener((ChannelFutureListener) future->{
            if(future.isSuccess()){
                logger.info("client connect successful!");
                completableFuture.complete(future.channel());
            }else{
                throw new IllegalStateException();
            }
        });
        return completableFuture.get();
    }
    private static Bootstrap initializeBootstrap(){
        eventLoopGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS,5000)
                .option(ChannelOption.SO_KEEPALIVE,true)
                .option(ChannelOption.TCP_NODELAY,true);
        return bootstrap;
    }
}
