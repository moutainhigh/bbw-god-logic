package com.bbw.god.game.dfdj.event;

import com.bbw.god.event.BaseEventParam;
import com.bbw.god.game.dfdj.config.DfdjRankType;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author suchaobin
 * @description 巅峰对决奖励发动事件参数
 * @date 2021/1/6 10:01
 **/
@Data
@EqualsAndHashCode(callSuper = true)
public class EPDfdjAwardSend extends BaseEventParam {
    private Integer season;
    private DfdjRankType rankType;
    private Integer rank;

    public EPDfdjAwardSend(Integer season, DfdjRankType rankType, Integer rank, BaseEventParam bep) {
        this.season = season;
        this.rankType = rankType;
        this.rank = rank;
        setValues(bep);
    }
}
