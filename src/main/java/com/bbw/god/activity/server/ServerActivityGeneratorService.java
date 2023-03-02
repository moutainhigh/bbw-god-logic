package com.bbw.god.activity.server;

import com.bbw.common.DateUtil;
import com.bbw.common.ListUtil;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.activity.config.ActivityTool;
import com.bbw.god.db.entity.CfgActivityEntity;
import com.bbw.god.db.entity.CfgServerEntity;
import com.bbw.god.detail.LogUtil;
import com.bbw.god.server.ServerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ServerActivityGeneratorService {

    @Autowired
    private ServerService serverService;

    private static final int FOREVER = 18250;

    /**
     * 开服活动初始化(开服调用前要执行清除处理)
     *
     * @param server
     */
    public void initServerActivityForNewServer(CfgServerEntity server) {
        log.info("{} 开始执行活动初始化", LogUtil.getLogServerPart(server));
        int sId = server.getMergeSid();
        // 一次性限时活动
        initOneTimeActivities(server);

        List<ServerActivity> sas = serverService.getServerDatas(sId, ServerActivity.class);

        // 每天,生成本日到下一个月的所有实例
        appendDayActivities(server, sas, 35, true);

        // 每七天的活动（基于开服时间），生成本周到下一个月的所有实例
        appendSevenDaysActivities(server, sas, 5, true);
    }

    /**
     * 一次性限时活动
     *
     * @param server
     */
    private void initOneTimeActivities(CfgServerEntity server) {

        List<ActivityEnum> activities = new ArrayList<>();
        activities.add(ActivityEnum.GongCLD);
        activities.add(ActivityEnum.NEWER_BOOST);
        activities.add(ActivityEnum.CARD_EXP_BOOST);
        activities.add(ActivityEnum.CARD_LEVEL_BOOST);
        //activities.add(ActivityEnum.DRAW_CARD_TH);
        // activities.add(ActivityEnum.ACC_R);
        //	activities.add(ActivityEnum.GOD_POWER_SWEEP);
        activities.add(ActivityEnum.NEWER_PACKAGE);
        List<Integer> beginDays = Arrays.asList(0, 0, 0, 0, 0);
        List<Integer> endDays = Arrays.asList(FOREVER, FOREVER, FOREVER, FOREVER, FOREVER);
        int sId = server.getMergeSid();
        Date dateBegin = DateUtil.getDateBegin(server.getBeginTime());
        for (int i = 0; i < activities.size(); i++) {
            ActivityEnum type = activities.get(i);
            Date aBeginDate = dateBegin;
            if (beginDays.get(i) > 0) {
                aBeginDate = DateUtil.addHours(dateBegin, 24 * beginDays.get(i) + 1);
                aBeginDate = DateUtil.getDateBegin(aBeginDate);
            }
            Date aEndDate = DateUtil.addHours(aBeginDate, 24 * endDays.get(i) + 1);
            aEndDate = DateUtil.getDateEnd(aEndDate);
            addServerActivity(type, sId, aBeginDate, aEndDate);
            log.info("{} 完成{}的初始化{},{}", LogUtil.getLogServerPart(server), type.getName(), DateUtil
                    .toDateTimeString(aBeginDate), DateUtil.toDateTimeString(aEndDate));
        }

    }

    /**
     * 定时追加活动 days 天每日、days/5 期七日
     *
     * @param server
     * @param days
     */
    public void appendActivities(CfgServerEntity server, int days) {
        int sId = server.getMergeSid();
        List<ServerActivity> sas = serverService.getServerDatas(sId, ServerActivity.class);
        // 每天,追加一个月的所有实例
        appendDayActivities(server, sas, 31, true);

        // 每七天的活动（基于开服时间），追加一个月的所有实例
        appendSevenDaysActivities(server, sas, 5, true);
    }

    /**
     * 指定日期追加活动 days 天每日、days/5 期七日
     *
     * @param server
     * @param baseDate
     * @param days
     */
    public void reappendActivities(CfgServerEntity server, Date baseDate, int days) {
        int sId = server.getMergeSid();
        List<ServerActivity> sas = serverService.getServerDatas(sId, ServerActivity.class);
        // 每天,追加一个月的所有实例
        reappendDayActivities(server, sas, baseDate, days);
        // 每七天的活动（基于开服时间），追加一个月的所有实例
        reappendSevenDaysActivities(server, sas, baseDate, days / 7);
    }

    /**
     * 初始化每日活动
     *
     * @param server
     * @param sas
     * @param days
     * @param incudeBaseDay 是否包含基准时间日期
     */
    private void appendDayActivities(CfgServerEntity server, List<ServerActivity> sas, int days, boolean incudeBaseDay) {
        int sId = server.getMergeSid();
        // 每天的活动
        List<ActivityEnum> dayActivities = Arrays.asList(ActivityEnum.TODAY_ACC_R, ActivityEnum.TODAY_ACC_R_2, ActivityEnum.TODAY_ACC_R_3);

        for (ActivityEnum type : dayActivities) {
            Date baseDate = getBaseBeginDate(server, sas, type);
            int i = incudeBaseDay ? 0 : 1;
            for (; i <= days; i++) {
                Date date = DateUtil.addDays(baseDate, i);
                Date dateBegin = DateUtil.getDateBegin(date);
                Date dateEnd = DateUtil.getDateEnd(date);
                // 每日元宝消费开服前七天不开启
                // if (type == ActivityEnum.PER_DAY_GOLD_CONSUME && DateUtil
                // .getDaysBetween(server.getBeginTime(), dateEnd) < 7) {
                // continue;
                // }
                addServerActivity(type, sId, dateBegin, dateEnd);
                log.info("{} 完成{}的初始化{},{}", LogUtil.getLogServerPart(server), type.getName(), DateUtil
                        .toDateTimeString(dateBegin), DateUtil.toDateTimeString(dateEnd));
            }
            log.info("{} 完成{}的初始化,初始化天数{}", LogUtil.getLogServerPart(server), type.getName(), days);
        }
    }

    /**
     * 每七天的活动
     *
     * @param server
     * @param sas
     * @param weeks
     * @param includeBaseWeek 是否包含基准时间周
     */
    private void appendSevenDaysActivities(CfgServerEntity server, List<ServerActivity> sas, int weeks, boolean includeBaseWeek) {
        // 每七天的活动
        List<ActivityEnum> sevenDaysActivities = Arrays.asList(ActivityEnum.ACC_R_DAYS_7, ActivityEnum.GOLD_CONSUME);

        int sId = server.getMergeSid();

        for (ActivityEnum type : sevenDaysActivities) {
            // 追加活动基准时间
            Date baseDate = getBaseBeginDate(server, sas, type);
            // 追加生成week周数的活动
            int i = includeBaseWeek ? 0 : 1;
            for (; i <= weeks; i++) {
                Date dateBegin = DateUtil.addWeeks(baseDate, i);
                // 第七天
                Date sevenEndDate = DateUtil.addHours(dateBegin, 24 * 6 + 1);
                Date dateEnd = DateUtil.getDateEnd(sevenEndDate);
                // 元宝消费开服前七天不开启
                if (type == ActivityEnum.GOLD_CONSUME && DateUtil.getDaysBetween(server.getBeginTime(), dateEnd) < 7) {
                    continue;
                }
                addServerActivity(type, sId, dateBegin, dateEnd);
                log.info("{} 完成{}的初始化{},{}", LogUtil.getLogServerPart(server), type.getName(), DateUtil
                        .toDateTimeString(dateBegin), DateUtil.toDateTimeString(dateEnd));
            }
            log.info("{} 完成{}的初始化,基准时间为{},生成周数{}", LogUtil.getLogServerPart(server), type.getName(), DateUtil
                    .toDateTimeString(baseDate), weeks);
        }
    }

    /**
     * 从指定日期开始新建每日活动
     *
     * @param server
     * @param sas
     * @param baseDate
     * @param days
     */
    private void reappendDayActivities(CfgServerEntity server, List<ServerActivity> sas, Date baseDate, int days) {
        // 每天的活动
        List<ActivityEnum> dayActivities = Arrays.asList(ActivityEnum.TODAY_ACC_R, ActivityEnum.TODAY_ACC_R_2, ActivityEnum.TODAY_ACC_R_3);
        int sId = server.getMergeSid();
        delServerActivity(sId, sas, dayActivities, baseDate);
        for (ActivityEnum type : dayActivities) {
            for (int i = 0; i <= days; i++) {
                Date date = DateUtil.addDays(baseDate, i);
                Date dateBegin = DateUtil.getDateBegin(date);
                Date dateEnd = DateUtil.getDateEnd(date);
                // 每日元宝消费开服前七天不开启
                // if (type == ActivityEnum.PER_DAY_GOLD_CONSUME && DateUtil
                // .getDaysBetween(server.getBeginTime(), dateEnd) < 7) {
                // continue;
                // }
                // 第一天以指定时间开始
                if (i == 0) {
                    dateBegin = baseDate;
                }
                addServerActivity(type, sId, dateBegin, dateEnd);
                log.info("{} 完成{}的初始化{},{}", LogUtil.getLogServerPart(server), type.getName(), DateUtil
                        .toDateTimeString(dateBegin), DateUtil.toDateTimeString(dateEnd));
            }
            log.info("{} 完成{}的初始化,初始化天数{}", LogUtil.getLogServerPart(server), type.getName(), days);
        }
    }

    /**
     * 从指定日期开始新建七日活动
     *
     * @param server
     * @param baseDate
     * @param weeks
     */
    private void reappendSevenDaysActivities(CfgServerEntity server, List<ServerActivity> sas, Date baseDate, int weeks) {
        // 每七天的活动
        List<ActivityEnum> sevenDaysActivities = Arrays.asList(ActivityEnum.ACC_R_DAYS_7, ActivityEnum.GOLD_CONSUME);
        int sId = server.getMergeSid();
        delServerActivity(sId, sas, sevenDaysActivities, baseDate);
        for (ActivityEnum type : sevenDaysActivities) {
            // 追加生成week周数的活动
            for (int i = 0; i <= weeks; i++) {
                Date dateBegin = DateUtil.addWeeks(baseDate, i);
                // 第七天
                Date sevenEndDate = DateUtil.addHours(dateBegin, 24 * 6 + 1);
                Date dateEnd = DateUtil.getDateEnd(sevenEndDate);
                dateBegin = DateUtil.getDateBegin(dateBegin);
                // 第一周以指定时间开始
                if (i == 0) {
                    dateBegin = baseDate;
                }
                // 元宝消费开服前七天不开启
                if (type == ActivityEnum.GOLD_CONSUME && DateUtil.getDaysBetween(server.getBeginTime(), dateEnd) < 7) {
                    continue;
                }
                addServerActivity(type, sId, dateBegin, dateEnd);
                log.info("{} 完成{}的初始化{},{}", LogUtil.getLogServerPart(server), type.getName(), DateUtil
                        .toDateTimeString(dateBegin), DateUtil.toDateTimeString(dateEnd));
            }
            log.info("{} 完成{}的初始化,基准时间为{},生成周数{}", LogUtil.getLogServerPart(server), type.getName(), DateUtil
                    .toDateTimeString(baseDate), weeks);
        }
    }

    /**
     * 获得活动基准时间 yyyy-MM-dd 00:00:00 （已生成的最新的活动的开始时间）
     *
     * @param server
     * @param sas
     * @param type
     * @return
     */
    private Date getBaseBeginDate(CfgServerEntity server, List<ServerActivity> sas, ActivityEnum type) {
        if (ListUtil.isNotEmpty(sas)) {
            List<ServerActivity> typeSars = sas.stream().filter(sa -> sa.getType() == type.getValue())
                    .collect(Collectors.toList());
            if (ListUtil.isNotEmpty(typeSars)) {
                return typeSars.get(typeSars.size() - 1).getBegin();
            }
        }
        // 如果没有该活动的任何实例则返回活动开服初始时间
        return DateUtil.getDateBegin(server.getBeginTime());

    }

    /**
     * 新增活动
     *
     * @param type
     * @param sId
     * @param dateBegin
     * @param dateEnd
     */
    private void addServerActivity(ActivityEnum type, int sId, Date dateBegin, Date dateEnd) {
        List<CfgActivityEntity> activityList = ActivityTool.getActivitiesByType(type);
        CfgActivityEntity activity = null;
        if (ListUtil.isEmpty(activityList)) {
            return;
        }
        activity = activityList.get(0);
        ServerActivity sa = ServerActivity.fromActivity(activity, dateBegin, dateEnd, sId);
        serverService.addServerData(sId, sa);
    }

    /**
     * 删除指定日期后的活动
     *
     * @param sId
     * @param sas
     * @param activityEnums
     * @param fromDate
     */
    private void delServerActivity(int sId, List<ServerActivity> sas, List<ActivityEnum> activityEnums, Date fromDate) {
        List<ServerActivity> sasToDel = sas.stream()
                .filter(tmp -> activityEnums.contains(ActivityEnum.fromValue(tmp.getType())) && tmp.gainEnd()
                        .after(fromDate))
                .collect(Collectors.toList());
        List<Long> saIdsToDel = sasToDel.stream().map(ServerActivity::getId).collect(Collectors.toList());
        serverService.deleteServerDatas(sId, saIdsToDel, ServerActivity.class);
    }
}
