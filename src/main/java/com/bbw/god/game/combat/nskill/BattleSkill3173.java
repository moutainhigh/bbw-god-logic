package com.bbw.god.game.combat.nskill;

import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import org.springframework.stereotype.Service;

/**
 * 白虎 3173:自带【祛毒】、【解封】。
 *
 * @author: suhq
 * @date: 2022/8/26 10:49 上午
 */
@Service
public class BattleSkill3173 extends BattleSkillService {
    /** 技能ID */
    private static final int SKILL_ID = CombatSkillEnum.BAI_HU.getValue();

    @Override
    public int getMySkillId() {
        return SKILL_ID;
    }

    @Override
    protected Action attack(PerformSkillParam psp) {
        return new Action();
    }
}
