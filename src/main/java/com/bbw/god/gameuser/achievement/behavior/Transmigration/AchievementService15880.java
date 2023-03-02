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
 * 成就15880的service
 * 展露拳脚
 *
 * @author fzj
 * @date 2021/9/30 9:49
 */
@Service
public class AchievementService15880 extends BehaviorAchievementService {
    @Override
    public BehaviorType getMyBehaviorType() {
        return BehaviorType.TRANSMIGRATION_CHALLENGE;
    }

    @Override
    public int getMyAchievementId() {
        return 15880;
    }

    @Override
    public int getMyProgress(long uid, UserAchievementInfo info) {
        if (isAccomplished(info)) {
            return getMyNeedValue();
        }
        BehaviorStatisticService service = statisticServiceFactory.getByBehaviorType(getMyBehaviorType());
        TransmigrationStatistic statistic = service.fromRedis(uid, StatisticTypeEnum.NONE, DateUtil.getTodayInt());
        if (statistic.getTotal() >= 1) {
            return 1;
        }
        return 0;
    }
}
