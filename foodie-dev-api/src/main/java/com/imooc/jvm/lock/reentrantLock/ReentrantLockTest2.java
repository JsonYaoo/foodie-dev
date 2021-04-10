package com.imooc.jvm.lock.reentrantLock;

import java.util.concurrent.locks.ReentrantLock;

/**
 * 测试ReentrantLock公平、非公平
 */
public class ReentrantLockTest2 {
    public static void main(String[] args) {
        FairTest test = new FairTest();
        Thread t1 = new Thread(test);
        Thread t2 = new Thread(test);
        Thread t3 = new Thread(test);
        t1.start();
        t2.start();
        t3.start();
    }
}

class FairTest implements Runnable {

    // synchronized只能创建非公平锁
    // ReentrantLock默认非公平, 锁性能：非公平锁 > 公平锁
    // 公平锁: 如果另一个线程持有锁或者有其他线程在等待队列中等待这个锁, 那么新发出的请求的线程将被放入到队列中
    // 非公平锁: 当锁被某个线程持有时, 新发出请求的线程才会被放入队列; 如果在发出请求的时候, 锁刚好变成可用状态, 那么这个线程会跳过队里等待过程而直接获得锁 => 所以性能高
    private ReentrantLock lock = new ReentrantLock(false);// 非公平锁: 线程不会按照请求顺序获得锁, 可插队
//    private ReentrantLock lock = new ReentrantLock(true);// 公平锁: 线程会按照请求顺序获得锁

    @Override
    public void run() {
        try {
            System.out.println(Thread.currentThread().getName() + "开始运行");
            lock.lock();
            System.out.println(Thread.currentThread().getName() + "拿到锁");
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            System.out.println("SLEEP 发生异常");
        } finally {
            System.out.println(Thread.currentThread().getName() + "释放锁");
            lock.unlock();
        }
    }
}
