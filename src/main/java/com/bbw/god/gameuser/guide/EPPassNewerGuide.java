package com.bbw.god.gameuser.guide;

import com.bbw.god.event.BaseEventParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 玩家通过新手引导事件参数
 *
 * @author suhq
 * @date 2019-10-18 14:49:09
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class EPPassNewerGuide extends BaseEventParam {

    public EPPassNewerGuide(BaseEventParam bep) {
        setValues(bep);
    }
}
