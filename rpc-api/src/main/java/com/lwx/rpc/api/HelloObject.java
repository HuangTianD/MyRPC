package com.lwx.rpc.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
//给服务传入的对象，需要能够序列化传输
public class HelloObject implements Serializable {
    private Integer id;
    private String message;
}
