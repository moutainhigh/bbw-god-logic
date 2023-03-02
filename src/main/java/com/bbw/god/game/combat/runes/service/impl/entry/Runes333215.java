package com.bbw.god.game.combat.runes.service.impl.entry;

import com.bbw.god.game.combat.data.CombatBuff;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.combat.runes.service.AbstractSkillSeriesRunes;
import com.bbw.god.game.combat.runes.service.series.DuSeriesService;
import com.bbw.god.game.combat.runes.service.series.SeriesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 毒爆词条 我方卡牌受到毒系技能伤害时，将会立即受到该技能的[500]%效果并清除中毒状态。
 * 该效果将在卡牌受到本次毒系技能的伤害后进行；毒爆伤害=毒系伤害*500%。
 *
 * @author longwh
 * @date 2023/1/5 15:31
 */
@Service
public class Runes333215 extends AbstractSkillSeriesRunes {
    @Autowired
    private DuSeriesService duSeriesService;

    @Override
    public int getRunesId() {
        return RunesEnum.DU_BAO_ENTRY.getRunesId();
    }

    @Override
    public SeriesService getSeriesService() {
        return duSeriesService;
    }

    @Override
    public List<Effect> getDealEffects(CombatRunesParam param, Effect effect, BattleCard playerCard) {
        List<Effect> effects = new ArrayList<>();
        // 添加受到该技能伤害[500]% 毒爆效果
        CombatBuff combatBuff = param.getPerformPlayer().gainBuff(getRunesId());
        int effectValue = Math.max(-effect.toValueEffect().getRoundHp(), -effect.toValueEffect().getHp());
        double hp = 5 * combatBuff.getLevel() * effectValue;
        CardValueEffect buffEffect = CardValueEffect.getSkillEffect(getRunesId(), playerCard.getPos());
        buffEffect.setHp((int) -hp);
        buffEffect.setSequence(param.getNextSeq());
        effects.add(buffEffect);
        // 解除中毒状态
        effect.toValueEffect().setValueType(CardValueEffect.CardValueEffectType.IN_TIME);
        return effects;
    }


}