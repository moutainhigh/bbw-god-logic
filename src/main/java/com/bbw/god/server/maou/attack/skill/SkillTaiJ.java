package com.bbw.god.server.maou.attack.skill;

import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.server.maou.attack.MaouAttackCard;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * @author suchaobin
 * @description: 太极
 * @date 2020-07-30 17:40
 **/
@Service
public class SkillTaiJ implements ISkillService {

    @Override
    public List<SkillPerformResult> peform(SkillPerformParam param) {
        MaouAttackCard performCard = param.getPerformCard();
        int atk = performCard.getAtk();
        int ackBuf = (int) (0.5 * atk);
        return Arrays.asList(SkillPerformResult.getInstance(performCard, getPerformId(), performCard, ackBuf));
    }

    @Override
    public int getPerformId() {
        return CombatSkillEnum.TJ.getValue();
    }
}
