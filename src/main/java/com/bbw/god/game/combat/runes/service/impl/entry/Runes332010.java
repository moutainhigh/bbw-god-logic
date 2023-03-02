package com.bbw.god.game.combat.runes.service.impl.entry;

import com.bbw.god.game.combat.PositionService;
import com.bbw.god.game.combat.data.CombatBuff;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.combat.runes.service.IRoundStageRunes;
import org.springframework.stereotype.Service;

/**
 * 胆识词条 我方中军位卡牌受到普通攻击前，增加[5]%防御值。
 *
 * @author: suhq
 * @date: 2022/9/22 2:13 下午
 */
@Service
public class Runes332010 implements IRoundStageRunes {

    @Override
    public int getRunesId() {
        return RunesEnum.DAN_SHI_ENTRY.getRunesId();
    }

    @Override
    public Action doRoundRunes(CombatRunesParam param) {
        Action action = new Action();
        BattleCard performCard = param.getPerformCard();
        if (null == performCard) {
            return action;
        }
        if (isPerformOpponent(performCard.getPos(), param.getPerformPlayer().getId())) {
            return action;
        }
        //中军位置
        int middlePosIndex = 3;
        int performCardIndex = PositionService.getBattleCardIndex(performCard.getPos());
        if (performCardIndex != middlePosIndex) {
            return action;
        }
        //对方中军位
        BattleCard oppMiddle = param.getOppoPlayer().getPlayingCards(middlePosIndex);
        if (null == oppMiddle) {
            return action;
        }
        CombatBuff combatBuff = param.getPerformPlayer().gainBuff(getRunesId());
        double addRate = 0.05 * combatBuff.getLevel();
        int addHp = (int) (performCard.getHp() * addRate);
        CardValueEffect effect = CardValueEffect.getSkillEffect(getRunesId(), performCard.getPos());
        effect.setHp(addHp);
        action.addEffect(effect);
        return action;
    }
}
