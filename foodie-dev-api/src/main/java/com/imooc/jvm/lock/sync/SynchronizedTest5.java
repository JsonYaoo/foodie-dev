package com.imooc.jvm.lock.sync;

import java.util.List;
import java.util.Vector;

/**
 * 测试锁优化: 锁粗化: 将多个连续的加锁、解锁操作连接在一起, 扩展成一个范围更大的锁
 */
@SuppressWarnings("Duplicates")
public class SynchronizedTest5 {
    public static void main(String[] args) {
        // 默认不加参数: 3894
        // -server -XX:+DoEscapeAnalysis -XX:+EliminateLocks: 开启逃逸分析, 开启锁消除&锁粗化, 默认都开启: 4092
        // -server -XX:-DoEscapeAnalysis -XX:-EliminateLocks: 关闭逃逸分析, 关闭锁消除&锁粗化: 4461
        // => 测试结果: 开启后性能提升10%
        List<Integer> list = new Vector<>();
        long start = System.currentTimeMillis();

        // => JVM可对其锁粗化
        for (int i = 0; i < 10000000; i++) {
            synchronized (list) {
                list.add(i);
            }
        }
        // => JVM自动进行锁粗化的代码如:
//        synchronized (list) {
//            for (int i = 0; i < 10000000; i++) {
//                    list.add(i);
//            }
//        }
        long end = System.currentTimeMillis();
        System.out.println(end - start);
    }
}
