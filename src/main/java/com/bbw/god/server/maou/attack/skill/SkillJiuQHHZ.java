package com.bbw.god.server.maou.attack.skill;

import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.server.maou.attack.MaouAttackCard;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author suhq
 * @description: 九曲黄河阵
 * 三宵同时在场，全体上阵卡牌攻防翻倍
 * @date 2019-12-31 13:46
 **/
@Service
public class SkillJiuQHHZ implements ISkillService {

    @Override
    public List<SkillPerformResult> peform(SkillPerformParam param) {
        List<MaouAttackCard> attackCards = param.getAttackCards();
        List<MaouAttackCard> groupCards = attackCards.stream().filter(tmp -> tmp.getGroupId() == getPerformId()).collect(Collectors.toList());
        if (groupCards.size() < 3) {
            return new ArrayList<>();
        }
        List<SkillPerformResult> results = groupCards.stream()
                .map(tmp -> SkillPerformResult.getInstance(getPerformId(), tmp, tmp.getAtk()))
                .collect(Collectors.toList());
        return results;
    }

    @Override
    public int getPerformId() {
        return CombatSkillEnum.JQHHZ_G.getValue();
    }
}
