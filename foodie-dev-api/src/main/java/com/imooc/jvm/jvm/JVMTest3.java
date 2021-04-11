package com.imooc.jvm.jvm;

/**
 * 测试类加载机制: 类的初始化
 * => static代码块合并, 并且顺序输出
 */
public class JVMTest3 {

    // 1. 1+2 => 输出1
//    static int i = 0;

    // 2.
    static {
        i = 1;
    }

    // 3. 2+3 => 输出0
    static int i = 0;

    public static void main(String[] args) {
        System.out.println(i);
    }
}
