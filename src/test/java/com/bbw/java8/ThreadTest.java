package com.bbw.java8;

import com.bbw.common.PowerRandom;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ThreadTest {
    public static List<Long> uids = Arrays.asList(123456789L, 234567890L, 345678901L);

    @Test
    public void main() throws InterruptedException {
        final long begin = System.currentTimeMillis();
        ExecutorService exec = Executors.newCachedThreadPool();
        if (exec instanceof ThreadPoolExecutor) {
            ((ThreadPoolExecutor) exec).setKeepAliveTime(10, TimeUnit.SECONDS);
        }
        final List<String> results = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            final int index = i;
            exec.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(100);
                        String result = Thread.currentThread().getName() + ",index=" + index;
                        log.info(result + ",passTime:" + (System.currentTimeMillis() - begin));
                        results.add(result);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
//        exec.shutdown();
//        System.out.println(results);
        System.out.println("thread active num:" + ((ThreadPoolExecutor) exec).getActiveCount());
        System.out.println("thread pool size:" + ((ThreadPoolExecutor) exec).getPoolSize());
        System.out.println("thread task count:" + ((ThreadPoolExecutor) exec).getTaskCount());
        System.out.println("thread completed task count:" + ((ThreadPoolExecutor) exec).getCompletedTaskCount());
        System.out.println("thread keep alive time:" + ((ThreadPoolExecutor) exec).getKeepAliveTime(TimeUnit.SECONDS));
        Thread.sleep(1000);
        System.out.println("---------");
//        System.out.println(results);
        System.out.println("thread active num:" + ((ThreadPoolExecutor) exec).getActiveCount());
        System.out.println("thread pool size:" + ((ThreadPoolExecutor) exec).getPoolSize());
        System.out.println("thread task count:" + ((ThreadPoolExecutor) exec).getTaskCount());
        System.out.println("thread completed task count:" + ((ThreadPoolExecutor) exec).getCompletedTaskCount());
        System.out.println("thread keep alive time:" + ((ThreadPoolExecutor) exec).getKeepAliveTime(TimeUnit.SECONDS));
        Thread.sleep(11000);
        System.out.println("---------");
        System.out.println("thread active num:" + ((ThreadPoolExecutor) exec).getActiveCount());
        System.out.println("thread pool size:" + ((ThreadPoolExecutor) exec).getPoolSize());
        System.out.println("thread task count:" + ((ThreadPoolExecutor) exec).getTaskCount());
        System.out.println("thread completed task count:" + ((ThreadPoolExecutor) exec).getCompletedTaskCount());
        System.out.println("thread keep alive time:" + ((ThreadPoolExecutor) exec).getKeepAliveTime(TimeUnit.SECONDS));
        System.out.println("---------");
        final long begin2 = System.currentTimeMillis();
        for (int i = 500; i < 508; i++) {
            final int index = i;
            exec.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(100);
                        String result = Thread.currentThread().getName() + ",index=" + index;
                        log.info(result + ",passTime:" + (System.currentTimeMillis() - begin2));
                        results.add(result);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        exec.shutdown();
        System.out.println("---------");
        System.out.println("thread active num:" + ((ThreadPoolExecutor) exec).getActiveCount());
        System.out.println("thread pool size:" + ((ThreadPoolExecutor) exec).getPoolSize());
        System.out.println("thread task count:" + ((ThreadPoolExecutor) exec).getTaskCount());
        System.out.println("thread completed task count:" + ((ThreadPoolExecutor) exec).getCompletedTaskCount());
        System.out.println("thread keep alive time:" + ((ThreadPoolExecutor) exec).getKeepAliveTime(TimeUnit.SECONDS));
        Thread.sleep(41000);
        System.out.println("---------");
        System.out.println("thread active num:" + ((ThreadPoolExecutor) exec).getActiveCount());
        System.out.println("thread pool size:" + ((ThreadPoolExecutor) exec).getPoolSize());
        System.out.println("thread task count:" + ((ThreadPoolExecutor) exec).getTaskCount());
        System.out.println("thread completed task count:" + ((ThreadPoolExecutor) exec).getCompletedTaskCount());
        System.out.println("thread keep alive time:" + ((ThreadPoolExecutor) exec).getKeepAliveTime(TimeUnit.SECONDS));
    }

    public static class MyThread implements Runnable {
        private Integer id;
        // private Integer index;

        public MyThread(int id) {
            this.id = id;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(100);
                Long uid = PowerRandom.getRandomFromList(uids);
                log.info("-----begin {},{}-----", id, uid);
                synchronized (uid) {
                    log.info("begin thread-index-" + id);
                    Thread.sleep(2000);
                    log.info("end thread-index-" + id);
                }
                log.info("-----end {},{}-----", id, uid);
            } catch (Exception e) {
                // TODO: handle exception
            }

        }

    }
}
