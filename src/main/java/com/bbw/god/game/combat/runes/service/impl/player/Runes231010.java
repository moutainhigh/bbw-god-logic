package com.bbw.god.game.combat.runes.service.impl.player;

import com.bbw.god.game.combat.data.CombatBuff;
import com.bbw.god.game.combat.data.Player;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.combat.runes.service.IInitStageRunes;
import org.springframework.stereotype.Service;

/**
 * 升仙符图	1阶	战斗开始时，有30%概率（可升级）发动，己方召唤师初始法力值+2。	每级额外+7%概率
 *
 * @author: suhq
 * @date: 2022/5/24 2:08 下午
 */
@Service
public class Runes231010 implements IInitStageRunes {

    @Override
    public int getRunesId() {
        return RunesEnum.SHENG_XIAN_PLAYER.getRunesId();
    }

    @Override
    public void doInitRunes(CombatRunesParam param) {
        CombatBuff combatBuff = param.getPerformPlayer().gainBuff(getRunesId());
        if (null == combatBuff || !combatBuff.ifToPerform(30, 7)) {
            return;
        }
        Player performPlayer = param.getPerformPlayer();
        performPlayer.setMaxMp(performPlayer.getMp() + 2);
        performPlayer.setMp(performPlayer.getMp() + 2);
    }
}
