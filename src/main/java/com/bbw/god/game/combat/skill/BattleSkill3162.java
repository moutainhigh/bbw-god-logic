package com.bbw.god.game.combat.skill;

import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.ClientAnimationService;
import com.bbw.god.game.combat.data.AnimationSequence;
import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 吸阳 3162：每回合，对敌方全体卡牌施放【吸星】，并对召唤师施放【噬魂】。
 *
 * @author: suhq
 * @date: 2021/9/25 3:25 上午
 */
@Service
public class BattleSkill3162 extends BattleSkillService {
    private static final int SKILL_ID = CombatSkillEnum.XI_YANG.getValue();// 技能ID
    @Autowired
    private BattleSkill3133 battleskill3133;
    @Autowired
    private BattleSkill3110 battleSkill3110;

    @Override
    public int getMySkillId() {
        return SKILL_ID;
    }

    @Override
    protected Action attack(PerformSkillParam psp) {
        Action action = new Action();
        List<BattleCard> oppoPlayingCards = psp.getOppoPlayer().getPlayingCards(true);
        if (oppoPlayingCards.isEmpty()) {
            return action;
        }
        int sequence = psp.getNextAnimationSeq();
        for (BattleCard targetCard : oppoPlayingCards) {
            List<Effect> effects = battleskill3133.getEffects(psp.getPerformCard(), targetCard);
            for (Effect effect : effects) {
                effect.replaceEffectSkillId(getMySkillId());
            }
            action.addEffects(effects);
            AnimationSequence asq = ClientAnimationService.getSkillAction(sequence, SKILL_ID,
                    psp.getPerformCard().getPos(), targetCard.getPos());
            action.addClientAction(asq);
        }

        Effect effect = battleSkill3110.getEffect(psp.getPerformCard(), 150, psp.getOppoZhsPos(), sequence);
        effect.replaceEffectSkillId(getMySkillId());
        action.addEffect(effect);

        return action;
    }
}
