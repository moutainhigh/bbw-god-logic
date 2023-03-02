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
public class EPLogNewerGuide extends BaseEventParam {
    // 更新后的引导值
    private Integer newerGuide;

    public EPLogNewerGuide(Integer newerGuide, BaseEventParam bep) {
        setValues(bep);
        this.newerGuide = newerGuide;
    }
}
