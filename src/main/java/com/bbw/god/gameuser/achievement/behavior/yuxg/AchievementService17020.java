package com.bbw.god.gameuser.achievement.behavior.yuxg;

import com.bbw.common.DateUtil;
import com.bbw.god.gameuser.achievement.UserAchievementInfo;
import com.bbw.god.gameuser.achievement.behavior.BehaviorAchievementService;
import com.bbw.god.gameuser.statistic.StatisticTypeEnum;
import com.bbw.god.gameuser.statistic.behavior.BehaviorStatisticService;
import com.bbw.god.gameuser.statistic.behavior.BehaviorType;
import com.bbw.god.gameuser.statistic.behavior.yuxg.YuXGStatistic;
import com.bbw.god.gameuser.yuxg.Enum.FuTuEnum;
import com.bbw.god.gameuser.yuxg.Enum.FuTuTypeEnum;
import org.springframework.stereotype.Service;

/**
 * 成就16720的service
 *
 * @author fzj
 * @date 2021/11/16 10:30
 */
@Service
public class AchievementService17020 extends BehaviorAchievementService {
    /**
     * 获取当前行为类型
     *
     * @return 当前行为类型
     */
    @Override
    public BehaviorType getMyBehaviorType() {
        return BehaviorType.YU_XG;
    }

    /**
     * 获取当前成就id
     *
     * @return 当前成就id
     */
    @Override
    public int getMyAchievementId() {
        return 17020;
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
        YuXGStatistic statistic = service.fromRedis(uid, StatisticTypeEnum.NONE, DateUtil.getTodayInt());
        Integer fiveSkill = statistic.getSkillsFuTuNums().get(FuTuTypeEnum.SKILLS_FU_TU.getName() + FuTuEnum.FIVE_FU_TU.getName());
        Integer fiveDefense = statistic.getDefenseFuTuNums().get(FuTuTypeEnum.DEFENSE_FU_TU.getName() + FuTuEnum.FIVE_FU_TU.getName());
        Integer fiveAttack = statistic.getAttackFuTuNums().get(FuTuTypeEnum.ATTACK_FU_TU.getName() + FuTuEnum.FIVE_FU_TU.getName());
        Integer fiveBlood = statistic.getBloodFuTuNums().get(FuTuTypeEnum.BLOOD_FU_TU.getName() + FuTuEnum.FIVE_FU_TU.getName());
        if (fiveSkill + fiveDefense + fiveAttack + fiveBlood >= 15){
            return 1;
        }
        return 0;
    }
}
