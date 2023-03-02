package com.bbw.god.game.combat.runes.service;

import com.bbw.common.ListUtil;
import com.bbw.god.game.combat.BattleCardService;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.service.series.SeriesService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

/**
 * 我方卡牌受到**系技能伤害时 根据不同符文处理效果
 *
 * @author longwh
 * @date 2023/1/5 12:01
 */
public abstract class AbstractSkillSeriesRunes implements IRoundStageRunes {
    @Autowired
    protected BattleCardService battleCardService;

    /**
     * 伤害系列服务
     *
     * @return
     */
    public abstract SeriesService getSeriesService();

    /**
     * 获取处理效果
     *
     * @param param
     * @param effect
     * @param playerCard
     * @return
     */
    public abstract List<Effect> getDealEffects(CombatRunesParam param, Effect effect, BattleCard playerCard);

    @Override
    public Action doRoundRunes(CombatRunesParam param) {
        Action action = new Action();
        for (Effect effect : param.getReceiveEffect()) {
            // 必须相关伤害系列技能
            if (!getSeriesService().check(effect.getPerformSkillID())) {
                continue;
            }
            if (isPerformSelf(effect.getSourcePos(), param.getPerformPlayer().getId())) {
                continue;
            }
            // 获取符文发动方 effect目标的卡牌
            Optional<BattleCard> playerCard = battleCardService.getCard(param.getPerformPlayer(), effect.getTargetPos());
            if (!playerCard.isPresent()) {
                continue;
            }
            List<Effect> dealEffects = getDealEffects(param, effect, playerCard.get());
            if (ListUtil.isEmpty(dealEffects)){
                continue;
            }
            action.addEffects(dealEffects);
        }
        return action;
    }
}