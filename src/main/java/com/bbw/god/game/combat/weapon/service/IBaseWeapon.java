package com.bbw.god.game.combat.weapon.service;

import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.game.combat.data.Player;
import com.bbw.god.game.combat.data.RDTempResult;
import com.bbw.god.game.combat.data.param.PerformWeaponParam;

/**
 * @author：lwb
 * @date: 2020/11/25 9:28
 * @version: 1.0
 */
public interface IBaseWeapon {
    /**
     * 当前武器ID
     *
     * @return
     */
    int getWeaponId();
    /**
     * 是否是 武器（法宝）id对应的服务
     *
     * @param weaponId
     * @return
     */
    default boolean match(int weaponId) {
        return getWeaponId() == weaponId;
    }

    /**
     * 默认释放的总次数
     * @return
     */
    default int getPerformTotalTimes(){
        return 1;
    }

    /**
     * 回合内释放的次数
     * @return
     */
    default int getPerformRoundTimes(){
        return 1;
    }

    /**
     *
     *使用检查
     * @param pwp
     * @return
     */
    default void doCheckUseLimit(PerformWeaponParam pwp) {
        int wid = pwp.getWeaponId();
        Player performPlayer = pwp.getPerformPlayer();
        long totalUseTimes = performPlayer.sumWeaponUseTimes(wid);
        // %s法宝一场战斗只能使用%s次
        if (totalUseTimes >= getPerformTotalTimes()) {
            throw new ExceptionForClientTip("combat.weapon.limit.total",wid, getPerformTotalTimes());
        }
        long roundTimes = performPlayer.sumCurrentRoundWeaponEffectTimes(wid,pwp.getCombat().getRound());
        // %s法宝一回合只能使用%s次
        if (roundTimes >= getPerformRoundTimes()) {
            throw new ExceptionForClientTip("combat.weapon.limit.round", wid,getPerformRoundTimes());
        }
    }

    /**
     * 延后生效的效果  预先返回给客户端的部分
     * @param pwp
     * @return
     */
    default RDTempResult beforehandAttack(PerformWeaponParam pwp){
        return new RDTempResult();
    }
    /**
     * 取整：抹去小数
     * @param b
     * @return
     */
    default int getInt(Double b) {
        return b.intValue();
    }
}
