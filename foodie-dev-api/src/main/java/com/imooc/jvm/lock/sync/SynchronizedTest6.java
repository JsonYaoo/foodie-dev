package com.imooc.jvm.lock.sync;

/**
 * 测试锁优化: 锁粗化: 将多个连续的加锁、解锁操作连接在一起, 扩展成一个范围更大的锁
 */
@SuppressWarnings("Duplicates")
public class SynchronizedTest6 {
    private final Object lock = new Object();

    // 粗化前
    public void doSomethingMethod1() {
        synchronized (lock) {
            // 1、do some thing
        }
        // 2、能够很快执行完毕，且无需同步的代码
        synchronized (lock) {
            // 3、do other thing
        }
    }

    // 手工代码调整进行粗化后：
    public void doSomethingMethod2() {
        // 进行锁粗化：整合成一次锁请求、同步、释放
        synchronized (lock) {
            // 1、do some thing
            // 2、能够很快执行完毕，且无需同步的代码
            // 3、do other thing
        }
    }
}
