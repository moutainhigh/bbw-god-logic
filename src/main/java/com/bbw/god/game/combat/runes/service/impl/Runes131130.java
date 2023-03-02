package com.bbw.god.game.combat.runes.service.impl;

import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.service.IRoundStageRunes;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 圣火符31130  每回合开始时，对敌方全体卡牌施放一次圣火技能（伤害按照军师位，阶数为队伍最低阶卡牌）
 * 能烧到云台,无视金刚
 * @author lwb
 * @date 2020/6/8 10:05
 */
@Service
public class Runes131130 implements IRoundStageRunes {
    @Override
    public int getRunesId() {
        return 131130;
    }

    @Override
    public Action doRoundRunes(CombatRunesParam param) {
        //
        //圣火	每回合破除敌方全体卡牌150~450点防御（不含云台）。，每升一阶增长50%的效果
        Action attackResult = new Action();
        List<BattleCard> oppoPlayingCards = param.getOppoPlayer().getPlayingCards(true);
        if (oppoPlayingCards.isEmpty()) {
            return attackResult;
        }
        int hp = this.getInt(500 * (1 + 0.5f * 6));
        int sequence = param.getNextSeq();
        for (BattleCard card : oppoPlayingCards) {
            CardValueEffect effect = CardValueEffect.getSkillEffect(getRunesId(), card.getPos());
            effect.setHp(-hp);
            effect.setSequence(sequence);
            attackResult.addEffect(effect);
        }
        return attackResult;
    }
}
