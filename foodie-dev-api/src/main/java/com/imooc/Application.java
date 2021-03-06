package com.imooc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

// 多个聚合工程时, 如果冲突还是会发生Spring Session登录的, 所以直接去掉依赖吧
//@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
@SpringBootApplication
// 让mybatis去扫描mapper而不是spring去扫描, 注意要用tk包下的
@MapperScan(basePackages = "com.imooc.mapper")
// 扫描SID所在包以及common层相关组件
@ComponentScan(basePackages = {"com.imooc", "org.n3r.idworker"})
// 开启定时任务
//@EnableScheduling
// 开启使用Redis作为Spring Session
//@EnableRedisHttpSession
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
