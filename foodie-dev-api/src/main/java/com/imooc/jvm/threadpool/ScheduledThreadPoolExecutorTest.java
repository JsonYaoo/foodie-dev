package com.imooc.jvm.threadpool;

import java.util.Date;
import java.util.concurrent.*;

/**
 * ScheduledThreadPoolExecutor测试
 */
public class ScheduledThreadPoolExecutorTest {

    public static void main(String[] args) {
        ScheduledThreadPoolExecutor scheduledThreadPoolExecutor =
                new ScheduledThreadPoolExecutor(
                        10,
                        Executors.defaultThreadFactory(),
                        new ThreadPoolExecutor.AbortPolicy()
                );

        // 延迟3秒后执行任务
//        scheduledThreadPoolExecutor.schedule(new Runnable() {
//            @Override
//            public void run() {
//                System.err.println("aaa");// 这里等待3秒打印了aaa
//            }
//        }, 3, TimeUnit.SECONDS);

        // 延迟4秒后执行任务, 可以获取执行结果
//        ScheduledFuture<String> schedule = scheduledThreadPoolExecutor.schedule(new Callable<String>() {
//            @Override
//            public String call() throws Exception {
//                return "bbb";
//            }
//        }, 4, TimeUnit.SECONDS);
//        try {
//            String s = schedule.get();// 这里等待3秒打印了aaa, 且在等1秒打印了bbb
//            System.err.println(s);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        }

        // 第一次直接执行, 然后每3秒执行一次任务
        ScheduledFuture<?> ccc = scheduledThreadPoolExecutor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
//                System.err.println("ccc");// 第一次直接打印ccc, 然后每3秒打印一次ccc
                System.err.println("scheduleAtFixedRate, " + new Date());// 第一次直接打印scheduleAtFixedRate, 然后每3秒打印一次scheduleAtFixedRate
                try {
                    Thread.sleep(1000L);// 睡一秒是为了保证能够完成打印
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, 0, 3, TimeUnit.SECONDS);
//        try {
//            Object o = ccc.get();// 会一直阻塞, 毫无反应
//            System.err.println(o);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        }

        // 第一次直接执行, 然后每隔3秒执行一次任务 => 3+1等于4, 所以每4秒执行一次任务
        scheduledThreadPoolExecutor.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                System.err.println("scheduleWithFixedDelay, " + new Date());// 第一次直接打印scheduleWithFixedDelay, 然后每4秒打印一次scheduleWithFixedDelay
                try {
                    Thread.sleep(1000L);// 睡一秒是为了保证能够完成打印
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, 0, 3, TimeUnit.SECONDS);

        // Timer是单线程执行的, API同ScheduledThreadPoolExecutor, 但ScheduledThreadPoolExecutor是多线程执行的, 项目中慎用Timer
//        Timer timer = new Timer();
    }
}
