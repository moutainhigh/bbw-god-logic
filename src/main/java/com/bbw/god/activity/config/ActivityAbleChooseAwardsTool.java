package com.bbw.god.activity.config;

import com.bbw.god.activity.cfg.CfgHolidayAbleChooseAwards;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.config.Cfg;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 节日可选择奖励工具类
 *
 * @author: huanghb
 * @date: 2022/1/4 17:07
 */
public class ActivityAbleChooseAwardsTool {
    /**
     * 获取配置类
     *
     * @return
     */
    public static List<CfgHolidayAbleChooseAwards> getCfgAbleChooseAwardsEnties(int activityType) {
        List<CfgHolidayAbleChooseAwards> ableChooseAwards = Cfg.I.get(CfgHolidayAbleChooseAwards.class);
        return ableChooseAwards.stream().filter(tmp -> tmp.getType().equals(activityType)).collect(Collectors.toList());
    }

    /**
     * 根据Id获取奖励配置
     *
     * @param activityId
     * @return
     */
    public static List<Award> getAwards(int activityId) {
        return Cfg.I.get(activityId, CfgHolidayAbleChooseAwards.class).getAwards();
    }

    /**
     * 需要设置额外奖励的活动id集合
     *
     * @return
     */
    public static List<Integer> getAbleChooseAwardIds(int type) {
        List<CfgHolidayAbleChooseAwards> ableChooseAwards = ActivityAbleChooseAwardsTool.getCfgAbleChooseAwardsEnties(type);
        return ableChooseAwards.stream().map(CfgHolidayAbleChooseAwards::getId).collect(Collectors.toList());
    }
}
