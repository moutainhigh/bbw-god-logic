package com.bbw.god.login.repairdata;

import com.bbw.common.DateUtil;
import com.bbw.god.activity.ActivityService;
import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.UserActivity;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.activity.config.ActivityTool;
import com.bbw.god.game.award.AwardStatus;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.statistic.behavior.recharge.RechargeStatistic;
import com.bbw.god.gameuser.statistic.behavior.recharge.RechargeStatisticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

import static com.bbw.god.login.repairdata.RepairDataConst.REPAIR_PER_ACCR_TIME;

/**
 * @author suchaobin
 * @description 修复充值活动状态问题
 * @date 2020/9/13 0:21
 **/
@Service
public class RepairHolidayRechargeActivityService implements BaseRepairDataService {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private RechargeStatisticService statisticService;
    @Autowired
    private ActivityService activityService;

    /**
     * 修复数据
     *
     * @param gu            玩家对象
     * @param lastLoginDate 上次登录时间
     */
    @Override
    public void repair(GameUser gu, Date lastLoginDate) {
        if (lastLoginDate.before(REPAIR_PER_ACCR_TIME)) {
            long uid = gu.getId();
            int sid = gameUserService.getOriServer(uid).getMergeSid();
            IActivity a = this.activityService.getActivity(sid, ActivityEnum.HOLIDAY_PER_ACC_R_10);
            if (null == a) {
                return;
            }
            int activityId = ActivityTool.getActivityByType(ActivityEnum.HOLIDAY_PER_ACC_R_10).getId();
            UserActivity userActivity = activityService.getUserActivity(uid, a.gainId(), activityId);
            if (null == userActivity) {
                return;
            }
            RechargeStatistic statistic = statisticService.fromRedis(uid, DateUtil.getTodayInt());
            Integer todayRecharge = statistic.getToday();
            if (todayRecharge == 0 || todayRecharge < 10) {
                return;
            }
            if (AwardStatus.AWARDED.getValue() == userActivity.getStatus()) {
                return;
            }
            int currentPor = userActivity.getProgress() / 10;
            int pro = todayRecharge / 10;
            if (pro == currentPor) {
                return;
            }
            userActivity.setStatus(AwardStatus.ENABLE_AWARD.getValue());
            gameUserService.updateItem(userActivity);
        }
    }
}
