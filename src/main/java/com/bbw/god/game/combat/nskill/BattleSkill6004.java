package com.bbw.god.game.combat.nskill;

import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 绝仙 6004：被其法术技能杀死的卡牌死亡技能无法生效，且不进入坟场
 *
 * @author fzj
 * @date 2022/7/6 9:13
 */
@Service
public class BattleSkill6004 extends BattleSkillService {
    /** 技能ID */
    private static final int SKILL_ID = 6004;

    @Override
    public int getMySkillId() {
        return SKILL_ID;
    }

    @Override
    protected Action attack(PerformSkillParam psp) {
        return new Action();
    }

    /**
     * 是否触发绝仙
     *
     * @param psp
     * @return
     */
    public boolean isHasJueXian(PerformSkillParam psp){
        Effect effect = psp.getReceiveEffect();
        //法术攻击
        boolean isSkillEffect = effect.getSourceType() == Effect.EffectSourceType.SKILL;
        Optional<BattleCard> optional = psp.getEffectSourceCard();
        //伤害来源卡是否持有绝仙
        return optional.isPresent() && optional.get().getSkills().stream().anyMatch(s -> s.getId() == SKILL_ID) && isSkillEffect;
    }
}
