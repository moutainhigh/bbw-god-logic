package com.bbw.god.gameuser.statistic.event;

import com.bbw.god.event.BaseEventParam;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.gameuser.statistic.resource.ResourceStatistic;
import com.bbw.god.rd.RDCommon;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author suchaobin
 * @description 资源统计事件参数
 * @date 2020/4/18 9:57
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class EPResourceStatistic extends BaseEventParam {
    private ResourceStatistic resourceStatistic;

    public EPResourceStatistic(Long guId, WayEnum way, RDCommon rd, ResourceStatistic resourceStatistic) {
        super(guId, way, rd);
        this.resourceStatistic = resourceStatistic;
    }
}
