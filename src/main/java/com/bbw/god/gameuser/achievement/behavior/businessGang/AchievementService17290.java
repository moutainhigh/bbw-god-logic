package com.bbw.god.gameuser.achievement.behavior.businessGang;

import com.bbw.common.DateUtil;
import com.bbw.god.gameuser.achievement.UserAchievementInfo;
import com.bbw.god.gameuser.achievement.behavior.BehaviorAchievementService;
import com.bbw.god.gameuser.statistic.StatisticTypeEnum;
import com.bbw.god.gameuser.statistic.behavior.BehaviorStatisticService;
import com.bbw.god.gameuser.statistic.behavior.BehaviorType;
import com.bbw.god.gameuser.statistic.behavior.businessgang.BusinessGangStatistic;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 成就17290服务类
 *
 * @author fzj
 * @date 2022/3/25 10:47
 */
@Service
public class AchievementService17290 extends BehaviorAchievementService {
    @Override
    public BehaviorType getMyBehaviorType() {
        return BehaviorType.BUSINESS_GANG;
    }

    @Override
    public int getMyAchievementId() {
        return 17290;
    }

    @Override
    public int getMyProgress(long uid, UserAchievementInfo info) {
        if (isAccomplished(info)) {
            return getMyNeedValue();
        }
        int finish = 0;
        BehaviorStatisticService service = statisticServiceFactory.getByBehaviorType(getMyBehaviorType());
        BusinessGangStatistic statistic = service.fromRedis(uid, StatisticTypeEnum.NONE, DateUtil.getTodayInt());
        List<Integer> finishTimes = new ArrayList<>(statistic.getFinishWeeklyTaskNum().values());
        for (Integer task : finishTimes) {
            if (null == task || task <= 0) {
                continue;
            }
            finish++;
        }
        return finish;
    }
}
