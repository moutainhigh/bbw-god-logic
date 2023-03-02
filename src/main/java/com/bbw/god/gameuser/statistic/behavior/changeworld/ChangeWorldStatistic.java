package com.bbw.god.gameuser.statistic.behavior.changeworld;

import com.bbw.god.gameuser.statistic.behavior.BehaviorStatistic;
import com.bbw.god.gameuser.statistic.behavior.BehaviorType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author suchaobin
 * @description 跳转世界统计
 * @date 2020/9/24 11:10
 **/
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ChangeWorldStatistic extends BehaviorStatistic {

    public ChangeWorldStatistic(Integer today, Integer total, Integer date) {
        super(today, total, date, BehaviorType.CHANGE_WORLD);
    }
}
