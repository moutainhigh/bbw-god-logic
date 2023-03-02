package com.bbw.god.game.combat.runes.service.impl.entry;

import com.bbw.god.game.combat.data.skill.SkillSection;
import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.combat.runes.service.AbstractForbidSkillsRunes;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 沉默词条 我方卡牌的回合技能将有[30]%概率无法发动。
 *
 * @author longwh
 * @date 2023/1/4 15:48
 */
@Service
public class Runes333205 extends AbstractForbidSkillsRunes {
    @Override
    public Integer getBaseProb() {
        return 30;
    }

    @Override
    public Integer getLevelProbStep() {
        return 0;
    }

    @Override
    public List<Integer> getSkillsToForbid() {
        List<Integer> skills = new ArrayList<>();
        Arrays.stream(SkillSection.getSkillAttackSection().getSkills()).forEach(skills::add);
        return skills;
    }

    @Override
    public int getRunesId() {
        return RunesEnum.CHEN_MO_ENTRY.getRunesId();
    }
}