package com.bbw.god.game.maou;

import com.bbw.db.redis.RedisHashUtil;
import com.bbw.god.activity.IActivity;
import com.bbw.god.game.award.AwardStatus;
import com.bbw.god.game.maou.cfg.CfgGameMaou;
import com.bbw.god.game.maou.cfg.GameMaouTool;
import com.bbw.god.game.maou.cfg.GameMaouType;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 参与者相关接口
 *
 * @author: suhq
 * @date: 2021/12/17 10:03 上午
 */
@Service
public class GameMaouJoinerService {
    @Autowired
    private RedisHashUtil<Long, Integer> gameMaouJoinerRedisUtil;
    @Autowired
    private GameUserService gameUserService;

    /**
     * 获取所有的参与者
     *
     * @param turn
     * @return
     */
    public Map<Long, Integer> getAllJoiners(IActivity activity, int turn) {
        String maouJoinerKey = GameMaouRedisKeys.getMaouTurnJoinerKey(activity, turn);
        return gameMaouJoinerRedisUtil.get(maouJoinerKey);
    }

    /**
     * 更新参与者的状态
     *
     * @param uid
     * @param turn
     * @param awardStatus
     */
    public void updateJoinerStatus(long uid, IActivity activity, int turn, AwardStatus awardStatus) {
        String maouJoinerKey = GameMaouRedisKeys.getMaouTurnJoinerKey(activity, turn);
        gameMaouJoinerRedisUtil.putField(maouJoinerKey, uid, awardStatus.getValue());
    }

    /**
     * 加入本轮魔王参与者集合,用于发放魔王击杀奖励
     *
     * @param uid
     * @param turn
     */
    public void join(long uid, IActivity activity, int turn) {
        String maouJoinerKey = GameMaouRedisKeys.getMaouTurnJoinerKey(activity, turn);
        if (gameMaouJoinerRedisUtil.hasField(maouJoinerKey, uid)) {
            return;
        }
        GameUser gu = gameUserService.getGameUser(uid);
        GameMaouType gameMaouType = GameMaouType.fromActivity(activity.gainType());
        CfgGameMaou maouConfig = GameMaouTool.getMaouConfig(gameMaouType);
        if (gu.getLevel() >= maouConfig.getKillAwardsNeedLevel()) {
            updateJoinerStatus(uid, activity, turn, AwardStatus.ENABLE_AWARD);
            return;
        }
        updateJoinerStatus(uid, activity, turn, AwardStatus.UNAWARD);
    }
}
