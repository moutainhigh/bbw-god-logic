package com.bbw.god.game.combat.nskill;

import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import org.springframework.stereotype.Service;

/**
 * 诛绝 6005：自带【诛仙】、【绝仙】。
 *
 * @author fzj
 * @date 2022/7/6 9:13
 */
@Service
public class BattleSkill6005 extends BattleSkillService {
    /** 技能ID */
    private static final int SKILL_ID = CombatSkillEnum.ZHU_JUE.getValue();

    @Override
    public int getMySkillId() {
        return SKILL_ID;
    }

    @Override
    protected Action attack(PerformSkillParam psp) {
        return new Action();
    }
}
