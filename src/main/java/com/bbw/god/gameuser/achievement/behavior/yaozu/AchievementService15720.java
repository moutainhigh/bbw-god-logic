package com.bbw.god.gameuser.achievement.behavior.yaozu;

import com.bbw.common.DateUtil;
import com.bbw.god.gameuser.achievement.UserAchievementInfo;
import com.bbw.god.gameuser.achievement.behavior.BehaviorAchievementService;
import com.bbw.god.gameuser.statistic.StatisticTypeEnum;
import com.bbw.god.gameuser.statistic.behavior.BehaviorStatisticService;
import com.bbw.god.gameuser.statistic.behavior.BehaviorType;
import com.bbw.god.gameuser.statistic.behavior.yaozu.YaoZuStatistic;
import com.bbw.god.gameuser.yaozu.YaoZuEnum;
import org.springframework.stereotype.Service;

/**
 * 斩妖除魔Ⅳ
 * 成就id=15720的service
 * 斩杀妲己召唤出来的15个雉鸡精
 * @author fzj
 * @date 2021/9/9 10:52
 */
@Service
public class AchievementService15720 extends BehaviorAchievementService {
    /**
     * 获取当前成就的类型
     *
     * @return 当前成就的类型
     */
    @Override
    public BehaviorType getMyBehaviorType() {
        return BehaviorType.YAO_ZU_WIN;
    }

    /**
     * 获取当前成就的id
     *
     * @return 当前成就的id
     */
    @Override
    public int getMyAchievementId() {
        return 15720;
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
        YaoZuStatistic statistic = service.fromRedis(uid, StatisticTypeEnum.NONE, DateUtil.getTodayInt());
        return statistic.getBeatYaoZuNums().get(YaoZuEnum.ZHI_JI_JING.getName());
    }
}
