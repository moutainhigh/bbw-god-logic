package com.bbw.god.game.combat.runes.service.impl;

import com.bbw.god.game.combat.data.param.CPlayerInitParam;
import com.bbw.god.game.combat.data.param.CombatPVEParam;
import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.combat.runes.service.IParamInitStageRunes;
import org.springframework.stereotype.Service;

/**
 * 地魔符131590紫：敌方全体卡牌的5级技能无效
 *
 * @author: suhq
 * @date: 2021/9/25 7:50 上午
 */
@Service
public class Runes131590 implements IParamInitStageRunes {

    @Override
    public int getRunesId() {
        return RunesEnum.DI_MO.getRunesId();
    }


    @Override
    public void doParamInitRunes(CPlayerInitParam performInitParam, CPlayerInitParam oppInitParam, CombatPVEParam pveParam) {
        oppInitParam.getCards().forEach(p -> {
            p.getSkills().remove(1);
            p.getSkills().add(1, 0);
        });
    }
}
