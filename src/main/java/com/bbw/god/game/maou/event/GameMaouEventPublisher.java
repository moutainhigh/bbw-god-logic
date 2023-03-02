package com.bbw.god.game.maou.event;

import com.bbw.common.SpringContextUtil;
import com.bbw.god.activity.IActivity;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.rd.RDCommon;

/**
 * 跨服魔王事件推送器
 *
 * @author: suhq
 * @date: 2021/12/17 4:17 下午
 */
public class GameMaouEventPublisher {
    /**
     * 魔王击杀事件
     *
     * @param uid
     */
    public static void pubKilledEvent(long uid, IActivity activity, int turn) {
        BaseEventParam bp = new BaseEventParam(uid, WayEnum.MAOU_GAME, new RDCommon());
        EPGameMaouKilled ep = EPGameMaouKilled.getInstance(activity, turn, bp);
        SpringContextUtil.publishEvent(new GameMaouKilledEvent(ep));
    }

    /**
     * 发布攻击魔王事件
     *
     * @param uid
     * @param blood
     */
    public static void pubAttackMaouEvent(long uid, int blood) {
        BaseEventParam bp = new BaseEventParam(WayEnum.MAOU_GAME);
        bp.setGuId(uid);
        EPAttackGameMaou attackMaou = EPAttackGameMaou.getInstance(blood, bp);
        SpringContextUtil.publishEvent(new GameMaouAttackEvent(attackMaou));
    }


}
