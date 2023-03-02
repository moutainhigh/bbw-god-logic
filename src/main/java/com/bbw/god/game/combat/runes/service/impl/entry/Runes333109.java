package com.bbw.god.game.combat.runes.service.impl.entry;

import com.bbw.god.game.combat.PositionService;
import com.bbw.god.game.combat.data.CombatBuff;
import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.combat.runes.service.IRoundStageRunes;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 同命词条 我方卡牌击杀对位卡牌时自身将减少等同于本次伤害[30]%的防御值。
 *
 * @author longwh
 * @date 2022/12/30 15:50
 */
@Service
public class Runes333109 implements IRoundStageRunes {

    @Override
    public int getRunesId() {
        return RunesEnum.TONG_MING_ENTRY.getRunesId();
    }

    @Override
    public Action doRoundRunes(CombatRunesParam param) {
        Action action = new Action();
        for (Effect effect : param.getReceiveEffect()) {
            if (!PositionService.isPlayingPos(effect.getSourcePos())) {
                continue;
            }
            if (!isPerformSelf(effect.getSourcePos(), param.getPerformPlayer().getId())) {
                continue;
            }
            if (!effect.isValueEffect()) {
                continue;
            }
            int effectHp = effect.toValueEffect().getHp() + effect.toValueEffect().getRoundHp();
            // 非伤害 不处理
            if (effectHp > 0) {
                continue;
            }
            // 目标为玩家不处理
            if (PositionService.isZhaoHuanShiPos(effect.getTargetPos())) {
                continue;
            }
            Optional<BattleCard> performCard = PositionService.getCard(param.getPerformPlayer(), effect.getSourcePos());
            if (!performCard.isPresent()) {
                continue;
            }
            BattleCard performCardFaceCard = param.getOppoPlayer().getPlayingCards(PositionService.getBattleCardIndex(performCard.get().getPos()));
            // 释放技能卡牌 无对位卡牌 不处理
            if (performCardFaceCard == null) {
                continue;
            }
            // 对位卡牌必须为技能目标
            if (effect.getTargetPos() != performCardFaceCard.getPos()) {
                continue;
            }
            // 受到本次伤害[30]%的防御值
            CombatBuff combatBuff = param.getPerformPlayer().gainBuff(getRunesId());
            double buffHp;
            if (CombatSkillEnum.NORMAL_ATTACK.getValue() == effect.getPerformSkillID()) {
                // 卡牌未死亡 不处理
                if (performCardFaceCard.isAlive()) {
                    continue;
                }
                // 为普通攻击效果时
                CardValueEffect valueEffect = effect.toValueEffect();
                int reHp = performCardFaceCard.getReduceRoundTempHp() + valueEffect.getRoundHp() + valueEffect.getHp();
                buffHp = -0.3 * combatBuff.getLevel() * (performCardFaceCard.getRoundHp() - reHp);
            }else {
                int cardHp = performCardFaceCard.getHp() + effect.toValueEffect().getHp() + effect.toValueEffect().getRoundHp();
                // 伤害无法致 对位卡牌死亡 不处理
                if (cardHp > 0) {
                    continue;
                }
                // 为技能效果时
                buffHp = -0.3 * combatBuff.getLevel() * performCardFaceCard.getHp();
            }
            CardValueEffect runesEffect = CardValueEffect.getSkillEffect(getRunesId(), performCard.get().getPos());
            runesEffect.setSequence(param.getNextSeq());
            runesEffect.setHp((int) buffHp);
            action.addEffect(runesEffect);
        }
        return action;
    }
}