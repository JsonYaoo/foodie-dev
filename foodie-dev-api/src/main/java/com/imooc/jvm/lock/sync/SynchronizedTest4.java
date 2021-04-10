package com.imooc.jvm.lock.sync;

/**
 * 测试锁优化: 锁消除
 */
@SuppressWarnings("Duplicates")
public class SynchronizedTest4 {
    private Object object2 = null;

    public void someMethod2() {
        object2 = this.someMethod();
    }

    // 局部变量object作为返回值返回, 出现在方法作用域外, 属于可逃逸
    // => 逃逸的object会作用在成员变量上, 放大了作用域, 这时JVM并不会对object锁进行锁消除
    private Object someMethod() {
        Object object = new Object();
        synchronized (object) {
            return object;
        }
    }
}
