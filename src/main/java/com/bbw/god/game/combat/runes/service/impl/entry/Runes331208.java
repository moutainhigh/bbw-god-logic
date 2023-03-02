package com.bbw.god.game.combat.runes.service.impl.entry;

import com.bbw.common.ListUtil;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 吸取词条 敌方卡牌普通攻击击退卡牌后，提高等同于被击退卡牌的[3]%永久攻防。
 *
 * @author: suhq
 * @date: 2022/9/22 2:13 下午
 */
@Slf4j
@Service
public class Runes331208 implements IRoundStageRunes {

    @Override
    public int getRunesId() {
        return RunesEnum.XI_QU_ENTRY.getRunesId();
    }

    @Override
    public Action doRoundRunes(CombatRunesParam param) {
        Action action = new Action();
        action.setNeedAddAnimation(false);
        if (ListUtil.isEmpty(param.getReceiveEffect())) {
            return action;
        }
        CombatBuff combatBuff = param.getPerformPlayer().gainBuff(getRunesId());
//        int seq = param.getNextSeq();
        for (Effect effect : param.getReceiveEffect()) {
            int performSkill = effect.getPerformSkillID();
            if (CombatSkillEnum.NORMAL_ATTACK.getValue() != performSkill) {
                continue;
            }
            if (isPerformSelf(effect.getSourcePos(), param.getPerformPlayer().getId())) {
                continue;
            }
            if (PositionService.isZhaoHuanShiPos(effect.getTargetPos())) {
                continue;
            }
            int targetIndex = PositionService.getBattleCardIndex(effect.getTargetPos());
            BattleCard targetCard = param.getPerformPlayer().getPlayingCards(targetIndex);
            //可能为null
            if (null == targetCard) {
                log.error("{}吸取词条获取效果目标为null:{}", param.getPerformPlayer().getUid(), effect);
                continue;
            }

            int addBuff = (int) (0.03 * targetCard.getAtk() * combatBuff.getLevel());

            CardValueEffect runeEffect = CardValueEffect.getSkillEffect(getRunesId(), effect.getSourcePos());
            runeEffect.setRoundAtk(addBuff);
            runeEffect.setRoundHp(addBuff);
            action.addEffect(runeEffect);
            action.setNeedAddAnimation(true);
        }
        return action;
    }
}
