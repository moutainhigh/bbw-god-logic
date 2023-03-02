package com.bbw.god.notify.rednotice;

import com.bbw.common.DateUtil;
import com.bbw.common.ListUtil;
import com.bbw.db.redis.RedisHashUtil;
import com.bbw.god.activity.ActivityLogic;
import com.bbw.god.activity.ActivityService;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.activity.config.ActivityParentTypeEnum;
import com.bbw.god.activity.processor.BigGodPlanProcessor;
import com.bbw.god.activity.rd.RDActivityTypeList;
import com.bbw.god.city.nvwm.nightmare.nuwamarket.NvWaMarketService;
import com.bbw.god.city.yed.AdventureType;
import com.bbw.god.city.yed.RDAdventures;
import com.bbw.god.city.yed.YeDProcessor;
import com.bbw.god.game.award.AwardStatus;
import com.bbw.god.game.config.server.ServerTool;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.game.transmigration.UserTransmigrationTargetLogic;
import com.bbw.god.game.wanxianzhen.service.WanXianLogic;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.achievement.*;
import com.bbw.god.gameuser.buddy.BuddyService;
import com.bbw.god.gameuser.chamberofcommerce.server.UserCocTaskService;
import com.bbw.god.gameuser.helpabout.UserHelpAboutService;
import com.bbw.god.gameuser.mail.MailServiceImpl;
import com.bbw.god.gameuser.mail.MailType;
import com.bbw.god.gameuser.mail.UserMail;
import com.bbw.god.gameuser.privilege.Privilege;
import com.bbw.god.gameuser.privilege.PrivilegeService;
import com.bbw.god.gameuser.statistic.behavior.recharge.RechargeStatistic;
import com.bbw.god.gameuser.statistic.behavior.recharge.RechargeStatisticService;
import com.bbw.god.gameuser.task.RDTaskList;
import com.bbw.god.gameuser.task.TaskStatusEnum;
import com.bbw.god.gameuser.task.TaskTypeEnum;
import com.bbw.god.gameuser.task.activitytask.guesstask.GuessDailyTaskService;
import com.bbw.god.gameuser.task.biggodplan.BigGodPlanTaskService;
import com.bbw.god.gameuser.task.biggodplan.UserBigGodPlanTask;
import com.bbw.god.gameuser.task.daily.DailyTaskProcessor;
import com.bbw.god.gameuser.task.godtraining.GodTrainingTaskService;
import com.bbw.god.gameuser.task.godtraining.UserGodTrainingTask;
import com.bbw.god.gameuser.task.main.UserMainTask;
import com.bbw.god.gameuser.task.main.UserMainTaskService;
import com.bbw.god.gameuser.task.sxdhchallenge.SxdhSeasonTaskProcessor;
import com.bbw.god.gameuser.treasure.UserTreasureService;
import com.bbw.god.gameuser.unique.UserMonster;
import com.bbw.god.login.RDNoticeInfo;
import com.bbw.god.rechargeactivities.RechargeActivityEnum;
import com.bbw.god.rechargeactivities.RechargeActivityProcessorFactory;
import com.bbw.god.rechargeactivities.processor.AbstractRechargeActivityProcessor;
import com.bbw.god.rechargeactivities.processor.WarTokenLevelAwardProcessor;
import com.bbw.god.rechargeactivities.processor.WarTokenTaskProcessor;
import com.bbw.god.rechargeactivities.wartoken.WarTokenLogic;
import com.bbw.god.server.ServerDataService;
import com.bbw.god.server.fst.FstRanking;
import com.bbw.god.server.fst.server.FstServerService;
import com.bbw.god.server.guild.*;
import com.bbw.god.server.guild.service.GuildEightDiagramsTaskService;
import com.bbw.god.server.guild.service.GuildInfoService;
import com.bbw.god.server.guild.service.GuildUserService;
import com.bbw.god.server.monster.MonsterService;
import com.bbw.god.server.monster.ServerMonster;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.bbw.god.game.data.redis.RedisKeyConst.SPLIT;

/**
 * @author suhq
 * @description 红点通知
 * @date 2020-02-06 14:01
 **/
@Service
@Slf4j
public class RedNoticeService {
    private static final String NOTICE_SPLIT = "_";

