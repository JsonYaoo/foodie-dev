package com.imooc.jvm.jvm.action;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.concurrent.*;

/**
 * 项目越跑越慢分析测试: 这个例子是因为线程争抢过于频繁导致的变慢
 */
public class SlowTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(SlowTest.class);
    private static final ScheduledThreadPoolExecutor SCHEDULER = new ScheduledThreadPoolExecutor(10,
        // 优化点2: 定义线程名称, 方便定位问题 => jstack
        new ThreadFactoryBuilder().setNameFormat("my-scheduler-%d").build(),
        new ThreadPoolExecutor.AbortPolicy()
    );

    public void schedule() {
        SCHEDULER.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                deal();
            }
            // 1. 这里每1秒执行1次定时任务，是为了迅速获得效果
            // 2. 实际项目中，定时任务的时间可以从数据库里获得
        }, 0, 1, TimeUnit.SECONDS);
    }

    private void deal() {
        // 优化点3: 应该是一个定时任务一个线程池, 而不是说一个定时任务创建一大堆的线程池
        // 模拟某公司有10个园区，每个园区弄1个线程池去执行
        for (int campusNo = 1; campusNo <= 10; campusNo++) {
            ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                10,
                10,
                10L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(100),
                // 优化点2: 定义线程名称, 方便定位问题
                new ThreadFactoryBuilder().setNameFormat("my-thread-pool-%d").build(),
                new ThreadPoolExecutor.AbortPolicy()
            );
            try {
                // 模拟每个园区有8幢楼(实际项目中是查库获得)
                for (int buildingNo = 1; buildingNo <= 8; buildingNo++) {
                    int finalBuildingNo = buildingNo;
                    int finalCampusNo = campusNo;
                    threadPoolExecutor.submit(new Callable<String>() {
                        @Override
                        public String call() throws Exception {
                            long begin = System.currentTimeMillis();
                            // 模拟查询出1幢楼里有1000个灯
                            for (int lightNoInBuilding = 1; lightNoInBuilding <= 1000; lightNoInBuilding++) {
                                LOGGER.debug(
                                    "给{}号园区{}号楼中下标为{}的灯发送开灯信号...",
                                    finalCampusNo,
                                    finalBuildingNo,
                                    lightNoInBuilding
                                );

                                // 这里读取一个文件，是为了模拟和灯通信
                                // 这里的文件大小在200KB左右
                                // 同学们测试时，得改下这个路径
//                                byte[] bytes = FileUtils.readFileToByteArray(
//                                    new File("/Users/itmuch.com/test.txt")
//                                );

                                // 每操作100盏灯之后，停顿100ms再继续
                                if (lightNoInBuilding % 100 == 0) {
                                    Thread.sleep(10L);
                                }
                            }
                            LOGGER.info("{} 操作1幢楼的灯所耗费时间：{}", Thread.currentThread().getName(), System.currentTimeMillis() - begin);
                            return "success";
                        }
                    });
                }
            } finally {
                // 优化点1: 用完时及时关闭线程池
                threadPoolExecutor.shutdown();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        new SlowTest().schedule();
        Thread.sleep(Integer.MAX_VALUE);
    }
}
