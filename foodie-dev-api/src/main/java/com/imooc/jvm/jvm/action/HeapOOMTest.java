package com.imooc.jvm.jvm.action;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 堆内存溢出测试: -Xms20m -Xmx20m -XX:+HeapDumpOnOutOfMemoryError
 */
public class HeapOOMTest {
    private List<String> oomList = new ArrayList<>();

    public static void main(String[] args) {
        HeapOOMTest oomTest = new HeapOOMTest();
        while (true) {
            oomTest.oomList.add(UUID.randomUUID().toString());
        }
    }
}

