package com.bbw.god.game.combat.runes.service.impl.player;

import com.bbw.common.ListUtil;
import com.bbw.common.PowerRandom;
import com.bbw.god.game.combat.ClientAnimationService;
import com.bbw.god.game.combat.data.AnimationSequence;
import com.bbw.god.game.combat.data.CombatBuff;
import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.combat.data.Player;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.combat.runes.service.IRoundStageRunes;
import com.bbw.god.game.combat.skill.BattleSkill3103;
import com.bbw.god.game.config.card.SkillEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 治愈符图	3阶	每回合开始时，有30%概率（可升级）对己方召唤师施放【治愈】。	每额外+7%概率
 *
 * @author: suhq
 * @date: 2022/5/25 9:35 上午
 */
@Service
public class Runes231207 implements IRoundStageRunes {
    @Autowired
    private BattleSkill3103 battleSkill3103;

    @Override
    public int getRunesId() {
        return RunesEnum.ZHI_YU_PLAYER.getRunesId();
    }

    @Override
    public Action doRoundRunes(CombatRunesParam param) {
        Player performPlayer = param.getPerformPlayer();
        Action ar = new Action();
        CombatBuff combatBuff = performPlayer.gainBuff(getRunesId());
        if (!combatBuff.ifToPerform(30, 7)) {
            return ar;
        }
        if (performPlayer.getHp() >= performPlayer.getMaxHp()) {
            return ar;
        }
        int lv = 0;
        int hv = 0;
        List<BattleCard> playingCards = performPlayer.getPlayingCards(true);
        if (ListUtil.isNotEmpty(playingCards)) {
            BattleCard randomCard = PowerRandom.getRandomFromList(playingCards);
            lv = randomCard.getLv();
            hv = randomCard.getHv();
        }
        CardValueEffect effect = battleSkill3103.buildEffect(performPlayer, lv, hv, 0);
        ar.addEffect(effect);
        AnimationSequence as = ClientAnimationService.getSkillAction(0, CombatSkillEnum.ZY.getValue(), -1, param.getMyPlayerPos());
        ar.addClientAction(as);
        return ar;
    }
}
