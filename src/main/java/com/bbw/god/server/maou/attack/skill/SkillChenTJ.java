package com.bbw.god.server.maou.attack.skill;

import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.server.maou.attack.MaouAttackCard;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author suhq
 * @description: 陈唐家
 * @date 2019-12-31 13:46
 **/
@Service
public class SkillChenTJ implements ISkillService {

    @Override
    public List<SkillPerformResult> peform(SkillPerformParam param) {
        List<MaouAttackCard> attackCards = param.getAttackCards();
        Long groupSize = attackCards.stream().filter(tmp -> tmp.getGroupId() == getPerformId()).count();
        if (groupSize <= 1) {
            return new ArrayList<>();
        }
        int ackBuf = 100;
        List<SkillPerformResult> results = attackCards.stream().map(tmp -> SkillPerformResult.getInstance(getPerformId(), tmp, ackBuf)).collect(Collectors.toList());
        return results;
    }

    @Override
    public int getPerformId() {
        return CombatSkillEnum.CTJ_G.getValue();
    }
}
