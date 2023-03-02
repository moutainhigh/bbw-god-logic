package com.bbw.god.server.maou.attack.skill;

import com.bbw.god.game.combat.data.CombatSkillEnum;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author suhq
 * @description: 常规物理攻击
 * @date 2019-12-31 13:46
 **/
@Service
public class SkillNormalAttack implements ISkillService {

    @Override
    public List<SkillPerformResult> peform(SkillPerformParam param) {
        final int atkBuf = param.getPerformCard().getAtk();
        List<SkillPerformResult> results = new ArrayList<>();
        SkillPerformResult result = SkillPerformResult.getInstance(param.getPerformCard(), getPerformId(), param.getPerformCard(), atkBuf);
        results.add(result);
        return results;
    }

    @Override
    public int getPerformId() {
        return CombatSkillEnum.NORMAL_ATTACK.getValue();
    }
}
