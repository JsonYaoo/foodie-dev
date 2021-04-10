package com.imooc.jvm.threadpool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Executors测试
 */
public class ExecutorsTest {

    public static void main(String[] args) {
        // n corePoolSize, n maxPoolSize, max LinkBlockingQueue
        // => 适用于线程数比较稳定的场景
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(1);
        fixedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                System.err.println("aaa");
            }
        });

        // 1 corePoolSize, 1 maxPoolSize, max LinkBlockingQueue
        // => 适用于需要严格控制执行顺序的场景
        ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
        singleThreadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                System.err.println("bbb");
            }
        });

        // 0 corePoolSize, max maxPoolSize, 1 SynchronousQueue
        // => 适用于生命周期很短的异步任务
        ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                System.err.println("ccc");
            }
        });

        // n corePoolSize, max maxPoolSize, 1 DelayedWorkQueue => 返回ScheduledExecutorService, 延迟3秒执行一次, 周期执行用scheduleAtFixedRate & scheduleWithFixedDelay
        // => 适用于延时任务 & 定时任务
        ExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(1);
        ((ScheduledExecutorService) scheduledThreadPool).schedule(new Runnable() {
            @Override
            public void run() {
                System.err.println("ddd");
            }
        }, 3, TimeUnit.SECONDS);

        // 不是继承ThreadPoolExecutor, 所以没有corePoolSize、maxPoolSize、BlockingQueue的概念 => cpu parallelisms
        // => 适用于分而治之 & 递归计算的CPU密集场景
        ExecutorService workStealingPool = Executors.newWorkStealingPool();
        workStealingPool.execute(new Runnable() {
            @Override
            public void run() {
                System.err.println("eee");
            }
        });
    }
}

