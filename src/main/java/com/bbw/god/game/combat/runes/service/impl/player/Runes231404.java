package com.bbw.god.game.combat.runes.service.impl.player;

import com.bbw.god.game.combat.data.CombatBuff;
import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.combat.data.Player;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.combat.runes.service.AbstractAddSkillRunes;
import com.bbw.god.game.config.card.CardSkillTool;
import com.bbw.god.game.config.card.CfgCardSkill;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 疾军符图	5阶	战斗开始时，有30%概率（可升级）发动，己方全体卡牌获得【疾驰】。	每级额外+7%概率
 *
 * @author: suhq
 * @date: 2022/5/24 2:08 下午
 */
@Service
public class Runes231404 extends AbstractAddSkillRunes {

    @Override
    public int getRunesId() {
        return RunesEnum.JI_JUN_PLAYER.getRunesId();
    }

    @Override
    public void doInitRunes(CombatRunesParam param) {
        Player player = param.getPerformPlayer();
        CombatBuff combatBuff = player.gainBuff(getRunesId());
        if (!combatBuff.ifToPerform(30, 7)) {
            return;
        }
        addSkill(param.getPerformPlayer());
    }

    @Override
    public List<CfgCardSkill> getAddSkills() {
        List<CfgCardSkill> list = new ArrayList<>();
        list.add(CardSkillTool.getCardSkillOpById(CombatSkillEnum.JC.getValue()).get());
        return list;
    }
}
