package com.imooc.jvm.threadpool;

/**
 * 测试线程池调优
 */
public class ThreadPoolCoreTest {

    public static void main(String[] args) {
        int i = Runtime.getRuntime().availableProcessors();
        System.err.println(i);
    }
}
