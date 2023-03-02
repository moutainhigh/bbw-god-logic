package com.bbw.god.game.combat.runes.service.impl.entry;

import com.bbw.god.game.combat.data.CombatBuff;
import com.bbw.god.game.combat.data.Player;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.combat.runes.service.IInitStageRunes;
import org.springframework.stereotype.Service;

/**
 * 劫血词条 敌方召唤师血量上限增加[10]%；我方召唤师血量上限减少[5]%。
 *
 * @author: suhq
 * @date: 2022/9/20 5:18 下午
 */
@Service
public class Runes331101 implements IInitStageRunes {

    @Override
    public int getRunesId() {
        return RunesEnum.JIE_XUE_ENTRY.getRunesId();
    }

    @Override
    public void doInitRunes(CombatRunesParam param) {
        Player oppPlayer = param.getOppoPlayer();
        Player performPlayer = param.getPerformPlayer();
        CombatBuff combatBuff = performPlayer.gainBuff(getRunesId());
        handleOpponent(oppPlayer, combatBuff);
        handleSelf(performPlayer, combatBuff);
    }

    /**
     * 处理对手血量
     *
     * @param opponent
     * @param combatBuff
     */
    private void handleOpponent(Player opponent, CombatBuff combatBuff) {
        int hp = opponent.getHp();
        double buffAddRate = 0.1 * combatBuff.getLevel();
        //计算属性加成
        hp = hp + (int) (hp * buffAddRate);
        opponent.setHp(hp);
        opponent.setBeginHp(hp);
        opponent.setMaxHp(hp);
    }

    /**
     * 处理己方血量
     *
     * @param performPlayer
     * @param combatBuff
     */
    private void handleSelf(Player performPlayer, CombatBuff combatBuff) {
        int hp = performPlayer.getMaxHp();
        double buffDeductRate = 0.05 * combatBuff.getLevel();
        //计算属性加成
        hp = hp - (int) (hp * buffDeductRate);
        if (performPlayer.getHp() > hp) {
            performPlayer.setHp(hp);
            performPlayer.setBeginHp(hp);
        } else {
            performPlayer.setHp(performPlayer.getHp());
            performPlayer.setBeginHp(performPlayer.getHp());
        }

        performPlayer.setMaxHp(hp);
    }
}
