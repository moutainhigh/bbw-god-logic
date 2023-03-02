package com.bbw.god.game.combat.runes.service.impl.entry;

import com.bbw.god.game.combat.PositionService;
import com.bbw.god.game.combat.data.CombatBuff;
import com.bbw.god.game.combat.data.PlayerId;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.combat.runes.service.IRoundStageRunes;
import org.springframework.stereotype.Service;

/**
 * 坚守词条 敌方中军位卡牌受到普通攻击前，增加[10]%防御值；我方中军位卡牌受到普通攻击前，减少[2]%防御值。
 *
 * @author: suhq
 * @date: 2022/9/22 2:13 下午
 */
@Service
public class Runes331108 implements IRoundStageRunes {

    @Override
    public int getRunesId() {
        return RunesEnum.JIAN_SHOU_ENTRY.getRunesId();
    }

    @Override
    public Action doRoundRunes(CombatRunesParam param) {
        Action action = new Action();
        CombatBuff combatBuff = param.getPerformPlayer().gainBuff(getRunesId());
        BattleCard performCard = param.getPerformCard();
        if (null == performCard) {
            return  action;
        }
        //中军位置
        int middlePosIndex = 3;
        int performIndex = PositionService.getBattleCardIndex(performCard.getPos());
        if (performIndex != middlePosIndex) {
            return action;
        }
        PlayerId runesPerformPlayerId = param.getPerformPlayer().getId();
        //己方中军位
        if (isPerformSelf(performCard.getPos(), runesPerformPlayerId)) {
            if (null == param.getOppoPlayer().getPlayingCards(middlePosIndex)) {
                return action;
            }
            double deductRate = 0.02 * combatBuff.getLevel();
            int deductHp = (int) (performCard.getHp() * deductRate);
            CardValueEffect effect = CardValueEffect.getSkillEffect(getRunesId(), performCard.getPos());
            effect.setHp(-deductHp);
            action.addEffect(effect);
            return action;
        }
        //敌人中军位
        if (null == param.getPerformPlayer().getPlayingCards(middlePosIndex)) {
            return action;
        }
        double addRate = 0.1 * combatBuff.getLevel();
        int addHp = (int) (performCard.getHp() * addRate);
        CardValueEffect effect = CardValueEffect.getSkillEffect(getRunesId(), performCard.getPos());
        effect.setHp(addHp);
        action.addEffect(effect);
        return action;
    }
}
