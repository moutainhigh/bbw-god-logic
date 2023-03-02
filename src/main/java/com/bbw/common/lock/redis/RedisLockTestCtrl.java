package com.bbw.common.lock.redis;

import com.bbw.god.controller.AbstractController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * 分布式锁测试接口
 *
 * @author: suhq
 * @date: 2022/1/4 2:56 下午
 */
@Deprecated
@RestController
public class RedisLockTestCtrl extends AbstractController {
    @Autowired
    private RedisLockTestService redisLockTestService;

    @GetMapping("coder/testRedisLock")
    public String testRedisLock() throws InterruptedException {
        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(30);
        //无锁测试
        for (int i = 0; i < 5; i++) {
            executor.execute(() -> {
                String result = redisLockTestService.testWithoutLock(1234567L);
                System.out.println(Thread.currentThread().getName() + " testWithoutRedisLock result:" + result);
            });
        }
        Thread.sleep(3000L);

        //有锁测试
        for (int i = 0; i < 5; i++) {
            executor.execute(() -> {
                String result = redisLockTestService.testLock(1234567L);
                System.out.println(Thread.currentThread().getName() + " testWithRedisLock result:" + result);
            });
        }
        Thread.sleep(3000L);

        //加锁+spel解析map测试
        Map<String, Integer> param = new HashMap<>();
        param.put("maou", 220105);
        for (int i = 0; i < 5; i++) {
            executor.execute(() -> {
                String result = redisLockTestService.testLockWithMapSpel(param);
                System.out.println(Thread.currentThread().getName() + " testSpelWithRedisLock result:" + result);
            });
        }
        Thread.sleep(3000L);

        //加锁+spel解析自定义类测试
        for (int i = 0; i < 5; i++) {
            executor.execute(() -> {
                String result = redisLockTestService.testLockWithObjSpel(new RedisLockTestService.Param("coder", 100L));
                System.out.println(Thread.currentThread().getName() + " testSpelWithRedisLock result:" + result);
            });
        }

        return "";
    }
}
