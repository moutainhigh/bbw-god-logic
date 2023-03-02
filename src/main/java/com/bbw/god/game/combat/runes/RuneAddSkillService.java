package com.bbw.god.game.combat.runes;

import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.combat.data.TimesLimit;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.skill.BattleSkill;
import com.bbw.god.game.config.card.CfgCardSkill;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 护符添加技能服务
 *
 * @author: suhq
 * @date: 2021/11/12 6:14 下午
 */
@Service
public class RuneAddSkillService {

    /**
     * 为卡牌添加技能，如果卡牌自带该技能 则将自带的替换成符文添加的
     *
     * @param runeId
     * @param targetCard
     * @param addSkills
     */
    public void addSkillTOCard(int runeId, BattleCard targetCard, List<CfgCardSkill> addSkills) {
        List<Integer> skillIdList = addSkills.stream().map(CfgCardSkill::getId).collect(Collectors.toList());
        List<BattleSkill> battleSkills = targetCard.getSkills().stream().filter(p -> !skillIdList.contains(p.getId())).collect(Collectors.toList());
        for (CfgCardSkill cardSkill : addSkills) {
            battleSkills.add(BattleSkill.instanceSkill(runeId, cardSkill));
        }
        targetCard.setSkills(battleSkills);
        targetCard.setBuff(runeId);
    }


    /**
     * 为卡牌添加生效1次的技能，如果卡牌自带该技能则跳过
     *
     * @param runeId
     * @param targetCard
     * @param skill
     */
    public void addSkillTOCard(int runeId, BattleCard targetCard, CombatSkillEnum skill) {
        int addSkillId = skill.getValue();
        if (targetCard.getSkills().stream().anyMatch(tmp -> tmp.getId() == addSkillId && tmp.getTimesLimit().hasPerformTimes())) {
            return;
        }
        List<BattleSkill> battleSkills = targetCard.getSkills().stream().filter(p -> addSkillId != p.getId()).collect(Collectors.toList());
        battleSkills.add(BattleSkill.instanceSkill(runeId, addSkillId, true, TimesLimit.oneTimeLimit()));
        targetCard.setSkills(battleSkills);
        targetCard.setBuff(runeId);
    }
}
