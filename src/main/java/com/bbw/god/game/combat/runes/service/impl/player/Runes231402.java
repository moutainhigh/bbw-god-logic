package com.bbw.god.game.combat.runes.service.impl.player;

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

/**
 * 斩魂符图	5阶	敌方卡牌进入坟场时，若坟场卡牌超过2张则有30%概率（可升级）将1张坟场卡牌移除对局。	每级额外+7%概率
 *
 * @author: suhq
 * @date: 2021/11/16 9:50 上午
 */
@Service
public class Runes231402 implements IRoundStageRunes {
    @Override
    public int getRunesId() {
        return RunesEnum.ZHAN_HUN_PLAYER.getRunesId();
    }

    @Override
    public Action doRoundRunes(CombatRunesParam param) {
        Action action = new Action();
        int oppDiscardNum = param.getOppoPlayer().getDiscard().size();
        if (oppDiscardNum < 2) {
            return action;
        }
        CombatBuff combatBuff = param.getPerformPlayer().gainBuff(getRunesId());
        if (!combatBuff.ifToPerform(30, 7)) {
            return action;
        }
        BattleCard cardToDegenerator = PowerRandom.getRandomFromList(param.getOppoPlayer().getDiscard());
        CardPositionEffect positionEffect = CardPositionEffect.getSkillEffectToTargetPos(getRunesId(), cardToDegenerator.getPos());
        positionEffect.setToPositionType(PositionType.DEGENERATOR);
        positionEffect.setToPos(-1);
        action.addEffect(positionEffect);
        return action;
    }
}
