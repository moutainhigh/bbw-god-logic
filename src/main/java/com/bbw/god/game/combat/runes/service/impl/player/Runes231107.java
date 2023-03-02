package com.bbw.god.game.combat.runes.service.impl.player;

import com.bbw.god.game.combat.data.CombatBuff;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.combat.runes.service.IInitStageRunes;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 蛊惑符图	2阶	战斗开始时，有30%概率（可升级）发动，使敌方本场战斗中卡牌上场所需法力值+1。	每级额外+7%概率
 *
 * @author: suhq
 * @date: 2022/5/24 2:08 下午
 */
@Service
public class Runes231107 implements IInitStageRunes {

    @Override
    public int getRunesId() {
        return RunesEnum.GU_HUO_PLAYER.getRunesId();
    }

    @Override
    public void doInitRunes(CombatRunesParam param) {
        CombatBuff combatBuff = param.getPerformPlayer().gainBuff(getRunesId());
        if (null == combatBuff || !combatBuff.ifToPerform(30, 7)) {
            return;
        }
        int mpAddtion = param.getOppoPlayer().getStatistics().getInitCardMp();
        mpAddtion += 1;
        param.getOppoPlayer().getStatistics().setInitCardMp(mpAddtion);
    }
}
