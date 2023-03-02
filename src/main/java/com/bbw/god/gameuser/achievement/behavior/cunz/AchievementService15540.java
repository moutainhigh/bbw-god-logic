package com.bbw.god.gameuser.achievement.behavior.cunz;

import com.bbw.common.DateUtil;
import com.bbw.god.gameuser.achievement.UserAchievementInfo;
import com.bbw.god.gameuser.achievement.behavior.BehaviorAchievementService;
import com.bbw.god.gameuser.statistic.StatisticTypeEnum;
import com.bbw.god.gameuser.statistic.behavior.BehaviorStatisticService;
import com.bbw.god.gameuser.statistic.behavior.BehaviorType;
import com.bbw.god.gameuser.statistic.behavior.task.CunZTaskStatistic;
import com.bbw.god.gameuser.task.TaskDifficulty;
import org.springframework.stereotype.Service;

/**
 * 最强武器
 * 成就id=15540相关的service
 *
 * @author fzj
 * @date 2021/8/12 15:41
 */
@Service
public class AchievementService15540 extends BehaviorAchievementService {
    /**
     * 获取当前成就的类型
     *
     * @return 当前成就的类型
     */
    @Override
    public BehaviorType getMyBehaviorType() {
        return BehaviorType.FINISH_CUNZ_TASK;
    }

    /**
     * 获取当前成就的id
     *
     * @return 当前成就的id
     */
    @Override
    public int getMyAchievementId() {
        return 15540;
    }

    /**
     * 获取当前成就的进度
     *
     * @param uid  玩家id
     * @param info 成就对象信息
     * @return 当前成就的进度
     */
    @Override
    public int getMyProgress(long uid, UserAchievementInfo info) {
        if (isAccomplished(info)) {
            return getMyNeedValue();
        }
        BehaviorStatisticService service = statisticServiceFactory.getByBehaviorType(getMyBehaviorType());
        CunZTaskStatistic statistic = service.fromRedis(uid, StatisticTypeEnum.NONE, DateUtil.getTodayInt());
        if (statistic.getDifficultyAccomplishedNums().get(TaskDifficulty.SUPER_LEVEL.getName()) >= 3) {
            return 1;
        }
        return 0;
    }
}
