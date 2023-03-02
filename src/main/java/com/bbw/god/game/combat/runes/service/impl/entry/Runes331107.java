package com.bbw.god.game.combat.runes.service.impl.entry;

import com.bbw.god.game.combat.PositionService;
import com.bbw.god.game.combat.data.CombatBuff;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.combat.runes.service.IRoundStageRunes;
import org.springframework.stereotype.Service;

/**
 * 勇猛词条 敌方先锋位卡牌普通攻击前，增加[5]%攻击值；我方先锋位卡牌普通攻击前，减少[2]%攻击值。
 *
 * @author: suhq
 * @date: 2022/9/22 2:13 下午
 */
@Service
public class Runes331107 implements IRoundStageRunes {

    @Override
    public int getRunesId() {
        return RunesEnum.YONG_MENG_ENTRY.getRunesId();
    }

    @Override
    public Action doRoundRunes(CombatRunesParam param) {
        Action action = new Action();
        CombatBuff combatBuff = param.getPerformPlayer().gainBuff(getRunesId());
        BattleCard performCard = param.getPerformCard();
        if (null == performCard) {
            return action;
        }
        //先锋位置
        int pioneerPosIndex = 1;
        int performIndex = PositionService.getBattleCardIndex(performCard.getPos());
        if (performIndex != pioneerPosIndex) {
            return action;
        }

        //己方先锋位
        if (isPerformSelf(performCard.getPos(), param.getPerformPlayer().getId())) {
            double deductRate = 0.02 * combatBuff.getLevel();
            int deductAttack = (int) (performCard.getAtk() * deductRate);
            CardValueEffect effect = CardValueEffect.getSkillEffect(getRunesId(), performCard.getPos());
            effect.setAtk(-deductAttack);
            action.addEffect(effect);
        } else {
            //敌人先锋位
            double addRate = 0.05 * combatBuff.getLevel();
            int addAttack = (int) (performCard.getAtk() * addRate);
            CardValueEffect effect = CardValueEffect.getSkillEffect(getRunesId(), performCard.getPos());
            effect.setAtk(addAttack);
            action.addEffect(effect);
        }
        return action;
    }
}
