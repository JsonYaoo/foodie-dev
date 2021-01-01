package com.imooc;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

// 4、打成wat包, 需要增加war包启动类: 在tomcat启动后, 初始化Servlet时调用
public class WarStarterApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        // 此时执行Application启动类
        return builder.sources(Application.class);
    }
}
