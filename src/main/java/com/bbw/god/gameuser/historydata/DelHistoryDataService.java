package com.bbw.god.gameuser.historydata;

import com.bbw.common.DateUtil;
import com.bbw.common.ListUtil;
import com.bbw.god.city.entity.UserTYFCell;
import com.bbw.god.city.entity.UserTYFTurn;
import com.bbw.god.city.taiyf.UserTyfFillRecord;
import com.bbw.god.detail.LogUtil;
import com.bbw.god.game.config.mall.CfgMallEntity;
import com.bbw.god.game.config.mall.MallEnum;
import com.bbw.god.game.config.mall.MallTool;
import com.bbw.god.game.transmigration.entity.UserTransmigrationRecord;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.UserData;
import com.bbw.god.gameuser.achievement.UserAchievement;
import com.bbw.god.gameuser.achievement.UserAchievementInfo;
import com.bbw.god.gameuser.god.UserGod;
import com.bbw.god.gameuser.mail.UserMail;
import com.bbw.god.gameuser.task.daily.UserDailyTask;
import com.bbw.god.gameuser.task.daily.service.UserDailyTaskService;
import com.bbw.god.gameuser.task.sxdhchallenge.UserSxdhSeasonTask;
import com.bbw.god.gameuser.task.timelimit.UserTimeLimitTask;
import com.bbw.god.mall.MallService;
import com.bbw.god.mall.UserMallRecord;
import com.bbw.god.mall.UserMallRefreshRecord;
import com.bbw.god.rechargeactivities.wartoken.UserWarTokenTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 删除旧数据服务
 *
 * @author suhq
 * @date 2020-10-23 15:08
 **/
@Component
public class DelHistoryDataService {
    public static final int dayBeforeAsDays = -2;
    public static final int dayBeforeAsWeeks = -15;
    public static final int dayBeforeAsMonths = -35;
    public static final int dayBeforeAsMail = -10;
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private UserDailyTaskService dailyTaskService;
    @Autowired
    private MallService mallService;


    /**
     * 删除多天天的每日任务
     *
     * @param uid
     */
    public void delExpiredDailyTasks(long uid) {
        Date dateBefore = DateUtil.addDays(DateUtil.now(), dayBeforeAsDays);
        int dateInt = DateUtil.toDateInt(dateBefore);
        List<UserDailyTask> dailyTasks = this.dailyTaskService.getAllDailyTasks(uid);
        if (ListUtil.isNotEmpty(dailyTasks)) {
            List<UserDailyTask> dels = dailyTasks.stream().filter(tmp -> (tmp.ifBefore(dateInt) && tmp.isNomalDailyTask()) || tmp.delHerobackTask()).collect(Collectors.toList());
            // 删除并备忘数据
            delUserData(uid, dels);
        }
    }

    /**
     * 删除过期限时任务
     * @param uid
     */
    public void delExpiredTimeLimitTasks(long uid) {
        Date dateBefore = DateUtil.addDays(DateUtil.now(), dayBeforeAsDays);
        List<UserTimeLimitTask> timeLimitTasks = gameUserService.getMultiItems(uid, UserTimeLimitTask.class);
        if (ListUtil.isNotEmpty(timeLimitTasks)) {
            List<UserTimeLimitTask> dels = timeLimitTasks.stream().filter(tmp -> (tmp.getTimeEnd().before(dateBefore))).collect(Collectors.toList());
            // 删除并备忘数据
            delUserData(uid, dels);
        }
    }

    /**
     * 删除玩家过期的附体神仙
     *
     * @param uid
     */
    public void delExpiredGod(long uid) {
        Date dateBefore = DateUtil.addDays(DateUtil.now(), dayBeforeAsDays);
        List<UserGod> uGods = this.gameUserService.getMultiItems(uid, UserGod.class);
        if (ListUtil.isNotEmpty(uGods)) {
            List<UserGod> dels = uGods.stream().filter(tmp -> tmp.getAttachTime().before(dateBefore)).collect(Collectors.toList());
            // 删除并备忘数据
            delUserData(uid, dels);
        }
    }

