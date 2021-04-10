package com.imooc.jvm.lock.sync;

/**
 * 测试锁优化: 锁消除
 */
@SuppressWarnings("Duplicates")
public class SynchronizedTest3 {
    public static void main(String[] args) {
        someMethod();
    }

    // 局部变量object只在方法作用域内, 属于不可逃逸 => 当使用不可逃逸的变量加锁, JVM会进行锁消除的优化
    private static void someMethod() {
        Object object = new Object();
        synchronized(object) {
            System.out.println(object);
        }
    }
}
