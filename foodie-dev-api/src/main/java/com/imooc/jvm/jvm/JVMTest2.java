package com.imooc.jvm.jvm;

/**
 * 测试JVM: 类的加载机制
 */
public class JVMTest2 {

    private static final String CONST_FIELD = "AAA";
    private static String staticField;
    private String field;

    public String add() {
        return staticField + field + CONST_FIELD;
    }

    public static void main(String[] args) {
        new JVMTest2().add();
    }
}