    @Autowired
    private ActivityLogic activityLogic;
    @Autowired
    private DailyTaskProcessor dailyTaskProcessor;
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private BuddyService buddyService;
    @Autowired
    private MonsterService monsterService;
    @Autowired
    private MailServiceImpl mailService;
    @Autowired
    private SxdhSeasonTaskProcessor sxdhSeasonTaskProcessor;
    @Autowired
    private UserCocTaskService userCocTaskService;
    @Autowired
    private GuildInfoService guildInfoService;
    @Autowired
    private UserMainTaskService mainTaskService;
    @Autowired
    private PrivilegeService privilegeService;
    @Autowired
    private GuildUserService guildUserService;
    @Autowired
    private FstServerService fstService;
    @Autowired
    private ServerDataService serverDataService;
    @Autowired
    private UserHelpAboutService userHelpAboutService;
    @Autowired
    private UserAchievementLogic userAchievementLogic;
    @Autowired
    private WanXianLogic wanXianLogic;
    @Autowired
    private YeDProcessor yeDProcessor;
    @Autowired
    private RechargeActivityProcessorFactory rechargeAPFactory;
    @Autowired
    private UserTreasureService userTreasureService;
    @Autowired
    private GuildEightDiagramsTaskService eightDiagramsTaskService;
    @Autowired
    private RechargeStatisticService rechargeStatisticService;
    @Autowired
    private RedisHashUtil<String, Long> redisHashUtil;
    @Autowired
    private GodTrainingTaskService godTrainingTaskService;
    @Autowired
    private WarTokenLogic warTokenLogic;
    @Autowired
    private WarTokenLevelAwardProcessor warTokenLevelAwardProcessor;
    @Autowired
    private WarTokenTaskProcessor warTokenTaskProcessor;
    @Autowired
    private UserTransmigrationTargetLogic userTransmigrationTargetLogic;
    @Autowired
    private ActivityService activityService;
    @Autowired
    private BigGodPlanTaskService bigGodPlanTaskService;
    @Autowired
    private BigGodPlanProcessor bigGodPlanProcessor;
    @Autowired
    private NvWaMarketService nvWaMarketService;
    @Autowired
    private GuessDailyTaskService guessDailyTaskService;


    private static final Long TIME_OUT = 24 * 60 * 60L;

    public List<String> getAllNotice(GameUser gu) {
        long uid = gu.getId();
        int sId = gu.getServerId();
        List<String> notices = new ArrayList<>();

        notices.addAll(getActivityNotices(gu, sId));
        //任务红点有及时通知，出登录外，其他地方不再传
//        notices.addAll(getTaskNotice(gu));
        notices.addAll(getAchievementNotices(uid));
        notices.addAll(getBuddyNotices(uid));
        notices.addAll(getBuddyMonsterNotices(uid, sId));
        notices.addAll(getMailNotices(uid));
//        notices.addAll(getSxdhNotices(uid));
//        notices.addAll(getCocNotices(uid));
        notices.addAll(getGuildNotices(gu));
//        notices.addAll(getPrivilegeNotice(gu));
//        notices.addAll(getFstNotice(gu));
//        notices.addAll(getSettingNotice(uid));
        notices.addAll(getWanXianNotice(uid));
        notices.addAll(getRechargeActivityNotice(uid));
        notices.addAll(getFirstRechargeNotice(gu));
//        notices.addAll(getAdventureNotice(uid));
        notices.addAll(getWarTokenNotice(uid));
//        notices.addAll(getTransmigrationNotice(uid));
        notices.addAll(getBigGodPlanTaskNotice(uid));
//        notices.addAll(getNvWaMarketNotice(uid));
        return notices;
    }

    /**
     * 登录红点，不重要的红点延后传
     *
     * @param gu
     * @param sId
     * @return
     */
    public List<String> getNoticeForLogin(GameUser gu, int sId) {
        List<String> notices = new ArrayList<>();

//        notices.addAll(getActivityNotices(uid, sId));
        notices.addAll(getTaskNotice(gu));
//        notices.addAll(getAchievementNotices(uid));
//        notices.addAll(getBuddyNotices(uid));
//        notices.addAll(getBuddyMonsterNotices(uid, sId));
//        notices.addAll(getMailNotices(uid));
//        notices.addAll(getCocNotices(uid));
//        notices.addAll(getGuildNotices(gu));
//        notices.addAll(getPrivilegeNotice(gu));
//        notices.addAll(getFstNotice(gu));
//        notices.addAll(getSettingNotice(uid));
//        notices.addAll(getWanXianNotice(uid));
//        notices.addAll(getRechargeActivityNotice(uid));
//        notices.addAll(getFirstRechargeNotice(uid));
//        notices.addAll(getAdventureNotice(uid));
//        notices.addAll(getWarTokenNotice(uid));
//        notices.addAll(getTransmigrationNotice(uid));
//        notices.addAll(getBigGodPlanTaskNotice(uid));
//        notices.addAll(getNvWaMarketNotice(uid));
        return notices;
    }

