package com.lwx.rpc.transport.netty.client;

import com.lwx.rpc.enitity.RpcRequest;
import com.lwx.rpc.enitity.RpcResponse;
import com.lwx.rpc.factory.SingletonFactory;
import com.lwx.rpc.serializer.CommonSerializer;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutionException;

public class NettyClientHandler extends SimpleChannelInboundHandler<RpcResponse> {
    private static final Logger logger = LoggerFactory.getLogger(NettyClientHandler.class);

    private final UnprocessedRequests unprocessedRequests;
    public NettyClientHandler(){this.unprocessedRequests = SingletonFactory.getInstance((UnprocessedRequests.class)); }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx,RpcResponse msg) throws Exception{
        try{
            logger.info(String.format("client get message:%s",msg));
            //处理请求
            unprocessedRequests.complete(msg);
        }finally{
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx,Throwable cause) throws Exception{
        logger.error("error when handling client:");
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    //心跳
    public void userEventTriggered(ChannelHandlerContext ctx,Object evt) throws Exception{
        if(evt instanceof IdleStateEvent){
            IdleState state = ((IdleStateEvent) evt).state();
            if(state == IdleState.WRITER_IDLE){
                logger.info("send heart package:{}",ctx.channel().remoteAddress());
                Channel channel = ChannelProvider.get((InetSocketAddress)ctx.channel().remoteAddress(), CommonSerializer.getByCode(CommonSerializer.DEFAULT_SERIALIZER));
                RpcRequest rpcRequest = new RpcRequest();
                rpcRequest.setHeartBeat(true);
                channel.writeAndFlush(rpcRequest).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            }
        }else{
            super.userEventTriggered(ctx,evt);
        }
    }
}
