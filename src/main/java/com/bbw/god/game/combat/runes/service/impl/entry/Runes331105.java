package com.bbw.god.game.combat.runes.service.impl.entry;

import com.bbw.god.game.combat.data.CombatBuff;
import com.bbw.god.game.combat.data.Player;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.combat.runes.service.IRoundStageRunes;
import org.springframework.stereotype.Service;

/**
 * 巧击词条	敌方卡牌普通攻击前，增加等同于对位卡牌的[2]%攻击值。
 *
 * @author: suhq
 * @date: 2022/9/22 2:07 下午
 */
@Service
public class Runes331105 implements IRoundStageRunes {

    @Override
    public int getRunesId() {
        return RunesEnum.QIAO_JI_ENTRY.getRunesId();
    }

    @Override
    public Action doRoundRunes(CombatRunesParam param) {
        Action action = new Action();
        Player player = param.getPerformPlayer();
        CombatBuff combatBuff = player.gainBuff(getRunesId());
        double addAttackRate = 0.02 * combatBuff.getLevel();
        int seq = param.getNextSeq();
        for (int i = 0; i < player.getPlayingCards().length; i++) {
            BattleCard card = player.getPlayingCards(i);
            if (card == null) {
                continue;
            }
            //对位卡牌（敌方卡牌）
            BattleCard oppositeCard = param.getOppoPlayer().getPlayingCards(i);
            if (null == oppositeCard) {
                continue;
            }
            int addAttack = (int) (card.getAtk() * addAttackRate);
            CardValueEffect effect = CardValueEffect.getSkillEffect(getRunesId(), oppositeCard.getPos());
            effect.setAtk(addAttack);
            action.addEffect(effect);
        }
        return action;
    }
}
