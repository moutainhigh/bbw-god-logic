package com.bbw.god.gameuser.nightmarenvwam.listener;

import com.bbw.common.SpringContextUtil;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.rd.RDCommon;

import java.util.List;

/**
 * 捏人事件发布器
 *
 * @author suhq
 * @date 2018年11月24日 下午9:30:48
 */
public class PinchPeopleEventPublisher {

    public static void pubPinchPeopleEvent(long uid, List<Integer> soilScore, RDCommon rd) {
        BaseEventParam bep = new BaseEventParam(uid, WayEnum.PINCH_PEOPLE, rd);
        SpringContextUtil.publishEvent(new PinchPeopleEvent(new EPPinchPeople(soilScore, bep)));
    }
}
