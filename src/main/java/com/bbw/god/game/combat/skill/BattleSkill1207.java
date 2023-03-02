package com.bbw.god.game.combat.skill;

import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardPositionEffect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.card.PositionType;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 鬼雄：死亡时击杀全场星级不大于自己的卡牌(包括金刚)，之后移出游戏。对拥有【金身】的卡牌无效。
 *
 * @author lwb
 * @date 2021/7/16 下午4:08
 **/
@Service
public class BattleSkill1207 extends BattleDieSkill {
    private static final int SKILL_ID = 1207;// 技能ID

    @Override
    public int getMySkillId() {
        return SKILL_ID;
    }

    @Override
    protected Action performSkill(PerformSkillParam psp) {
        Action attackResult = new Action();
        List<BattleCard> oppoPlayingCards = psp.getOppoPlayingCards(false);
        if (oppoPlayingCards.isEmpty()) {
            return attackResult;
        }
        int star = psp.getPerformCard().getStars();
        int seq = psp.getNextAnimationSeq();
        for (BattleCard card : oppoPlayingCards) {
            if (card.getStars() > star) {
                continue;
            }
            if (card.hasSkill(CombatSkillEnum.JIN_SHEN.getValue())) {
                continue;
            }
            CardPositionEffect effect = CardPositionEffect.getSkillEffectToTargetPos(SKILL_ID, card.getPos());
            effect.setToPositionType(PositionType.DISCARD);
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
