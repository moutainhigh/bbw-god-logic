package com.bbw.god.game.maou.event;

import com.bbw.god.event.BaseEventParam;
import lombok.Data;

import java.io.Serializable;

/**
 * 攻打跨服魔王参数
 *
 * @author: suhq
 * @date: 2021/12/17 4:14 下午
 */
@Data
public class EPAttackGameMaou extends BaseEventParam implements Serializable {

    private static final long serialVersionUID = -6218187550735434799L;
    private int blood = 0;

    public static EPAttackGameMaou getInstance(int blood, BaseEventParam bep) {
        EPAttackGameMaou ep = new EPAttackGameMaou();
        ep.setBlood(blood);
        ep.setValues(bep);
        return ep;
    }
}
