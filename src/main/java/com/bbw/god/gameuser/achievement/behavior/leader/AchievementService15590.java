package com.bbw.god.gameuser.achievement.behavior.leader;

import com.bbw.common.DateUtil;
import com.bbw.god.gameuser.achievement.UserAchievementInfo;
import com.bbw.god.gameuser.achievement.behavior.BehaviorAchievementService;
import com.bbw.god.gameuser.statistic.StatisticTypeEnum;
import com.bbw.god.gameuser.statistic.behavior.BehaviorStatisticService;
import com.bbw.god.gameuser.statistic.behavior.BehaviorType;
import com.bbw.god.gameuser.statistic.behavior.leader.equipment.LeaderEquipmentStatistic;
import org.springframework.stereotype.Service;

/**
 * 刀光剑影
 *
 * @author fzj
 * @description 成就15590的service
 * @date 2021/8/11 16:02
 */
@Service
public class AchievementService15590 extends BehaviorAchievementService {
    /**
     * 获取当前成就类型
     *
     * @return 当前成就类型
     */
    @Override
    public BehaviorType getMyBehaviorType() {
        return BehaviorType.LEADER_EQUIPMENT;
    }

    /**
     * 获取当前成就ID
     *
     * @return 当前成就ID
     */
    @Override
    public int getMyAchievementId() {
        return 15590;
    }

    /**
     * 获取当前成就进度
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
        BehaviorStatisticService service = statisticServiceFactory.getByBehaviorType(getMyBehaviorType());
        LeaderEquipmentStatistic statistic = service.fromRedis(uid, StatisticTypeEnum.NONE, DateUtil.getTodayInt());
        return statistic.getWeaponLv();
    }
}
