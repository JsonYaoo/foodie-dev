package com.imooc.jvm.lock.reentrantLock;

import java.util.concurrent.locks.ReentrantLock;

/**
 * 测试ReentrantLock互斥性、可重入性
 */
@SuppressWarnings("Duplicates")
public class ReentrantLockTest1 implements Runnable {

    private static int i = 0;
    private ReentrantLock lock = new ReentrantLock();

    private void increase() {
        try {
            // 测试互斥性
            lock.lock();

            // 测试可重入性
            lock.lock();
            lock.lock();
            i = i + 1;
        } finally {
            // 测试互斥性
            lock.unlock();

            // 测试可重入性
            lock.unlock();
            lock.unlock();
        }
    }

    @Override
    public void run() {
        for (int j = 0; j < 10000; j++) {
            increase();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ReentrantLockTest1 t = new ReentrantLockTest1();
        Thread t1 = new Thread(t);
        t1.start();
        Thread t2 = new Thread(t);
        t2.start();
        t1.join();
        t2.join();
        System.out.println(i);
    }
}
