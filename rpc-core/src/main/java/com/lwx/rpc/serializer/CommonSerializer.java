package com.lwx.rpc.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface CommonSerializer {
    Integer KRYO_SERIALIZER = 0;
    Integer JSON_SERIALIZER = 1;
    Integer HESSIAN_SERIALIZER = 2;
    Integer PROTOBUF_SERIALIZER = 3;
    Integer DEFAULT_SERIALIZER = KRYO_SERIALIZER;
    byte[] serialize(Object obj) throws JsonProcessingException;
    Object deserialize(byte[] bytes,Class<?> clazz);
    int getCode();

    static CommonSerializer getByCode(int code){
        switch(code){
            case 0:
                return new KryoSerializer();
            case 1:
                return new JsonSerializer();
            case 3:
                return new ProtobufSerializer();
            default:
                return null;
        }
    }
}
