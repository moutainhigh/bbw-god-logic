package com.bbw.god.game.combat.runes.service.impl.entry;

import com.bbw.god.game.combat.data.skill.SkillSection;
import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.combat.runes.service.AbstractForbidSkillsRunes;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 禁言词条 我方卡牌的上场技能将有[25]%概率无法发动。
 *
 * @author longwh
 * @date 2023/1/4 9:38
 */
@Service
public class Runes333204 extends AbstractForbidSkillsRunes {

    @Override
    public int getRunesId() {
        return RunesEnum.JIN_YAN_ENTRY.getRunesId();
    }

    @Override
    public Integer getBaseProb() {
        return 25;
    }

    @Override
    public Integer getLevelProbStep() {
        return 0;
    }

    @Override
    public List<Integer> getSkillsToForbid() {
        List<Integer> skills = new ArrayList<>();
        Arrays.stream(SkillSection.getDeploySection().getSkills()).forEach(skills::add);
        return skills;
    }
}