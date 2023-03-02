package com.bbw.god.game.combat.runes.service.impl.entry;

import com.bbw.common.CloneUtil;
import com.bbw.god.game.combat.PositionService;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.card.PositionType;
import com.bbw.god.game.combat.data.skill.SkillSection;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.combat.runes.service.IRoundStageRunes;
import org.springframework.stereotype.Service;

/**
 * 同步词条 我方卡牌将受到与对位卡牌相同的负面上场、回合技能效果。
 * 负面效果为：使卡牌从场上离开、使用卡牌的攻击/防御值降低。
 * 对位卡牌自己对自己使用负面效果时不会触发（自己将自己从场上离开）
 *
 * @author longwh
 * @date 2023/1/4 15:48
 */
@Service
public class Runes333207 implements IRoundStageRunes {

    @Override
    public int getRunesId() {
        return RunesEnum.TONG_BU_ENTRY.getRunesId();
    }

    @Override
    public Action doRoundRunes(CombatRunesParam param) {
        Action action = new Action();
        for (Effect effect : param.getReceiveEffect()) {
            // 不为上场、回合技能效果 不处理
            if (!check(effect.getPerformSkillID())) {
                continue;
            }
            // 效果针对玩家 不处理
            if (PositionService.isZhaoHuanShiPos(effect.getTargetPos())) {
                continue;
            }
            // 效果针对我方 不处理
            if (!param.isEffectToEnemy()) {
                continue;
            }
            // 对位卡牌自己对自己使用负面效果时不会触发
            if (effect.getSourcePos() == effect.getTargetPos()) {
                continue;
            }
            // 非位置移动、非影响卡牌的属性 不处理
            if (!effect.isPositionEffect() && !effect.isValueEffect()) {
                continue;
            }
            if (effect.isPositionEffect()) {
                // 非离开战场 不处理
                if (PositionType.BATTLE == effect.toPositionEffect().getToPositionType()) {
                    continue;
                }
            }
            if (effect.isValueEffect()) {
                // 非减益伤害 不处理
                if (!isDeBuffValueEffect(effect.toValueEffect())) {
                    continue;
                }
            }
            int targetCardIndex = PositionService.getBattleCardIndex(effect.getTargetPos());
            BattleCard playingCard = param.getPerformPlayer().getPlayingCards(targetCardIndex);
            // 我方卡牌不存在 不处理
            if (playingCard == null) {
                continue;
            }
            // 触发 补充一个效果动画
            CardValueEffect animationEffect = CardValueEffect.getSkillEffect(getRunesId(), effect.getTargetPos());
            action.addEffect(animationEffect);
            // 复制相同的负面上场、回合技能效果。目标为我方卡牌
            Effect cloneEffect = CloneUtil.clone(effect);
            cloneEffect.setSourcePos(effect.getTargetPos());
            cloneEffect.setTargetPos(playingCard.getPos());
            cloneEffect.setSequence(param.getNextSeq());
            action.addEffect(cloneEffect);
        }
        return action;
    }

    /**
     * 判断效果是否为减益
     *
     * @param effect
     * @return
     */
    private boolean isDeBuffValueEffect(CardValueEffect effect) {
        return effect.getHp() < 0 || effect.getRoundHp() < 0 || effect.getAtk() < 0 || effect.getRoundAtk() < 0
                || effect.getAtkTimes() < 0 || effect.getRoundAtkTimes() < 0 || effect.getHpTmes() < 0 || effect.getRoundHpTmes() < 0;
    }

    /**
     * 检查上场、回合技能效果
     *
     * @param skillId
     * @return
     */
    private boolean check(int skillId) {
        // 上场技能
        SkillSection deploySection = SkillSection.getDeploySection();
        // 攻击技能
        SkillSection skillSection = SkillSection.getSkillAttackSection();
        return deploySection.contains(skillId) || skillSection.contains(skillId);
    }
}