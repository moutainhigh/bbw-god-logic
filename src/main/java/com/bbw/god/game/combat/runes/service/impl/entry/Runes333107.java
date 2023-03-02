package com.bbw.god.game.combat.runes.service.impl.entry;

import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.data.skill.SkillSection;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.combat.runes.service.IRoundStageRunes;
import org.springframework.stereotype.Service;

/**
 * 豁免词条 我方卡牌无法克制敌方卡牌。
 *
 * @author longwh
 * @date 2022/12/30 13:48
 */
@Service
public class Runes333107 implements IRoundStageRunes {
    @Override
    public int getRunesId() {
        return RunesEnum.HUO_MIAN_ENTRY.getRunesId();
    }

    @Override
    public Action doRoundRunes(CombatRunesParam param) {
        Action action = new Action();
        for (Effect effect : param.getReceiveEffect()) {
            int skillId = effect.getPerformSkillID();
            // 必须为克制技能
            if (!SkillSection.isAttributerestraint(skillId)) {
                continue;
            }
            if (isPerformOpponent(effect.getSourcePos(), param.getPerformPlayer().getId())) {
                continue;
            }
            effect.setValid(false);
            //触发 补充一个动画
            CardValueEffect aminEffect = CardValueEffect.getSkillEffect(getRunesId(), effect.getTargetPos());
            action.addEffect(aminEffect);
        }
        return action;
    }

}