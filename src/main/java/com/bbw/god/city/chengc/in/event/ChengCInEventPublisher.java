package com.bbw.god.city.chengc.in.event;

import com.bbw.common.SpringContextUtil;
import com.bbw.god.city.chengc.UserCity;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.event.EventParam;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.rd.RDCommon;

import java.util.List;

/**
 * 城内事件发布器
 *
 * @author suhq
 * @date 2019-05-24 09:04:46
 */
public class ChengCInEventPublisher {

    /**
     * 发布城内建筑升级事件
     *
     * @param guId
     * @param uc
     * @param levelUpBuildings
     * @param way
     * @param rd
     */
    public static void pubBuildingLevelUpEvent(long guId, UserCity uc, List<Integer> levelUpBuildings, WayEnum way, RDCommon rd) {
        EPBuildingLevelUp value = new EPBuildingLevelUp(uc, levelUpBuildings);
        SpringContextUtil.publishEvent(new BuildingLevelUpEvent(new EventParam<EPBuildingLevelUp>(guId, value, way, rd)));
    }

    /**
     * 发布城内法坛解锁事件
     * @param uid
     * @param uc
     */
    public static void pubUnlockFaTanEvent(long uid, UserCity uc) {
        BaseEventParam bep = new BaseEventParam(uid);
        EPUnlockFaTan ep = new EPUnlockFaTan(uc, bep);
        SpringContextUtil.publishEvent(new UnlockFaTanEvent(ep));
    }
}
