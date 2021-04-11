package com.imooc.jvm.jvm;

/**
 * 测试编译优化机制: 逃逸分析测试 & 标量替换
 */
class EscapeTest1 {

    public static SomeClass someClass;

    // 全局变量赋值逃逸
    public void globalVariablePointerEscape() {
        someClass = new SomeClass();
    }

    // 方法返回值逃逸
    // someMethod(){
    //   SomeClass someClass = methodPointerEscape();
    // }
    public SomeClass methodPointerEscape() {
        return new SomeClass();
    }

    // 实例引用传递逃逸
    public void instancePassPointerEscape() {
        this.methodPointerEscape().printClassName(this);
    }

    public void someTest() {
        // someTest没有逃逸时, 且可以进一步分解, 则可以进行标量替换
        SomeTest someTest = new SomeTest();
        someTest.age = 1;
        someTest.id = 1;

        // 开启标量替换之后, 上述代码会被优化成:
        int age = 1;
        int id = 1;
    }
}

class SomeClass {
    public void printClassName(EscapeTest1 escapeTest1) {
        System.out.println(escapeTest1.getClass().getName());
    }
}

class SomeTest {
    int id;
    int age;
}