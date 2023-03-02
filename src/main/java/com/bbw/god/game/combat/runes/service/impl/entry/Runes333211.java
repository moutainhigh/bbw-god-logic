package com.bbw.god.game.combat.runes.service.impl.entry;

import com.bbw.common.ListUtil;
import com.bbw.god.game.combat.PositionService;
import com.bbw.god.game.combat.data.CombatBuff;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.skill.SkillSection;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.combat.runes.service.IRoundStageRunes;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 衰弱词条 我方卡牌受到的来自己方的正面技能效果减少[20]%；来自对方的负面技能效果增加[20]%
 *
 * @author longwh
 * @date 2023/1/5 9:48
 */
@Service
public class Runes333211 implements IRoundStageRunes {

    @Override
    public int getRunesId() {
        return RunesEnum.SHUAI_RUO_ENTRY.getRunesId();
    }

    @Override
    public Action doRoundRunes(CombatRunesParam param) {
        Action action = new Action();
        if (ListUtil.isEmpty(param.getReceiveEffect())) {
            return action;
        }
        for (Effect effect : param.getReceiveEffect()) {
            // 跳过普通攻击技能效果
            if (SkillSection.getNormalAttackSection().contains(effect.getPerformSkillID())) {
                continue;
            }
            // 目标为玩家 不处理
            if (PositionService.isZhaoHuanShiPos(effect.getTargetPos())) {
                continue;
            }
            // 敌方卡牌 不处理
            if (param.isEffectToEnemy()) {
                continue;
            }
            // 非伤害技能 不处理
            if (!effect.isValueEffect()) {
                continue;
            }
            // 获取效果目标卡牌
            Optional<BattleCard> card = PositionService.getCard(param.getPerformPlayer(), effect.getTargetPos());
            // 计算buff效果 注：参数 hp 为负值 即本次攻击效果造成的扣除血量
            CombatBuff combatBuff = param.getPerformPlayer().gainBuff(getRunesId());
            double buffRate = 0.2 * combatBuff.getLevel();
            CardValueEffect cardValueEffect = effect.toValueEffect();
            if (isPerformSelf(effect.getSourcePos(), param.getPerformPlayer().getId())) {
                // 来自己方的正面技能效果减少[20]%
                if (!isDeBuffValueEffect(cardValueEffect)) {
                    dealBuffValueEffect(cardValueEffect, - buffRate);
                    card.ifPresent(battleCard -> dealBuffValueEffectTimes(cardValueEffect, battleCard, buffRate));
                }
            }else {
                // 来自对方的负面技能效果增加[20]%
                if (isDeBuffValueEffect(cardValueEffect)) {
                    dealBuffValueEffect(cardValueEffect, buffRate);
                    card.ifPresent(battleCard -> dealBuffValueEffectTimes(cardValueEffect, battleCard, buffRate));
                }
            }
            CardValueEffect aminEffect = CardValueEffect.getSkillEffect(getRunesId(), effect.getTargetPos());
            action.addEffect(aminEffect);
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
     * 处理buff效果
     *
     * @param effect
     * @return
     */
    private void dealBuffValueEffect(CardValueEffect effect, double buffRate) {
        double hp = effect.getHp() * (1 + buffRate);
        double roundHp = effect.getRoundHp() * (1 + buffRate);
        double atk = effect.getAtk() * (1 + buffRate);
        double roundAtk = effect.getRoundAtk() * (1 + buffRate);
        effect.setHp((int) hp);
        effect.setRoundHp((int) roundHp);
        effect.setAtk((int) atk);
        effect.setRoundAtk((int) roundAtk);
    }

    /**
     * 处理倍率buff效果
     *
     * @param effect
     * @return
     */
    private void dealBuffValueEffectTimes(CardValueEffect effect, BattleCard card, double buffRate) {
        double hpTimes = card.getHp() * effect.getHpTmes() * buffRate / card.getHp() + effect.getHpTmes();
        double roundHpTimes = card.getRoundHp() * effect.getRoundHpTmes() * buffRate / card.getRoundHp() + effect.getRoundHpTmes();
        double atkTimes = card.getAtk() * effect.getAtkTimes() * buffRate / card.getAtk() + effect.getAtkTimes();
        double roundAtkTimes = card.getRoundAtk() * effect.getRoundAtkTimes() * buffRate / card.getRoundAtk() + effect.getRoundAtkTimes();
        effect.setHpTmes(hpTimes);
        effect.setRoundHpTmes(roundHpTimes);
        effect.setAtkTimes(atkTimes);
        effect.setRoundAtkTimes(roundAtkTimes);
    }
}