package com.bbw.god.game.combat;

import lombok.Data;

/**
 * @author lwb
 * @date 2020/4/26 11:42
 */
@Data
public class CombatResInfo {
    private long uid;//玩家ID
    private int oppoLostHp=0;//对手掉的血
    private int oppoLostCard=0;//对手掉的卡
}
