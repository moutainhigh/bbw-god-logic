package com.bbw.god.gameuser.leadercard.equipment;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 战斗加成
 *
 * @author suhq
 * @date 2021-03-26 13:55
 **/
@NoArgsConstructor
@Data
public class FightAddition implements Serializable {
    private static final long serialVersionUID = 1L;
    /** 攻击 */
    private Integer attack = 0;
    /** 防御 */
    private Integer defence = 0;
    /** 血量 */
    private Integer blood = 0;

    public FightAddition(int attack, int defence, int blood) {
        this.attack = attack;
        this.defence = defence;
        this.blood = blood;
    }
}
