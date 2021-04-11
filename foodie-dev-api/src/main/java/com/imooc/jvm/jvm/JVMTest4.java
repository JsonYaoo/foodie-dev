package com.imooc.jvm.jvm;

/**
 * 测试类加载机制: 类的初始化
 * => JVMTest4静态块 -> main -> JVMTest4构造块 -> JVMTest4构造方法
 */
public class JVMTest4 {
    static {
        System.out.println("JVMTest4静态块");
    }


    public JVMTest4() {
        System.out.println("JVMTest4构造方法");
    }

    {
        System.out.println("JVMTest4构造块");
    }

    public static void main(String[] args) {
        System.out.println("main");
        new JVMTest4();
    }
}

