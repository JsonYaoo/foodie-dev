package com.imooc.jvm.async;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * 异步化测试: @Async实现异步化 => 底层也是使用线程池
 * => 需要交由Spring管理
 */
@Component
public class ASyncDemoAsyncAnnotation {

  @Autowired
  private AsyncJob asyncJob;

  private void subBiz1() throws InterruptedException {
    Thread.sleep(1000L);
    System.out.println(new Date() + "subBiz1");
  }

  private void subBiz2() throws InterruptedException {
    Thread.sleep(1000L);
    System.out.println(new Date() + "biz2");
  }

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

  // 3. 异步方法生效(不建议)
  @Autowired
  private ApplicationContext applicationContext;

  public void biz() throws InterruptedException, ExecutionException {
    this.subBiz1();
    // 1. 异步方法生效
    Future<String> logResult = this.asyncJob.saveOpLog();
    // 2. 异步方法失效(不建议)
//    Future<String> logResult = this.saveOpLog();
    // 3. 异步方法生效(不建议)
//    ASyncDemoAsyncAnnotation aSyncDemoAsyncAnnotation = applicationContext.getBean(ASyncDemoAsyncAnnotation.class);
//    Future<String> logResult = aSyncDemoAsyncAnnotation.saveOpLog();
    this.subBiz2();

    // 最后获取结果, 方法会阻塞
    System.err.println("async返回: " + logResult.get());
    System.out.println(new Date() + "执行结束");
  }
}
