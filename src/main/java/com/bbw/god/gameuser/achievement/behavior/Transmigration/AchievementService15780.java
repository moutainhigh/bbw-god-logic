package com.bbw.god.gameuser.achievement.behavior.Transmigration;

import com.bbw.common.DateUtil;
import com.bbw.god.game.config.city.CityTool;
import com.bbw.god.gameuser.achievement.UserAchievementInfo;
import com.bbw.god.gameuser.achievement.behavior.BehaviorAchievementService;
import com.bbw.god.gameuser.statistic.StatisticTypeEnum;
import com.bbw.god.gameuser.statistic.behavior.BehaviorStatisticService;
import com.bbw.god.gameuser.statistic.behavior.BehaviorType;
import com.bbw.god.gameuser.statistic.behavior.Transmigration.TransmigrationStatistic;
import org.springframework.stereotype.Service;

/**
 * 成就15780的service
 * 名扬东鲁
 * @author fzj
 * @date 2021/9/18 15:42
 */
@Service
public class AchievementService15780 extends BehaviorAchievementService {
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
        return 15780;
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
        //朝歌城池
        String cityName = CityTool.getCityById(1024).getName();
        return statistic.getSuccessPerCity().get(cityName);
    }
}
