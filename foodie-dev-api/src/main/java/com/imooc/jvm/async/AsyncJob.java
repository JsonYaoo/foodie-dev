package com.imooc.jvm.async;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.Future;

/**
 * 测试@Async: 实际执行业务Async类
 * => @Async注意点:
 *      1) @Async标注的方法必须返回void或者Future, 访问控制符必须为public
 *      2) 建议将@Async标注的方法放到独立的类中, 否则一旦使用this调用就会使得注解失效 => 异步方法是在代理对象中的, 而本对象中的是同步方法
 *      3) 建议自定义BlockingQueue的大小(即队列容量), 配置文件里配置, 默认是Integer.MAX_VALUE
 */
@Component
public class AsyncJob {

  @Async
  public Future<String> saveOpLog() throws InterruptedException {
    try {
      Thread.sleep( 1000L);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    String result = new Date() + "插入操作日志";
    System.out.println(result);
    return new AsyncResult<>(result);
  }


}
