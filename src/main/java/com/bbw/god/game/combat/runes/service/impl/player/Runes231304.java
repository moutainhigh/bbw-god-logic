package com.bbw.god.game.combat.runes.service.impl.player;

import com.bbw.common.ListUtil;
import com.bbw.common.PowerRandom;
import com.bbw.god.game.combat.ClientAnimationService;
import com.bbw.god.game.combat.PositionService;
import com.bbw.god.game.combat.data.AnimationSequence;
import com.bbw.god.game.combat.data.CombatBuff;
import com.bbw.god.game.combat.data.Player;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.BattleSkillEffect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.skill.BattleSkill;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.combat.runes.service.IRoundStageRunes;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 侵蚀符图	4阶	每回合开始时，有30%概率（可升级）永久侵蚀敌方场上1张卡牌一个技能，被侵蚀的技能无法发动。	每级额外+7%概率
 *
 * @author: suhq
 * @date: 2022/5/25 10:21 上午
 */
@Service
public class Runes231304 implements IRoundStageRunes {

    @Override
    public int getRunesId() {
        return RunesEnum.QIN_SHI_PLAYER.getRunesId();
    }

    @Override
    public Action doRoundRunes(CombatRunesParam param) {
        Player player = param.getPerformPlayer();
        Action ar = new Action();
        CombatBuff combatBuff = player.gainBuff(getRunesId());
        if (!combatBuff.ifToPerform(30, 7)) {
            return ar;
        }
        List<BattleCard> oppPlayingCards = param.getOppoPlayer().getPlayingCards(true);
        if (ListUtil.isEmpty(oppPlayingCards)) {
            return ar;
        }
        BattleCard targetCard = PowerRandom.getRandomFromList(oppPlayingCards);
        //获取可侵蚀的技能
        List<BattleSkill> ableBanSkills = targetCard.getSkills().stream().filter(tmp -> !tmp.isForbid()).collect(Collectors.toList());
        if (ListUtil.isEmpty(ableBanSkills)) {
            return ar;
        }
        int animSeq = param.getNextSeq();
        int effectSeq = param.getNextSeq();
        BattleSkill skillToBan = PowerRandom.getRandomFromList(ableBanSkills);
        BattleSkillEffect effect = BattleSkillEffect.getSkillEffect(getRunesId(), targetCard.getPos());
        effect.setLastRound(Integer.MAX_VALUE);
        effect.setSequence(effectSeq);
        effect.forbid(skillToBan, getRunesId());
        ar.addEffect(effect);
        AnimationSequence anim = ClientAnimationService.getSkillAction(animSeq, effect.getSourceID(), PositionService.getZhaoHuanShiPos(player.getId()), targetCard.getPos());
        ar.addClientAction(anim);
        return ar;
    }
}