    public int getNoticeNum(List<String> allNotices, ModuleEnum module) {
        List<String> partNotices = allNotices.stream().filter(tmp -> tmp.startsWith(module.getValue() + NOTICE_SPLIT)).collect(Collectors.toList());
        return partNotices.stream().mapToInt(tmp -> {
            String[] values = tmp.split(NOTICE_SPLIT);
            return Integer.parseInt(values[values.length - 1]);
        }).sum();
    }

    public int getNoticeNum(List<String> allNotices, ModuleEnum module, int type) {
        String prefix = module.getValue() + NOTICE_SPLIT + type + NOTICE_SPLIT;
        Optional<String> optional = allNotices.stream().filter(tmp -> tmp.startsWith(prefix)).findFirst();
        if (optional.isPresent()) {
            String[] values = optional.get().split(NOTICE_SPLIT);
            return Integer.parseInt(values[values.length - 1]);
        }
        return 0;
    }


    private List<String> getActivityNotices(GameUser gu, int sId) {
        long uid = gu.getId();
        List<String> activityNotices = new ArrayList<>();
        for (ActivityParentTypeEnum activityParentType : ActivityParentTypeEnum.values()) {
            if (activityParentType == ActivityParentTypeEnum.NO_UI_ACTIVITY) {
                continue;
            }
            List<RDActivityTypeList.RDActivityType> rdActivityTypes = this.activityLogic.getActivityTypes(gu, sId, activityParentType);
            if (ListUtil.isNotEmpty(rdActivityTypes)) {
                List<String> partActivityNotices = rdActivityTypes.stream().map(tmp -> buildNoticeData4(ModuleEnum.ACTIVITY, activityParentType.getValue(), tmp.getType(), tmp.getNum())).collect(Collectors.toList());
                activityNotices.addAll(partActivityNotices);
                continue;
            }
            // 如果首充活动没有奖励可以领取，看是否首充，没首充的话，给一次红点提示
            if (ActivityParentTypeEnum.FIRST_RECHARGE_ACTIVITY == activityParentType) {
                // 已经通知过就不通知了
                if (ifAlreadyNotice(uid, ActivityEnum.GOD_BLESS.getValue(), ModuleEnum.ACTIVITY)) {
                    continue;
                }
                RechargeStatistic statistic = rechargeStatisticService.fromRedis(uid, DateUtil.getTodayInt());
                if (0 == statistic.getTotal()) {
                    List<String> partActivityNotices = rdActivityTypes.stream().map(tmp -> buildNoticeData4(ModuleEnum.ACTIVITY, activityParentType.getValue(), ActivityEnum.GOD_BLESS.getValue(), 1)).collect(Collectors.toList());
                    activityNotices.addAll(partActivityNotices);
                    // 添加通知记录
                    addNoticeToRedis(uid, ActivityEnum.GOD_BLESS.getValue(), ModuleEnum.ACTIVITY);
                }
            }
        }
        return activityNotices;
    }

