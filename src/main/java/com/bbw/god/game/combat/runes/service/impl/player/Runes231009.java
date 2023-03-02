package com.bbw.god.game.combat.runes.service.impl.player;

import com.bbw.god.game.combat.data.CombatBuff;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.combat.runes.service.IInitStageRunes;
import org.springframework.stereotype.Service;

/**
 * 禁法符图	1阶	战斗开始时，有30%概率（可升级）发动，禁用双方所有法宝。	每级额外+7%概率
 *
 * @author: suhq
 * @date: 2022/5/24 2:08 下午
 */
@Service
public class Runes231009 implements IInitStageRunes {

    @Override
    public int getRunesId() {
        return RunesEnum.JIN_FA_PLAYER.getRunesId();
    }

    @Override
    public void doInitRunes(CombatRunesParam param) {
        CombatBuff combatBuff = param.getPerformPlayer().gainBuff(getRunesId());
        if (null == combatBuff || !combatBuff.ifToPerform(30, 7)) {
            return;
        }
    }
}
