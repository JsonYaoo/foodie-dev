package com.imooc.jvm.jvm.action;

/**
 * 栈内存溢出: StackOverflowError测试
 * 默认栈深度配置：19109
 * 栈深度配置测试: -Xss144k：1529 => 注意不能配成-Xss=144k
 */
public class StackOOMTest1 {
    private int stackLength = 1;

    private void stackLeak() {
        stackLength++;
        this.stackLeak();
    }

    public static void main(String[] args) {
        StackOOMTest1 oom = new StackOOMTest1();
        try {
            oom.stackLeak();
        } catch (Throwable e) {
            System.out.println("stack length:" + oom.stackLength);
            throw e;
        }
    }
}