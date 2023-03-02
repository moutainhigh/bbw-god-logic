package com.bbw.god.game.combat.attackstrategy.service;

import com.bbw.god.fight.FightTypeEnum;
import com.bbw.god.game.combat.CombatRedisService;
import com.bbw.god.game.combat.attackstrategy.StrategyEnum;
import com.bbw.god.game.combat.data.Combat;
import com.bbw.god.game.combat.data.CombatInfo;
import com.bbw.god.game.combat.video.RDVideo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 策略抽象类
 *
 * @author: suhq
 * @date: 2021/9/22 10:19 上午
 */
@Slf4j
public abstract class AbstractStrategyLogic {
    @Autowired
    private CombatRedisService combatRedisService;

    /**
     * 匹配战斗类型对应的策略逻辑
     *
     * @param fightType
     * @return
     */
    public abstract boolean matchFight(FightTypeEnum fightType);

    /**
     * 是否要保存该攻略
     *
     * @param combat
     * @param combatInfo
     * @return
     */
    protected abstract boolean isToSave(Combat combat, CombatInfo combatInfo);


    protected abstract void doSave(Combat combat, CombatInfo combatInfo);

    /**
     * 保存战斗到策略
     *
     * @param combat
     */
    public void logCombatToStrategy(Combat combat) {
        try {
            //非玩家即P1胜利的 不保存
            if (!combat.pveWinnerIsUser() || combat.getP1().getUid() < 0) {
                return;
            }
            CombatInfo combatInfo = combatRedisService.getCombatInfo(combat.getId());
            //信息缺失 或者 城池级别小于 需要记录的城池级别
            if (combatInfo == null) {
                return;
            }
            boolean toSave = isToSave(combat, combatInfo);
            if (!toSave) {
                return;
            }
            doSave(combat, combatInfo);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 保存和上传攻略
     *
     * @param entity
     */
    public abstract <T> void saveAndUpload(T entity);

    /**
     * 获取策略列表
     *
     * @param uid
     * @param gid
     * @param baseId
     * @param strategyEnum
     * @return
     */
    public abstract RDVideo listStrategy(long uid, int gid, int baseId, StrategyEnum strategyEnum);

    /**
     * 获取除了最新以外的攻城
     *
     * @param uid
     * @param gid
     * @param baseId
     * @return
     */
    public abstract RDVideo listBetterStrategy(long uid, int gid, int baseId);
}
