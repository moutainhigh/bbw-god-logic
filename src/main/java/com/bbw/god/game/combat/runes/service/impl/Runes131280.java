package com.bbw.god.game.combat.runes.service.impl;

import com.bbw.god.game.combat.ClientAnimationService;
import com.bbw.god.game.combat.CombatRedisService;
import com.bbw.god.game.combat.DeployCardsSolutionService;
import com.bbw.god.game.combat.data.AnimationSequence;
import com.bbw.god.game.combat.data.CombatConfig;
import com.bbw.god.game.combat.data.CombatInfo;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardPositionEffect;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.card.PositionType;
import com.bbw.god.game.combat.data.param.CardMovement;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.service.IRoundStageRunes;
import com.bbw.god.game.config.city.CityTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 同泽符 131280  每回合将手牌中与城池属性相同的卡牌，尽可能召唤到场上
 * @author lwb
 * @date 2020/6/8 10:05
 */
@Service
public class Runes131280 implements IRoundStageRunes {
    @Autowired
    private DeployCardsSolutionService upToService;
    @Autowired
    private CombatRedisService redisService;
    @Override
    public int getRunesId() {
        return 131280;
    }
    @Override
    public Action doRoundRunes(CombatRunesParam param) {
        Action ar = new Action();
        //王者（AI)	每回合从手牌中随机召唤同属性卡牌，填满我方阵型的空位。
        List<BattleCard> myHandCards = param.getPerformPlayer().getHandCardList();
        if (myHandCards.isEmpty()) {
            return ar;
        }
        //同属性的卡
        int cityType= getCityAttrType(param.getCombatId());
        List<BattleCard> typeCards = myHandCards.stream().filter(card -> card.getType().getValue() ==cityType).collect(Collectors.toList());
        if (typeCards.isEmpty()) {
            return ar;
        }
        int maxCardNum = CombatConfig.MAX_BATTLE_CARD - 1;
        List<CardMovement> movements = upToService.randomSolutionCardToBattle(param.getPerformPlayer(), typeCards, maxCardNum);

        if (movements.isEmpty()) {
            return ar;
        }
        int sequence = param.getNextSeq();
        for (CardMovement move : movements) {
            CardPositionEffect effect = CardPositionEffect.getSkillEffectToTargetPos(getRunesId(), move.getFromPos());
            effect.moveTo(PositionType.BATTLE, move.getToPos());
            effect.setAttackPower(Effect.AttackPower.getMaxPower());
            effect.setSequence(sequence);
            ar.addEffect(effect);
            //王者效果动画是有次序的，而释放动画为同步的 所以这里手动生成动画
            AnimationSequence animationSequence = ClientAnimationService.getSkillAction(sequence, getRunesId(), param.getMyPlayerPos(), move.getFromPos());
            ar.addClientAction(animationSequence);
        }
        return ar;
    }

    private int getCityAttrType(long combatId){
        CombatInfo info=redisService.getCombatInfo(combatId);
        if (info==null || info.getCityId()==null){
            return 0;
        }
        return CityTool.getCityById(info.getCityId()).getProperty();
    }
}
