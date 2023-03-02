package com.bbw.god.game.combat.runes.service.impl.player;

import com.bbw.common.ListUtil;
import com.bbw.god.game.combat.data.CombatBuff;
import com.bbw.god.game.combat.data.Player;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardPositionEffect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.card.PositionType;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.combat.runes.service.IRoundStageRunes;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 冷箭符图 231307：每回合开始时，有敌方场上卡牌数*7%概率（可升级）发动，将敌方场上攻防总和最高的卡牌送入坟场，一次战斗触发一次。每级+1%
 *
 * @author longwh
 * @date 2022/11/18 10:10
 */
@Service
public class Runes231307 implements IRoundStageRunes {

    @Override
    public int getRunesId() {
        return RunesEnum.LENG_JIAN_PLAYER.getRunesId();
    }

    @Override
    public Action doRoundRunes(CombatRunesParam param) {
        Action action = new Action();
        Player player = param.getPerformPlayer();
        CombatBuff combatBuff = player.gainBuff(getRunesId());
        //是否能触发发送
        boolean cantToPerform = combatBuff.getLastPerformRound() >= 0 && param.getRound() != combatBuff.getLastPerformRound();
        if (cantToPerform) {
            // 已经发动，跳过
            return action;
        }
        List<BattleCard> playingCards = param.getOppoPlayer().getPlayingCards(true);
        if (ListUtil.isEmpty(playingCards)) {
            return action;
        }
        // 计算概率，发动 ifToPerform
        if (!combatBuff.ifToPerform(7 * playingCards.size(), 1)) {
            return action;
        }
        // 一次战斗触发一次
        combatBuff.setLastPerformRound(param.getRound());
        // 将敌方场上攻防总和最高的卡牌送入坟场
        int maxHpAtk=0;
        BattleCard targetCard = null;
        for (BattleCard card:playingCards){
            int temp=card.getAtk()+card.getHp();
            if (temp>maxHpAtk){
                maxHpAtk=temp;
                targetCard=card;
            }
        }
        CardPositionEffect effect = CardPositionEffect.getSkillEffectToTargetPos(getRunesId(), targetCard.getPos());
        effect.moveTo(PositionType.DISCARD);
        effect.setToPos(-1);
        action.addEffect(effect);
        return action;
    }
}