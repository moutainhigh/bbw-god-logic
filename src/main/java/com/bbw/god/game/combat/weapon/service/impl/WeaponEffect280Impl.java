package com.bbw.god.game.combat.weapon.service.impl;

import com.bbw.god.game.combat.BattleCardService;
import com.bbw.god.game.combat.DeployCardsSolutionService;
import com.bbw.god.game.combat.PositionService;
import com.bbw.god.game.combat.data.Player;
import com.bbw.god.game.combat.data.RDTempResult;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardPositionEffect;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.card.PositionType;
import com.bbw.god.game.combat.data.param.CCardParam;
import com.bbw.god.game.combat.data.param.CardMovement;
import com.bbw.god.game.combat.data.param.PerformWeaponParam;
import com.bbw.god.game.combat.pve.CombatPVEInitService;
import com.bbw.god.game.combat.weapon.service.IWeaponAfterEffect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * 招魂幡	使用后每回合召唤1只鬼兵上场。一场战斗限用1次。
 * @author：lwb
 * @date: 2020/11/25 9:36
 * @version: 1.0
 */
@Service
public class WeaponEffect280Impl implements IWeaponAfterEffect {
    @Autowired
    private BattleCardService battleCardService;
    @Autowired
    private DeployCardsSolutionService upToService;
    @Override
    public int getWeaponId() {
        return 280;
    }

    @Override
    public Action takeAfterAttack(PerformWeaponParam pwp) {
        // 招魂幡	使用后每回合召唤1只鬼兵上场。一场战斗限用1次。
        Action ar = new Action();
        BattleCard card=pwp.getBattleCard();
        int maxCardNum = 1;
        List<BattleCard> cards=new ArrayList<BattleCard>();
        cards.add(card);
        List<CardMovement> movements = upToService.randomSolutionCardToBattle(pwp.getCombat().getPlayer(pwp.getPerformPlayerId()), cards, maxCardNum);
        if (movements.isEmpty()) {
            return ar;
        }
        for (CardMovement move : movements) {
            CardPositionEffect effect = CardPositionEffect.getSkillEffectToTargetPos(getWeaponId(), move.getFromPos());
            effect.moveTo(PositionType.BATTLE, move.getToPos());
            effect.setAttackPower(Effect.AttackPower.getMaxPower());
            effect.setSequence(pwp.getNextAnimationSeq());
            ar.addEffect(effect);
        }
        initCard(pwp);//增加新的鬼兵
        return ar;

    }

    private BattleCard initCard(PerformWeaponParam pwp) {
        // 0级0阶级 424 鬼兵
        CCardParam bcip = CCardParam.init(424, 0, 0,null);
        BattleCard card = CombatPVEInitService.initBattleCard(bcip, pwp.getPerformPlayer().getCardInitId());
        Player player= pwp.getPerformPlayer();
        int emptyPos=battleCardService.getEmptyPos(player.getReinforceCards(), PositionService.getReinforceCardsBeginPos(player.getId()));
        card.setPos(emptyPos);
        //初始化一张鬼兵，加入到援军
        player.getReinforceCards().add(card);
        pwp.resetPos(card.getPos());
        return card;
    }
    @Override
    public RDTempResult beforehandAttack(PerformWeaponParam pwp) {
        RDTempResult rd=new RDTempResult();
        pwp.setCanEffectTimes(30);
        BattleCard card=initCard(pwp);
        rd.addCard(pwp.getPerformPlayerId(),card);
        return rd;
    }

}
