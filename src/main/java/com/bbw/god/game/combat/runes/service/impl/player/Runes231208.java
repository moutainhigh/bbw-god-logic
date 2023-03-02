package com.bbw.god.game.combat.runes.service.impl.player;

import com.bbw.common.ListUtil;
import com.bbw.common.PowerRandom;
import com.bbw.god.game.combat.ClientAnimationService;
import com.bbw.god.game.combat.data.*;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.skill.BattleSkill;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.combat.runes.service.IRoundStageRunes;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 解禁符图	3阶	每回合开始时，有30%概率（可升级）解除己方场上1张卡牌的封禁、侵蚀状态。	每额外+7%概率 <br/>
 * 1、封禁状态由【禁术】、【封咒】、【蚀月】、【祭鞭】提供；
 * 2、侵蚀状态由【侵蚀符图】提供。
 * <br/>
 * 参考：BattleSkill1016
 *
 * @author: suhq
 * @date: 2022/5/25 9:35 上午
 */
@Service
public class Runes231208 implements IRoundStageRunes {
    private static List<Integer> CLEAR_SKILL_IDS = Arrays.asList(
            CombatSkillEnum.JINS.getValue(),
            CombatSkillEnum.FZ.getValue(),
            CombatSkillEnum.SY.getValue(),
            CombatSkillEnum.JI_BIAN.getValue(),
            RunesEnum.QIN_SHI_PLAYER.getRunesId()
    );

    @Override
    public int getRunesId() {
        return RunesEnum.JIE_JIN_PLAYER.getRunesId();
    }

    @Override
    public Action doRoundRunes(CombatRunesParam param) {
        Player player = param.getPerformPlayer();
        Action ar = new Action();
        CombatBuff combatBuff = player.gainBuff(getRunesId());
        if (!combatBuff.ifToPerform(30, 7)) {
            return ar;
        }
        List<BattleCard> ableForbidCards = player.getPlayingCards(true).stream().filter(card ->{
            return card.getStatus().stream().anyMatch(p -> check(p.getSkillID()));
        }).collect(Collectors.toList());
        if (ListUtil.isEmpty(ableForbidCards)){
            return ar;
        }
        BattleCard forbidCard = PowerRandom.getRandomFromList(ableForbidCards);
        forbidCard.setStatus(forbidCard.getStatus().stream().filter(p -> !check(p.getSkillID())).collect(Collectors.toSet()));
        for (BattleSkill skill : forbidCard.getSkills()) {
            int from = skill.getTimesLimit().getBanFrom();
            if (!check(from)) {
                continue;
            }
            skill.setTimesLimit(TimesLimit.noLimit());
        }
        AnimationSequence as = ClientAnimationService.getSkillAction(0, getRunesId(), param.getMyPlayerPos(), forbidCard.getPos());
        ar.addClientAction(as);
        return ar;
    }


    private boolean check(int fromId) {
        for (int id : CLEAR_SKILL_IDS) {
            if (fromId == id) {
                return true;
            }
        }
        return false;
    }
}
