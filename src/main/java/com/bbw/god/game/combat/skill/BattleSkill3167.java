package com.bbw.god.game.combat.skill;

import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.ClientAnimationService;
import com.bbw.god.game.combat.data.AnimationSequence;
import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 毒矢：
 * 每回合，施放1次【拦截】，
 * 并使目标进入中毒状态：每回合破除80点永久防御值，无视【回光】，该状态可叠加，每阶增加50%效果。
 *
 * @author: suhq
 * @date: 2021/12/3 2:12 下午
 */
@Service
public class BattleSkill3167 extends BattleSkillService {
    private static final int SKILL_ID = CombatSkillEnum.DU_SHI.getValue();// 技能ID
    @Autowired
    private BattleSkill3102 battleSkill3102;
    @Autowired
    private BattleSkill3127 battleSkill3127;

    @Override
    public int getMySkillId() {
        return SKILL_ID;
    }

    @Override
    protected Action attack(PerformSkillParam psp) {
        Action action = new Action();
        Action lanJieAction = battleSkill3102.buildEffects(null, psp);
        if (!lanJieAction.existsEffect()) {
            return action;
        }
        List<Effect> effects = new ArrayList<>();
        effects.addAll(lanJieAction.getEffects());
        int sequence = psp.getNextAnimationSeq();
        AnimationSequence asq = ClientAnimationService.getSkillAction(sequence, getMySkillId(),
                psp.getPerformCard().getPos(), effects.get(0).getTargetPos());
        action.addClientAction(asq);

        Action duAction = battleSkill3127.buildEffects(psp.getOppoPlayer().getPlayingCards()[0], psp);
        if (duAction.existsEffect()) {
            effects.addAll(duAction.getEffects());
        }
        for (Effect effect : effects) {
            effect.replaceEffectSkillId(getMySkillId());
        }
        action.addEffects(effects);

        return action;
    }
}
