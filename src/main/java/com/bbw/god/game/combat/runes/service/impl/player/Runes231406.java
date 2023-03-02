package com.bbw.god.game.combat.runes.service.impl.player;

import com.bbw.god.game.combat.data.CombatBuff;
import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.combat.runes.service.AbstractAddSkillRunes;
import com.bbw.god.game.config.card.CardSkillTool;
import com.bbw.god.game.config.card.CfgCardSkill;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 金刚符图 231406：战斗开始时，有30%概率（可升级）发动，己方全体卡牌获得【金刚】。等级效果：每级增加7%概率
 *
 * @author longwh
 * @date 2023/2/28 15:07
 */
@Service
public class Runes231406 extends AbstractAddSkillRunes {
    @Override
    public int getRunesId() {
        return RunesEnum.JIN_GANG_PLAYER.getRunesId();
    }

    @Override
    public void doInitRunes(CombatRunesParam param) {
        CombatBuff combatBuff = param.getPerformPlayer().gainBuff(getRunesId());
        if (null == combatBuff || !combatBuff.ifToPerform(30, 7)) {
            return;
        }
        addSkill(param.getPerformPlayer());
    }

    @Override
    public List<CfgCardSkill> getAddSkills() {
        List<CfgCardSkill> list = new ArrayList<>();
        list.add(CardSkillTool.getCardSkillOpById(CombatSkillEnum.JG.getValue()).get());
        return list;
    }
}