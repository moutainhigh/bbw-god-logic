package com.bbw.god.game.combat.runes.service.impl.player;

import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.combat.runes.service.AbstractLockSkillsRune;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * 固灵符图	3阶	战斗开始时，有30%概率（可升级）发动，使敌方卡牌【怨灵】、【咒怨】无法发动。	每级额外+7%概率
 *
 * @author: suhq
 * @date: 2022/5/25 9:05 上午
 */
@Service
public class Runes231205 extends AbstractLockSkillsRune {
    private static List<Integer> SKILLS_TO_BAN = Arrays.asList(
            CombatSkillEnum.YL.getValue(),
            CombatSkillEnum.ZHOU_YUAN.getValue()
    );

    @Override
    public int getRunesId() {
        return RunesEnum.GU_LING_PLAYER.getRunesId();
    }

    @Override
    public List<Integer> getSKillsToBan() {
        return SKILLS_TO_BAN;
    }
}
