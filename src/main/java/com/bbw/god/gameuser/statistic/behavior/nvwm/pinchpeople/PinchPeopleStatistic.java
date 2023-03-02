package com.bbw.god.gameuser.statistic.behavior.nvwm.pinchpeople;

import com.bbw.god.gameuser.statistic.behavior.BehaviorStatistic;
import com.bbw.god.gameuser.statistic.behavior.BehaviorType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Map;

import static com.bbw.god.gameuser.statistic.StatisticConst.NUM;
import static com.bbw.god.gameuser.statistic.StatisticConst.UNDERLINE;

/**
 * 捏人行为统计
 *
 * @author fzj
 * @date 2021/11/1 18:17
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class PinchPeopleStatistic extends BehaviorStatistic {
    public static String PINCH_PEOPLE_TIMES = "pinchPeoleTimes";
    /** 捏人次数 */
    private Integer pinchPeoleTimes = 0;


    public PinchPeopleStatistic() {
        super(BehaviorType.PINCH_PEOPLE);
    }

    /**
     * 捏人次数统计
     *
     * @param date
     * @param redisMap
     */
    public PinchPeopleStatistic(int date, Map<String, Integer> redisMap) {
        setBehaviorType(BehaviorType.PINCH_PEOPLE);
        String dateNumStr = date + UNDERLINE + NUM;
        Integer today = redisMap.getOrDefault(dateNumStr, 0);
        setToday(today);
        //捏人次数
        pinchPeoleTimes = redisMap.getOrDefault(PINCH_PEOPLE_TIMES, 0);

    }
}
