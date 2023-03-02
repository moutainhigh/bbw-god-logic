package com.bbw.god.game.combat.runes.service.impl;

import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.service.IRoundStageRunes;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 舞戚符31050  每回合向己方所有卡牌施放集体鼓舞（含云台，与友方鼓舞加法叠加）
 * @author lwb
 * @date 2020/6/8 10:05
 */
@Service
public class Runes131050 implements IRoundStageRunes {

    @Override
    public Action doRoundRunes(CombatRunesParam param) {
        //该回合所有卡牌的攻击力提高50%。
        Action ar = new Action();
        List<BattleCard> cards=param.getPerformPlayer().getPlayingCards(true);
        int seq=param.getNextSeq();
        for (BattleCard card:cards) {
            if (card==null){
                continue;
            }
            CardValueEffect effect = CardValueEffect.getSkillEffect(getRunesId(), card.getPos());
            effect.setAtk(getInt(card.getAtk()*0.5f));
            effect.setSequence(seq);
            ar.addEffect(effect);
        }
//        AnimationSequence action= ClientAnimationService.getSkillAction(seq, getRunesId(), PositionService.getZhaoHuanShiPos(param.getPerformPlayer().getId()));
//        ar.addClientAction(action);
        return ar;
    }

    @Override
    public int getRunesId() {
        return 131050;
    }
}
