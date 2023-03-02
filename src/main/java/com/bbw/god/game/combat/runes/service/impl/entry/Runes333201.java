package com.bbw.god.game.combat.runes.service.impl.entry;

import com.bbw.god.game.combat.BattleCardService;
import com.bbw.god.game.combat.data.CombatBuff;
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

import java.util.Optional;

/**
 * 反噬词条 我方卡牌使用上场、回合技能时减少[15]%防御值。
 *
 * @author longwh
 * @date 2023/1/2 9:34
 */
@Service
public class Runes333201 implements IRoundStageRunes {
    @Autowired
    private BattleCardService battleCardService;

    @Override
    public int getRunesId() {
        return RunesEnum.FAN_SHI_ENTRY.getRunesId();
    }

    @Override
    public Action doRoundRunes(CombatRunesParam param) {
        Action action = new Action();
        for (Effect effect : param.getReceiveEffect()) {
            int skillId = effect.getPerformSkillID();
            // 技能必须上场、回合技能
            if (!check(skillId)) {
                continue;
            }
            if (isPerformOpponent(effect.getSourcePos(), param.getPerformPlayer().getId())) {
                continue;
            }
            Optional<BattleCard> playerCard = battleCardService.getCard(param.getPerformPlayer(), effect.getSourcePos());
            if (!playerCard.isPresent()){
                continue;
            }
            // 减少[15]%防御值
            CombatBuff combatBuff = param.getPerformPlayer().gainBuff(getRunesId());
            double hp = - 0.15 * combatBuff.getLevel() * playerCard.get().getHp();
            CardValueEffect runesEffect = CardValueEffect.getSkillEffect(getRunesId(), effect.getSourcePos());
            runesEffect.setHp((int) hp);
            action.addEffect(runesEffect);
        }

        return action;
    }

    /**
     * 检查技能
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