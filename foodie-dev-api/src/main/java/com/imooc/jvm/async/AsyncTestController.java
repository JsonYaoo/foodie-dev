package com.imooc.jvm.async;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

/**
 * 异步化测试: 测试@Async
 */
@RestController
public class AsyncTestController {

    @Autowired
    private ASyncDemoAsyncAnnotation aSyncDemoAsyncAnnotation;

    @GetMapping("/async-test")
    public String asyncTest() throws InterruptedException, ExecutionException {
        aSyncDemoAsyncAnnotation.biz();
        return "success";
    }

}
