package com.bbw.god.game.combat.runes.service.impl.player;

import com.bbw.god.game.combat.data.CombatBuff;
import com.bbw.god.game.combat.data.Player;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.combat.runes.service.IInitStageRunes;
import org.springframework.stereotype.Service;

/**
 * 辟土符图	4阶	战斗开始时，有30%概率（可升级）发动，解锁己方所有阵位。	每级额外+7%概率
 *
 * @author: suhq
 * @date: 2022/5/25 10:21 上午
 */
@Service
public class Runes231306 implements IInitStageRunes {

    @Override
    public int getRunesId() {
        return RunesEnum.PI_TU_PLAYER.getRunesId();
    }

    @Override
    public void doInitRunes(CombatRunesParam param) {
        Player player = param.getPerformPlayer();
        CombatBuff combatBuff = player.gainBuff(getRunesId());
        if (!combatBuff.ifToPerform(30, 7)) {
            return;
        }
        param.getPerformPlayer().setUnlockAllPosBuff(true);
    }
}
