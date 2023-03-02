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
 * 刺骨词条 我方卡牌普通攻击后减少[5]%永久攻击值。
 *
 * @author longwh
 * @date 2023/1/4 16:49
 */
@Service
public class Runes333210 implements IRoundStageRunes {
    @Autowired
    private BattleCardService battleCardService;

    @Override
    public int getRunesId() {
        return RunesEnum.CI_GU_ENTRY.getRunesId();
    }

    @Override
    public Action doRoundRunes(CombatRunesParam param) {
        Action action = new Action();
        for (Effect effect : param.getReceiveEffect()) {
            // 必须为普通攻击技能效果
            if (!SkillSection.getNormalAttackSection().contains(effect.getPerformSkillID())) {
                continue;
            }
            if (isPerformOpponent(effect.getSourcePos(), param.getPerformPlayer().getId())) {
                continue;
            }
            // 获取玩家目标卡牌
            Optional<BattleCard> playerCard = battleCardService.getCard(param.getPerformPlayer(), effect.getSourcePos());
            if (!playerCard.isPresent()) {
                continue;
            }
            // 减少[5]%永久攻击值
            CombatBuff combatBuff = param.getPerformPlayer().gainBuff(getRunesId());
            double atk = -0.05 * combatBuff.getLevel() * playerCard.get().getAtk();
            CardValueEffect runesEffect = CardValueEffect.getSkillEffect(getRunesId(), effect.getSourcePos());
            runesEffect.setRoundAtk((int) atk);
            runesEffect.setSequence(param.getNextSeq());
            action.addEffect(runesEffect);
        }
        return action;
    }
}