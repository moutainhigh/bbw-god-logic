package com.bbw.god.game.combat.runes.service.impl;

import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.combat.runes.service.AbstractAddSkillRunes;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.config.card.CardSkillTool;
import com.bbw.god.game.config.card.CfgCardSkill;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 钻地符 131460 己方全体卡牌获得钻地技能。
 * @author lwb
 * @date 2020/6/8 10:05
 */
@Service
public class Runes131460 extends AbstractAddSkillRunes {
    @Override
    public int getRunesId() {
        return 131460;
    }

    @Override
    public void doInitRunes(CombatRunesParam param) {
        addSkill(param.getPerformPlayer());
    }

    @Override
    public List<CfgCardSkill> getAddSkills() {
        List<CfgCardSkill> list=new ArrayList<>();
        list.add(CardSkillTool.getCardSkillOpById(CombatSkillEnum.ZD.getValue()).get());
        return list;
    }
}
