package com.bbw.god.game.combat.runes.service.impl.entry;

import com.bbw.god.game.combat.data.CombatBuff;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.combat.runes.service.IRoundStageRunes;
import org.springframework.stereotype.Service;

/**
 * 气魄词条 我方先锋位卡牌普通攻击时，增加[5]%攻击值。
 *
 * @author: suhq
 * @date: 2022/9/22 2:13 下午
 */
@Service
public class Runes332009 implements IRoundStageRunes {

    @Override
    public int getRunesId() {
        return RunesEnum.QI_PO_ENTRY.getRunesId();
    }

    @Override
    public Action doRoundRunes(CombatRunesParam param) {
        Action action = new Action();
        //先锋位置
        int pioneerPosIndex = 1;
        //己方先锋位
        BattleCard selfPioneer = param.getPerformPlayer().getPlayingCards(pioneerPosIndex);
        if (null == selfPioneer) {
            return action;
        }
        CombatBuff combatBuff = param.getPerformPlayer().gainBuff(getRunesId());
        double addRate = 0.05 * combatBuff.getLevel();
        int addAttack = (int) (selfPioneer.getAtk() * addRate);
        
        CardValueEffect effect = CardValueEffect.getSkillEffect(getRunesId(), selfPioneer.getPos());
        effect.setAtk(addAttack);
        action.addEffect(effect);
        return action;
    }
}
