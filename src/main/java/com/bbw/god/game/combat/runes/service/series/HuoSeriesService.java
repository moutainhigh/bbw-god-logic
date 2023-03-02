package com.bbw.god.game.combat.runes.service.series;

import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.skill.BattleSkillSeriesTable;
import org.springframework.stereotype.Service;

/**
 * 火系服务
 *
 * @author: suhq
 * @date: 2021/11/16 11:14 上午
 */
@Service
public class HuoSeriesService extends SeriesService {

    HuoSeriesService() {
        this.series = BattleSkillSeriesTable.HUO_SERIES;
    }

    @Override
    void doAddInjure(int runeId, Effect effect, double addRate, CombatRunesParam param, Action action) {
        CardValueEffect valueEffect = effect.toValueEffect();
        if (runeId == CombatSkillEnum.ZHI_YAN.getValue() || runeId == CombatSkillEnum.HUO_QIU.getValue()) {
            valueEffect.setAtk((int) (valueEffect.getRoundAtk() * addRate));
            valueEffect.setHp((int) (valueEffect.getRoundHp() * addRate));
        } else {
            valueEffect.setHp((int) (valueEffect.getHp() * (1 + addRate)));
            valueEffect.setAtk((int) (valueEffect.getAtk() * (1 + addRate)));
        }
        action.setTakeEffect(true);
    }

    @Override
    void doAddInjure(int runeId, Effect effect, int addValue, CombatRunesParam param, Action action) {
        CardValueEffect valueEffect = effect.toValueEffect();
        valueEffect.setHp(valueEffect.getHp() - addValue);
        action.setTakeEffect(true);
    }

    @Override
    void doDeductInjure(int runeId, Effect effect, double deductRate, CombatRunesParam param, Action action) {
        CardValueEffect valueEffect = effect.toValueEffect();
        valueEffect.setHp((int) (valueEffect.getHp() * (1 - deductRate)));
        valueEffect.setAtk((int) (valueEffect.getAtk() * (1 - deductRate)));
        action.setTakeEffect(true);
    }
}
