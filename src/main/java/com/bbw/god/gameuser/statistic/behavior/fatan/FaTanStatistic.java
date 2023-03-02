package com.bbw.god.gameuser.statistic.behavior.fatan;

import com.bbw.god.gameuser.statistic.behavior.BehaviorStatistic;
import com.bbw.god.gameuser.statistic.behavior.BehaviorType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Map;

import static com.bbw.god.gameuser.statistic.StatisticConst.NUM;
import static com.bbw.god.gameuser.statistic.StatisticConst.UNDERLINE;

/**
 * 法坛行为统计
 *
 * @author fzj
 * @date 2021/11/1 18:17
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class FaTanStatistic extends BehaviorStatistic {
    public static String UNLOCK_FATAN_NUM = "unlockFaTanNum";
    public static String ALL_FATAN_LV = "totalFaTanLv";
    /** 解锁法坛总数量 */
    private Integer unlockFaTanNum = 0;
    /** 法坛总等级 */
    private Integer totalFaTanLv = 0;

    public FaTanStatistic() {
        super(BehaviorType.FA_TAN);
    }

    public FaTanStatistic(int date, Map<String, Integer> redisMap) {
        setBehaviorType(BehaviorType.FA_TAN);
        String dateNumStr = date + UNDERLINE + NUM;
        Integer today = redisMap.getOrDefault(dateNumStr, 0);
        setToday(today);
        //解锁法坛总数量统计
        unlockFaTanNum = redisMap.getOrDefault(UNLOCK_FATAN_NUM, 0);
        //法坛总等级统计
        totalFaTanLv = redisMap.getOrDefault(ALL_FATAN_LV, 0);
    }
}
