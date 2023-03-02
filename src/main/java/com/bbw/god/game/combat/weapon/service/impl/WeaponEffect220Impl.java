package com.bbw.god.game.combat.weapon.service.impl;

import com.bbw.common.CloneUtil;
import com.bbw.god.game.combat.data.TimesLimit;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.BattleSkillEffect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.param.PerformWeaponParam;
import com.bbw.god.game.combat.data.skill.BattleSkill;
import com.bbw.god.game.combat.weapon.service.IWeaponAfterEffect;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 * 打神鞭 使用后该回合敌方场上全体卡牌技能失效。一场战斗限用1次。
 * @author：lwb
 * @date: 2020/11/25 9:36
 * @version: 1.0
 */
@Service
public class WeaponEffect220Impl implements IWeaponAfterEffect {
    @Override
    public int getWeaponId() {
        return 220;
    }

    @Override
    public Action takeAfterAttack(PerformWeaponParam pwp) {
        Action ar = new Action();
        //打神鞭 使用后该回合敌方场上全体卡牌技能失效。一场战斗限用1次。
        List<BattleCard> oppoPlayingCards = pwp.getOppoPlayingCards(true);//对手卡牌
        int seq=pwp.getNextAnimationSeq();
        for (BattleCard card : oppoPlayingCards) {
            List<BattleSkill> skills = card.getSkills();
            if (skills.isEmpty()) {
                continue;
            }
            int skillid=3131;//封咒动画效果
            BattleSkillEffect weaponEffect = BattleSkillEffect.getWeaponEffect(skillid, card.getPos());
            weaponEffect.setSequence(seq);
            for (BattleSkill skill : skills) {
                TimesLimit limt = CloneUtil.clone(skill.getTimesLimit());
                weaponEffect.forbidOneRound(skill.getId(), limt,getWeaponId());
            }
            pwp.getMultiplePos().add(card.getPos());
            ar.addEffect(weaponEffect);
        }
        return ar;
    }

}
