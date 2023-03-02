package com.bbw.god.server.maou.attack.skill;

import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.server.maou.attack.MaouAttackCard;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author suhq
 * @description: 梅山七怪
 * @date 2019-12-31 13:46
 **/
@Service
public class SkillHuangJJ implements ISkillService {

    @Override
    public List<SkillPerformResult> peform(SkillPerformParam param) {
        List<MaouAttackCard> attackCards = param.getAttackCards();
        List<MaouAttackCard> groupCards = attackCards.stream().filter(tmp -> tmp.getGroupId() == getPerformId()).collect(Collectors.toList());
        if (groupCards.size() <= 1) {
            return new ArrayList<>();
        }
        final int ackBuf = 300;
        List<SkillPerformResult> results = groupCards.stream()
                .map(tmp -> SkillPerformResult.getInstance(getPerformId(), tmp, ackBuf))
                .collect(Collectors.toList());
        return results;
    }

    @Override
    public int getPerformId() {
        return CombatSkillEnum.HJJ_G.getValue();
    }
}