    /**
     * 删除过期商品购买记录
     *
     * @param uid
     */
    public void delExpiredMall(long uid) {
        delExpiredMall(uid, MallEnum.SM, dayBeforeAsDays);
        delExpiredMall(uid, MallEnum.TTCJ_LB, dayBeforeAsDays);
        delExpiredMall(uid, MallEnum.GOLD_CONSUME, dayBeforeAsMonths);
        delExpiredMall(uid, MallEnum.MONTH_RECHARGE_BAG, dayBeforeAsMonths);
        delExpiredMall(uid, MallEnum.WEEK_RECHARGE_BAG, dayBeforeAsMonths);
        delExpiredMall(uid, MallEnum.THLB, 1, dayBeforeAsDays);
        delExpiredMall(uid, MallEnum.THLB, 7, dayBeforeAsMonths);
        Date dateBefore = DateUtil.addDays(DateUtil.now(), -8);
        List<UserMallRecord> umrs = mallService.getRecords(uid);
        umrs = umrs.stream().filter(tmp -> !tmp.ifValid() && tmp.getDateTime().before(dateBefore)).filter(tmp -> {
            CfgMallEntity mallEntity = MallTool.getMall(tmp.getType(), tmp.getBaseId());
            return mallEntity == null || mallEntity.getLimit() == 0;
        }).collect(Collectors.toList());
        delUserData(uid, umrs);
        //删除神秘商店刷新记录
        List<UserMallRefreshRecord> refreshRecords = gameUserService.getMultiItems(uid, UserMallRefreshRecord.class);
        refreshRecords = refreshRecords.stream().filter(tmp -> tmp.getDateTime().before(dateBefore)).collect(Collectors.toList());
        delUserData(uid, refreshRecords);
    }

    /**
     * 删除过期商品购买记录
     * @param uid
     * @param mallEnum
     * @param daysBefore
     */
    private void delExpiredMall(long uid, MallEnum mallEnum, int daysBefore) {
        Date dateBefore = DateUtil.addDays(DateUtil.now(), daysBefore);
        List<UserMallRecord> mRecords = this.mallService.getUserMallRecord(uid, mallEnum);
        if (ListUtil.isNotEmpty(mRecords)) {
            List<UserMallRecord> dels = mRecords.stream().filter(tmp -> tmp.getDateTime().before(dateBefore)).collect(Collectors.toList());
            // 删除并备忘数据
            delUserData(uid, dels);
        }

    }

    /**
     * 删除过期商品记录
     * @param uid
     * @param mallType
     * @param peroid
     * @param dayBefore
     */
    private void delExpiredMall(long uid, MallEnum mallType, int peroid, int dayBefore) {
        List<UserMallRecord> mRecords = this.mallService.getUserMallRecord(uid, mallType);
        if (ListUtil.isNotEmpty(mRecords)) {
            Date dateBefore = DateUtil.addDays(DateUtil.now(), dayBefore);
            List<UserMallRecord> dateMallToDel = mRecords.stream().filter(tmp -> {
                if (null == tmp.getBaseId()) {
                    return true;
                }
                CfgMallEntity cfgMall = MallTool.getMall(mallType.getValue(), tmp.getBaseId());
                if (null != cfgMall) {
                    return cfgMall.getPeroid() == peroid && tmp.getDateTime().before(dateBefore);
                }
                return false;
            }).collect(Collectors.toList());
            // 删除并备忘数据
            delUserData(uid, dateMallToDel);
        }

    }

    /**
     * 删除过期邮件
     * @param uid
     */
    public void delExpireMail(long uid) {
        Date dateBefore = DateUtil.addDays(DateUtil.now(), dayBeforeAsMail);
        List<UserMail> mailList = this.gameUserService.getMultiItems(uid, UserMail.class);
        if (ListUtil.isEmpty(mailList)) {
            return;
        }
        Date now = DateUtil.now();
        List<UserMail> expiredMail = mailList.stream()
                .filter(mail -> mail.mailTimeOutDate(now) || (mail.getDeleted() && mail.getSendTime().before(dateBefore)))
                .collect(Collectors.toList());
        delUserData(uid, expiredMail);
    }

