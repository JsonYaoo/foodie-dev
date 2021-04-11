package com.imooc.jvm.jvm;

/**
 * 测试JVM: 对比JVM内存结构
 */
// 方法区: JVMTest1.class
public class JVMTest1 {

    // 方法区: main方法的描述信息
    public static void main(String[] args) {
        // 虚拟机栈: 局部变量demo, 指向ox00088
        // 堆: 0x00088(name="aaa")
        Demo demo = new Demo("aaa");

        // 方法区: printName方法的描述信息
        demo.printName();
    }
}

// 方法区: Demo.class
class Demo {
    // 方法区: 成员变量的描述信息
    private String name;

    public Demo(String name) {
        this.name = name;
    }

    public void printName() {
        System.out.println(this.name);
    }
}