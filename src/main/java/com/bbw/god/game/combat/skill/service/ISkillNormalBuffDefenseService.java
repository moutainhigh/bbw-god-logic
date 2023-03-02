package com.bbw.god.game.combat.skill.service;

import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import com.bbw.god.game.combat.skill.magicdefense.BattleSkillDefenseTableService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface ISkillNormalBuffDefenseService extends ISkillBaseService {

    /**
     * 物理BUFF加成防御=》默认生成反向减益
     * @param psp
     * @return
     */
    default Action takeNormalBuffDefense(PerformSkillParam psp) {
        Action ar = new Action();
        //对位卡牌
        Optional<BattleCard> oppoCard = psp.getFaceToFaceCard();
        //对位没有卡牌,不触发技能
        if (!oppoCard.isPresent()) {
            return ar;
        }
        //是否有增益自身的法术效果
        List<CardValueEffect> effects = oppoCard.get().getRoundDelayEffects();
        if (null == effects || effects.isEmpty()) {
            return ar;
        }
        return buildNegativeEffect(oppoCard.get(),ar);
    }

    /**
     * 生成负面效果
     * 以无相为例子
     * @param targetCard
     * @param ar
     * @return
     */
    default Action buildNegativeEffect(BattleCard targetCard,Action ar){
        //以无相为例子。面对属性克制(4106, 4107, 4108, 4109, 4110)卡牌和暴击(4104)技能无视其加成效果。
        int effectValue = 0;
        int[] defenseSkillIds = BattleSkillDefenseTableService.getDefenseTableBySkillId(this.getMySkillId());
        //计算 属性克制卡牌和暴击技能 的增益总和
        for (int skillId : defenseSkillIds) {
            Optional<CardValueEffect> effect = targetCard.getRoundDelayEffect(skillId);
            if (effect.isPresent() && effect.get().getAtk() > 0) {//正向增益
                effectValue += effect.get().getAtk();
            }
            targetCard.removeRoundDelayEffect(skillId);
        }
        //如果对方有增益，则自己触发无相
        if (effectValue > 0) {
            //给对位卡牌生成 一个反向效果，减益
            CardValueEffect oppoEffect = CardValueEffect.getSkillEffect(getMySkillId(),targetCard.getPos());
            oppoEffect.setAtk(-effectValue);
            ar.addEffect(oppoEffect);
        }
        return ar;
    }

}
