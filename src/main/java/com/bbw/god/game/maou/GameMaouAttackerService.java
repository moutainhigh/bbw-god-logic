package com.bbw.god.game.maou;

import com.bbw.common.DateUtil;
import com.bbw.db.redis.RedisHashUtil;
import com.bbw.god.activity.IActivity;
import com.bbw.god.game.maou.cfg.CfgGameMaou;
import com.bbw.god.game.maou.cfg.GameMaouTool;
import com.bbw.god.game.maou.cfg.GameMaouType;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.privilege.Privilege;
import com.bbw.god.gameuser.privilege.PrivilegeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 参与者相关接口
 *
 * @author: suhq
 * @date: 2021/12/17 10:03 上午
 */
@Service
public class GameMaouAttackerService {

    @Autowired
    private RedisHashUtil<Long, GameMaouAttacker> gameMaouAttackerRedisUtil;
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private PrivilegeService privilegeService;

    /**
     * 获取攻击者信息
     *
     * @param uid
     * @return
     */
    public GameMaouAttacker getOrCreateAttacker(long uid, IActivity activity) {
        //跨服魔王玩家攻击记录
        String maouAttackerKey = GameMaouRedisKeys.getMaouAttackerRedisKey(activity);
        GameMaouAttacker attacker = gameMaouAttackerRedisUtil.getField(maouAttackerKey, uid);
        //获得总攻打次数
        GameMaouType gameMaouType = GameMaouType.fromActivity(activity.gainType());
        int newTotalAttackTimes = getTotalAttackTimes(uid, gameMaouType);
        //没有攻击信息则创建
        if (null == attacker) {
            attacker = GameMaouAttacker.getInstance(uid, gameMaouType, newTotalAttackTimes);
            gameMaouAttackerRedisUtil.putField(maouAttackerKey, uid, attacker);
            return attacker;
        }
        //每日刷新次数
        boolean isMultiDaySinceLastAttack = DateUtil.getDaysBetween(attacker.getLastAttackTime(), DateUtil.now()) > 0;
        boolean isAttackTimesFull = attacker.getRemainAttackTimes().intValue() == attacker.getTotalAttackTimes();
        if (isMultiDaySinceLastAttack && !isAttackTimesFull) {
            attacker.setRemainAttackTimes(attacker.getTotalAttackTimes());
            gameMaouAttackerRedisUtil.putField(maouAttackerKey, uid, attacker);
        }
        //是否有新的灵印次数
        boolean hasNewExtraTimes = newTotalAttackTimes > attacker.getTotalAttackTimes();
        if (!hasNewExtraTimes) {
            return attacker;
        }
        //添加额外攻击次数
        attacker.addExtraAttackTimes(newTotalAttackTimes);
        gameMaouAttackerRedisUtil.putField(maouAttackerKey, uid, attacker);
        return attacker;
    }

    /**
     * 更新攻击者信息
     *
     * @param attacker
     */
    public void updateAttacker(GameMaouAttacker attacker, IActivity activity) {
        String maouAttackerKey = GameMaouRedisKeys.getMaouAttackerRedisKey(activity);
        long uid = attacker.getUid();
        gameMaouAttackerRedisUtil.putField(maouAttackerKey, uid, attacker);
    }

    /**
     * 获得总攻打次数
     *
     * @param uid
     * @return
     */
    private int getTotalAttackTimes(long uid, GameMaouType gameMaouType) {
        List<Privilege> privileges = privilegeService.getPrivileges(uid);
        CfgGameMaou maouConfig = GameMaouTool.getMaouConfig(gameMaouType);
        int totalTime = maouConfig.getFreeTimes();
        if (privileges.contains(Privilege.DilingYin)) {
            totalTime += maouConfig.getExtraTimesForDiLY();
        }
        if (privileges.contains(Privilege.TianlingYin)) {
            totalTime += maouConfig.getExtraTimesForTingLY();
        }
        return totalTime;
    }
}
