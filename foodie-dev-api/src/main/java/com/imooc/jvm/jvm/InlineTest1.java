package com.imooc.jvm.jvm;

/**
 * 测试编译优化机制: 方法内联
 */
public class InlineTest1 {

    private static int add1(int x1, int x2, int x3, int x4) {
        return add2(x1, x2) + add2(x3, x4);
    }

    private static int add2(int x1, int x2) {
        return x1 + x2;
    }

    // 内联后: 减少压栈和出栈的操作
    private static int addInline(int x1, int x2, int x3, int x4) {
        return x1 + x2 + x3 + x4;
    }
}
