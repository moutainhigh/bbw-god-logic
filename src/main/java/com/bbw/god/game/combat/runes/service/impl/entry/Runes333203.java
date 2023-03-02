package com.bbw.god.game.combat.runes.service.impl.entry;

import com.bbw.god.game.combat.data.CombatBuff;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.data.skill.SkillSection;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.combat.runes.service.IRoundStageRunes;
import org.springframework.stereotype.Service;

/**
 * 僵值词条 我方卡牌的普通攻击将有[30]%概率无法进行。
 *
 * @author longwh
 * @date 2023/1/4 9:33
 */
@Service
public class Runes333203 implements IRoundStageRunes {

    @Override
    public int getRunesId() {
        return RunesEnum.JIANG_ZHI_ENTRY.getRunesId();
    }

    @Override
    public Action doRoundRunes(CombatRunesParam param) {
        Action action = new Action();
        for (Effect effect : param.getReceiveEffect()) {
            // 效果 不是 来自词条释放方，不处理
            if (isPerformOpponent(effect.getSourcePos(), param.getPerformPlayer().getId())) {
                continue;
            }
            // 非普通攻击不处理
            if (!SkillSection.getNormalAttackSection().contains(effect.getSourceID())) {
                continue;
            }
            CombatBuff combatBuff = param.getPerformPlayer().gainBuff(getRunesId());
            if (!combatBuff.ifToPerform(30, 0)) {
                continue;
            }
            // 普通攻击无效
            effect.setNeedAnimation(false);
            effect.setValid(false);
            //触发 补充一个动画
            CardValueEffect animationEffect = CardValueEffect.getSkillEffect(getRunesId(), effect.getSourcePos());
            animationEffect.setSequence(param.getNextSeq());
            action.addEffect(animationEffect);
        }
        return action;
    }
}