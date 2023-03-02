package com.bbw.god.game.combat.runes.service.impl.entry;

import com.bbw.god.game.combat.ClientAnimationService;
import com.bbw.god.game.combat.data.AnimationSequence;
import com.bbw.god.game.combat.data.CombatBuff;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.combat.runes.service.IRoundStageRunes;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 离间词条 我方卡牌的组合技能将有[30]%概率无法发动
 *
 * @author longwh
 * @date 2023/1/4 9:33
 */
@Service
public class Runes333202 implements IRoundStageRunes {

    @Override
    public int getRunesId() {
        return RunesEnum.LI_JIAN_ENTRY.getRunesId();
    }

    @Override
    public Action doRoundRunes(CombatRunesParam param) {
        Action action = new Action();
        CombatBuff combatBuff = param.getPerformPlayer().gainBuff(getRunesId());
        if (!combatBuff.ifToPerform(30, 0)) {
            return action;
        }
        combatBuff.setToPerform(true);
        combatBuff.setLevel(combatBuff.getLevel());
        combatBuff.setRound(1);

        //触发 补充一个动画
        AnimationSequence amin = ClientAnimationService.getSkillAction(param.getNextSeq(), getRunesId(), param.getMyPlayerPos());
        action.addClientAction(amin);
        return action;
    }
}