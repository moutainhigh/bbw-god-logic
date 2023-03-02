package com.bbw.god.game.award.giveback;

import com.bbw.db.redis.RedisValueUtil;
import com.bbw.god.game.data.redis.RedisKeyConst;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collection;

/**
 * 待返还的奖励池
 *
 * @author: suhq
 * @date: 2022/5/26 2:51 下午
 */
@Slf4j
public abstract class AbstractGiveBackPool {
    /** 保存时间超过该值的为慢日志，并记录日志 */
    private static final int SLOW_LOG_TIME = 2000 * 1;
    /** 缓冲池数量 */
    private static final int POOL_SIZE = 2;
    /** 基础键值 */
    protected static final String BASE_KEY = RedisKeyConst.RUNTIME_KEY + RedisKeyConst.SPLIT + "giveBackAwardPool";
    /** 数据池增长序列，用来切换数据池 */
    private static final String SEQ_KEY = RedisKeyConst.SPLIT + "seq";
    /** 上一个缓冲池序列 */
    private static final String LAST_SEQ_KEY = RedisKeyConst.SPLIT + "seqlast";

    /** 缓冲池系列 */
    @Autowired
    private RedisValueUtil<Integer> valueRedis;
    @Autowired
    private GiveBackPoolStatusService giveBackPoolStatusService;

    /**
     * 返还池类型，一种类型对应一个池
     *
     * @return
     */
    public abstract String getPoolType();

    /**
     * 返还奖励
     */
    protected abstract void doGiveBack();


    /**
     * 将数据池的对象持久化到数据库
     */
    public void giveBack() {
        try {
            //返还中
            if (giveBackPoolStatusService.isSaving(getBaseKey())) {
                return;
            }
            //切换到新的数据池
            this.switchPool();

            //标志为返还中
            giveBackPoolStatusService.setSaving(getBaseKey());

            Long begin = System.currentTimeMillis();

            //返还
            doGiveBack();

            Long end = System.currentTimeMillis();
            if (end - begin > SLOW_LOG_TIME) {
                log.error("返还奖励（" + getPoolType() + "），总耗时：" + (end - begin));
            } else {
                log.info("返还奖励（" + getPoolType() + "），总耗时：" + (end - begin));
            }
        } finally {
            giveBackPoolStatusService.setNotSaving(getBaseKey());
        }
    }

    /**
     * 返回基础键值
     *
     * @return
     */
    protected String getBaseKey() {
        return BASE_KEY + RedisKeyConst.SPLIT + getPoolType();
    }

    /**
     * 获得放待返还奖励的key
     *
     * @return
     */
    protected String getKey() {
        return getBaseKey() + RedisKeyConst.SPLIT + this.getCurrentPoolSeq();
    }

    /** 当前序列的KEY */
    private String getSeqKey() {
        return getBaseKey() + SEQ_KEY;
    }

    /** 上一个缓冲池序列的key */
    private String getLastSeqKey() {
        return getBaseKey() + LAST_SEQ_KEY;
    }

    /** 当前序列的值 */
    private int getCurrentPoolSeq() {
        Integer value = valueRedis.get(getSeqKey());
        if (null == value) {
            value = 1;
            valueRedis.set(getSeqKey(), value);
        }
        int rtn = (int) (value % POOL_SIZE) + 1;
        return rtn;
    }

    /** 上一个缓冲池序列的值 */
    private int getLastPoolSeq() {
        Integer value = valueRedis.get(getLastSeqKey());
        if (null == value) {
            value = 1;
            valueRedis.set(getSeqKey(), value);
        }
        int rtn = (int) (value % POOL_SIZE) + 1;
        return rtn;
    }

    /**
     * 重置缓冲池增长序列值。1分钟保存1次，理论上可以用4000+年，可以不重置
     */
    public void resetPoolSeq() {
        int value = this.getCurrentPoolSeq();
        //保存当前缓冲池
        valueRedis.set(getLastSeqKey(), value);
        value++;
        valueRedis.set(getSeqKey(), value);
    }

    /**
     * 切换数据池
     */
    private void switchPool() {
        //保存当前缓冲池
        valueRedis.set(getLastSeqKey(), valueRedis.get(getSeqKey()));
        //自增长1
        valueRedis.increment(getSeqKey(), 1);
    }

    /**
     * 获取上一个缓冲池的key
     *
     * @return
     */
    protected String getLastPoolKey() {
        String lastPoolKey = this.getBaseKey() + RedisKeyConst.SPLIT + this.getLastPoolSeq();
        return lastPoolKey;
    }

    @Getter
    public static class GiveBackResult<T> {
        @Setter
        private int successSize = 0;
        private ArrayList<T> failure = new ArrayList<>();

        public void failureAdd(T e) {
            failure.add(e);
        }

        public void failureAdd(Collection<T> coll) {
            failure.addAll(coll);
        }
    }

}
