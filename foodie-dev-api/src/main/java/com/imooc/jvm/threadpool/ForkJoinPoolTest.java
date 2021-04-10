package com.imooc.jvm.threadpool;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

/**
 * ForkJoinPool测试: 实现1到100的求和
 */
public class ForkJoinPoolTest {

    public static void main(String[] args) {
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        ForkJoinTask<Integer> task = forkJoinPool.submit(new MyTask(1, 100));
        try {
            Integer sum = task.get();
            System.err.println(sum);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        // 底层使用ForkJoinPool实现
//        new ArrayList<>().parallelStream()
    }
}

class MyTask extends RecursiveTask<Integer> {

    // 当前任务计算的起始
    private int start;
    // 当前任务计算的结束
    private int end;
    // 阈值, 如果end-start在阈值以内, 那么就不用再细分任务了
    public static final int threshold = 2;

    @Override
    protected Integer compute() {
        int sum = 0;
        boolean needFork = (end - start) > threshold;
        if(needFork) {
            int middle = (start + end) / 2;
            MyTask leftTask = new MyTask(start, middle);
            MyTask rightTask = new MyTask(middle+1, end);

            // 执行子任务
            leftTask.fork();
            rightTask.fork();

            // 合并子任务结果
            Integer leftResult = leftTask.join();
            Integer rightResult = rightTask.join();
            sum = leftResult + rightResult;
        }
        // 任务足够细, 不需要再细分, 则直接相加
        else {
            for (int i = start; i <= end; i++) {
                sum = sum + i;
            }    
        }

        return sum;
    }

    public MyTask(int start, int end) {
        this.start = start;
        this.end = end;
    }
}