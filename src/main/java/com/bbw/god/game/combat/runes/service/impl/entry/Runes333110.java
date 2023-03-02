package com.bbw.god.game.combat.runes.service.impl.entry;

import com.bbw.common.CloneUtil;
import com.bbw.god.game.combat.BattleCardService;
import com.bbw.god.game.combat.data.AnimationSequence;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardPositionEffect;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.card.PositionType;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.combat.runes.service.IRoundStageRunes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 策反词条 我方卡牌死亡后将该卡牌移除对局，并将一张该卡牌的复制牌洗入对方牌库。
 *
 * @author longwh
 * @date 2023/1/2 9:30
 */
@Service
public class Runes333110 implements IRoundStageRunes {
    @Autowired
    private BattleCardService battleCardService;

    @Override
    public int getRunesId() {
        return RunesEnum.CE_FAN_ENTRY.getRunesId();
    }

    @Override
    public Action doRoundRunes(CombatRunesParam param) {
        Action action = new Action();
        // 敌方卡不处理
        if (param.isEnemyTargetCard()) {
            return action;
        }
        int targetPos = param.getTargetCard().getPos();
        // 我方卡牌移除对局
        CardPositionEffect moveEffect = CardPositionEffect.getSkillEffectToTargetPos(getRunesId(), targetPos);
        moveEffect.setToPositionType(PositionType.DEGENERATOR);
        moveEffect.setSequence(param.getNextSeq());
        action.addEffect(moveEffect);
        // 获取敌方牌堆空余位置
        int toPos = battleCardService.getEmptyPos(param.getOppoPlayer(), PositionType.DRAWCARD);
        BattleCard cloneCard = CloneUtil.clone(param.getTargetCard());
        cloneCard.setPos(toPos);
        battleCardService.replaceCard(param.getOppoPlayer(), cloneCard);
        // 补充卡牌变更效果
        AnimationSequence targetPosAs = new AnimationSequence(param.getNextSeq(), Effect.EffectResultType.CARD_POSITION_CHANGE);
        AnimationSequence.Animation targetPosAction = new AnimationSequence.Animation();
        targetPosAction.setPos1(targetPos);
        targetPosAction.setPos2(toPos);
        targetPosAction.setSkill(getRunesId());
        targetPosAs.add(targetPosAction);
        action.addClientAction(targetPosAs);
        return action;
    }
}