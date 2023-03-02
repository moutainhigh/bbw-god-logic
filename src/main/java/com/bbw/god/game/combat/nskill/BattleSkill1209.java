package com.bbw.god.game.combat.nskill;

import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import org.springframework.stereotype.Service;

/**
 * 朱雀 1209:自带【飞行】、【复活】。
 *
 * @author: suhq
 * @date: 2022/8/26 10:49 上午
 */
@Service
public class BattleSkill1209 extends BattleSkillService {
    /** 技能ID */
    private static final int SKILL_ID = CombatSkillEnum.ZHU_QUE.getValue();

    @Override
    public int getMySkillId() {
        return SKILL_ID;
    }

    @Override
    protected Action attack(PerformSkillParam psp) {
        return new Action();
    }
}
