package com.bbw.god.server.fst.event;

import com.bbw.common.SpringContextUtil;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.event.EventParam;
import com.bbw.god.rd.RDCommon;

public class FstEventPublisher {

    /**
     * 加入封神台事件
     *
     * @param guId
     */
    public static void pubIntoFstEvent(long guId) {
        SpringContextUtil.publishEvent(new IntoFstEvent(new EventParam<Integer>(guId, new RDCommon())));
    }

    /**
     * 发布封神台胜利事件
     *
     * @param bep
     * @param ev
     */
    public static void pubFstWinEvent(BaseEventParam bep, EVFstWin ev) {
        SpringContextUtil.publishEvent(new FstWinEvent(new EventParam<EVFstWin>(bep, ev)));
    }

    /**
     * 发布封神台守卫成功事件
     *
     * @param bep
     */
    public static void pubFstGuardWinEvent(BaseEventParam bep) {
        SpringContextUtil.publishEvent(new FstGuardWinEvent(bep));
    }

    /**
     * 跨服封神台战斗结算事件
     *
     * @param
     */
    public static void pubGameFstFightOverEvent(long p1, boolean isWin) {
        BaseEventParam bep = new BaseEventParam(p1);
        EPFstFightOver ep = new EPFstFightOver(bep, isWin);
        SpringContextUtil.publishEvent(new GameFstFightOverEvent(ep));
    }

    /**
     * 封神台积分领取事件
     *
     * @param param
     */

    public static void pubFstIncrementPointEvent(FstIncrementPoint param) {
        SpringContextUtil.publishEvent(new FstIncrementPointEvent(param));
    }

}
