package com.bbw.god.gameuser.historydata;

import com.bbw.common.DateUtil;
import com.bbw.common.ListUtil;
import com.bbw.god.activity.ActivityService;
import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.UserActivity;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.activity.game.GameActivity;
import com.bbw.god.activity.game.GameActivityService;
import com.bbw.god.activity.server.ServerActivity;
import com.bbw.god.activity.server.ServerActivityService;
import com.bbw.god.detail.LogUtil;
import com.bbw.god.game.data.GameDataService;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.card.UserCard;
import com.bbw.god.gameuser.card.event.EPCardDel;
import com.bbw.god.gameuser.card.event.UserCardDelEvent;
import com.bbw.god.login.event.LoginEvent;
import com.bbw.god.server.ServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 删除旧数据
 *
 * @author suhq
 * @date 2019-06-19 09:58:17
 */
@Component
public class DelHistoryUserActivityListener {
    @Autowired
    private GameDataService gameDataService;
    @Autowired
    private ServerService serverService;
    @Autowired
    private GameUserService gameUserService;

    @Autowired
    private ServerActivityService serverActivityService;
    @Autowired
    private GameActivityService gameActivityService;
    @Autowired
    private ActivityService activityService;
    @Autowired
    private DelHistoryDataService delHistoryDataService;

    @Async
    @EventListener
    public void delAsLogin(LoginEvent event) {
        long uid = event.getLoginPlayer().getUid();
        // 删除过期的玩家活动记录
        delExpiredActivities(uid);
        // 删除5天前结束的活动
        delDaysAgoUserActivities(uid, 7);
        //删除活动实例不存的ua
        delNotExistActivity(uid);
    }

    @Async
    @EventListener
    public void delCard(UserCardDelEvent event) {
        EPCardDel ep = event.getEP();
        List<UserCard> uCards = ep.getDelCards();
        if (ListUtil.isNotEmpty(uCards)) {
            LogUtil.logDeletedUserDatas(uCards, "卡牌数据");
        }
    }

    /**
     * 删除过期的玩家活动记录
     *
     * @param uid
     */
    private void delExpiredActivities(long uid) {
        int sId = this.gameUserService.getActiveSid(uid);
        Date now = DateUtil.now();
        List<IActivity> expiredActivities = new ArrayList<>();
        Date dateBeforeAsDays = DateUtil.addDays(now, DelHistoryDataService.dayBeforeAsDays);
        Date dateBeforeAsWeeks = DateUtil.addDays(now, DelHistoryDataService.dayBeforeAsWeeks);
        Date dateBeforeAsMonths = DateUtil.addDays(now, DelHistoryDataService.dayBeforeAsMonths);
        // 区服
        List<Integer> dayServerActivity = Arrays.asList(
                ActivityEnum.TODAY_ACC_R.getValue(),
                ActivityEnum.HOLIDAY_DAY_ACC_R.getValue(),
                ActivityEnum.HOLIDAY_DAY_ACC_R_1_51.getValue(),
                ActivityEnum.HOLIDAY_DAY_ACC_R_1_52.getValue(),
                ActivityEnum.HOLIDAY_DAY_ACC_R2.getValue(),
                ActivityEnum.HOLIDAY_PER_DAY_ACC_R.getValue());
        List<ServerActivity> sas = serverActivityService.getServerActivities(sId);
        List<ServerActivity> expiredSas = sas.stream().filter(tmp -> {
            boolean unvalid = !tmp.ifTimeValid();
            boolean isDayBefore = dayServerActivity.contains(tmp.getType()) && tmp.ifBefore(dateBeforeAsDays, now);
            boolean isMonthBefore = tmp.ifBefore(dateBeforeAsMonths, now);
            return unvalid && (isDayBefore || isMonthBefore);
        }).collect(Collectors.toList());
        expiredActivities.addAll(expiredSas);

        // 全服
        List<Integer> dayGameActivity = Arrays.asList(ActivityEnum.DICE.getValue());
        List<Integer> weekGameActivity = Arrays.asList(
                ActivityEnum.MULTI_DAY_ACC_R.getValue(),
                ActivityEnum.MULTI_DAY_ACC_R2.getValue(),
                ActivityEnum.MULTI_DAY_ACC_R3.getValue());
        List<GameActivity> gas = gameActivityService.getGameActivitiesBySid(sId);
        List<GameActivity> expiredGas = gas.stream().filter(tmp -> {
            boolean unvalid = !tmp.ifTimeValid();
            boolean isDayBefore = dayGameActivity.contains(tmp.getType()) && tmp.ifBefore(dateBeforeAsDays, now);
            boolean isWeekBefore = weekGameActivity.contains(tmp.getType()) && tmp.ifBefore(dateBeforeAsWeeks, now);
            boolean isMonthBefore = tmp.ifBefore(dateBeforeAsMonths, now);
            return unvalid && (isDayBefore || isWeekBefore || isMonthBefore);
        }).collect(Collectors.toList());
        expiredActivities.addAll(expiredGas);
        expiredActivities.remove(null);
        // 删除并备忘数据
        if (ListUtil.isNotEmpty(expiredActivities)) {
            List<Long> aIds = expiredActivities.stream().map(IActivity::gainId).collect(Collectors.toList());
            List<UserActivity> userActivities = activityService.getUserActivities(uid);
            List<UserActivity> uaToDels = userActivities.stream().filter(ua -> aIds.contains(ua.getAId())).collect(Collectors.toList());
            System.out.println("删除过期的玩家活动记录");
            delHistoryDataService.delUserData(uid, uaToDels);
        }
    }

    /**
     * 删除days天前结束的活动实例
     *
     * @param uid
     */
    private void delDaysAgoUserActivities(long uid, int days) {
        int sid = gameUserService.getActiveSid(uid);
        List<IActivity> activities = activityService.getDaysAgoActivities(sid, days);
        List<Long> aIds = activities.stream().map(IActivity::gainId).collect(Collectors.toList());
        List<UserActivity> userActivities = activityService.getUserActivities(uid);
        List<UserActivity> uaToDels = userActivities.stream().filter(ua -> aIds.contains(ua.getAId())).collect(Collectors.toList());
        System.out.println("删除days天前结束的活动实例");
        delHistoryDataService.delUserData(uid, uaToDels);
    }

    /**
     * 删除不存在活动实例的ua
     *
     * @param uid
     */
    private void delNotExistActivity(long uid) {
        int sid = gameUserService.getActiveSid(uid);
        List<IActivity> as = new ArrayList<>();
        // 区服活动实例
        List<ServerActivity> sas = this.serverActivityService.getServerActivities(sid);
        // 游戏活动实例
        List<GameActivity> gas = this.gameActivityService.getGameActivitiesBySid(sid);
        as.addAll(sas);
        as.addAll(gas);
        List<Long> aIds = as.stream().map(IActivity::gainId).collect(Collectors.toList());
        List<UserActivity> userActivities = activityService.getUserActivities(uid);
        List<UserActivity> uaToDels = userActivities.stream().filter(ua -> !aIds.contains(ua.getAId())).collect(Collectors.toList());
        System.out.println("删除不存在活动实例的ua");
        delHistoryDataService.delUserData(uid, uaToDels);
    }

}
