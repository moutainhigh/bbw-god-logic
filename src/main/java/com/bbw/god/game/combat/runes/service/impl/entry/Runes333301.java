package com.bbw.god.game.combat.runes.service.impl.entry;

import com.bbw.god.game.combat.PositionService;
import com.bbw.god.game.combat.data.CombatBuff;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardPositionEffect;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.card.PositionType;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.combat.runes.service.IRoundStageRunes;
import org.springframework.stereotype.Service;

/**
 * 血代词条 我方死亡的卡牌不进入坟场而是返回卡组，每返回1张我方召唤师减少[10]%当前血量。
 * 该效果只针对需要移动到坟场的卡牌生效；
 * 当死亡后因为自身或其他效果需要移动到异次元时，不会触发该效果 (技能优先级的问题)。
 *
 * @author longwh
 * @date 2023/1/5 15:32
 */
@Service
public class Runes333301 implements IRoundStageRunes {
    @Override
    public int getRunesId() {
        return RunesEnum.XUE_DAI_ENTRY.getRunesId();
    }

    @Override
    public Action doRoundRunes(CombatRunesParam param) {
        Action action = new Action();
        // 敌方不处理
        if (param.isEnemyTargetCard()) {
            return action;
        }
        // 不是进入坟场 不处理
        if (!PositionService.isDiscardPos(param.getTargetCard().getPos())) {
            return action;
        }
        // 不进入坟场而是返回卡组
        CardPositionEffect cardPositionEffect = CardPositionEffect.getSkillEffectToTargetPos(getRunesId(), param.getTargetCard().getPos());
        cardPositionEffect.setToPositionType(PositionType.DRAWCARD);
        cardPositionEffect.setSequence(param.getNextSeq());
        action.addEffect(cardPositionEffect);
        // 召唤师减少[10]%当前血量
        CombatBuff combatBuff = param.getPerformPlayer().gainBuff(getRunesId());
        double hp = 0.1 * combatBuff.getLevel() * param.getPerformPlayer().getHp();
        CardValueEffect valueEffect = CardValueEffect.getSkillEffect(getRunesId(), param.getMyPlayerPos());
        valueEffect.setHp((int) -hp);
        valueEffect.setSequence(param.getNextSeq());
        action.addEffect(valueEffect);
        return action;
    }
}