package com.bbw.god.game.combat.runes.service.impl.player;

import com.bbw.god.game.combat.data.CombatBuff;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.combat.runes.service.IRoundStageRunes;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 激将符图	2阶	每回合开始时，有30%概率（可升级）己方全体卡牌增加10%攻击值。	每级额外+7%概率
 *
 * @author: suhq
 * @date: 2021/9/29 2:42 下午
 */
@Service
public class Runes231105 implements IRoundStageRunes {

    @Override
    public int getRunesId() {
        return RunesEnum.JI_JIANG_PLAYER.getRunesId();
    }

    @Override
    public Action doRoundRunes(CombatRunesParam param) {
        Action ar = new Action();
        CombatBuff combatBuff = param.getPerformPlayer().gainBuff(getRunesId());
        if (!combatBuff.ifToPerform(30, 7)) {
            return ar;
        }
        List<BattleCard> cards = param.getPerformPlayer().getPlayingCards(true);
        int seq = param.getNextSeq();
        for (BattleCard card : cards) {
            if (card == null) {
                continue;
            }
            CardValueEffect effect = CardValueEffect.getSkillEffect(getRunesId(), card.getPos());
            effect.setAtk(getInt(card.getAtk() * 0.1f));
            effect.setSequence(seq);
            ar.addEffect(effect);
        }
//        AnimationSequence action= ClientAnimationService.getSkillAction(seq, getRunesId(), PositionService.getZhaoHuanShiPos(param.getPerformPlayer().getId()));
//        ar.addClientAction(action);
        return ar;
    }
}
