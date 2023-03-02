package com.bbw.god.game.combat.runes.service.impl;

import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.service.AbstractAddSkillRunes;
import com.bbw.god.game.config.card.CardSkillTool;
import com.bbw.god.game.config.card.CfgCardSkill;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 疾驰符31070  己方全体卡牌获得疾驰技能，该技能无法被封印。
 * @author lwb
 * @date 2020/6/8 10:05
 */
@Service
public class Runes131070 extends AbstractAddSkillRunes {
    @Override
    public int getRunesId() {
        return 131070;
    }

    @Override
    public void doInitRunes(CombatRunesParam param) {
        addSkill(param.getPerformPlayer());
    }

    @Override
    public List<CfgCardSkill> getAddSkills() {
        List<CfgCardSkill> list=new ArrayList<>();
        list.add(CardSkillTool.getCardSkillOpById(CombatSkillEnum.JC.getValue()).get());
        return list;
    }
}
