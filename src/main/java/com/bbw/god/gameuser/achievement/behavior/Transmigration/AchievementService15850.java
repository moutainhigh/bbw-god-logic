package com.bbw.god.gameuser.achievement.behavior.Transmigration;

import com.bbw.common.DateUtil;
import com.bbw.god.gameuser.achievement.UserAchievementInfo;
import com.bbw.god.gameuser.achievement.behavior.BehaviorAchievementService;
import com.bbw.god.gameuser.statistic.StatisticTypeEnum;
import com.bbw.god.gameuser.statistic.behavior.BehaviorStatisticService;
import com.bbw.god.gameuser.statistic.behavior.BehaviorType;
import com.bbw.god.gameuser.statistic.behavior.Transmigration.TransmigrationStatistic;
import org.springframework.stereotype.Service;

/**
 * 成就15850的service
 * 知难而进III
 * @author fzj
 * @date 2021/9/18 15:43
 */
@Service
public class AchievementService15850 extends BehaviorAchievementService {
    /**
     * 获取当前成就的类型
     *
     * @return 当前成就的类型
     */
    @Override
    public BehaviorType getMyBehaviorType() {
        return BehaviorType.TRANSMIGRATION_CHALLENGE;
    }
    /**
     * 获取当前成就的id
     *
     * @return 当前成就的id
     */

    @Override
    public int getMyAchievementId() {
        return 15850;
    }
    /**
     * 获取当前成就的id
     *
     * @return 当前成就的id
     */
    @Override
    public int getMyProgress(long uid, UserAchievementInfo info) {
        if (isAccomplished(info)) {
            return getMyNeedValue();
        }
        BehaviorStatisticService service = statisticServiceFactory.getByBehaviorType(getMyBehaviorType());
        TransmigrationStatistic statistic = service.fromRedis(uid, StatisticTypeEnum.NONE, DateUtil.getTodayInt());
        return statistic.getTotal();
    }
}
