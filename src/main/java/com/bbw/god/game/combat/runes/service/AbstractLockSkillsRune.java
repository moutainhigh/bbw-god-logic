package com.bbw.god.game.combat.runes.service;

import com.bbw.god.game.combat.data.CombatBuff;
import com.bbw.god.game.combat.data.TimesLimit;
import com.bbw.god.game.combat.data.attack.BattleSkillEffect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.skill.BattleSkill;
import com.bbw.god.game.combat.runes.CombatRunesParam;

import java.util.List;
import java.util.Optional;

/**
 * 战斗开始时，有30%概率（可升级）发动，使敌方卡牌***无法发动。	每级额外+7%概率
 *
 * @author: suhq
 * @date: 2022/5/25 10:21 上午
 */
public abstract class AbstractLockSkillsRune implements IInitStageRunes {

    public abstract List<Integer> getSKillsToBan();

    @Override
    public void doInitRunes(CombatRunesParam param) {
        CombatBuff combatBuff = param.getPerformPlayer().gainBuff(getRunesId());
        if (!combatBuff.ifToPerform(30, 7)) {
            return;
        }
        List<BattleCard> cards = param.getOppoPlayer().getDrawCards();
        for (BattleCard card : cards) {
            if (null == card) {
                continue;
            }
            for (Integer skillToBan : getSKillsToBan()) {
                Optional<BattleSkill> skillOp = card.getSkill(skillToBan);
                // 不存在
                if (!skillOp.isPresent()) {
                    continue;
                }
                card.getSkills().removeIf(s -> s.getId() == skillOp.get().getId());
            }
        }
    }
}
