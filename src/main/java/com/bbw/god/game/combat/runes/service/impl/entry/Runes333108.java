package com.bbw.god.game.combat.runes.service.impl.entry;

import com.bbw.god.game.combat.PositionService;
import com.bbw.god.game.combat.data.CombatBuff;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.data.skill.SkillSection;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.combat.runes.service.IRoundStageRunes;
import org.springframework.stereotype.Service;

/**
 * 深入词条 我方卡牌受到的所有非永久技能伤害将额外受到[10]%的永久伤害。
 *
 * @author longwh
 * @date 2022/12/30 14:50
 */
@Service
public class Runes333108 implements IRoundStageRunes {

    @Override
    public int getRunesId() {
        return RunesEnum.SHEN_RU_ENTRY.getRunesId();
    }

    @Override
    public Action doRoundRunes(CombatRunesParam param) {
        Action action = new Action();
        for (Effect effect : param.getReceiveEffect()) {
            int skillId = effect.getPerformSkillID();
            if (!check(skillId)) {
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
            int effectHp = effect.toValueEffect().getRoundHp();
            // 永久技能伤害 不处理
            if (effectHp < 0){
                continue;
            }
            // 受到[10]%的永久伤害
            CombatBuff combatBuff = param.getPerformPlayer().gainBuff(getRunesId());
            double roundHp = 0.1 * combatBuff.getLevel() * effect.toValueEffect().getHp();
            CardValueEffect runesEffect = CardValueEffect.getSkillEffect(getRunesId(), effect.getTargetPos());
            runesEffect.setRoundHp((int) roundHp);
            action.addEffect(runesEffect);
        }

        return action;
    }

    /**
     * 检查技能是否符合
     *
     * @param skillId
     * @return
     */
    private boolean check(int skillId) {
        // 上场技能
        SkillSection deploySection = SkillSection.getDeploySection();
        // 攻击技能
        SkillSection skillSection = SkillSection.getSkillAttackSection();
        SkillSection fightBackSection = SkillSection.getFightBackSection();
        return deploySection.contains(skillId) || skillSection.contains(skillId) || fightBackSection.contains(skillId);
    }
}