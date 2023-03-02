package com.bbw.god.game.maou;

import com.bbw.common.DateUtil;
import com.bbw.common.lock.RedisLockUtil;
import com.bbw.db.redis.RedisHashUtil;
import com.bbw.god.activity.IActivity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 跨服魔王信息服务
 *
 * @author: suhq
 * @date: 2021/12/15 5:37 下午
 */
@Service
public class GameMaouInfoService {
    /** 记录魔王轮次 */
    private static String TURN_FIELD = "turn";
    /** 记录跨服魔王结束时间 */
    private static String END_TIME = "endTime";
    /** 记录本轮魔王剩余血量 */
    private static String REMAIN_BLOOD_FIELD = "remainBloodThisTurn";
    /** 记录本轮魔王开始时间 */
    private static String BEGIN_TIME = "beginTimeThisTurn";
    /** 初始魔王轮次 */
    private static Long INIT_TURN = 1L;

    @Autowired
    private RedisHashUtil<String, Long> gameMaouRedisUtil;
    @Autowired
    private RedisLockUtil redisLockUtil;


    /**
     * 是否已存在有效的魔王实例
     *
     * @param activity
     * @return
     */
    public boolean hasMaou(IActivity activity) {
        String redisKey = GameMaouRedisKeys.getMaouInfoRedisKey(activity);
        Long endTime = gameMaouRedisUtil.getField(redisKey, END_TIME);
        if (null == endTime) {
            return false;
        }
        long now = DateUtil.toDateTimeLong();
        if (now >= endTime) {
            return false;
        }
        return true;
    }

    /**
     * 初始化(加锁，线程安全)
     *
     * @param activity
     * @param initBlood
     */
    public void init(IActivity activity, long initBlood) {
        String lockKey = GameMaouRedisKeys.getMaouLockKey(activity);
        redisLockUtil.doSafe(lockKey, 10, tmp -> {
            if (hasMaou(activity)) {
                return true;
            }
            String redisKey = GameMaouRedisKeys.getMaouInfoRedisKey(activity);
            Map<String, Long> field = getMaouInfoToSave(initBlood, activity.gainEnd());
            gameMaouRedisUtil.putAllField(redisKey, field);
            return true;
        });

    }


    /**
     * 下一轮
     *
     * @param activity
     * @param initBlood 初始血量
     * @param beginTime 新的开始时间 yyyyMMddHHmmss
     */
    public void toNextTurn(IActivity activity, long initBlood, long beginTime) {
        String redisKey = GameMaouRedisKeys.getMaouInfoRedisKey(activity);
        int nextTurn = getCurTurn(activity) + 1;
        gameMaouRedisUtil.putField(redisKey, TURN_FIELD, (long) nextTurn);
        gameMaouRedisUtil.putField(redisKey, REMAIN_BLOOD_FIELD, initBlood);
        gameMaouRedisUtil.putField(redisKey, BEGIN_TIME, beginTime);
    }

    /**
     * 获得当前轮次
     *
     * @return
     */
    public Integer getCurTurn(IActivity activity) {
        String redisKey = GameMaouRedisKeys.getMaouInfoRedisKey(activity);
        Number turn = gameMaouRedisUtil.getField(redisKey, TURN_FIELD);
        return null == turn ? INIT_TURN.intValue() : turn.intValue();
    }

    /**
     * 获得剩余血量
     *
     * @return
     */
    public Integer getRemainBlood(IActivity activity) {
        String redisKey = GameMaouRedisKeys.getMaouInfoRedisKey(activity);
        Number remainBlood = gameMaouRedisUtil.getField(redisKey, REMAIN_BLOOD_FIELD);
        return remainBlood.intValue();

    }

    /**
     * 增加失血量
     *
     * @param activity
     * @param remainBlood
     * @return 返回加后的值
     */
    public void updateBlood(IActivity activity, long remainBlood) {
        String redisKey = GameMaouRedisKeys.getMaouInfoRedisKey(activity);
        this.gameMaouRedisUtil.putField(redisKey, REMAIN_BLOOD_FIELD, remainBlood);
    }

    /**
     * 跨服魔王可以开始攻击的时间
     *
     * @return
     */
    public Date getAttackBeginDate(IActivity activity) {
        String redisKey = GameMaouRedisKeys.getMaouInfoRedisKey(activity);
        Long beginTime = gameMaouRedisUtil.getField(redisKey, BEGIN_TIME);
        return DateUtil.fromDateLong(beginTime);
    }

    /**
     * 来魔王开始还有多少时间（ms）
     *
     * @return
     */
    public long getRemainTimeToBegin(IActivity activity) {
        Date attackBeginDate = getAttackBeginDate(activity);
        long remainTimeToBegin = attackBeginDate.getTime() - System.currentTimeMillis();
        return remainTimeToBegin > 0 ? remainTimeToBegin : 0L;
    }

    /**
     * 获取要保存的魔王的信息
     *
     * @param initBlood
     * @param maouEndDate
     * @return
     */
    private Map<String, Long> getMaouInfoToSave(long initBlood, Date maouEndDate) {
        Map<String, Long> field = new HashMap<>();
        field.put(TURN_FIELD, INIT_TURN);
        field.put(REMAIN_BLOOD_FIELD, initBlood);
        field.put(BEGIN_TIME, DateUtil.toDateTimeLong());
        field.put(END_TIME, DateUtil.toDateTimeLong(maouEndDate));
        return field;
    }
}
