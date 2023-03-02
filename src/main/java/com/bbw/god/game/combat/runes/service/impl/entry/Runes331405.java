package com.bbw.god.game.combat.runes.service.impl.entry;

import com.bbw.common.ListUtil;
import com.bbw.common.PowerRandom;
import com.bbw.god.game.combat.data.CombatBuff;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardPositionEffect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.card.PositionType;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.combat.runes.service.IRoundStageRunes;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 易位	敌方卡牌从场上离开时，有[7]%概率从手牌中将1张卡牌放置在原先位置上。
 *
 * @author: suhq
 * @date: 2022/9/23 3:43 下午
 */
@Service
public class Runes331405 implements IRoundStageRunes {

    @Override
    public int getRunesId() {
        return RunesEnum.YI_WEI_ENTRY.getRunesId();
    }

    @Override
    public Action doRoundRunes(CombatRunesParam param) {
        Action action = new Action();
        //己方不处理
        if (!param.isEnemyTargetCard()) {
            return action;
        }
        CombatBuff combatBuff = param.getPerformPlayer().gainBuff(getRunesId());
        int rate = 7 * combatBuff.getLevel();
        if (!PowerRandom.hitProbability(rate)) {
            return action;
        }
        //获取敌方手牌
        List<BattleCard> oppHandCards = param.getOppoPlayer().getHandCardList();
        if (ListUtil.isEmpty(oppHandCards)) {
            return action;
        }
        BattleCard targetCard = PowerRandom.getRandomFromList(oppHandCards);
        CardPositionEffect positionEffect = CardPositionEffect.getSkillEffectToTargetPos(getRunesId(), targetCard.getPos());
        positionEffect.moveTo(PositionType.BATTLE, param.getCardSourcePos());
        positionEffect.setSequence(param.getNextSeq());
        action.addEffect(positionEffect);
        return action;

    }

}
