package com.bbw.god.game.combat.runes.service.impl;

import com.bbw.common.PowerRandom;
import com.bbw.god.game.combat.PositionService;
import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.combat.data.Player;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardPositionEffect;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.card.PositionType;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.service.IRoundStageRunes;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 封神符 131420  每回合若己方场上有空余位置，则从坟场里拉一张卡牌上场。
 * @author lwb
 * @date 2020/6/8 10:05
 */
@Service
public class Runes131420 implements IRoundStageRunes {
    @Override
    public int getRunesId() {
        return 131420;
    }
    @Override
    public Action doRoundRunes(CombatRunesParam param) {
        Action ar = new Action();
        // 每回合若己方场上有空余位置，则从坟场里拉一张卡牌上场。
        Player me = param.getPerformPlayer();
        int[] emptyBattlePos = me.getEmptyBattlePos(false);
        if (emptyBattlePos.length == 0 && !me.yunTaiIsEmpty()) {
            //没空位不发动
            return ar;
        }
        List<BattleCard> discards = me.getDiscard();
        if (me.yunTaiIsEmpty() && emptyBattlePos.length ==0){
            //必然是飞行卡
            discards=discards.stream().filter(p->p.hasEffectiveSkill(CombatSkillEnum.FX.getValue())).collect(Collectors.toList());
        }
        if (discards.isEmpty()) {
            //坟场没有合适的卡 不发动
            return ar;
        }
        BattleCard card = PowerRandom.getRandomFromList(discards);
        CardPositionEffect effect = CardPositionEffect.getSkillEffectToTargetPos(getRunesId(), card.getPos());
        effect.setAttackPower(Effect.AttackPower.getMaxPower());
        effect.setSequence(param.getNextSeq());
        // 随机到云台卡，并且云台位空
        if (card.canFly() && me.yunTaiIsEmpty()) {
            // 设置到云台
            int yunTaiPos = PositionService.getYunTaiPos(me.getId());
            effect.moveTo(PositionType.BATTLE, yunTaiPos);
            ar.addEffect(effect);
        }else if (emptyBattlePos.length>0){
            // 非云台位的其他战场位置
            int index = PowerRandom.randomInt(emptyBattlePos.length);
            effect.moveTo(PositionType.BATTLE, emptyBattlePos[index]);
            ar.addEffect(effect);
        }
        return ar;
    }

}
