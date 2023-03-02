package com.bbw.common.lock.redis;

import com.bbw.common.lock.redis.annotation.RedisLock;
import com.bbw.common.lock.redis.annotation.RedisLockParam;
import com.bbw.db.redis.RedisValueUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 分布式锁测试
 *
 * @author: suhq
 * @date: 2022/1/4 2:23 下午
 */
@Slf4j
@Component
@Deprecated
public class RedisLockTestService {
    @Autowired
    private RedisValueUtil<Integer> redisValueUtil;


    public String testWithoutLock(long uid) {
        return doBusiness();
    }

    @RedisLock(key = "game:var:redisLockTest")
    public String testLock(@RedisLockParam long uid) {
        return doBusiness();
    }

    @RedisLock(key = "game:var:redisLockTest")
    public String testLockWithMapSpel(@RedisLockParam(spel = "maou") Map<String, Integer> param) {
        return doBusiness();
    }

    @RedisLock(key = "game:var:redisLockTest")
    public String testLockWithObjSpel(@RedisLockParam(spel = "userName") Param param) {
        return doBusiness();
    }

    private String doBusiness() {
        //        Integer.valueOf("rr4"); //模拟业务错误
        String key = "game:var:redisLockTestValue";
        Integer value = redisValueUtil.get(key);
        value = value + 1;
        redisValueUtil.set(key, value);
        return redisValueUtil.get(key) + "," + System.currentTimeMillis();
    }

    @Data
    @AllArgsConstructor
    public static class Param {
        private String userName;
        private Long uid;
    }

}
