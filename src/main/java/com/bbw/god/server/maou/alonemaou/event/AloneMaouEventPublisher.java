package com.bbw.god.server.maou.alonemaou.event;

import com.bbw.common.SpringContextUtil;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.rd.RDCommon;
import com.bbw.god.server.maou.alonemaou.ServerAloneMaou;
import com.bbw.god.server.maou.alonemaou.attackinfo.AloneMaouLevelInfo;

/**
 * 魔王事件推送器
 *
 * @author suhq
 * @date 2019年2月28日 下午4:56:56
 */
public class AloneMaouEventPublisher {
    /**
     * 魔王击杀事件
     *
     * @param aloneMaou
     * @param maouLevelInfo
     * @param rd
     */
    public static void pubKilledEvent(ServerAloneMaou aloneMaou, AloneMaouLevelInfo maouLevelInfo, boolean firstKilled,RDCommon rd) {
        BaseEventParam bp = new BaseEventParam(maouLevelInfo.getGuId(), WayEnum.MAOU_ALONE_FIGHT, rd);
        SpringContextUtil.publishEvent(new AloneMaouKilledEvent(new EPAloneMaou(aloneMaou, maouLevelInfo,firstKilled, bp)));
    }

    public static void pubPassLevelEvent(long uid){
        EPPassAloneMaou ep=EPPassAloneMaou.getInstance(new BaseEventParam(uid));
        SpringContextUtil.publishEvent(new AloneMaouPassEvent(ep));
    }

}
