package com.bbw.god.game.combat.weapon.service;

import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.param.PerformWeaponParam;

/**
 * 战斗法宝  立即生效法宝系列
 * @author：lwb
 * @date: 2020/11/25 9:26
 * @version: 1.0
 */
public interface IWeaponInTimeEffect extends IBaseWeapon{

    /**
     * 立即生效的效果
     * @param pwp
     * @return
     */
    Action takeInTimeAttack(PerformWeaponParam pwp);
}
