package com.bbw.god.game.maou.event;

import com.bbw.god.activity.IActivity;
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
public class EPGameMaouKilled extends BaseEventParam implements Serializable {

    private static final long serialVersionUID = -6218187550735434799L;
    private IActivity activity;
    private int turn = 0;

    public static EPGameMaouKilled getInstance(IActivity activity, int turn, BaseEventParam bep) {
        EPGameMaouKilled ep = new EPGameMaouKilled();
        ep.setActivity(activity);
        ep.setTurn(turn);
        ep.setValues(bep);
        return ep;
    }
}
