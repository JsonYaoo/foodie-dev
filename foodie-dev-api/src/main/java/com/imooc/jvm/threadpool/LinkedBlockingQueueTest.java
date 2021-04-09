package com.imooc.jvm.threadpool;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * LinkedBlockingQueue测试
 */
public class LinkedBlockingQueueTest {

    public static void main(String[] args) {
        LinkedBlockingQueue<Object> linkedBlockingQueue = new LinkedBlockingQueue<>(1);
        // 插入元素 => 操作失败时, 会抛出异常
        linkedBlockingQueue.add("abc");
//        linkedBlockingQueue.add("abc");// 这时会抛出IllegalStateException：Queue full

        // 插入元素 => 操作失败时, 不会抛出异常, 但会返回特殊值
        boolean offer = linkedBlockingQueue.offer("def");
        System.err.println(offer);

        // 插入元素 => 操作失败时, 不会抛出异常, 没有返回值, 但会一直阻塞
//        try {
//            linkedBlockingQueue.put("ghi");
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        // 插入元素 => 操作失败时, 不会抛出异常, 但设置了过期时间, 过期后会返回特殊值
//        try {
//            boolean offer1 = linkedBlockingQueue.offer("jkl", 5, TimeUnit.SECONDS);
//            System.err.println(offer1);// false
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        // 移除元素 => 操作失败时, 会抛出异常
        Object remove = linkedBlockingQueue.remove();
        System.err.println(remove);// "abc"
//        Object remove2 = linkedBlockingQueue.remove();// java.util.NoSuchElementException

        // 移除元素 => 操作失败时, 不会抛出异常, 但会返回特殊值
        Object poll = linkedBlockingQueue.poll();
        System.err.println(poll);// null

        // 移除元素 => 操作失败时, 不会抛出异常, 没有返回值, 但会一直阻塞
//        try {
//            linkedBlockingQueue.take();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        // 删除元素 => 操作失败时, 不会抛出异常, 但设置了过期时间, 过期后会返回特殊值
        try {
            Object poll1 = linkedBlockingQueue.poll(5, TimeUnit.SECONDS);
            System.err.println(poll);// null
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 检查元素 => 操作失败时, 会抛出异常
//        Object element = linkedBlockingQueue.element();
//        System.err.println(element);// java.util.NoSuchElementException

        // 检查元素 => 操作失败时, 不会抛出异常, 但会返回特殊值
        Object peek = linkedBlockingQueue.peek();
        System.err.println(peek);// null
    }
}
