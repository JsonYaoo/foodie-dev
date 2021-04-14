package com.imooc.jvm.jhsdb;

/**
 * 测试jhsdb可视化工具: -XX:+UseSerialGC -XX:-UseCompressedOops: 使用Serial收集器(为了更好看到测试效果), 关闭压缩指针(为了兼容jhsdb)
 */
public class JHSDBTest {

    static class Test {
        // 引用挂在类上, 所以这个SomeObject对象存储在方法区
        static SomeObject staticObj = new SomeObject();

        // 被JHSDBTest实例引用, 所以这个instanceObj对象存储在堆内存
        SomeObject instanceObj = new SomeObject();

        void foo() {
            // 被main方法栈引用, 存储在栈上的局部变量表里面, 在这里是Stack Roots对象(根对象)
            SomeObject localObj = new SomeObject();
            System.out.println("done"); // 这里设一个断点
        }
    }

    private static class SomeObject {
        private Short age;
        private String name;
    }

    public static void main(String[] args) throws InterruptedException {
        Test test = new Test();
        test.foo();
        Thread.sleep(Integer.MAX_VALUE);
    }
}