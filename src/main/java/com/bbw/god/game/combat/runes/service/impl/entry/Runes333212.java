package com.bbw.god.game.combat.runes.service.impl.entry;

import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.combat.runes.service.AbstractSkillSeriesRunes;
import com.bbw.god.game.combat.runes.service.series.JuSeriesService;
import com.bbw.god.game.combat.runes.service.series.SeriesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 致命词条 我方卡牌受到狙系技能伤害时，若减少后的防御值小于等于受到的伤害则立即死亡。
 *
 * @author longwh
 * @date 2023/1/5 9:48
 */
@Service
public class Runes333212 extends AbstractSkillSeriesRunes {
    @Autowired
    private JuSeriesService juSeriesService;

    @Override
    public int getRunesId() {
        return RunesEnum.ZHI_MING_ENTRY.getRunesId();
    }

    @Override
    public SeriesService getSeriesService() {
        return juSeriesService;
    }

    @Override
    public List<Effect> getDealEffects(CombatRunesParam param, Effect effect, BattleCard playerCard) {
        List<Effect> effects = new ArrayList<>();
        // 伤害值
        int effectValue = Math.max(-effect.toValueEffect().getRoundHp(), -effect.toValueEffect().getHp());
        // 减少后的防御值
        int deRoundHp = playerCard.getRoundHp() - effectValue;
        // 小于等于受到的伤害则立即死亡
        if (deRoundHp <= effectValue) {
            CardValueEffect buffEffect = CardValueEffect.getSkillEffect(getRunesId(), playerCard.getPos());
            buffEffect.setHp(-deRoundHp);
            buffEffect.setSequence(param.getNextSeq());
            effects.add(buffEffect);
        }
        return effects;
    }
}