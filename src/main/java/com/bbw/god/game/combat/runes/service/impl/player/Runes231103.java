package com.bbw.god.game.combat.runes.service.impl.player;

import com.bbw.common.ListUtil;
import com.bbw.common.PowerRandom;
import com.bbw.god.game.combat.data.CombatBuff;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.combat.runes.service.IRoundStageRunes;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 震慑符图	2阶	每回合开始时，有30%概率（可升级）对敌方1张地面卡牌施放【震慑】。	每级额外+7%概率
 *
 * @author: suhq
 * @date: 2021/11/12 5:16 下午
 */
@Service
public class Runes231103 implements IRoundStageRunes {
    @Override
    public int getRunesId() {
        return RunesEnum.ZHEN_SHE_PLAYER.getRunesId();
    }

    @Override
    public Action doRoundRunes(CombatRunesParam param) {
        Action ar = new Action();
        CombatBuff combatBuff = param.getPerformPlayer().gainBuff(getRunesId());
        if (!combatBuff.ifToPerform(30, 7)) {
            return ar;
        }
        List<BattleCard> cards = param.getOppoPlayer().getPlayingCards(false);
        if (ListUtil.isEmpty(cards)) {
            return ar;
        }
        BattleCard targetCard = PowerRandom.getRandomFromList(cards);
        CardValueEffect effect = CardValueEffect.getSkillEffect(getRunesId(), targetCard.getPos());
        effect.setAtk(-getInt(targetCard.getAtk() * 0.5f));
        effect.setSequence(param.getNextSeq());
        ar.addEffect(effect);
        return ar;
    }

}
