package com.bbw.god.game.combat.runes.service;

import com.bbw.god.game.combat.data.Player;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.CombatRunesStageTool;
import com.bbw.god.game.combat.runes.RuneAddSkillService;
import com.bbw.god.game.config.card.CfgCardSkill;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 所有添加技能的符文 继承
 *
 * @author lwb
 * @date 2020/9/16 16:27
 */
public abstract class AbstractAddSkillRunes implements IInitStageRunes {
    @Autowired
    private RuneAddSkillService runeAddSkillService;

    @Override
    public void doInitRunes(CombatRunesParam param) {
        addSkill(param.getPerformPlayer());
    }

    public void addSkill(Player player) {
        for (BattleCard card : player.getDrawCards()) {
            if (card == null) {
                continue;
            }
            addSkillTOCard(card);
        }
    }

    public void addSkillTOCard(BattleCard card) {
        runeAddSkillService.addSkillTOCard(getRunesId(), card, getAddSkills());
    }

    public abstract List<CfgCardSkill> getAddSkills();

}