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
 * 集中词条 我方非后军位卡牌受到任何技能效果前，后军位卡牌受到相同效果并使原卡牌不受效果影响。
 * 当我方卡牌处于后军位时不管是我方技能还是敌方技能，以其他位置卡牌为技能对象之后，
 * 该卡牌将受到相同的技能效果，随后使原先作为目标的卡牌受到的效果无效。
 *
 * @author longwh
 * @date 2023/1/5 16:05
 */
@Service
public class Runes333304 implements IRoundStageRunes {
    @Autowired
    private BattleCardService battleCardService;

    @Override
    public int getRunesId() {
        return RunesEnum.JI_ZHONG_ENTRY.getRunesId();
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
            // 后军位置
            int rearIndex = 4;
            // 效果对象为后军位置 不处理
            if (PositionService.getBattleCardIndex(effect.getTargetPos()) == rearIndex) {
                continue;
            }
            // 获取后军位置卡牌
            int rearCardPos = PositionService.getBattleCardPos(param.getPerformPlayer().getId(), rearIndex);
            Optional<BattleCard> rearCard = battleCardService.getCard(param.getPerformPlayer(), rearCardPos);
            // 后军位置无卡牌 不处理
            if (!rearCard.isPresent()){
                continue;
            }
            // 改变技能效果目标为后军卡牌
            effect.setTargetPos(rearCard.get().getPos());
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