package com.bbw.god.server.maou.attack.skill;

import com.bbw.common.PowerRandom;
import com.bbw.god.game.combat.data.CombatSkillEnum;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author suhq
 * @description: 暴击
 * @date 2019-12-31 13:46
 **/
@Service
public class SkillBaoJ implements ISkillService {

    @Override
    public List<SkillPerformResult> peform(SkillPerformParam param) {
        if (!isAblePerform()) {
            return new ArrayList<>();
        }
        int performCardId = param.getPerformCard().getId();
        int baseAck = param.getPerformCard().getAtk();
        int hv = param.getPerformCard().getHv();
        Double ackBufDouble = baseAck * (0.5 + 0.1 * hv);
        int ackBuf = ackBufDouble.intValue();
        List<SkillPerformResult> results = new ArrayList<>();
        SkillPerformResult result = SkillPerformResult.getInstance(param.getPerformCard(), getPerformId(), param.getPerformCard(), ackBuf);
        results.add(result);
        return results;
    }

    private boolean isAblePerform() {
        return PowerRandom.getRandomBySeed(100) <= 70;
    }

    @Override
    public int getPerformId() {
        return CombatSkillEnum.BJ.getValue();
    }
}
