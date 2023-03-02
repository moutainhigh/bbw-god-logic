package com.bbw.god.gameuser.achievement.behavior.businessGang;

import com.bbw.common.DateUtil;
import com.bbw.god.gameuser.achievement.UserAchievementInfo;
import com.bbw.god.gameuser.achievement.behavior.BehaviorAchievementService;
import com.bbw.god.gameuser.businessgang.Enum.BusinessGangEnum;
import com.bbw.god.gameuser.businessgang.Enum.BusinessGangNpcEnum;
import com.bbw.god.gameuser.statistic.StatisticTypeEnum;
import com.bbw.god.gameuser.statistic.behavior.BehaviorStatisticService;
import com.bbw.god.gameuser.statistic.behavior.BehaviorType;
import com.bbw.god.gameuser.statistic.behavior.businessgang.BusinessGangStatistic;
import org.springframework.stereotype.Service;

/**
 * 成就17130服务类
 *
 * @author fzj
 * @date 2022/3/25 10:47
 */
@Service
public class AchievementService17130 extends BehaviorAchievementService {
    @Override
    public BehaviorType getMyBehaviorType() {
        return BehaviorType.BUSINESS_GANG;
    }

    @Override
    public int getMyAchievementId() {
        return 17130;
    }

    @Override
    public int getMyProgress(long uid, UserAchievementInfo info) {
        if (isAccomplished(info)) {
            return getMyNeedValue();
        }
        BehaviorStatisticService service = statisticServiceFactory.getByBehaviorType(getMyBehaviorType());
        BusinessGangStatistic statistic = service.fromRedis(uid, StatisticTypeEnum.NONE, DateUtil.getTodayInt());
        Integer favorability = statistic.getNpcFavorability().get(BusinessGangNpcEnum.ZHAO_GM.getName());
        if (favorability == null) {
            return 0;
        }
        int target = 6500;
        return favorability >= target ? 1 : 0;
    }
}
