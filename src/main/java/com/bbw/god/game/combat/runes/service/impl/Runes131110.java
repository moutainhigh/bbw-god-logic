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
 * 无相符 131110  己方全体卡牌获得无相技能，该技能无法被封印。
 * @author lwb
 * @date 2020/6/8 10:05
 */
@Service
public class Runes131110 extends AbstractAddSkillRunes {
    @Override
    public int getRunesId() {
        return 131110;
    }


    @Override
    public void doInitRunes(CombatRunesParam param) {
        addSkill(param.getPerformPlayer());
    }

    @Override
    public List<CfgCardSkill> getAddSkills() {
        List<CfgCardSkill> list=new ArrayList<>();
        list.add(CardSkillTool.getCardSkillOpById(CombatSkillEnum.WX.getValue()).get());
        return list;
    }
}
