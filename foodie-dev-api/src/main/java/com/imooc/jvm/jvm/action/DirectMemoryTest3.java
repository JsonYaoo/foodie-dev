//package com.imooc.jvm.jvm.action;
//
//import sun.misc.Unsafe;
//
//import java.lang.reflect.Field;
//
///**
// * 直接内存溢出测试: 使用Unsafe来类测试分配内存 => Unsafe类在JDK11不能直接使用, 需要编写一个Module.java, 并在里面做一定的说明才能使用
// * => java.lang.OutOfMemoryError:
// *      1. Unsafe导致直接内存溢出报错没有小尾巴
// *      2. 设置最大直接内存大小: -XX:MaxDirectMemorySize=100m, 所以该配置对Unsafe不起作用
// */
//public class DirectMemoryTest3 {
//    private static final int GB_1 = 1024 * 1024 * 1024;
//
//    public static void main(String[] args) throws IllegalAccessException, NoSuchFieldException {
//        //通过反射获取Unsafe类并通过其分配直接内存
//        Field unsafeField = Unsafe.class.getDeclaredFields()[0];
//        unsafeField.setAccessible(true);
//        Unsafe unsafe = (Unsafe) unsafeField.get(null);
//
//        int i = 0;
//        while (true) {
//            unsafe.allocateMemory(GB_1);
//            System.out.println(++i);
//        }
//    }
//}