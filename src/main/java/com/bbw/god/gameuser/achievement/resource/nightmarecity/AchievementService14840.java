package com.bbw.god.gameuser.achievement.resource.nightmarecity;

import com.bbw.common.DateUtil;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.gameuser.achievement.UserAchievementInfo;
import com.bbw.god.gameuser.achievement.resource.ResourceAchievementService;
import com.bbw.god.gameuser.statistic.StatisticTypeEnum;
import com.bbw.god.gameuser.statistic.resource.ResourceStatisticService;
import com.bbw.god.gameuser.statistic.resource.nightmarecity.NightmareCityStatistic;
import org.springframework.stereotype.Service;

/**
 * @author suchaobin
 * @description 成就id=14840的service
 * @date 2020/9/15 10:53
 **/
@Service
public class AchievementService14840 extends ResourceAchievementService {
    /**
     * 获取当前资源类型
     *
     * @return 当前资源类型
     */
    @Override
    public AwardEnum getMyAwardEnum() {
        return AwardEnum.NIGHTMARE_CITY;
    }

    /**
     * 获取当前成就id
     *
     * @return 当前成就id
     */
    @Override
    public int getMyAchievementId() {
        return 14840;
    }

    /**
     * 获取当前成就进度(用于展示给客户端)
     *
     * @param uid  玩家id
     * @param info 成就对象信息
     * @return 当前成就进度
     */
    @Override
    public int getMyProgress(long uid, UserAchievementInfo info) {
        if (isAccomplished(info)) {
            return getMyNeedValue();
        }
        ResourceStatisticService service = statisticServiceFactory.getByAwardEnum(getMyAwardEnum());
        NightmareCityStatistic statistic = service.fromRedis(uid, StatisticTypeEnum.GAIN, DateUtil.getTodayInt());
        return statistic.getGoldCountryCity();
    }
}