    /**
     * 删除旧版玩家成就对象信息
     *
     * @param uid 玩家id
     */
    public void delOldUserAchievement(long uid) {
        UserAchievementInfo achievementInfo = gameUserService.getSingleItem(uid, UserAchievementInfo.class);
        // 还未生成新版成就对象，不删除旧版成就信息
        if (null == achievementInfo) {
            return;
        }
        // 已经生成了新版成就对象，删除旧版的信息
        List<UserAchievement> userAchievements = gameUserService.getMultiItems(uid, UserAchievement.class);
        delUserData(uid, userAchievements);
    }

    /**
     * 删除旧的太一府
     * @param uid
     */
    public void delOldUserTyf(long uid) {
        List<UserTYFTurn> turns = gameUserService.getMultiItems(uid, UserTYFTurn.class);
        List<UserTYFCell> cells = gameUserService.getMultiItems(uid, UserTYFCell.class);
        delUserData(uid, turns);
        delUserData(uid, cells);
        List<UserTyfFillRecord> fillRecords = gameUserService.getMultiItems(uid, UserTyfFillRecord.class);
        if (ListUtil.isEmpty(fillRecords) || fillRecords.size() <= 5) {
            return;
        }
        fillRecords = fillRecords.stream().filter(tmp -> tmp.getIsFillAll()).collect(Collectors.toList());
        //删除5轮前的数据
        delUserData(uid, fillRecords.subList(0, fillRecords.size() - 5));
    }

    /**
     * 删除神仙大会赛季挑战
     * @param uid
     */
    public void delExpiredSxdhSeasonTasks(long uid) {
        Date beforeDate = DateUtil.addDays(DateUtil.now(), -65);
        List<UserSxdhSeasonTask> seasonTasks = gameUserService.getMultiItems(uid, UserSxdhSeasonTask.class);
        if (ListUtil.isEmpty(seasonTasks)) {
            return;
        }
        seasonTasks = seasonTasks.stream().filter(tmp -> tmp.getGenerateTime().before(beforeDate)).collect(Collectors.toList());
        gameUserService.deleteItems(uid, seasonTasks);
    }

    /**
     * 删除战令任务
     * @param uid
     */
    public void delExpiredWarTokenTasks(long uid) {
        Date beforeDate = DateUtil.addDays(DateUtil.now(), -15);
        int beforeDateInt = DateUtil.toDateInt(beforeDate);
        List<UserWarTokenTask> warTokenTasks = gameUserService.getMultiItems(uid, UserWarTokenTask.class);
        if (ListUtil.isEmpty(warTokenTasks)) {
            return;
        }
        warTokenTasks = warTokenTasks.stream().filter(tmp -> tmp.getInitDate() <= beforeDateInt).collect(Collectors.toList());
        gameUserService.deleteItems(uid, warTokenTasks);
    }

    /**
     * 删除玩家过期轮回记录
     *
     * @param uid
     */
    public void delExpiredTransmigrationRecord(long uid) {
        Date beforeDate = DateUtil.addDays(DateUtil.now(), dayBeforeAsMonths);
        List<UserTransmigrationRecord> records = gameUserService.getMultiItems(uid, UserTransmigrationRecord.class);
        if (ListUtil.isEmpty(records)) {
            return;
        }
        records = records.stream().filter(tmp -> tmp.getDate().before(beforeDate)).collect(Collectors.toList());
        gameUserService.deleteItems(uid, records);
    }


    /**
     * 删除过期数据
     *
     * @param uid
     * @param dels
     * @param <T>
     */
    public <T extends UserData> void delUserData(long uid, List<T> dels) {
        if (ListUtil.isEmpty(dels)) {
            return;
        }
        // 备忘
        LogUtil.logDeletedUserDatas(dels, "过期数据");
        // 删除
        this.gameUserService.deleteItems(uid, dels);

    }

}