    private List<String> getTaskNotice(GameUser gu) {
        List<String> taskNotices = new ArrayList<>();
        long uid = gu.getId();
        taskNotices.add(getDailyTaskNotice(uid));
        taskNotices.add(getMainTaskNotice(uid));
        taskNotices.addAll(getGodTrainingTaskNotice(gu));
        try {
            taskNotices.add(getSxdhSeasonTaskNotice(uid));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return taskNotices;
    }

    public List<String> getWanXianNotice(long uid) {
        List<String> notices = new ArrayList<>();
        RDNoticeInfo.ActivityShow signUpIcon1 = wanXianLogic.signUpIcon(uid, WanXianLogic.TYPE_REGULAR_RACE);
        if (signUpIcon1 == null) {
            RDNoticeInfo.ActivityShow show = wanXianLogic.getMenuIcon(uid);
            if (show == null || show.getShowType() == 0) {
                return notices;
            }
            notices.add(buildNoticeData3(ModuleEnum.WANXIAN, TaskTypeEnum.MAP_TIP.getValue(), show.getAwardNum()));
            return notices;
        }
        notices.add(buildNoticeData3(ModuleEnum.WANXIAN, 10, signUpIcon1.getAwardNum()));
        RDNoticeInfo.ActivityShow signUpIcon2 = wanXianLogic.signUpIcon(uid, WanXianLogic.TYPE_SPECIAL_RACE);
        notices.add(buildNoticeData3(ModuleEnum.WANXIAN, 20, signUpIcon2.getAwardNum()));
        return notices;
    }

    public List<String> getGodTrainingTaskNotice(GameUser gu) {
        List<String> notices = new ArrayList<>();
        if (gu.getStatus().isGrowTaskCompleted()) {
            return notices;
        }
        long uid = gu.getId();
        List<UserGodTrainingTask> tasks = godTrainingTaskService.getCurUserTrainingTasks(uid);
        if (ListUtil.isEmpty(tasks)) {
            return notices;
        }
        Date beginTime = DateUtil.fromDateLong(tasks.stream().findFirst().get().getGenerateTime());
        Date now = DateUtil.now();
        int daysBetween = DateUtil.getDaysBetween(beginTime, now);
        int days = daysBetween + 1;
        Map<Integer, List<UserGodTrainingTask>> map = tasks.stream().filter(tmp ->
                days >= tmp.getDays()).collect(Collectors.groupingBy(UserGodTrainingTask::getDays));
        Set<Map.Entry<Integer, List<UserGodTrainingTask>>> entries = map.entrySet();
        for (Map.Entry<Integer, List<UserGodTrainingTask>> entry : entries) {
            Integer day = entry.getKey();
            if (0 == day) {
                day = 8;
            }
            List<UserGodTrainingTask> task = entry.getValue();
            int num = (int) task.stream().filter(tmp -> tmp.getStatus() == TaskStatusEnum.ACCOMPLISHED.getValue()).count();
            String notice = buildNoticeData3(ModuleEnum.GOD_TRAINING, day, num);
            notices.add(notice);
        }
        return notices;
    }

    public List<String> getBigGodPlanTaskNotice(long uid) {

        List<String> notices = new ArrayList<>();
        //活动未开启
        if (!bigGodPlanProcessor.isOpened(gameUserService.getActiveSid(uid))) {
            return notices;
        }
        List<UserBigGodPlanTask> tasks = bigGodPlanTaskService.getCurUserBigGodPlanTasks(uid);
        if (ListUtil.isEmpty(tasks)) {
            return notices;
        }
        Date beginTime = DateUtil.fromDateLong(tasks.stream().findFirst().get().getGenerateTime());
        Date now = DateUtil.now();
        int daysBetween = DateUtil.getDaysBetween(beginTime, now);
        int days = daysBetween + 1;
        Map<Integer, List<UserBigGodPlanTask>> map = tasks.stream().filter(tmp ->
                days >= tmp.getDays()).collect(Collectors.groupingBy(UserBigGodPlanTask::getDays));
        Set<Map.Entry<Integer, List<UserBigGodPlanTask>>> entries = map.entrySet();
        for (Map.Entry<Integer, List<UserBigGodPlanTask>> entry : entries) {
            Integer day = entry.getKey();
            if (0 == day) {
                day = 8;
            }
            List<UserBigGodPlanTask> task = entry.getValue();
            int num = (int) task.stream().filter(tmp -> tmp.getStatus() == TaskStatusEnum.ACCOMPLISHED.getValue()).count();
            String notice = buildNoticeData3(ModuleEnum.BIG_GOD_PLAN, day, num);
            notices.add(notice);
        }
        return notices;
    }

    public String getDailyTaskNotice(long uid) {
        RDTaskList rdTaskList = this.dailyTaskProcessor.getTasks(uid, 0);
        Long taskAccomplishedNum = 0L;
        if (ListUtil.isNotEmpty(rdTaskList.getItems())) {
            taskAccomplishedNum = rdTaskList.getItems().stream().filter(tmp -> tmp.getStatus() == TaskStatusEnum.ACCOMPLISHED.getValue()).count();
        }
        return buildNoticeData3(ModuleEnum.TASK, TaskTypeEnum.DAILY_TASK.getValue(), taskAccomplishedNum.intValue());
    }

    /**
     * 获得活动每日任务信息
     *
     * @param uid
     * @return
     */
    public String getActivityDailyTaskNotice(long uid) {
        RDTaskList rdTaskList = this.guessDailyTaskService.getTasks(uid, 0);
        Long taskAccomplishedNum = 0L;
        if (ListUtil.isNotEmpty(rdTaskList.getItems())) {
            taskAccomplishedNum = rdTaskList.getItems().stream().filter(tmp -> tmp.getStatus() == TaskStatusEnum.ACCOMPLISHED.getValue()).count();
        }
        return buildNoticeData3(ModuleEnum.TASK, TaskTypeEnum.DAILY_TASK.getValue(), taskAccomplishedNum.intValue());
    }

    public String getMainTaskNotice(long uid) {
        int noticeNum = 0;
        List<UserMainTask> umTasks = this.mainTaskService.getUserMainTasks(uid);
        for (UserMainTask umt : umTasks) {
            noticeNum += (umt.getEnableAwardIndex() - umt.getAwardedIndex());
        }
        return buildNoticeData3(ModuleEnum.TASK, TaskTypeEnum.MAIN_TASK.getValue(), noticeNum);
    }

    public String getSxdhSeasonTaskNotice(long uid) {
        RDTaskList rdTaskList = this.sxdhSeasonTaskProcessor.getTasks(uid, 0);
        Long taskAccomplishedNum = 0L;
        if (ListUtil.isNotEmpty(rdTaskList.getItems())) {
            taskAccomplishedNum = rdTaskList.getItems().stream().filter(tmp -> tmp.getStatus() == TaskStatusEnum.ACCOMPLISHED.getValue()).count();
        }
        return buildNoticeData3(ModuleEnum.SXDH, TaskTypeEnum.SXDH_SEASON_TASK.getValue(), taskAccomplishedNum.intValue());
    }

    private List<String> getAchievementNotices(long uid) {
        UserAchievementInfo info = this.gameUserService.getSingleItem(uid, UserAchievementInfo.class);
        BitSet accomplishedIds = info.getAccomplishedIds();
        List<String> notices = new ArrayList<>();
        for (AchievementTypeEnum achievementType : AchievementTypeEnum.values()) {
            List<CfgAchievementEntity> achievements = AchievementTool.getAchievements(achievementType);
            int accomplishNum = 0;
            for (CfgAchievementEntity achievement : achievements) {
                Integer achievementId = achievement.getId();
                if (accomplishedIds.get(achievementId) && !info.getAwardedIds().get(achievementId)) {
                    accomplishNum++;
                }
            }
            notices.add(buildNoticeData3(ModuleEnum.ACHIEVEMENT, achievementType.getValue(), accomplishNum));
        }
        return notices;
    }

    private List<String> getBuddyNotices(long uid) {
        int askMarkId = 10;
        List<String> notices = new ArrayList<>();
        int askNum = this.buddyService.getAskCount(uid);
        notices.add(buildNoticeData3(ModuleEnum.BUDDY, askMarkId, askNum));
        return notices;
    }

    private List<String> getBuddyMonsterNotices(long uid, int sId) {
        int buddyMonsterFindMarkId = 10;
        List<String> notices = new ArrayList<>();
        List<ServerMonster> sMonsters = this.monsterService.getBuddyMonsters(uid, sId);
        int monsterCount = sMonsters.size();
        notices.add(buildNoticeData3(ModuleEnum.BUDDY_MONSTER, buddyMonsterFindMarkId, monsterCount));
        UserMonster userMonster = gameUserService.getSingleItem(uid, UserMonster.class);
        if (null != userMonster && monsterCount > 0) {
            Date nextBeatTime = userMonster.getNextBeatTime();
            if (nextBeatTime.before(DateUtil.now())) {
                int ableFightBuddyMonster = 20;
                notices.add(buildNoticeData3(ModuleEnum.BUDDY_MONSTER, ableFightBuddyMonster, 1));
            }
        }
        return notices;
    }

    private List<String> getMailNotices(long uid) {
        List<String> notices = new ArrayList<>();
        List<UserMail> mails = this.mailService.getUserMails(uid);
        Map<MailType, Long> typeNumMap = mails.stream()
                .filter(tmp -> !tmp.getDeleted())
                .filter(tmp -> {
                    if (tmp.getType() == MailType.AWARD) {
                        return !tmp.getAccepted();
                    } else {
                        return !tmp.getRead();
                    }
                }).collect(Collectors.groupingBy(UserMail::getType, Collectors.counting()));
        for (MailType mailType : typeNumMap.keySet()) {
            notices.add(buildNoticeData3(ModuleEnum.MAIL, mailType.getValue(), typeNumMap.get(mailType).intValue()));
        }
        return notices;
    }

//    private List<String> getSxdhNotices(long uid) {
//        int xianDouMarkId = 10;
//        List<String> notices = new ArrayList<>();
//        SxdhFighter fighter = this.sxdhFighterService.getFighter(uid);
//        Date awardDate = this.sxdhService.getAwardDateTime();
//        if (!fighter.ifAwardedBean(awardDate)) {
//            notices.add(buildNoticeData3(ModuleEnum.SXDH, xianDouMarkId, 1));
//        }
//        return notices;
//    }

//    private List<String> getCocNotices(long uid) {
//        List<String> notices = new ArrayList<>();
//        int accomplishNum = this.userCocTaskService.getTaskRemaind(uid);
//        notices.add(buildNoticeData3(ModuleEnum.COC, CocMark.TASK.getValue(), accomplishNum));
//        return notices;
//    }

    private List<String> getGuildNotices(GameUser gu) {
        long uid = gu.getId();
        int sid = gu.getServerId();
        List<String> notices = new ArrayList<>();
        if (!this.guildUserService.hasGuild(uid)) {
            return notices;
        }
        int accomplishNum = this.guildInfoService.getTaskRemind(uid);
        Optional<UserGuildTaskInfo> optional = eightDiagramsTaskService.getGuildTaskInfoOp(uid);
        if (optional.isPresent()) {
            accomplishNum += optional.get().getBox().size();
        }
        notices.add(buildNoticeData3(ModuleEnum.GUILD, GuildMark.TASK.getValue(), accomplishNum));
        //TODO 待优化
        if (gu.getLevel() < GuildConstant.OPEN_LEVEL) {
            return notices;
        }
        UserGuild userGuild = this.gameUserService.getSingleItem(uid, UserGuild.class);
        if (userGuild == null) {
            return notices;
        }
        GuildInfo guiInfo = this.serverDataService.getServerData(sid, GuildInfo.class, userGuild.getGuildId());
        if (guiInfo == null) {
            return notices;
        }
        if ((guiInfo.getBossId() != null && guiInfo.getBossId().equals(uid)) || (guiInfo.getViceBossId() != null && guiInfo.getViceBossId().equals(uid))) {
            List<Long> askUids = guiInfo.getExamineUids();
            notices.add(buildNoticeData3(ModuleEnum.GUILD, GuildMark.APPLY.getValue(), askUids.size()));
        }
        return notices;
    }

    private List<String> getPrivilegeNotice(GameUser gu) {
        AwardStatus awardStatus = this.privilegeService.getTianlingStatus(gu);
        int noticeNum = awardStatus == AwardStatus.ENABLE_AWARD ? 1 : 0;
        List<String> notices = new ArrayList<>();
        notices.add(buildNoticeData3(ModuleEnum.PRIVILEGE, Privilege.TianlingYin.getValue(), noticeNum));
        return notices;
    }

    private List<String> getFstNotice(GameUser gu) {
        // 封神台10级开放
        Integer level = gu.getLevel();
        long uid = gu.getId();
        if (level < 10) {
            return new ArrayList<>();
        }
        Optional<FstRanking> optional = this.fstService.getFstRanking(uid);
        List<String> notices = new ArrayList<>();
        if (optional.isPresent()) {
            int fstPointMark = 10;
            FstRanking fstRanking = optional.get();
            notices.add(buildNoticeData3(ModuleEnum.FST, fstPointMark, fstRanking.getIncrementPoints() >= 30000 ? 1 : 0));
        }
        return notices;
    }

    private List<String> getSettingNotice(long uid) {
        List<String> notices = new ArrayList<>();
        int helpAboutMarkId = 10;
        int award = this.userHelpAboutService.getCanAwardNum(uid);
        notices.add(buildNoticeData3(ModuleEnum.SETTING, helpAboutMarkId, award));
        return notices;
    }


    public String buildNoticeData3(ModuleEnum module, int type, int num) {
        return module.getValue() + NOTICE_SPLIT + type + NOTICE_SPLIT + num;
    }

    public String buildNoticeData4(ModuleEnum module, int parentType, int type, int num) {
        return module.getValue() + NOTICE_SPLIT + parentType + NOTICE_SPLIT + type + NOTICE_SPLIT + num;
    }

    /**
     * 获取奇珍红点
     *
     * @param uid
     * @return
     */
    private List<String> getRechargeActivityNotice(long uid) {
        List<String> notices = new ArrayList<>();
        for (AbstractRechargeActivityProcessor aap : rechargeAPFactory.getProcessorsByParentType(RechargeActivityEnum.DIAMOND_PACK)) {
            int num = aap.getCanGainAwardNum(uid);
            if (num > 0 & aap.isShow(uid)) {
                notices.add(buildNoticeData3(ModuleEnum.RECHARGER_ACTIVITY_GIFT, aap.getCurrentEnum().getType(), num));
            }
        }
        for (AbstractRechargeActivityProcessor aap : rechargeAPFactory.getProcessorsByParentType(RechargeActivityEnum.TE_HUI_PACK)) {
            int num = aap.getCanGainAwardNum(uid);
            if (num > 0 & aap.isShow(uid)) {
                notices.add(buildNoticeData3(ModuleEnum.RECHARGER_ACTIVITY_TEHUI, aap.getCurrentEnum().getType(), num));
            }
        }
        for (AbstractRechargeActivityProcessor aap : rechargeAPFactory.getProcessorsByParentType(RechargeActivityEnum.CARD_PACK)) {
            int num = aap.getCanGainAwardNum(uid);
            if (num > 0 && aap.isShow(uid)) {
                notices.add(buildNoticeData3(ModuleEnum.RECHARGER_ACTIVITY_CARD, aap.getCurrentEnum().getType(), num));
            }
        }
        return notices;
    }

    private List<String> getFirstRechargeNotice(GameUser gu) {
        List<String> notices = new ArrayList<>();
        int sid = gameUserService.getOriServer(gu.getId()).getMergeSid();
        List<RDActivityTypeList.RDActivityType> activityTypes = activityLogic.getActivityTypes(gu, sid, ActivityParentTypeEnum.FIRST_RECHARGE_ACTIVITY);
        for (RDActivityTypeList.RDActivityType rdActivityType : activityTypes) {
            int num = rdActivityType.getNum();
            Integer type = rdActivityType.getType();
            notices.add(buildNoticeData3(ModuleEnum.FIRST_RECHARGE_ACTIVITY, type, num));
        }
        return notices;
    }

    private List<String> getAdventureNotice(long uid) {
        RDAdventures rdAdventures = yeDProcessor.listAdventures(uid);
        List<RDAdventures.RDAdventureInfo> adventureList = rdAdventures.getAdventureList();
        int num = 0;
        num += adventureList.stream().filter(tmp -> !tmp.getType().equals(AdventureType.YYSR.getValue()) && tmp.getStatus().equals(AwardStatus.ENABLE_AWARD.getValue())).count();
        return Collections.singletonList(buildNoticeData3(ModuleEnum.ADVENTURE, TaskTypeEnum.MAP_TIP.getValue(), num));
    }

    public List<String> getPerDayFirstLoginNotice(long uid) {
        List<String> notices = new ArrayList<>();
        // 每天第一次登陆，一定没有买元素礼包
        notices.add(buildNoticeData3(ModuleEnum.MALL, TaskTypeEnum.MAP_TIP.getValue(), 1));
        notices.addAll(getCardPoolNotice(uid));
        return notices;
    }

    public List<String> getCardPoolNotice(long uid) {
        if (ifAlreadyNotice(uid, 0, ModuleEnum.CARD_POOL)) {
            return new ArrayList<>();
        }
        // 卡池是否满足10连抽卡条件
        int jxl = userTreasureService.getTreasureNum(uid, TreasureEnum.JU_XIAN_LING.getValue());
        int xzy = userTreasureService.getTreasureNum(uid, TreasureEnum.XZY.getValue());
        int jzy = userTreasureService.getTreasureNum(uid, TreasureEnum.JZY.getValue());
        int mzy = userTreasureService.getTreasureNum(uid, TreasureEnum.MZY.getValue());
        int szy = userTreasureService.getTreasureNum(uid, TreasureEnum.SZY.getValue());
        int hzy = userTreasureService.getTreasureNum(uid, TreasureEnum.HZY.getValue());
        int tzy = userTreasureService.getTreasureNum(uid, TreasureEnum.TZY.getValue());
        if (jxl >= 95 || xzy >= 9 || jzy >= 9 || mzy >= 9 || szy >= 9 || hzy >= 9 || tzy >= 9) {
            addNoticeToRedis(uid, 0, ModuleEnum.CARD_POOL);
            return Collections.singletonList(buildNoticeData3(ModuleEnum.CARD_POOL, TaskTypeEnum.MAP_TIP.getValue(), 1));
        }
        return new ArrayList<>();
    }

//    private List<String> getZxzNotice(GameUser gu) {
//        // 诛仙阵15级开放
//        Integer level = gu.getLevel();
//        if (level < 15) {
//            return new ArrayList<>();
//        }
//        // 有免费次数就加红点通知
//        boolean needCost = zxzLogic.isNeedCost(gu.getId());
//        if (!needCost) {
//            return Collections.singletonList(buildNoticeData3(ModuleEnum.ZXZ, TaskTypeEnum.MAP_TIP.getValue(), 1));
//        }
//        return new ArrayList<>();
//    }

    private String getGroupRedNoticeKey(int sid) {
        Integer groupId = ServerTool.getServer(sid).getGroupId();
        return "game" + SPLIT + "redNotice" + SPLIT + DateUtil.getTodayInt() + SPLIT + groupId;
    }

    private void addNoticeToRedis(long uid, int type, ModuleEnum moduleEnum) {
        int sid = gameUserService.getActiveSid(uid);
        String key = getGroupRedNoticeKey(sid);
        String field = "";
        switch (moduleEnum) {
            case CARD_POOL:
                field = uid + SPLIT + moduleEnum.getValue();
                break;
            case ACTIVITY:
                field = uid + SPLIT + type + SPLIT + moduleEnum.getValue();
                break;
            default:
                return;
        }
        redisHashUtil.putField(key, field, uid, TIME_OUT);
    }

    private Boolean ifAlreadyNotice(long uid, int type, ModuleEnum moduleEnum) {
        int sid = gameUserService.getActiveSid(uid);
        String key = getGroupRedNoticeKey(sid);
        String field = "";
        switch (moduleEnum) {
            case CARD_POOL:
                field = uid + SPLIT + moduleEnum.getValue();
                break;
            case ACTIVITY:
                field = uid + SPLIT + type + SPLIT + moduleEnum.getValue();
                break;
            default:
                return false;
        }
        return redisHashUtil.hasField(key, field);
    }

    private List<String> getWarTokenNotice(long uid) {
        List<String> list = new ArrayList<>();
        try {
            if (!warTokenLogic.openWarToken(uid)) {
                return list;
            }
            int num = warTokenLevelAwardProcessor.getCanGainAwardNum(uid);
            if (num > 0) {
                list.add(buildNoticeData3(ModuleEnum.WAR_TOKEN, 20, num));
            }
            num = warTokenTaskProcessor.getCanGainAwardNum(uid);
            if (num > 0) {
                list.add(buildNoticeData3(ModuleEnum.WAR_TOKEN, 10, num));
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return list;
        }
    }

    private List<String> getTransmigrationNotice(long uid) {
        List<String> list = new ArrayList<>();
        try {
            int num = userTransmigrationTargetLogic.getEnableAwardNum(uid);
            if (num > 0) {
                list.add(buildNoticeData3(ModuleEnum.TRANSMIGRATION, 10, num));
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return list;
        }
    }

    /**
     * 女娲集市红点
     *
     * @param uid
     * @return
     */
    private List<String> getNvWaMarketNotice(long uid) {
        List<String> list = new ArrayList<>();
        try {
            int num = nvWaMarketService.getNvWaMarketNotice(uid);
            if (num > 0) {
                list.add(buildNoticeData3(ModuleEnum.NV_WA_MARKET, 20, num));
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return list;
        }
    }
}
