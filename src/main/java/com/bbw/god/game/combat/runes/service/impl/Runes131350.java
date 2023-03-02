package com.bbw.god.game.combat.runes.service.impl;

import com.bbw.common.PowerRandom;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardPositionEffect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.card.PositionType;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.service.IRoundStageRunes;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 刚烈符 131350  己方1张卡牌死亡时，将会随机将敌方1张卡牌送入坟场。（随机含云台）
 * @author lwb
 * @date 2020/6/8 10:05
 */
@Service
public class Runes131350 implements IRoundStageRunes {
    @Override
    public int getRunesId() {
        return 131350;
    }
    @Override
    public Action doRoundRunes(CombatRunesParam param) {
        Action ar = new Action();
        if (param.isEnemyTargetCard()){
            return ar;
        }
        List<BattleCard> playingCards = param.getOppoPlayer().getPlayingCards(true);
        if (playingCards.isEmpty()){
            return ar;
        }
        BattleCard targetCard= PowerRandom.getRandomFromList(playingCards);
        CardPositionEffect effect=CardPositionEffect.getSkillEffectToTargetPos(getRunesId(),targetCard.getPos());
        effect.setToPositionType(PositionType.DISCARD);
        effect.setSequence(param.getNextSeq());
        ar.addEffect(effect);
        return ar;
    }

}
