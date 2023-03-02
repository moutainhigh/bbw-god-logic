package com.bbw.god.game.combat.runes.service.impl.entry;

import com.bbw.common.CloneUtil;
import com.bbw.common.ListUtil;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.data.skill.SkillSection;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.combat.runes.service.IRoundStageRunes;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 追击词条 我方召唤师及卡牌受到来自敌方的技能效果时，该效果将额外生效1次。
 *
 * @author longwh
 * @date 2023/1/5 16:05
 */
@Service
public class Runes333305 implements IRoundStageRunes {
    @Override
    public int getRunesId() {
        return RunesEnum.ZHUI_JI_ENTRY.getRunesId();
    }

    @Override
    public Action doRoundRunes(CombatRunesParam param) {
        Action action = new Action();
        List<Effect> effectList = new ArrayList<>();
        for (Effect effect : param.getReceiveEffect()) {
            // 普通攻击不处理
            if (SkillSection.getNormalAttackSection().contains(effect.getSourceID())) {
                continue;
            }
            // 非敌方的技能效果 不处理
            if (!param.isEnemyTargetCard()){
                continue;
            }
            // 效果针对敌方不处理
            if (param.isEffectToEnemy()) {
                continue;
            }
            // 触发 补充一个效果动画
            CardValueEffect animationEffect = CardValueEffect.getSkillEffect(getRunesId(), effect.getTargetPos());
            animationEffect.setSequence(param.getNextSeq());
            action.addEffect(animationEffect);
            // 效果将额外生效1次
            Effect cloneEffect = CloneUtil.clone(effect);
            cloneEffect.setSequence(param.getNextSeq());
            effectList.add(cloneEffect);
        }
        if (ListUtil.isNotEmpty(effectList)) {
            param.getReceiveEffect().addAll(effectList);
        }
        return action;
    }
}