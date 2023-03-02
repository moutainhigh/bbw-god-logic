package com.bbw.god.game.combat.runes.service.impl;

import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.service.IRoundStageRunes;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 道法符 131220  每回合对己方全体卡牌施放一次道法技能（数值按照军师位，阶数为队伍最低阶卡牌）
 * @author lwb
 * @date 2020/6/8 10:05
 */
@Service
public class Runes131220 implements IRoundStageRunes {
    @Override
    public int getRunesId() {
        return 131220;
    }

    @Override
    public Action doRoundRunes(CombatRunesParam param) {
        Action ar = new Action();
        List<BattleCard> playingCards = param.getPerformPlayer().getPlayingCards(true);
        if (playingCards.isEmpty()) {
            return ar;
        }
        // 道法 只要其在场上，我方战场上所有其它卡牌攻防各加500，每阶再加30点。
        int val = 500+getMinHv(playingCards)*30;
        int sequence = param.getNextSeq();
        for (BattleCard card:playingCards) {
            CardValueEffect effect = CardValueEffect.getSkillEffect(getRunesId(), card.getPos());
            effect.setSequence(sequence);
            effect.setHp(val);
            effect.setAtk(val);
            ar.addEffect(effect);
        }
        return ar;
    }

}
