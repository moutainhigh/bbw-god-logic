package com.bbw.god.game.combat.runes.service.impl.entry;

import com.bbw.god.game.combat.data.CombatBuff;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.combat.runes.service.AbstractSkillSeriesRunes;
import com.bbw.god.game.combat.runes.service.series.HuoSeriesService;
import com.bbw.god.game.combat.runes.service.series.SeriesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 烧伤词条 我方卡牌受到火系技能伤害时，本回合将减少[20]%攻击值。
 *
 * @author longwh
 * @date 2023/1/5 9:48
 */
@Service
public class Runes333213 extends AbstractSkillSeriesRunes {
    @Autowired
    private HuoSeriesService huoSeriesService;

    @Override
    public int getRunesId() {
        return RunesEnum.SHAO_SHANG_ENTRY.getRunesId();
    }

    @Override
    public SeriesService getSeriesService() {
        return huoSeriesService;
    }

    @Override
    public List<Effect> getDealEffects(CombatRunesParam param, Effect effect, BattleCard playerCard) {
        List<Effect> effects = new ArrayList<>();
        // 本回合将减少[20]%攻击值
        CombatBuff combatBuff = param.getPerformPlayer().gainBuff(getRunesId());
        double atk = 0.2 * combatBuff.getLevel() * playerCard.getAtk();
        CardValueEffect buffEffect = CardValueEffect.getSkillEffect(getRunesId(), playerCard.getPos());
        buffEffect.setAtk((int) -atk);
        buffEffect.setSequence(param.getNextSeq());
        effects.add(buffEffect);
        return effects;
    }
}