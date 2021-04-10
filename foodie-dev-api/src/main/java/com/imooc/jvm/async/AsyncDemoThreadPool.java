package com.imooc.jvm.async;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 异步化测试: 线程池实现异步化
 * => 需要注意的是线程池不需要没用一次创建一次, 测试时记得关闭线程池
 */
public class AsyncDemoThreadPool {

  private ThreadPoolExecutor executor = new ThreadPoolExecutor(
          // 核心线程数 => 相当于"正式员工"
          4,
          // 最大线程数 => 相当于"最大员工数" = "正式员工" + "临时员工", 当核心线程数Queue满了会生成"临时员工"
          4,
          // 允许线程空闲的时间, 默认情况下指的是非核心线程的空闲时间 => 相当于"员工的空闲时间"
          // 如果开启核心线程空闲过期的设置, 则核心线程也会被标记为空闲, 空闲过期过后会被回收
//                executor.allowCoreThreadTimeOut(true);
          10L,
          // keepAliveTime的时间单位
          TimeUnit.SECONDS,
          // 当线程达到corePoolSize时, 则把等待执行的任务传入BlockingQueue, 存储到队列中 => 相当于"任务计划"
          // 这时如果是无界队列, 则任务会一直在队列中排队下去, 而不会去创建非核心线程, 因为只有在队列满了, 才会去创建非核心线程
          new LinkedBlockingDeque<>(2500),
          // 线程工厂, 用于创建线程: defaultThreadFactory、privilegedThreadFactory(访问控制) => 相当于"人才市场"
          Executors.defaultThreadFactory(),
          // 拒绝任务的策略, 在线程繁忙且队列满时触发: AbortPolicy、CallerRusPolicy、DiscardOldestPolicy、DiscardPolicy => 相当于"解雇策略"
          new ThreadPoolExecutor.AbortPolicy()
  );

  private void subBiz1() throws InterruptedException {
    Thread.sleep(1000L);
    System.out.println(new Date() + "subBiz1");
  }

  private void subBiz2() throws InterruptedException {
    Thread.sleep(1000L);
    System.out.println(new Date() + "biz2");
  }


  private void saveOpLog() throws InterruptedException {
    executor.submit(new SaveOpLogThread2());
  }

  private void biz() throws InterruptedException {
    this.subBiz1();
    this.saveOpLog();
    this.subBiz2();

    System.out.println(new Date() + "执行结束");
  }

  public static void main(String[] args) throws InterruptedException {
    new AsyncDemoThreadPool().biz();
  }
}

class SaveOpLogThread2 implements Runnable {

  @Override
  public void run() {
    try {
      Thread.sleep( 1000L);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    System.out.println(new Date() + "插入操作日志");
  }

}
