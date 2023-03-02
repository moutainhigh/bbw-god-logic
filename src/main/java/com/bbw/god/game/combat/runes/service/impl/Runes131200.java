package com.bbw.god.game.combat.runes.service.impl;

import com.bbw.common.ListUtil;
import com.bbw.god.game.combat.data.CombatBuff;
import com.bbw.god.game.combat.data.param.CPlayerInitParam;
import com.bbw.god.game.combat.data.param.CombatPVEParam;
import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.combat.runes.service.IParamInitStageRunes;
import org.springframework.stereotype.Service;

/**
 * 挑离符 禁用敌方所有组合技。
 *
 * @author: suhq
 * @date: 2021/12/29 9:48 上午
 */
@Service
public class Runes131200 implements IParamInitStageRunes {
    @Override
    public int getRunesId() {
        return RunesEnum.TIAO_LI.getRunesId();
    }

    @Override
    public void doParamInitRunes(CPlayerInitParam performInitParam, CPlayerInitParam oppInitParam, CombatPVEParam pveParam) {
        if (ListUtil.isEmpty(oppInitParam.getBuffs())) {
            return;
        }
        CombatBuff combatBuff = oppInitParam.gainBuff(getRunesId());
        if (null == combatBuff) {
            return;
        }
        combatBuff.setToPerform(true);
    }
}
