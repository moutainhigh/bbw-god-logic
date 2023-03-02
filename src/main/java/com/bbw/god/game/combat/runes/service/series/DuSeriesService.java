package com.bbw.god.game.combat.runes.service.series;

import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.skill.BattleSkillSeriesTable;
import org.springframework.stereotype.Service;

/**
 * 毒系服务
 *
 * @author: suhq
 * @date: 2021/11/16 2:06 下午
 */
@Service
public class DuSeriesService extends SeriesService {
    DuSeriesService() {
        this.series = BattleSkillSeriesTable.DU_SERIES;
    }

    @Override
    void doAddInjure(int runeId, Effect effect, double addRate, CombatRunesParam param, Action action) {
        CardValueEffect valueEffect = effect.toValueEffect();
        valueEffect.setRoundHp((int) (valueEffect.getRoundHp() * (1 + addRate)));
        action.setTakeEffect(true);
    }

    @Override
    void doAddInjure(int runeId, Effect effect, int addValue, CombatRunesParam param, Action action) {
        CardValueEffect valueEffect = effect.toValueEffect();
        valueEffect.setRoundHp(valueEffect.getRoundHp() - addValue);
        action.setTakeEffect(true);
    }

    @Override
    void doDeductInjure(int runeId, Effect effect, double deductRate, CombatRunesParam param, Action action) {
        CardValueEffect valueEffect = effect.toValueEffect();
        valueEffect.setRoundHp((int) (valueEffect.getRoundHp() * (1 - deductRate)));
        action.setTakeEffect(true);
    }
}
