package com.bbw.god.game.combat.runes.service.impl;

import com.bbw.god.game.combat.data.TimesLimit;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.skill.BattleSkill;
import com.bbw.god.game.combat.data.skill.SkillSection;
import com.bbw.god.game.combat.runes.service.AbstractAddSkillRunes;
import com.bbw.god.game.config.card.CfgCardSkill;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 天赋符 131460 己方卡牌拥有的上场技能，可以每回合发动一次。
 * @author lwb
 * @date 2020/6/8 10:05
 */
@Service
public class Runes131360 extends AbstractAddSkillRunes {

    private static SkillSection goToPlayingSkill=SkillSection.getDeploySection();
    @Override
    public int getRunesId() {
        return 131360;
    }

    @Override
    public void addSkillTOCard(BattleCard card) {
        for (BattleSkill skill : card.getSkills()) {
            if (goToPlayingSkill.contains(skill.getId())){
                skill.setTimesLimit(TimesLimit.instance(1000,1,1000));
            }
        }
    }

    @Override
    public List<CfgCardSkill> getAddSkills() {
        return new ArrayList<>();
    }
}
