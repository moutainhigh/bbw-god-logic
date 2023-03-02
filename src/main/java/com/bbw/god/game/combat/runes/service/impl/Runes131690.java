package com.bbw.god.game.combat.runes.service.impl;

import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.combat.runes.service.IRoundStageRunes;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 净火符131690：每回合开始时，减少敌方全体卡牌3000点防御值，无视【回光】、【金刚】，可对云台生效。
 *
 * @author fzj
 * @date 2021/9/26 10:17
 */
@Service
public class Runes131690 implements IRoundStageRunes {
    @Override
    public int getRunesId() {
        return RunesEnum.JING_HUO.getRunesId();
    }

    @Override
    public Action doRoundRunes(CombatRunesParam param) {
        Action action = new Action();
        List<BattleCard> oppoPlayingCards = param.getOppoPlayer().getPlayingCards(true);
        if (oppoPlayingCards.isEmpty()) {
            return action;
        }
        int hp = this.getInt(500 * (1 + 0.5f * 10));
        int sequence = param.getNextSeq();
        for (BattleCard card : oppoPlayingCards) {
            CardValueEffect effect = CardValueEffect.getSkillEffect(getRunesId(), card.getPos());
            effect.setHp(-hp);
            effect.setSequence(sequence);
            action.setTakeEffect(true);
            action.addEffect(effect);
        }
        return action;
    }
}
