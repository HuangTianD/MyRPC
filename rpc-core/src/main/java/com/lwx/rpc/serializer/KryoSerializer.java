package com.lwx.rpc.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.lwx.rpc.enitity.RpcRequest;
import com.lwx.rpc.enitity.RpcResponse;
import com.lwx.rpc.enumeration.SerializerCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
//kyro编码器
public class KryoSerializer implements CommonSerializer{
    private static final Logger logger = LoggerFactory.getLogger(KryoSerializer.class);

    private static final ThreadLocal<Kryo> kryoThreadLocal = ThreadLocal.withInitial(()->{
        Kryo kryo = new Kryo();
        kryo.register(RpcResponse.class);
        kryo.register(RpcRequest.class);
        kryo.setReferences(true);
        kryo.setRegistrationRequired(false);
        return kryo;
    });

    @Override
    public byte[] serialize(Object obj){
        try(ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            Output output = new Output(byteArrayOutputStream)){
            Kryo kryo = kryoThreadLocal.get();
            kryo.writeObject(output,obj);
            kryoThreadLocal.remove();
            return output.toBytes();
        }catch(Exception e){
            logger.error("error when kryo serialize:",e);
        }
        return null;
    }

    @Override
    public Object deserialize(byte[] bytes,Class<?> clazz){
        try(ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream((bytes));
            Input input = new Input(byteArrayInputStream)){
            Kryo kryo =  kryoThreadLocal.get();
            Object o = kryo.readObject(input,clazz);
            kryoThreadLocal.remove();
            return o;
        }catch(Exception e){
            logger.error("error when deserialize with kryo:",e);
        }
        return null;
    }

    @Override
    public int getCode(){
        return SerializerCode.valueOf("KRYO").getCode();
    }
}
