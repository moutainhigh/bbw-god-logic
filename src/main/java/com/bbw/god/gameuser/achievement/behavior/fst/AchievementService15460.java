package com.bbw.god.gameuser.achievement.behavior.fst;

import com.bbw.common.DateUtil;
import com.bbw.god.gameuser.achievement.UserAchievementInfo;
import com.bbw.god.gameuser.achievement.behavior.BehaviorAchievementService;
import com.bbw.god.gameuser.statistic.StatisticTypeEnum;
import com.bbw.god.gameuser.statistic.behavior.BehaviorStatisticService;
import com.bbw.god.gameuser.statistic.behavior.BehaviorType;
import com.bbw.god.gameuser.statistic.behavior.fst.FstStatistic;
import org.springframework.stereotype.Service;

/**
 * 稳如泰山
 * 成就id=15460的service
 *
 * @author fzj
 * @date 2021/8/16 11:02
 */
@Service
public class AchievementService15460 extends BehaviorAchievementService {
    @Override
    public BehaviorType getMyBehaviorType() {
        return BehaviorType.FST;
    }

    @Override
    public int getMyAchievementId() {
        return 15460;
    }

    @Override
    public int getMyProgress(long uid, UserAchievementInfo info) {
        if (isAccomplished(info)) {
            return getMyNeedValue();
        }
        BehaviorStatisticService service = statisticServiceFactory.getByBehaviorType(getMyBehaviorType());
        FstStatistic statistic = service.fromRedis(uid, StatisticTypeEnum.NONE, DateUtil.getTodayInt());
        if (statistic.getWinNum() >= 201) {
            return 1;
        }
        return 0;
    }
}
