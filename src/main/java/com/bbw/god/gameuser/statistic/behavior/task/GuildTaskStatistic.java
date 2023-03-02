package com.bbw.god.gameuser.statistic.behavior.task;

import com.bbw.god.gameuser.statistic.behavior.BehaviorStatistic;
import com.bbw.god.gameuser.statistic.behavior.BehaviorType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author suchaobin
 * @description 行会任务统计
 * @date 2020/11/25 16:31
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class GuildTaskStatistic extends BehaviorStatistic {
    public GuildTaskStatistic(Integer today, Integer total, Integer date) {
        super(today, total, date, BehaviorType.GUILD_TASK);
    }

    public GuildTaskStatistic() {
        super(BehaviorType.GUILD_TASK);
    }
}