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
 * 痘仙：每回合，对敌方全体卡牌施放1次【入痘】。
 *
 * @author: suhq
 * @date: 2022/5/9 2:35 下午
 */
@Service
public class BattleSkill3171 extends BattleSkillService {
    private static final int SKILL_ID = CombatSkillEnum.DOU_XIAN.getValue();
    @Autowired
    private BattleSkill3127 battleSkill3127;

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
            List<Effect> effects = battleSkill3127.buildEffects(targetCard, psp).getEffects();
            for (Effect effect : effects) {
                effect.replaceEffectSkillId(getMySkillId());
            }
            action.addEffects(effects);
            AnimationSequence asq = ClientAnimationService.getSkillAction(sequence, CombatSkillEnum.RD.getValue(),
                    psp.getPerformCard().getPos(), targetCard.getPos());
            action.addClientAction(asq);
        }

        return action;
    }
}
