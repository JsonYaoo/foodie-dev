package com.imooc;

import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class EsConfig {

    /**
     * 解决ES启动报错的Netty引起的issue
     * Relation: https://github.com/netty/netty/issues/6956
     */
    @PostConstruct
    void init(){
        System.setProperty("es.set.netty.runtime.available.processors", "false");
    }
}
