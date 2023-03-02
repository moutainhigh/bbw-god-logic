package com.bbw.god.activity.game;

import com.bbw.common.DateUtil;
import com.bbw.common.ListUtil;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.activity.config.ActivityTool;
import com.bbw.god.db.entity.CfgActivityEntity;
import com.bbw.god.game.data.GameDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class GameActivityGeneratorService {
    @Autowired
    private GameDataService gameDataService;

    /**
     * 初始化全服游戏数据
     */
    public void initGameActivity() {
        log.info("开始执行活动初始化。。。");

        List<GameActivity> gas = gameDataService.getGameDatas(GameActivity.class);
        // 单一实例
        initSingleActivities(gas);

        // 每天,生成本日到下一个月的所有实例
        appendDayActivities(gas, 35, true);

        // 每月的活动，生成本月和下一个月的实例
        appendMonthActivities(gas, 0);
        appendMonthActivities(gas, 1);

        appendRechargeSign(gas, 0);
        appendRechargeSign(gas, 1);
    }

    /**
     * 定时追加活动 days 天每日、days/30 个月月活动、days/5 期七日
     *
     * @param days
     */
    public void appendActivities(int days) {
        List<GameActivity> gas = gameDataService.getGameDatas(GameActivity.class);
        // 每天,追加一个月的所有实例
        appendDayActivities(gas, 31, true);
        // 每七天的活动，生成本周到下一个月的所有实例
        //appendSevenDaysActivities(gas, 5, false);
        // 每月的活动，追加一个月的实例
        appendMonthActivities(gas, 1);
        appendRechargeSign(gas, 1);
    }

    /**
     * 初始化单一活动（全服只有一个实例）
     *
     * @param gas
     */
    private void initSingleActivities(List<GameActivity> gas) {
        // 已生成过，则不做任何处理
        if (ListUtil.isNotEmpty(gas)) {
            return;
        }
        // 单一活动
        List<ActivityEnum> permanentActivities = Arrays.asList(ActivityEnum.FIRST_R, ActivityEnum.SEVEN_LOGIN,
                ActivityEnum.XingJBK, ActivityEnum.LIMIT_CARD, ActivityEnum.MULTIPLE_REBATE);

        for (ActivityEnum type : permanentActivities) {
            addGameActivity(type);
            log.info("完成{}的永久初始化", type.getName());
        }
    }

    /**
     * 初始化每日活动
     *
     * @param gas
     * @param days
     * @param incudeBaseDay 是否包含基准时间日期
     */
    private void appendDayActivities(List<GameActivity> gas, int days, boolean incudeBaseDay) {
        // 每天的活动
        List<ActivityEnum> dayActivities = Arrays.asList(ActivityEnum.DICE);

        for (ActivityEnum type : dayActivities) {
            Date baseDate = getBaseBeginDate(gas, type);
            int i = incudeBaseDay ? 0 : 1;
            for (; i <= days; i++) {
                Date date = DateUtil.addDays(baseDate, i);
                Date dateBegin = DateUtil.getDateBegin(date);
                Date dateEnd = DateUtil.getDateEnd(date);
                addGameActivity(type, dateBegin, dateEnd);
                log.info("完成{}的初始化{},{}", type.getName(), DateUtil.toDateTimeString(dateBegin),
                        DateUtil.toDateTimeString(dateEnd));
            }
            log.info("完成{}的初始化,初始化天数{}", type.getName(), days);
        }
    }

    /**
     * 每七天的活动
     *
     * @param gas
     * @param weeks
     * @param includeBaseWeek 是否包含基准时间日期
     */
    /*private void appendSevenDaysActivities(List<GameActivity> gas, int weeks, boolean includeBaseWeek) {
        // 每七天的活动
        List<ActivityEnum> sevenDaysActivities = Arrays.asList(ActivityEnum.MULTI_DAY_ACC_R,
                ActivityEnum.MULTI_DAY_ACC_R2, ActivityEnum.MULTI_DAY_ACC_R3, ActivityEnum.SUNDAY_ACC);

        for (ActivityEnum type : sevenDaysActivities) {
            // 追加活动基准时间
            Date baseDate = getBaseEndDate(gas, type);
            if (null == baseDate) {
                continue;
            }
            // 追加生成week周数的活动
            for (int i = includeBaseWeek ? 0 : 1; i <= weeks; i++) {
                Date dateEnd = DateUtil.addWeeks(baseDate, i);
                int daysBetween = 0;
                switch (type) {
                    case MULTI_DAY_ACC_R:
                    case MULTI_DAY_ACC_R2:
                    case MULTI_DAY_ACC_R3:
                        daysBetween = 1;
                        break;
                    case SUNDAY_ACC:
                        daysBetween = 0;
                        break;
                    default:
                        break;
                }
                Date sevenBeginDate = DateUtil.addHours(dateEnd, -24 * daysBetween);
                Date dateBegin = DateUtil.getDateBegin(sevenBeginDate);

                addGameActivity(type, dateBegin, dateEnd);
                log.info("完成{}的初始化{},{}", type.getName(), DateUtil.toDateTimeString(dateBegin),
                        DateUtil.toDateTimeString(dateEnd));
            }
            log.info("完成{}的初始化,基准时间为{},生成周数{}", type.getName(), DateUtil.toDateTimeString(baseDate), weeks);
        }
    }*/

    /**
     * 初始化每月活动
     *
     * @param gas
     * @param monthOffset 0本月，1下个月
     */
    private void appendMonthActivities(List<GameActivity> gas, int monthOffset) {
        List<ActivityEnum> monthActivities = Arrays.asList(ActivityEnum.MONTH_LOGIN);

        for (ActivityEnum type : monthActivities) {
            // 追加活动基准时间
            Date baseDate = getBaseBeginDate(gas, type);
            // 月开始、结束日期
            Date monthBegin = DateUtil.getMonthBegin(baseDate, monthOffset);
            Date monthEnd = DateUtil.getMonthEnd(baseDate, monthOffset);
            // 将时间转换为起始和结束时间
            Date dateBegin = DateUtil.getDateBegin(monthBegin);
            Date dateEnd = DateUtil.getDateEnd(monthEnd);
            addGameActivity(type, dateBegin, dateEnd);
            log.info("完成{}的初始化{},{}", type.getName(), DateUtil.toDateTimeString(dateBegin),
                    DateUtil.toDateTimeString(dateEnd));
        }
    }

    private void appendRechargeSign(List<GameActivity> gas, int monthOffset) {
        ActivityEnum type = ActivityEnum.RECHARGE_SIGN;
        //活动区间
        int activityInterval = 10;
        // 追加活动基准时间
        Date baseEndDate = getBaseEndDate(gas, type);
        //下个自动生成时间
        Date nextMonthBegin = DateUtil.getMonthBegin(DateUtil.now(), monthOffset);
        Date nextGenerateTime = DateUtil.addDays(nextMonthBegin, 20);
        //时间差
        int daysBetween = DateUtil.getDaysBetween(baseEndDate, nextGenerateTime);
        daysBetween = Math.max(daysBetween, 0);
        //开启活动数量
        int openNum = (daysBetween / activityInterval) + 1;
        Date begin = DateUtil.addSeconds(baseEndDate, 1);
        for (int i = 0; i < openNum; i++) {
            Date end = DateUtil.addDays(begin, 9);
            addGameActivity(type, begin, end);
            begin = DateUtil.addSeconds(end, 1);
        }
        log.info("完成{}的初始化,共生成{}个活动实例", type.getName(), openNum);
    }

    /**
     * 获得活动基准时间 yyyy-MM-dd 00:00:00 （已生成的最近的活动的开始时间）
     *
     * @param gas
     * @param type
     * @return
     */
    private Date getBaseBeginDate(List<GameActivity> gas, ActivityEnum type) {
        if (ListUtil.isNotEmpty(gas)) {
            List<GameActivity> typeSars = gas.stream().filter(ga -> ga.getType() == type.getValue())
                    .collect(Collectors.toList());
            if (ListUtil.isNotEmpty(typeSars)) {
                // 返回最近城池的实例的开始时间
                return typeSars.get(typeSars.size() - 1).getBegin();
            }
        }
        // 如果没有该活动的任何实例则返回当前日期的开始时间
        return DateUtil.getDateBegin(DateUtil.now());
    }

    private Date getBaseEndDate(List<GameActivity> gas, ActivityEnum type) {
        if (ListUtil.isNotEmpty(gas)) {
            List<GameActivity> typeSars = gas.stream().filter(ga -> ga.getType() == type.getValue())
                    .collect(Collectors.toList());
            if (ListUtil.isNotEmpty(typeSars)) {
                // 返回最近城池的实例的结束时间
                return typeSars.get(typeSars.size() - 1).getEnd();
            }
        }
        return null;
    }

    /**
     * 新增全服活动
     *
     * @param type
     * @param dateBegin
     * @param dateEnd
     */
    private void addGameActivity(ActivityEnum type, Date dateBegin, Date dateEnd) {
        CfgActivityEntity activity = ActivityTool.getActivitiesByType(type).get(0);
        GameActivity ga = GameActivity.fromActivity(activity, dateBegin, dateEnd);
        gameDataService.addGameData(ga);
    }

    /**
     * 新增全服活动
     *
     * @param type
     */
    public void addGameActivity(ActivityEnum type) {
        CfgActivityEntity activity = ActivityTool.getActivitiesByType(type).get(0);
        GameActivity ga = GameActivity.fromActivity(activity);
        gameDataService.addGameData(ga);
    }

    /**
     * 删除指定日期后的活动
     *
     * @param gas
     * @param activityEnums
     * @param fromDate
     */
//    private void delGameActivity(List<GameActivity> gas, List<ActivityEnum> activityEnums, Date fromDate) {
//        List<GameActivity> gasToDel = gas.stream()
//                .filter(tmp -> activityEnums.contains(ActivityEnum.fromValue(tmp.getType())) && tmp.gainEnd()
//                        .after(fromDate))
//                .collect(Collectors.toList());
//        List<Long> gaIdsToDel = gasToDel.stream().map(GameActivity::getId).collect(Collectors.toList());
//        gameDataService.deleteGameDatas(gaIdsToDel, GameActivity.class);
//    }
}
