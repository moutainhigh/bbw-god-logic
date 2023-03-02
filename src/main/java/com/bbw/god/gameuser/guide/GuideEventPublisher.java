package com.bbw.god.gameuser.guide;

import com.bbw.common.SpringContextUtil;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.rd.RDCommon;

/**
 * 新手引导事件发布器
 *
 * @author suhq
 * @date 2018年11月24日 下午9:30:48
 */
public class GuideEventPublisher {

    public static void pubPassNewerGuideEvent(long uid, RDCommon rd) {
        BaseEventParam bep = new BaseEventParam(uid, WayEnum.NONE, rd);
        SpringContextUtil.publishEvent(new PassNewerGuideEvent(new EPPassNewerGuide(bep)));
    }

    public static void pubLogNewerGuideEvent(long uid, Integer newerGuide, RDCommon rd) {
        BaseEventParam bep = new BaseEventParam(uid, WayEnum.NONE, rd);
        SpringContextUtil.publishEvent(new LogNewerGuideEvent(new EPLogNewerGuide(newerGuide, bep)));
    }

}
