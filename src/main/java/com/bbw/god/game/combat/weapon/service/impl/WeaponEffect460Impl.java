package com.bbw.god.game.combat.weapon.service.impl;

import com.bbw.common.PowerRandom;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.game.combat.BattleCardService;
import com.bbw.god.game.combat.data.Player;
import com.bbw.god.game.combat.data.RDTempResult;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardPositionEffect;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.card.PositionType;
import com.bbw.god.game.combat.data.param.PerformWeaponParam;
import com.bbw.god.game.combat.weapon.service.IWeaponInTimeEffect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * 如意乾坤袋 使用后，更换掉当前手牌，重新抽取5张。一场战斗限用3次。
 * @author：lwb
 * @date: 2020/11/25 9:36
 * @version: 1.0
 */
@Service
public class WeaponEffect460Impl implements IWeaponInTimeEffect {
    @Autowired
    private BattleCardService battleCardService;
    @Override
    public int getWeaponId() {
        return 460;
    }

    @Override
    public RDTempResult beforehandAttack(PerformWeaponParam pwp) {
        RDTempResult rd = new RDTempResult();
        Player player = pwp.getPerformPlayer();
        // 手牌
        BattleCard[] hands = player.getHandCards();
        BattleCard[] printCards = new BattleCard[hands.length];
        int extra=player.getStatistics().getHandCardRoundMpAddtion()+player.getStatistics().getInitCardMp();
        for (int i = 0; i < hands.length; i++) {
            BattleCard card = hands[i];
            if (card == null || !pwp.getMultiplePos().contains(card.getPos())) {
                continue;
            }
            int baseMp = BattleCard.MIN_MP;
            if (!card.hasMinMpSkill()) {
                // 如果不存在疾驰技能则获取上阵法术值
                baseMp = battleCardService.getCostMp(card, i);// 位置+星级
            }
            int costMp=baseMp+extra;
            costMp = Math.max(BattleCard.MIN_MP, costMp);
            card.setMp(costMp);
            // 位置标识
            printCards[i] = card;
        }
        // 牌堆
        BattleCard[] draws = player.getDrawCards().toArray(new BattleCard[0]);
        rd.addCards(pwp.getPerformPlayerId(), printCards);
        rd.addCards(pwp.getPerformPlayerId(), draws);
        return rd;
    }

    @Override
    public int getPerformTotalTimes() {
        return 3;
    }

    @Override
    public int getPerformRoundTimes() {
        return 3;
    }

    @Override
    public Action takeInTimeAttack(PerformWeaponParam pwp) {
        // 如意乾坤袋 使用后，更换掉当前手牌，从牌堆抽取等量卡牌到手牌对应位置。一场战斗限用3次。
        Action ar = new Action();
        List<BattleCard> handCards = pwp.getCombat().getHandCards(pwp.getPerformPlayerId());
        List<BattleCard> drawCards = pwp.getPerformPlayer().getDrawCards();
        if (drawCards.isEmpty()) {
            // 牌堆没有卡牌
            throw new ExceptionForClientTip("combat.player.drawcard.is.empty");
        }
        // 移动手牌=》牌堆
        int seq = pwp.getNextAnimationSeq();
        List<Effect> moveToDarwEffects = new ArrayList<Effect>();
        int drawCardSize = drawCards.size();
        for (BattleCard card : handCards) {
            if (null == card || !pwp.getMultiplePos().contains(card.getPos())) {
                continue;
            }
            if (drawCardSize == 0) {
                break;
            }
            CardPositionEffect effect = CardPositionEffect.getWeaponEffect(getWeaponId(), card.getPos(),
                    PositionType.DRAWCARD);
            effect.setSequence(seq);
            moveToDarwEffects.add(effect);
            drawCardSize--;
        }
        List<Effect> moveToHandEffects = new ArrayList<>();
        seq = pwp.getNextAnimationSeq();
        if (!moveToDarwEffects.isEmpty()) {
            List<BattleCard> newHandCards = PowerRandom.getRandomsFromList(moveToDarwEffects.size(), drawCards);
            // 移动牌堆新卡=》手牌
            for (int i = 0; i < newHandCards.size(); i++) {
                CardPositionEffect effect = CardPositionEffect.getWeaponEffectMoveToPos(getWeaponId(),
                        newHandCards.get(i).getPos(), moveToDarwEffects.get(i).getTargetPos());
                effect.setSequence(seq);
                moveToHandEffects.add(effect);
            }
        }
        if (!moveToDarwEffects.isEmpty() && moveToDarwEffects.size() == moveToHandEffects.size()) {
            ar.addEffects(moveToDarwEffects);
            ar.addEffects(moveToHandEffects);
        }
        return ar;
    }
}
