package com.bbw.god.game.combat.runes.service.impl.entry;

import com.bbw.common.ListUtil;
import com.bbw.common.PowerRandom;
import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.BattleSkillEffect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.combat.runes.service.IRoundStageRunes;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 乱流词条 我方卡牌的普通攻击将随机攻击敌方卡牌。
 *
 * @author longwh
 * @date 2023/1/4 16:49
 */
@Service
public class Runes333209 implements IRoundStageRunes {

    @Override
    public int getRunesId() {
        return RunesEnum.LUAN_LIU_ENTRY.getRunesId();
    }

    @Override
    public Action doRoundRunes(CombatRunesParam param) {
        Action action = new Action();
        for (BattleCard playingCard : param.getPerformPlayer().getPlayingCards()) {
            if (playingCard == null){
                continue;
            }
            // 随机获取一张敌方战场卡牌
            List<BattleCard> playingCards = param.getOppoPlayer().getPlayingCards(true);
            List<BattleCard> randomCards = PowerRandom.getRandomsFromList(playingCards, 1);
            if (ListUtil.isEmpty(randomCards)){
                continue;
            }
            // 改变攻击目标
            BattleSkillEffect effect = BattleSkillEffect.getSkillEffect(getRunesId(), playingCard.getPos());
            effect.changeSkillAttackTarget(CombatSkillEnum.NORMAL_ATTACK.getValue(), randomCards.get(0).getPos());
            action.addEffect(effect);
        }
        return action;
    }
}