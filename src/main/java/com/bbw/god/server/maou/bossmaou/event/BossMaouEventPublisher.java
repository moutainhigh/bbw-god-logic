package com.bbw.god.server.maou.bossmaou.event;

import com.bbw.common.SpringContextUtil;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.rd.RDCommon;
import com.bbw.god.server.maou.bossmaou.ServerBossMaou;

/**
 * 魔王事件推送器
 *
 * @author suhq
 * @date 2019年2月28日 下午4:56:56
 */
public class BossMaouEventPublisher {
    /**
     * 魔王击杀事件
     *
     * @param bossMaou
     * @param rd
     */
    public static void pubKilledEvent(ServerBossMaou bossMaou, RDCommon rd) {
        BaseEventParam bp = new BaseEventParam(bossMaou.getKiller(), WayEnum.MAOU_BOSS_FIGHT, rd);
        SpringContextUtil.publishEvent(new BossMaouKilledEvent(new EPBossMaou(bossMaou, bp)));
    }

    /**
     * 魔王奖励发送事件
     *
     * @param bossMaou
     */
    public static void pubAwardSendEvent(ServerBossMaou bossMaou) {
        BaseEventParam bp = new BaseEventParam(WayEnum.MAOU_BOSS_FIGHT);
        SpringContextUtil.publishEvent(new BossMaouAwardSendEvent(new EPBossMaou(bossMaou, bp)));
    }

    /**
     * 攻击魔王事件
     */
    public static void pubAttackMaouEvent(int blood,long uid) {
        BaseEventParam bp = new BaseEventParam(WayEnum.MAOU_BOSS_FIGHT);
        bp.setGuId(uid);
        EPAttackMaou attackMaou=EPAttackMaou.getInstance(blood,bp);
        SpringContextUtil.publishEvent(new BossMaouAttackEvent(attackMaou));
    }


}
