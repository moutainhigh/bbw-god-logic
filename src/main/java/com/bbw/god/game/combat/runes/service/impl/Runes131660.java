package com.bbw.god.game.combat.runes.service.impl;

import com.bbw.god.game.combat.PositionService;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.combat.runes.service.IRoundStageRunes;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 激将符131660紫：己方所有阵位获得先锋位效果。
 * ①　给除了先锋位以外的阵位附加先锋位效果。
 *
 * @author: suhq
 * @date: 2021/9/29 2:42 下午
 */
@Service
public class Runes131660 implements IRoundStageRunes {

    @Override
    public int getRunesId() {
        return RunesEnum.JI_JIANG.getRunesId();
    }

    @Override
    public Action doRoundRunes(CombatRunesParam param) {
        Action ar = new Action();
        List<BattleCard> cards = param.getPerformPlayer().getPlayingCards(true);
        int seq = param.getNextSeq();
        for (BattleCard card : cards) {
            if (card == null) {
                continue;
            }
            if (PositionService.isXianFengPos(card.getPos())) {
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
