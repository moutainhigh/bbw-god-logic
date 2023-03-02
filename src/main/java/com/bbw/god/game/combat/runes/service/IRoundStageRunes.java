package com.bbw.god.game.combat.runes.service;

import com.bbw.common.ListUtil;
import com.bbw.god.game.combat.PositionService;
import com.bbw.god.game.combat.data.PlayerId;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.runes.CombatRunesParam;

import java.util.List;

/**
 * @author lwb
 * @date 2020/9/16 14:47
 */
public interface IRoundStageRunes extends IBaseRunesService{

    Action doRoundRunes(CombatRunesParam param);

    default int getMinHv(List<BattleCard> cards){
        if (ListUtil.isEmpty(cards)){
            return 0;
        }
        int hv=10;
        for (BattleCard card:cards){
            if (card.getHv()<hv){
                hv=card.getHv();
            }
        }
        return hv;
    }

    /**
     * 发动技能效果和现在执行的符是否都是来自一方
     *
     * @param sourcePos
     * @param playerId
     * @return
     */
    default boolean isPerformSelf(int sourcePos, PlayerId playerId) {
        PlayerId source = PositionService.getPlayerIdByPos(sourcePos);
        return source.getValue() == playerId.getValue();
    }

    /**
     * 发动技能效果和现在执行的符是否不是来自一方
     *
     * @param sourcePos
     * @param playerId
     * @return
     */
    default boolean isPerformOpponent(int sourcePos, PlayerId playerId) {
        return !isPerformSelf(sourcePos, playerId);
    }
}
