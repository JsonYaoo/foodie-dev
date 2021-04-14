package com.imooc.jvm.jvm.action;

import java.nio.ByteBuffer;

/**
 * 直接内存溢出测试: 使用来ByteBuffer类测试分配内存: 底层也还是使用Unsafe类来分配直接内存的
 * => 1. ByteBuffer直接内存溢出报错是java.lang.OutOfMemoryError: Direct buffer memory
 *    2. 设置最大直接内存大小: -XX:MaxDirectMemorySize, 所以该配置对ByteBuffer有效
 */
public class DirectMemoryTest4 {
    private static final int GB_1 = 1024 * 1024 * 1024;

    /**
     * ByteBuffer参考文档：
     * https://blog.csdn.net/z69183787/article/details/77102198/
     *
     * @param args args
     */
    public static void main(String[] args) {
        int i= 0;
        while (true) {
            ByteBuffer buffer = ByteBuffer.allocateDirect(GB_1);
            System.out.println(++i);
        }
    }
}
