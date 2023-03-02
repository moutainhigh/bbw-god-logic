package com.bbw.god.game.combat.weapon.service.impl;

import com.bbw.god.game.combat.PositionService;
import com.bbw.god.game.combat.data.Combat;
import com.bbw.god.game.combat.data.Player;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.BattleSkillEffect;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.param.PerformWeaponParam;
import com.bbw.god.game.combat.skill.BattleSkill3106;
import com.bbw.god.game.combat.weapon.service.IWeaponAfterEffect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * 太极图	控制一张敌方卡牌，该回合使其攻击自己的召唤师。一场战斗限用1次。	魅惑
 * @author：lwb
 * @date: 2020/11/25 9:36
 * @version: 1.0
 */
@Service
public class WeaponEffect240Impl implements IWeaponAfterEffect {
    @Autowired
    private BattleSkill3106 battleSkill3106Service;//魅惑
    @Override
    public int getWeaponId() {
        return 240;
    }

    @Override
    public Action takeAfterAttack(PerformWeaponParam pwp) {
        //太极图	控制一张敌方卡牌，该回合使其攻击自己的召唤师。一场战斗限用1次。	【魅惑】
        pwp.effectOppoPlayer();
        Action ar = new Action();
        pwp.getBattleCard();
        int fromPos= PositionService.getZhaoHuanShiPos(pwp.getPerformPlayerId());

        Combat combat=pwp.getCombat();
        Player opPlayer=combat.getOppoPlayer(pwp.getPerformPlayer().getId());
        int ZhsPos=PositionService.getZhaoHuanShiPos(opPlayer.getId());
        BattleCard myCard =combat.getBattleCard(pwp.getTargetPos());
        int sequence = combat.getAnimationSeq();
        BattleSkillEffect effect=battleSkill3106Service.getEffect(sequence, myCard, pwp.getTargetPos(), ZhsPos);
        effect.setAttackPower(Effect.AttackPower.getMaxPower());
        ar.addEffect(effect);
        return ar;
    }

}
