package com.bbw.god.game.combat.runes.service.impl.player.blood;

import com.bbw.god.game.combat.data.CombatBuff;
import com.bbw.god.game.combat.data.Player;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.service.IInitStageRunes;
import com.bbw.god.gameuser.yuxg.cfg.CfgFuTuSlotRate;

/**
 * 血量符图
 *
 * @author: suhq
 * @date: 2021/11/12 3:04 下午
 */
public abstract class PlayerBloodRune implements IInitStageRunes {

    @Override
    public void doInitRunes(CombatRunesParam param) {
        Player player = param.getPerformPlayer();
        CombatBuff combatBuff = player.gainBuff(getRunesId());
        int hp = player.getHp();
        int buffAddHp = getAddBlood(combatBuff);
        //计算符图槽属性加成
        hp = hp + buffAddHp + buffAddHp * combatBuff.getExtraRate() / CfgFuTuSlotRate.FUTU_SLOT_BASE_RATE;
        player.setHp(hp);
        player.setBeginHp(hp);
        player.setMaxHp(hp);
    }

    /**
     * 获取可增加的血量
     *
     * @return
     */
    public abstract int getAddBlood(CombatBuff combatBuff);
}
