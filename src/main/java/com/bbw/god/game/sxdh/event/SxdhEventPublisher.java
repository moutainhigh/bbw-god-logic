package com.bbw.god.game.sxdh.event;

import com.bbw.common.SpringContextUtil;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.game.config.WayEnum;

/**
 * @author 作者 ：lwb
 * @version 创建时间：2019年11月25日 上午10:32:45
 * 类说明
 */
public class SxdhEventPublisher {

    public static void pubGainSxdhBeanEvent(GainSxdhBean gsb) {
        SpringContextUtil.publishEvent(new GainSxdhBeanEvent(gsb));
    }

    public static void pubCardRefreshEvent(long uid, int refreshCardNum) {
        BaseEventParam bep = new BaseEventParam(uid, WayEnum.SXDH_REFRESH_CARD);
        SpringContextUtil.publishEvent(new SxdhCardRefreshEvent(EPSxdhCardRefresh.instance(bep, refreshCardNum)));
    }

    public static void pubSxdhAwardSendEvent(EPSxdhAwardSend ep) {
        SpringContextUtil.publishEvent(new SxdhAwardSendEvent(ep));
    }
}
