package com.bbw.god.gameuser.biyoupalace.event;

import com.bbw.common.SpringContextUtil;
import com.bbw.god.event.BaseEventParam;

/**
 * @author 作者 ：lwb
 * @version 创建时间：2019年10月24日 下午4:11:44
 * 类说明
 */
public class BiyouEventPublisher {

    public static void pubGainAwardEvent(EPBiyouGainAward event) {
        SpringContextUtil.publishEvent(new EPBiyouGainAwardEvent(event));
    }

    public static void pubRealizedEvent(long uid, int chapter) {
        BaseEventParam bep = new BaseEventParam(uid);
        EPBiyouRealized ep = EPBiyouRealized.instance(bep, chapter);
        SpringContextUtil.publishEvent(new BiyouRealizedEvent(ep));
    }

    public static void pubSecretBiographyUnlockEvent(long uid) {
        BaseEventParam bep = new BaseEventParam(uid);
        EPSecretBiographyUnlock ep = EPSecretBiographyUnlock.instance(bep);
        SpringContextUtil.publishEvent(new SecretBiographyUnlockEvent(ep));
    }

}
