package com.imooc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
// 让mybatis去扫描mapper而不是spring去扫描, 注意要用tk包下的
@MapperScan(basePackages = "com.imooc.mapper")
// 扫描SID所在包以及common层相关组件
@ComponentScan(basePackages = {"com.imooc", "org.n3r.idworker"})
public class FdfsApplication {

    public static void main(String[] args) {
        SpringApplication.run(FdfsApplication.class, args);
    }

}
