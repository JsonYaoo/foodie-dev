package com.imooc.jvm.threadpool;

import java.util.concurrent.*;

/**
 * 线程池测试
 */
public class ThreadPoolTest {

    public static void main(String[] args) {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                // 核心线程数 => 相当于"正式员工"
                10,
                // 最大线程数 => 相当于"最大员工数" = "正式员工" + "临时员工", 当核心线程数Queue满了会生成"临时员工"
                10,
                // 允许线程空闲的时间, 默认情况下指的是非核心线程的空闲时间 => 相当于"员工的空闲时间"
                // 如果开启核心线程空闲过期的设置, 则核心线程也会被标记为空闲, 空闲过期过后会被回收
//                executor.allowCoreThreadTimeOut(true);
                10L,
                // keepAliveTime的时间单位
                TimeUnit.SECONDS,
                // 存储等待执行的任务, 传入BlockingQueue => 相当于"任务计划"
                new LinkedBlockingDeque<>(),
                // 线程工厂, 用于创建线程: defaultThreadFactory、privilegedThreadFactory(访问控制) => 相当于"人才市场"
                Executors.defaultThreadFactory(),
                // 拒绝任务的策略, 在线程繁忙且队列满时触发: AbortPolicy、CallerRusPolicy、DiscardOldestPolicy、DiscardPolicy => 相当于"解雇策略"
                new ThreadPoolExecutor.AbortPolicy()
        );

        // 测试任务提交
        executor.execute(new Runnable() {
            @Override
            public void run() {
                System.err.println("线程池测试");
            }
        });

        executor.execute(new Runnable() {
            @Override
            public void run() {
                System.err.println("线程池测试2");
            }
        });

        Future<String> submit = executor.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
//                return "测试submit";
                throw new RuntimeException("测试submit异常");
            }
        });
        try {
            // 测试结果 => 线程执行异常会从get()方法中抛出来, 可以捕获到重新提交任务到线程池
            System.err.println(submit.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.err.println("1");
        } catch (ExecutionException e) {
            e.printStackTrace();
            System.err.println("2");
            submit = executor.submit(new Callable<String>() {
                @Override
                public String call() throws Exception {
                    return "测试submit";
                }
            });
            try {
                System.err.println(submit.get() + "2");
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            } catch (ExecutionException e1) {
                e1.printStackTrace();
            }
        }

        executor.shutdown();
    }
}
