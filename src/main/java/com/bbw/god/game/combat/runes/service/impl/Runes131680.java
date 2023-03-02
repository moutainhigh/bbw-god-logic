package com.bbw.god.game.combat.runes.service.impl;

import com.bbw.common.ListUtil;
import com.bbw.god.game.combat.ClientAnimationService;
import com.bbw.god.game.combat.data.AnimationSequence;
import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.combat.runes.service.IRoundStageRunes;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * 不动符131680：己方全体卡牌不会受到【威风】、【斥退】、【落羽】的效果影响。
 *
 * @author fzj
 * @date 2021/9/26 10:01
 */
@Service
public class Runes131680 implements IRoundStageRunes {
    /** 【威风】、【斥退】、【落羽】技能ID */
    private static List<Integer> DEFENCE_SKILLS = Arrays.asList(CombatSkillEnum.WF.getValue(), CombatSkillEnum.CT.getValue(), CombatSkillEnum.LY.getValue());

    @Override
    public int getRunesId() {
        return RunesEnum.BU_DONG.getRunesId();
    }

    @Override
    public Action doRoundRunes(CombatRunesParam param) {
        Action action = new Action();
        if (param.isEffectToEnemy()) {
            return action;
        }
        int seq = 0;
        List<Effect> effectsToRemove = new ArrayList<>();
        for (Effect effect : param.getReceiveEffect()) {
            int performSkillID = effect.getPerformSkillID();
            if (!DEFENCE_SKILLS.contains(performSkillID)) {
                continue;
            }
            effectsToRemove.add(effect);
            if (0 == seq){
                seq = param.getNextSeq();
            }
            AnimationSequence anim = ClientAnimationService.getSkillAction(seq,CombatSkillEnum.DFENG.getValue(), effect.getTargetPos());
            action.addClientAction(anim);
            action.setTakeEffect(true);

        }
        if (ListUtil.isNotEmpty(effectsToRemove)) {
            param.getReceiveEffect().removeAll(effectsToRemove);
        }
        return action;
    }
}
