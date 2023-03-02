package com.bbw.god.game.combat.weapon.service;

import com.bbw.god.game.combat.data.TimesLimit;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.BattleSkillEffect;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.param.PerformWeaponParam;
import com.bbw.god.game.combat.data.skill.BattleSkill;

import java.util.Optional;

/**
 * 战斗法宝  延后生效法宝系列
 * @author：lwb
 * @date: 2020/11/25 9:26
 * @version: 1.0
 */
public interface IWeaponAfterEffect extends IBaseWeapon{
    /**
     * 延后生效的效果
     * @param pwp
     * @return
     */
    Action takeAfterAttack(PerformWeaponParam pwp);

    /**
     * 添加永久攻防
     * @param pwp
     * @param roundAtk
     * @param roundHp
     * @return
     */
    default Action addRoundAtkHp(PerformWeaponParam pwp, int roundAtk, int roundHp) {
        pwp.effectSelf();
        Action ar = new Action();
        if (pwp.getBattleCard()==null){
            return ar;
        }
        CardValueEffect effect = CardValueEffect.getWeaponEffect(getWeaponId(), pwp.getBattleCard().getPos());
        effect.setRoundAtk(roundAtk);
        effect.setRoundHp(roundHp);
        effect.setSequence(pwp.getNextAnimationSeq());
        ar.addEffect(effect);
        return ar;
    }

    /**
     * 添加技能
     * @param targetCard
     * @param skillId
     * @param timesLimit
     * @return
     */
    default Optional<BattleSkillEffect> addBattleSkillEffect(BattleCard targetCard, int skillId, TimesLimit timesLimit) {
        Optional<BattleSkill> skill = targetCard.getSkills().stream().filter(p -> !p.isForbid() && p.getId() == skillId)
                .findFirst();
        if (skill.isPresent()) {
            // 存在相同的技能 且 该技能是有效的 则不再重复添加
            return Optional.empty();
        }
        BattleSkillEffect skillEffect = BattleSkillEffect.getWeaponEffect(getWeaponId(), targetCard.getPos());
        skillEffect.addSkill(skillId, timesLimit);
        return Optional.ofNullable(skillEffect);
    }

}
