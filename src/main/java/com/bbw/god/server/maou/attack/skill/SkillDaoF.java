package com.bbw.god.server.maou.attack.skill;

import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.server.maou.attack.MaouAttackCard;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author suhq
 * @description: 道法技能
 * @date 2019-12-31 13:46
 **/
@Service
public class SkillDaoF implements ISkillService {

    @Override
    public List<SkillPerformResult> peform(SkillPerformParam param) {
        int hv = param.getPerformCard().getHv();
        final int ackBuf = 500 + 30 * hv;
        List<MaouAttackCard> attackCards = param.getAttackCards();
        List<SkillPerformResult> results = attackCards.stream()
                .filter(tmp -> tmp.getId() != param.getPerformCard().getId())
                .map(tmp -> SkillPerformResult.getInstance(param.getPerformCard(), getPerformId(), tmp, ackBuf))
                .collect(Collectors.toList());
        return results;
    }

    @Override
    public int getPerformId() {
        return CombatSkillEnum.DF.getValue();
    }
}
