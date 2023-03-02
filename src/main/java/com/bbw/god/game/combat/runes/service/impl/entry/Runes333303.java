package com.bbw.god.game.combat.runes.service.impl.entry;

import com.bbw.common.ListUtil;
import com.bbw.god.game.combat.BattleCardService;
import com.bbw.god.game.combat.PositionService;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.skill.SkillSection;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.combat.runes.service.IRoundStageRunes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 分离词条 我方前军位卡牌不会受到除自身以外的任何技能效果影响。
 * 当我方卡牌处于前军位时不管是我方技能还是敌方技能，在以该卡牌为技能对象之后，技能效果都将无效。
 *
 * @author longwh
 * @date 2023/1/5 15:32
 */
@Service
public class Runes333303 implements IRoundStageRunes {
    @Autowired
    private BattleCardService battleCardService;

    @Override
    public int getRunesId() {
        return RunesEnum.FEN_LI_ENTRY.getRunesId();
    }

    @Override
    public Action doRoundRunes(CombatRunesParam param) {
        Action action = new Action();
        List<Effect> effectList = new ArrayList<>();
        for (Effect effect : param.getReceiveEffect()) {
            // 普通攻击不处理
            if (SkillSection.getNormalAttackSection().contains(effect.getSourceID())) {
                continue;
            }
            // 目标不是战场位置 不处理
            if (!PositionService.isPlayingPos(effect.getTargetPos())) {
                continue;
            }
            // 效果针对敌方不处理
            if (param.isEffectToEnemy()) {
                continue;
            }
            //前军位置
            int formerIndex = 2;
            // 不是前军位置 不处理
            if (PositionService.getBattleCardIndex(effect.getTargetPos()) != formerIndex) {
                continue;
            }
            // 获取符文发动方 effect目标的卡牌
            Optional<BattleCard> playerCard = battleCardService.getCard(param.getPerformPlayer(), effect.getTargetPos());
            if (!playerCard.isPresent()) {
                continue;
            }
            // 自身技能效果 不处理
            if (effect.getSourcePos() == playerCard.get().getPos()) {
                continue;
            }
            // 其他技能效果都将无效
            effect.setValid(false);
            // 触发 补充一个效果动画
            CardValueEffect animationEffect = CardValueEffect.getSkillEffect(getRunesId(), effect.getTargetPos());
            animationEffect.setSequence(param.getNextSeq());
            effectList.add(animationEffect);

        }
        if (ListUtil.isNotEmpty(effectList)) {
            action.addEffects(effectList);
        }
        return action;
    }
}