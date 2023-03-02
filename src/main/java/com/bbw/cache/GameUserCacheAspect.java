package com.bbw.cache;

import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.UserData;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 玩家数据本地缓存接口
 *
 * @author: suhq
 * @date: 2022/11/4 4:24 下午
 */
@Slf4j
@Aspect
@Component
public class GameUserCacheAspect {
    private static String CACHE_TYPE = "gameUserCache";
    /** 缓存最大有效期，用于保底过期删除 */
    private static int CACHE_MAX_VALID_SECONDS = 3600;
    private static ConcurrentHashMap<Long, AtomicInteger> seeds = new ConcurrentHashMap<>();

    /**
     * 获得玩家数据切片
     *
     * @return
     */
    @Around("execution(* com.bbw.god.gameuser.GameUserService.getGameUser(..)) ")
    public Object getGameUser(ProceedingJoinPoint point) throws Throwable {
        Object[] args = point.getArgs();
        long uid = (Long) args[0];
        GameUser gameUser = doGetFromCache(uid);
        int seed = getSeed(uid).get();
        //如果获取的数据为null,在获取一次
        if (null == gameUser || seed <= 0) {
            addSeed(uid);
//            log.error("=========={}getUserData获取null,或者seed(={})<0,重新获取。", uid, seed);
            gameUser = (GameUser) point.proceed();
            cacheData(gameUser);
        }
        //返回一个新的集合引用
        return gameUser;
    }

    /**
     * 获取seed
     *
     * @param uid
     * @return
     */
    public AtomicInteger getSeed(Long uid) {
        AtomicInteger seed = seeds.get(uid);
        if (null == seed) {
            seed = new AtomicInteger(0);
            seeds.put(uid, seed);
        }
        return seed;
    }

    /**
     * 获取seed
     *
     * @param uid
     * @return
     */
    public void addSeed(Long uid) {
        AtomicInteger seed = getSeed(uid);
        int seedValue = seed.incrementAndGet();
//        System.out.println("==========seed after add:" + seedValue);
    }

    /**
     * 获取seed
     *
     * @param uid
     * @return
     */
    public void deductSeed(Long uid) {
        AtomicInteger seed = getSeed(uid);
        int seedValue = seed.decrementAndGet();
        if (seedValue < 0) {
            seed.set(0);
        }
//        System.out.println("==========seed after deduct:" + seedValue);
    }

    /**
     * 从本地缓存获取数据
     *
     * @param uid
     * @return
     */
    private GameUser doGetFromCache(long uid) {
        String cacheKey = getCacheKey(uid);
        return LocalCache.getInstance().get(CACHE_TYPE, cacheKey);
    }

    /**
     * 移除缓存
     *
     * @param uid
     */
    public <T extends UserData> void removeCache(long uid) {
        String cacheKey = getCacheKey(uid);
        LocalCache.getInstance().remove(CACHE_TYPE, cacheKey);
    }

    /**
     * 缓存到本地缓存
     *
     * @param user
     */
    private void cacheData(GameUser user) {
        String cacheKey = getCacheKey(user.getId());
        LocalCache.getInstance().put(CACHE_TYPE, cacheKey, user, CACHE_MAX_VALID_SECONDS);
    }

    /**
     * 获得缓存key
     *
     * @param uid
     * @return
     */
    private <T extends UserData> String getCacheKey(long uid) {
        String key = "local_usr_" + uid + "_" + CACHE_TYPE;
        return key;
    }

}
