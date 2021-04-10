package com.imooc.jvm.lock.sync;

import java.util.List;
import java.util.Vector;

/**
 * 测试偏向锁
 */
public class SynchronizedTest2 {

    // Vector底层方法都用synchronized实现
    private static List<Integer> list = new Vector<>();

    // 默认应用直接启动: 4088 => (-XX:+UseBiasedLocking --XX:BiasedLockingStartupDelay=5)
    // 关闭偏向锁：4673 => -XX:-UseBiasedLocking
    // 开启偏向锁：3900 => -XX:+UseBiasedLocking --XX:BiasedLockingStartupDelay=0
    // => 测试结果: 开启后提升性能20%左右, 因为检查的是markword上的线程ID, 不用做大量的CAS操作
    public static void main(String[] args) {
        long start = System.currentTimeMillis();

        // 同一个线程多次获取synchronized锁
        for (int i = 0; i < 10000000; i++) {
            list.add(i);
        }

        long end = System.currentTimeMillis();
        System.out.println(end - start);
    }
}
