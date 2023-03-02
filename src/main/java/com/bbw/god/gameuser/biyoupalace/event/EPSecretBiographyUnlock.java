package com.bbw.god.gameuser.biyoupalace.event;

import com.bbw.god.event.BaseEventParam;
import lombok.Getter;
import lombok.Setter;

/**
 * 碧游宫秘传解锁事件参数
 *
 * @author suhq
 * @date 2021/7/2 上午9:12
 **/
@Getter
@Setter
public class EPSecretBiographyUnlock extends BaseEventParam {

    public static EPSecretBiographyUnlock instance(BaseEventParam ep) {
        EPSecretBiographyUnlock ew = new EPSecretBiographyUnlock();
        ew.setValues(ep);
        return ew;
    }
}
