package com.bbw.god.game.combat.runes.service.series;

import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.combat.skill.BattleSkillSeriesTable;
import org.springframework.stereotype.Service;

/**
 * 狙系服务
 *
 * @author: suhq
 * @date: 2021/11/16 2:06 下午
 */
@Service
public class JuSeriesService extends SeriesService {
    JuSeriesService() {
        this.series = BattleSkillSeriesTable.JU_SERIES;
    }

    @Override
    void doAddInjure(int runeId, Effect effect, double addRate, CombatRunesParam param, Action action) {
        CardValueEffect valueEffect = effect.toValueEffect();
        int skillId = effect.getPerformSkillID();
        if (skillId == CombatSkillEnum.SHUANG_JU.getValue()
                || skillId == CombatSkillEnum.FJ.getValue()
                || skillId == RunesEnum.JIAN_ZHEN_ENTRY.getRunesId()) {
            valueEffect.setRoundHp((int) (valueEffect.getRoundHp() * (1 + addRate)));
        } else {
            valueEffect.setHp((int) (valueEffect.getHp() * (1 + addRate)));
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
        valueEffect.setRoundHp((int) (valueEffect.getRoundHp() * (1 - deductRate)));
        action.setTakeEffect(true);
    }
}
