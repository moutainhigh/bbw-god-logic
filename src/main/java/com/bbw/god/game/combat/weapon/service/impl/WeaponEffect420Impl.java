package com.bbw.god.game.combat.weapon.service.impl;

import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.combat.data.TimesLimit;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.BattleSkillEffect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.param.PerformWeaponParam;
import com.bbw.god.game.combat.weapon.service.IWeaponAfterEffect;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 *  定神丹	使用后，该回合威风、斥退、妖术对我方卡牌无效。一场战斗限用1次。
 * @author：lwb
 * @date: 2020/11/25 9:36
 * @version: 1.0
 */
@Service
public class WeaponEffect420Impl implements IWeaponAfterEffect {
    @Override
    public int getWeaponId() {
        return 420;
    }

    @Override
    public Action takeAfterAttack(PerformWeaponParam pwp) {
        Action ar = new Action();
        //定神丹	使用后，该回合威风、斥退、妖术对我方卡牌无效。一场战斗限用1次。
        List<BattleCard> playingCards = pwp.getPerformPlayerPlayingCards(true);
        int skillId = CombatSkillEnum.WEAPON_DSD_DEFENSE.getValue();
        for (BattleCard card : playingCards) {
            BattleSkillEffect skillEffect = BattleSkillEffect.getWeaponEffect(getWeaponId(), card.getPos());
            skillEffect.addSkill(skillId, TimesLimit.oneRoundLimit());
            ar.addEffect(skillEffect);
            pwp.getMultiplePos().add(card.getPos());
        }
        return ar;
    }

}
