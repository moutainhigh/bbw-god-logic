package com.bbw.god.gameuser.statistic.behavior.yaozu;

import com.bbw.god.gameuser.statistic.behavior.BehaviorStatistic;
import com.bbw.god.gameuser.statistic.behavior.BehaviorType;
import com.bbw.god.gameuser.yaozu.YaoZuEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.bbw.god.gameuser.statistic.StatisticConst.*;
import static com.bbw.god.gameuser.statistic.StatisticConst.TOTAL;

/**
 * 妖族行为统计
 *
 * @author fzj
 * @date 2021/9/8 15:52
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class YaoZuStatistic extends BehaviorStatistic {
    /** 击败妖族数量 YaoZuEnum:击败数量*/
    private Map<String, Integer> beatYaoZuNums;

    public YaoZuStatistic() {
        super(BehaviorType.YAO_ZU_WIN);
    }

    public YaoZuStatistic(int date, Map<String, Integer> redisMap) {
        setBehaviorType(BehaviorType.YAO_ZU_WIN);
        String dateNumStr = date + UNDERLINE + NUM;
        Integer today = redisMap.get(dateNumStr) == null ? 0 : redisMap.get(dateNumStr);
        Integer total = redisMap.get(TOTAL) == null ? 0 : redisMap.get(TOTAL);
        setToday(today);
        setTotal(total);

        //击败统计
        Map<String, Integer> yaoZuMap = new HashMap<>();
        for (YaoZuEnum yaoZu : YaoZuEnum.values()){
            String yaoZuField = yaoZu.getName();
            Integer num = redisMap.get(yaoZuField);
            num = num == null ? 0 : num;
            yaoZuMap.put(yaoZuField, num);
        }
        setBeatYaoZuNums(yaoZuMap);
    }
}

