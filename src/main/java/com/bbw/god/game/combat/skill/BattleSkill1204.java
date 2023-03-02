package com.bbw.god.game.combat.skill;

import com.bbw.common.PowerRandom;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardPositionEffect;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.card.PositionType;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 怨灵 被击退后，破除敌方场上全体卡牌（不含云台）20%~30%防御（最大防御），每升一阶增加5%的效果。
 *
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-08 15:32
 */
@Service
public class BattleSkill1204 extends BattleDieSkill {
    private static final int SKILL_ID = 1204;// 技能ID

    @Override
    public int getMySkillId() {
        return SKILL_ID;
    }

    @Override
    protected Action performSkill(PerformSkillParam psp) {

        Action attackResult = new Action();
        // 怨灵 被击退后，破除敌方场上全体卡牌（不含云台）20%~30%防御（最大防御），每升一阶增加5%的效果。
        List<BattleCard> oppoPlayingCards = psp.getOppoPlayingCards(false);
        if (oppoPlayingCards.isEmpty()) {
            return attackResult;
        }
        int seq = psp.getNextAnimationSeq();
        for (BattleCard card : oppoPlayingCards) {
            int maxHp = card.getRoundHp() > card.getHp() ? card.getRoundHp() : card.getHp();
            int min = this.getInt(maxHp * (0.2 + 0.05 * psp.getPerformCard().getHv()));
            int max = this.getInt(maxHp * (0.3 + 0.05 * psp.getPerformCard().getHv()));
            int hp = PowerRandom.getRandomBetween(min, max);
            CardValueEffect effect = CardValueEffect.getSkillEffect(SKILL_ID, card.getPos());
            effect.setHp(-hp);
            effect.setSequence(seq);
            attackResult.addEffect(effect);
        }
        //立即移除到对局
        CardPositionEffect effect = CardPositionEffect.getSkillEffectToTargetPos(getMySkillId(), psp.getPerformCard().getPos());
        effect.moveTo(PositionType.DEGENERATOR);
        effect.setToPositionType(PositionType.DEGENERATOR);
        effect.setSequence(seq);
        attackResult.addEffect(effect);
        return attackResult;

    }
}
