package com.bbw.god.gameuser.achievement.behavior.businessGang;

import com.bbw.common.DateUtil;
import com.bbw.god.gameuser.achievement.UserAchievementInfo;
import com.bbw.god.gameuser.achievement.behavior.BehaviorAchievementService;
import com.bbw.god.gameuser.businessgang.Enum.BusinessGangEnum;
import com.bbw.god.gameuser.statistic.StatisticTypeEnum;
import com.bbw.god.gameuser.statistic.behavior.BehaviorStatisticService;
import com.bbw.god.gameuser.statistic.behavior.BehaviorType;
import com.bbw.god.gameuser.statistic.behavior.businessgang.BusinessGangStatistic;
import org.springframework.stereotype.Service;

/**
 * 成就17080服务类
 *
 * @author fzj
 * @date 2022/3/25 10:47
 */
@Service
public class AchievementService17080 extends BehaviorAchievementService {
    @Override
    public BehaviorType getMyBehaviorType() {
        return BehaviorType.BUSINESS_GANG;
    }

    @Override
    public int getMyAchievementId() {
        return 17080;
    }

    @Override
    public int getMyProgress(long uid, UserAchievementInfo info) {
        if (isAccomplished(info)) {
            return getMyNeedValue();
        }
        BehaviorStatisticService service = statisticServiceFactory.getByBehaviorType(getMyBehaviorType());
        BusinessGangStatistic statistic = service.fromRedis(uid, StatisticTypeEnum.NONE, DateUtil.getTodayInt());
        Integer prestige = statistic.getBusinessGangPrestige().get(BusinessGangEnum.ZHENG_CAI.getName());
        if (prestige == null) {
            return 0;
        }
        int target = 9300   ;
        return prestige >= target ? 1 : 0;
    }
}
