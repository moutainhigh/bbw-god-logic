package com.bbw.god.game.combat.weapon.service.impl;

import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.game.combat.DeployCardsSolutionService;
import com.bbw.god.game.combat.data.RDTempResult;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardPositionEffect;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.card.PositionType;
import com.bbw.god.game.combat.data.param.CardMovement;
import com.bbw.god.game.combat.data.param.PerformWeaponParam;
import com.bbw.god.game.combat.weapon.service.IWeaponAfterEffect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * 阴阳镜 拉己方一张坟场中的卡牌回战场。
 * @author：lwb
 * @date: 2020/11/25 9:36
 * @version: 1.0
 */
@Service
public class WeaponEffect230Impl implements IWeaponAfterEffect {
    @Autowired
    private DeployCardsSolutionService upToService;

    @Override
    public int getWeaponId() {
        return 230;
    }

    @Override
    public Action takeAfterAttack(PerformWeaponParam pwp) {
        //阴阳镜 拉己方一张坟场中的卡牌回战场。(不拉王者卡)
        Action ar = new Action();
        List<BattleCard> mdiscards= pwp.getPerformPlayer().getDiscard();
        List<BattleCard> discards =mdiscards.stream().filter(p->!p.hasKingSkill()).collect(Collectors.toList());
        if (discards.isEmpty()) {
            return ar;
        }
        int maxCardNum=1;
        List<CardMovement> movements = upToService.randomSolutionCardToBattle(pwp.getCombat().getPlayer(pwp.getPerformPlayerId()), discards, maxCardNum);
        if (movements.isEmpty()) {
            return ar;
        }
        for (CardMovement move : movements) {
            CardPositionEffect effect = CardPositionEffect.getSkillEffectToTargetPos(getWeaponId(), move.getFromPos());
            effect.moveTo(PositionType.BATTLE, move.getToPos());
            effect.setAttackPower(Effect.AttackPower.getMaxPower());
            effect.setSequence(pwp.getNextAnimationSeq());
            ar.addEffect(effect);
            pwp.resetPos(move.getFromPos());
        }
        return ar;
    }

    @Override
    public RDTempResult beforehandAttack(PerformWeaponParam pwp) {
        List<BattleCard> discards= pwp.getPerformPlayer().getDiscard();
        if (discards.isEmpty()) {
            throw new ExceptionForClientTip("combat.player.discard.is.empty");
        }
        if (discards.size()==1 && discards.get(0).hasKingSkill()) {
            throw new ExceptionForClientTip("combat.forbidden.weapon.to.king.skill");
        }
        return new RDTempResult();
    }



}
