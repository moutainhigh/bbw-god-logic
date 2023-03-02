package com.bbw.god.game.maou;

import com.bbw.common.DateUtil;
import com.bbw.god.activity.IActivity;
import com.bbw.god.game.data.redis.GameRedisKey;
import com.bbw.god.game.maou.cfg.GameMaouType;

import static com.bbw.god.game.data.redis.RedisKeyConst.SPLIT;

/**
 * 跨服魔王Redis对应的key
 *
 * @author: suhq
 * @date: 2021/12/15 5:48 下午
 */
public class GameMaouRedisKeys {
    /**
     * 跨服魔王信息对应的key
     *
     * @param activity
     * @return
     */
    public static String getMaouInfoRedisKey(IActivity activity) {
        GameMaouType maouType = GameMaouType.fromActivity(activity.gainType());
        StringBuilder sb = new StringBuilder();
        sb.append(GameRedisKey.PREFIX);
        sb.append(GameRedisKey.SPLIT);
        sb.append("maou" + maouType.getValue());
        sb.append(GameRedisKey.SPLIT);
        sb.append(DateUtil.toDateTimeLong(activity.gainBegin()));
        return sb.toString();
    }

    /**
     * 跨服魔王玩家攻击记录的key
     *
     * @param activity
     * @return
     */
    public static String getMaouAttackerRedisKey(IActivity activity) {
        return getMaouInfoRedisKey(activity) + GameRedisKey.SPLIT + "attackers";
    }

    /**
     * 某一轮的魔王的信息
     *
     * @param activity
     * @param turn
     * @return
     */
    public static String getMaouTurnKey(IActivity activity, int turn) {
        return getMaouInfoRedisKey(activity) + GameRedisKey.SPLIT + "turn" + GameRedisKey.SPLIT + turn;
    }

    /**
     * 跨服魔王某一轮的参与者记录
     *
     * @param activity
     * @param turn
     * @return
     */
    public static String getMaouTurnJoinerKey(IActivity activity, int turn) {
        return getMaouTurnKey(activity, turn) + GameRedisKey.SPLIT + "joiners";
    }

    /**
     * 获取魔王锁的key
     *
     * @param activity
     * @return
     */
    public static String getMaouLockKey(IActivity activity) {
        String maouInfoRedisKey = GameMaouRedisKeys.getMaouInfoRedisKey(activity);
        return maouInfoRedisKey + SPLIT + "lock";
    }
}
