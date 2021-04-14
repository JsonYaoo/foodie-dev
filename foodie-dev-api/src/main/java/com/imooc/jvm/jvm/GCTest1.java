package com.imooc.jvm.jvm;

/**
 * 垃圾回收测试: 测试finalize()方法
 * => 测试结论:
 *      1) 尽量避免使用finalize()方法, 操作不当可能会导致问题
 *      2) finalize()优先级低, 何时会被调用无法确定, 因为什么时间发生GC不确定(这时是手动使用System.gc()来模拟GC的发生)
 *      3) 建议使用try...catch...finally来替代finalize()
 */
@SuppressWarnings("Duplicates")
public class GCTest1 {

    private static GCTest1 obj;

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        System.out.println("finalize被调用了");
        obj = this;
    }

    public static void main(String[] args) throws InterruptedException {
        obj = new GCTest1();
        obj = null;
        System.gc();

        Thread.sleep(1000L);
        if (obj == null) {
            System.out.println("obj == null");
        } else {
            System.out.println("obj可用");
        }

        Thread.sleep(1000L);
        obj = null;// 如果主线程执行完毕, 且这句话没有被执行时, obj对象会一直和GCTest1对象保持引用, 由于obj已经移除了F-Queue队列, 很有可能再也无法被回收, 出现内存泄露问题
        System.gc();
        if (obj == null) {
            System.out.println("obj == null");
        } else {
            System.out.println("obj可用");
        }
    }
}
