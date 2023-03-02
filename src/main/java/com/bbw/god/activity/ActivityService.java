package com.bbw.god.activity;

import com.bbw.cache.UserCacheService;
import com.bbw.common.DateUtil;
import com.bbw.common.ListUtil;
import com.bbw.god.activity.config.*;
import com.bbw.god.activity.game.GameActivity;
import com.bbw.god.activity.game.GameActivityService;
import com.bbw.god.activity.processor.ActivityProcessorFactory;
import com.bbw.god.activity.processor.IActivityProcessor;
import com.bbw.god.activity.server.ServerActivity;
import com.bbw.god.activity.server.ServerActivityService;
import com.bbw.god.db.entity.CfgActivityEntity;
import com.bbw.god.game.award.AwardStatus;
import com.bbw.god.game.config.CfgProductGroup;
import com.bbw.god.game.data.GameDataService;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.mail.MailService;
import com.bbw.god.gameuser.pay.UserReceipt;
import com.bbw.god.gameuser.task.daily.heroback.HeroBackTaskInfoProcessor;
import com.bbw.god.pay.ReceiptService;
import com.bbw.god.server.ServerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ActivityService {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private UserCacheService userCacheService;
    @Autowired
    private ServerService serverService;
    @Autowired
    private ActivityConfig activityConfig;
    @Autowired
    private ServerActivityService serverActivityService;
    @Autowired
    private GameActivityService gameActivityService;
    @Autowired
    private GameDataService gameDataService;
    @Autowired
    private MailService mailService;
    @Autowired
    private HeroBackTaskInfoProcessor heroBackTaskProcessor;
    @Autowired
    private ActivityProcessorFactory activityProcessorFactory;
    @Autowired
    private ReceiptService receiptService;

    /**
     * 获取玩家所有活动记录，游戏业务逻辑必须调用该方法！！！<br/>
     * 不能直接使用GameUserService
     *
     * @param uid
     * @return
     */
    public List<UserActivity> getUserActivities(long uid) {
        return this.userCacheService.getUserDatas(uid, UserActivity.class);
    }

    /**
     * 新增活动,游戏业务逻辑必须调用该方法！！！<br/>
     * 不能直接使用GameUserService
     *
     * @param uid
     * @param ua
     */
    public void addUserActivity(long uid, UserActivity ua) {
        userCacheService.addUserData(ua);
    }

    /**
     * 获得玩家记录
     *
     * @param guId
     * @param aId
     * @param caId
     * @return
     */
    public UserActivity getUserActivity(long guId, long aId, int caId) {
        CfgActivityEntity ca = ActivityTool.getActivity(caId);
        List<UserActivity> userActivities = this.getUserActivities(guId, aId, ActivityEnum.fromValue(ca.getType()));
        return userActivities.stream().filter(ua -> ua.getBaseId() == caId && ua.getAId() == aId).findFirst()
                .orElse(null);
    }

    /**
     * 获得用户领取记录
     *
     * @param guId
     * @param aId
     * @param type
     * @return
     */
    public List<UserActivity> getUserActivities(long guId, long aId, ActivityEnum type) {
        // 获得当前所有的福利对应的活动
        List<CfgActivityEntity> activities = ActivityTool.getActivitiesByType(type);
        List<Long> aIds = Arrays.asList(aId);
        List<UserActivity> uas = this.getUserActivities(guId, activities, aIds);
        // 轮次活动
        if (this.activityConfig.getRoundActivities().contains(type) && ListUtil.isNotEmpty(uas)) {
            final int maxRound = uas.stream().mapToInt(UserActivity::getRound).max().getAsInt();
            UserActivity lastNotAwarded = uas.stream().filter(tmp -> tmp.getStatus() < AwardStatus.AWARDED.getValue() && tmp.getRound() < maxRound).findFirst().orElse(null);
            if (lastNotAwarded != null) {// 有未达成、未领取的
                final int curRound = lastNotAwarded.getRound();
                uas = uas.stream().filter(tmp -> tmp.getRound() == curRound).collect(Collectors.toList());
            } else {// 没有达成任何活动、全部达成并领取
                uas = uas.stream().filter(tmp -> tmp.getRound() == maxRound).collect(Collectors.toList());
            }
        }
        return uas;
    }

    /**
     * 获得最新的记录
     *
     * @param guId
     * @param aId
     * @param type
     * @return
     */
    public List<UserActivity> getNewestUserActivities(long guId, long aId, ActivityEnum type) {
        // 获得当前所有的福利对应的活动
        List<CfgActivityEntity> activities = ActivityTool.getActivitiesByType(type);
        List<Long> aIds = Arrays.asList(aId);
        List<UserActivity> uas = this.getUserActivities(guId, activities, aIds);
        // 轮次活动
        if (this.activityConfig.getRoundActivities().contains(type) && ListUtil.isNotEmpty(uas)) {
            final int maxRound = uas.stream().mapToInt(UserActivity::getRound).max().getAsInt();
            uas = uas.stream().filter(tmp -> tmp.getRound() == maxRound).collect(Collectors.toList());
        }
        return uas;
    }

    /**
     * 获得用户领取记录
     *
     * @param guId
     * @param as
     * @param pType
     * @return
     */
    public List<UserActivity> getUserActivities(long guId, List<IActivity> as, ActivityParentTypeEnum pType) {
        // 获得活动基础数据
        List<CfgActivityEntity> activities = ActivityTool.getActivitiesByParentType(pType);
        List<Long> aIds = as.stream().map(IActivity::gainId).collect(Collectors.toList());
        return this.getUserActivities(guId, activities, aIds);
    }

    public List<UserActivity> getUserActivities(long guId, List<IActivity> as) {
        // 获得活动基础数据
        List<CfgActivityEntity> activities = ActivityTool.getActivities();
        List<Long> aIds = as.stream().map(IActivity::gainId).collect(Collectors.toList());
        return this.getUserActivities(guId, activities, aIds);
    }

    public List<UserActivity> getUserActivities(long guId, List<CfgActivityEntity> activities, List<Long> aIds) {
        // 活动ID
        List<Integer> caIds = activities.stream().map(CfgActivityEntity::getId).collect(Collectors.toList());
        // 玩家活动记录
        List<UserActivity> uas = getUserActivities(guId);
        if (ListUtil.isNotEmpty(uas)) {
            return uas.stream().filter(ua -> caIds.contains(ua.getBaseId()) && aIds.contains(ua.getAId()))
                    .collect(Collectors.toList());
        }

        return new ArrayList<>();
    }

    /**
     * 获得区服活动实例集合
     *
     * @param pType
     * @return
     */
    public List<IActivity> getActivities(int sId, ActivityParentTypeEnum pType) {
        List<IActivity> as = new ArrayList<>();
        // 区服活动实例
        List<ServerActivity> sas = this.serverActivityService.getServerActivities(sId, pType);
        if (ListUtil.isNotEmpty(sas)) {
            List<ServerActivity> distintSas = new ArrayList<>();
            // 去除重复生成的数据
            List<Integer> types = new ArrayList<>();
            for (ServerActivity sa : sas) {
                if (types.contains(sa.getType())) {
                    this.serverService.deleteServerData(sa);
                } else {
                    distintSas.add(sa);
                    types.add(sa.getType());
                }
            }
            as.addAll(distintSas);
        }
        // 游戏活动实例
        List<GameActivity> gas = this.gameActivityService.getGameActivitiesBySid(sId, pType);
        if (ListUtil.isNotEmpty(gas)) {
            List<GameActivity> distintGas = new ArrayList<>();
            // 去除重复生成的数据
            List<Integer> types = new ArrayList<>();
            for (GameActivity ga : gas) {
                if (types.contains(ga.getType())) {
                    this.gameDataService.deleteGameData(ga);
                } else {
                    distintGas.add(ga);
                    types.add(ga.getType());
                }
            }
            as.addAll(distintGas);
        }

        if (ListUtil.isNotEmpty(as)) {
            // 活动排序
            as = as.stream().filter(tmp -> ActivityTool.getActivitiesByType(ActivityEnum.fromValue(tmp.gainType())).size() > 0)
                    .collect(Collectors.toList());
            as.sort((o1, o2) -> {
                int serial1 =
                        ActivityTool.getActivitiesByType(ActivityEnum.fromValue(o1.gainType())).get(0).getSerial();
                int serial2 =
                        ActivityTool.getActivitiesByType(ActivityEnum.fromValue(o2.gainType())).get(0).getSerial();
                return serial1 - serial2;
            });
        }
        return as;
    }

    /**
     * 获取days天前过期的活动数据
     *
     * @param sid
     * @return
     */
    public List<IActivity> getDaysAgoActivities(int sid, int days) {
        List<IActivity> as = new ArrayList<>();
        // 区服活动实例
        List<ServerActivity> sas = this.serverActivityService.getServerActivities(sid);
        // 游戏活动实例
        List<GameActivity> gas = this.gameActivityService.getGameActivitiesBySid(sid);
        as.addAll(sas);
        as.addAll(gas);
        Date date = DateUtil.addDays(DateUtil.now(), -days);
        // 返回一个月前结束的实例
        return as.stream().filter(a -> DateUtil.getDaysBetween(a.gainEnd(), date) >= 0).collect(Collectors.toList());
    }

    /**
     * 获得当前活动实例
     *
     * @param sId
     * @param type
     * @return
     */
    public IActivity getActivity(int sId, ActivityEnum type) {
        List<CfgActivityEntity> cas = ActivityTool.getActivitiesByType(type);
        if (ListUtil.isEmpty(cas)) {
            return null;
        }
        CfgActivityEntity ca = cas.get(0);
        // 区服实例
        if (ca.getScope() == ActivityScopeEnum.SERVER.getValue()) {
            return this.serverActivityService.getSa(sId, ca);
        }
        // 游戏实例
        return this.gameActivityService.getGa(sId, ca);
    }

    public IActivity getGameActivity(int sId, ActivityEnum type) {
        List<CfgActivityEntity> cas = ActivityTool.getActivitiesByType(type);
        if (ListUtil.isEmpty(cas)) {
            return null;
        }
        CfgActivityEntity ca = cas.get(0);
        // 游戏实例
        return this.gameActivityService.getGa(sId, ca);
    }

    /**
     * 获得包含历史的活动
     *
     * @param sId
     * @param type
     * @return
     */
    public List<IActivity> getActivitiesIncludeHistory(int sId, ActivityEnum type) {
        List<CfgActivityEntity> cas = ActivityTool.getActivitiesByType(type);
        if (ListUtil.isEmpty(cas)) {
            return null;
        }
        CfgActivityEntity ca = cas.get(0);
        // 区服实例
        if (ca.getScope() == ActivityScopeEnum.SERVER.getValue()) {
            return this.serverActivityService.getSasIncludeHistory(sId, ca);
        }
        // 游戏实例
        return this.gameActivityService.getGasIncludeHistory(sId, ca);
    }

    /**
     * 活动是否有效
     *
     * @param sId
     * @param type
     * @return
     */
    public boolean isActive(int sId, ActivityEnum type) {
        return this.getActivity(sId, type) != null;
    }

    /**
     * 获取首冲翻倍重置的时间
     *
     * @param sId
     * @return
     */
    public String getTimeAsResetDoubleFirstR(int sId) {
        String resetTime = "2019-01-01 00:00:00";

        List<ServerActivity> sas = this.serverActivityService.getServerActivities(sId).stream()
                .filter(sa -> sa.getType() == ActivityEnum.RESET_FIRST_DOUBLE_R.getValue())
                .collect(Collectors.toList());
        if (ListUtil.isNotEmpty(sas)) {
            resetTime = DateUtil.toDateTimeString(sas.get(sas.size() - 1).getBegin());
        }
        log.info("首冲翻倍重置的时间%s", resetTime);
        return resetTime;
    }

    /**
     * 处理活动进度
     *
     * @param guId
     * @param addedProgress
     * @param type
     */
    public void handleUaProgress(long guId, int sId, int addedProgress, ActivityEnum type) {
        if (ActivityEnum.GongCLD == type || ActivityEnum.ACC_R == type || ActivityEnum.MULTIPLE_REBATE == type) {
            log.info("攻城略地、累充（非节日累充）、3倍返利不调用handleUaProgress");
            return;
        }
        IActivity a = this.getActivity(sId, type);
        // 活动未生效，不做任何处理
        if (a == null) {
            return;
        }
        List<CfgActivityEntity> cas = ActivityTool.getActivitiesByType(type);
        // 活动是否通过邮件发放奖励
        boolean isAwardByMail = cas.get(0).getParentType() == ActivityParentTypeEnum.NO_UI_ACTIVITY.getValue();
        List<UserActivity> uas = this.getUserActivities(guId, a.gainId(), type);
        boolean isUasNotEmpty = ListUtil.isNotEmpty(uas);
        if (ListUtil.isEmpty(uas) && ActivityEnum.isHerobackActivity(type)) {
            // 回归签到 玩家没有UserActivity表示没有参与资格
            return;
        }
        if (ActivityEnum.isMultiDayRechargeActivity(type)) {
            // 多日累充判断玩家注册时间
            Date regTime = gameUserService.getGameUser(guId).getRoleInfo().getRegTime();
            if (regTime == null) {
                return;
            }
            int daysBetween = DateUtil.getDaysBetween(regTime, DateUtil.now());
            if (daysBetween < 3) {
                return;
            }
        }
        if (ActivityEnum.isTodayDayAccRechargeActivity(type)) {
            // 今日累充判断玩家注册时间
            Date regTime = gameUserService.getGameUser(guId).getRoleInfo().getRegTime();
            int daysBetween = 4;
            if (regTime == null) {
                if (ActivityEnum.TODAY_ACC_R.equals(type)) {
                    //如果注册时间为空 则默认为是老账号 所以注册前3天的累冲不计算
                    return;
                }
            } else {
                daysBetween = DateUtil.getDaysBetween(regTime, DateUtil.now());
            }
            if (daysBetween < 3) {
                if (!ActivityEnum.TODAY_ACC_R.equals(type)) {
                    //如果注册时间小于3天 且不是注册前3天的累冲 则不计算
                    return;
                }
            } else if (DateUtil.isWeekEndDate(new Date()) && !ActivityEnum.TODAY_ACC_R_3.equals(type)) {
                return;
            } else if (!DateUtil.isWeekEndDate(new Date()) && !ActivityEnum.TODAY_ACC_R_2.equals(type)) {
                return;
            }
        }
        //签到卡判定
        if (ActivityEnum.RECHARGE_SIGN.equals(type)) {
            List<UserReceipt> receipts = receiptService.getReceiptsSinceDate(guId, CfgProductGroup.CfgProduct.RECHARGE_SIGN, a.gainBegin());
            if (ListUtil.isEmpty(receipts)) {
                return;
            }
        }
        for (CfgActivityEntity ca : cas) {
            UserActivity uActivity = null;
            if (isUasNotEmpty) {
                uActivity = uas.stream().filter(ua -> ua.getBaseId().equals(ca.getId())).findFirst().orElse(null);
            }
            if (isAwardByMail) {
                // 处理邮件活动
                this.handleUaAsMailAward(guId, uActivity, a.gainId(), addedProgress, ca);
            } else {
                // 处理界面活动
                this.handleUa(guId, uActivity, a.gainId(), addedProgress, ca);
            }

        }
    }

    /**
     * 处理累充活动
     *
     * @param guId
     * @param sId
     * @param addedProgress
     * @param type
     */
    public void handleUaProgressAsRound(long guId, int sId, int addedProgress, ActivityEnum type) {
        IActivity a = this.getActivity(sId, type);
        // 活动未生效，不做任何处理
        if (a == null) {
            return;
        }
        List<UserActivity> uas = this.getNewestUserActivities(guId, a.gainId(), type);
        boolean isUasNotEmpty = ListUtil.isNotEmpty(uas);
        int curRound = 1;
        if (isUasNotEmpty) {
            curRound = uas.get(0).getRound();
        }
        List<CfgActivityEntity> cas = ActivityTool.getActivitiesByType(type);

        int finalCurRound = curRound;
        List<CfgActivityEntity> curCas = cas.stream().filter(tmp -> tmp.getSeries() == finalCurRound).collect(Collectors.toList());
        for (int i = 0; i < curCas.size(); i++) {
            CfgActivityEntity ca = curCas.get(i);
            UserActivity uActivity = null;
            if (isUasNotEmpty) {
                uActivity = uas.stream().filter(ua -> ua.getBaseId().equals(ca.getId())).findFirst().orElse(null);
            }

            int newProgress = addedProgress;// 用于判定是否需要处理下一轮
            if (uActivity != null) {
                newProgress += uActivity.getProgress();
            }
            // 处理活动
            this.handleUaWithRound(guId, uActivity, a.gainId(), curRound, addedProgress, ca);

            if (i == curCas.size() - 1 && newProgress >= ca.getNeedValue()) {
                addedProgress = newProgress - ca.getNeedValue();
                int nextRound = ++curRound;
                i = -1;// 处理下一轮
                uas = new ArrayList<>();
                curCas = ActivityTool.getActivitiesByType(type).stream().filter(tmp -> tmp.getSeries().equals(nextRound)).collect(Collectors.toList());

            }

        }
    }

    /**
     * 修复进度，当前仅支持今日充值、累计充值
     *
     * @param uid
     */
    public void repaireToRMBProgress(long uid) {
        int sId = this.gameUserService.getActiveSid(uid);
        List<ActivityEnum> needRepairs = Arrays.asList(ActivityEnum.TODAY_ACC_R, ActivityEnum.ACC_R);
        List<IActivity> as = needRepairs.stream().map(tmp -> this.getActivity(sId, tmp)).collect(Collectors.toList());
        if (ListUtil.isNotEmpty(as)) {
            as = as.stream().filter(tmp -> tmp != null).collect(Collectors.toList());
        }
        if (ListUtil.isEmpty(as)) {
            return;
        }
        List<UserActivity> uas = this.getUserActivities(uid, as);
        if (ListUtil.isNotEmpty(uas)) {
            uas.forEach(tmp -> tmp.setProgress(tmp.getProgress() / 10));
            this.gameUserService.updateItems(uas);
        }
        //

    }

    /**
     * 处理通过邮件发放奖励的活动的
     *
     * @param guId
     * @param ua
     * @param aId
     * @param addedProgress
     * @param ca
     */
    private UserActivity handleUaAsMailAward(Long guId, UserActivity ua, long aId, int addedProgress, CfgActivityEntity ca) {
        // 奖励已发放
        if (ua != null && ua.getStatus() == AwardStatus.AWARDED.getValue()) {
            return ua;
        }
        // 新增或者更新用户活动记录
        if (ua == null) {
            ua = UserActivity.fromActivityAsMailAward(guId, aId, addedProgress, ca);
            addUserActivity(guId, ua);
        } else {
            ua.addProgressAsMailAward(addedProgress, ca);
            this.gameUserService.updateItem(ua);

        }
        // 达成条件，发放奖励
        if (ua.getStatus() == AwardStatus.AWARDED.getValue()) {
            this.mailService.sendAwardMail(ca.getName(), ca.getDetail(), guId, ca.getAwards());
        }
        return ua;
    }

    /**
     * 处理通过界面领取的活动
     *
     * @param guId
     * @param ua
     * @param aId
     * @param addedProgress
     * @param ca
     */
    public UserActivity handleUa(Long guId, UserActivity ua, long aId, int addedProgress, CfgActivityEntity ca) {
        if (ua == null) {
            ua = UserActivity.fromActivity(guId, aId, addedProgress, ca);
            addUserActivity(guId, ua);
        } else {
            if (ua.getStatus() == AwardStatus.UNAWARD.getValue()) {
                if (ca.getType() == ActivityEnum.SEVEN_LOGIN.getValue() && DateUtil.isToday(ua.getDate()) && ua.getProgress() == 1) {
                    //7日之约当日生成，所以进度已有1的情况 则不再重复添加，防止重置7日之约 和 今日首登 导致进度重复+1的问题
                    return ua;
                }
                ua.addProgress(addedProgress, ca);
                this.gameUserService.updateItem(ua);
            } else if (ua.getStatus() == AwardStatus.ENABLE_REPLENISH.getValue() && ca.getType() == ActivityEnum.MONTH_LOGIN.getValue()) {
                ua.addProgress(addedProgress, ca);
                this.gameUserService.updateItem(ua);
            }
        }
        return ua;
    }

    private UserActivity handleUaWithRound(Long guId, UserActivity ua, long aId, int round, int addedProgress, CfgActivityEntity ca) {
        if (ua == null) {
            ua = UserActivity.fromActivity(guId, aId, round, addedProgress, ca);
            addUserActivity(guId, ua);
            this.gameUserService.addItem(guId, ua);
        } else if (ua.getStatus() == AwardStatus.UNAWARD.getValue()) {
            ua.addProgress(addedProgress, ca);
            this.gameUserService.updateItem(ua);
        }
        return ua;
    }

    /**
     * 重置七日之约，并将今日置为可领取
     *
     * @param sId
     * @param guId
     */
    public void resetSevenLogin(int sId, long guId) {
        IActivity a = getActivity(sId, ActivityEnum.SEVEN_LOGIN);
        List<UserActivity> uas = getUserActivities(guId, a.gainId(), ActivityEnum.SEVEN_LOGIN);
        if (ListUtil.isNotEmpty(uas)) {
            this.gameUserService.deleteItems(guId, uas);
        }
        //将今日七日之约置位可领取
        handleUaProgress(guId, sId, 1, ActivityEnum.SEVEN_LOGIN);
    }

    /**
     * 加入英雄回归活动
     *
     * @param gu
     * @param lastLoginDate
     */
    public void joinHeroBackActivity(GameUser gu, Date lastLoginDate) {
        Long uid = gu.getId();
        int sId = gu.getServerId();
        long seconds = DateUtil.getSecondsBetween(lastLoginDate, new Date());
        long second30Days = 60 * 60 * 24 * 30;// 30天的秒数
        if (gu.getLevel() < 30 || seconds < second30Days) {
            // 必须满30级且 30天未登陆
            return;
        }
        List<IActivity> activityies = getActivities(sId, ActivityParentTypeEnum.HERO_BACK_ACTIVITY);
        if (ListUtil.isEmpty(activityies)) {
            return;
        }
        initHeroback(uid, this.gameUserService.getActiveSid(uid));
    }

    /**
     * 加入充值活动
     *
     * @param gu
     */
    public void joinAccRechargeActivity(GameUser gu) {
        long uid = gu.getId();
        Date regTime = gu.getRoleInfo().getRegTime();
        int daysBetween = DateUtil.getDaysBetween(regTime, DateUtil.now());
        List<CfgActivityEntity> cas;
        if (daysBetween < 3) {
            cas = ActivityTool.getActivitiesByType(ActivityEnum.TODAY_ACC_R);
            addUserActivities(uid, cas);
            return;
        }
        int weekDay = DateUtil.getToDayWeekDay();
        switch (weekDay) {
            case 1:
            case 2:
                cas = ActivityTool.getActivitiesByType(ActivityEnum.MULTI_DAY_ACC_R);
                break;
            case 3:
            case 4:
                cas = ActivityTool.getActivitiesByType(ActivityEnum.MULTI_DAY_ACC_R2);
                break;
            case 5:
            case 6:
                cas = ActivityTool.getActivitiesByType(ActivityEnum.MULTI_DAY_ACC_R3);
                break;
            case 7:
                cas = ActivityTool.getActivitiesByType(ActivityEnum.SUNDAY_ACC);
                break;
            default:
                return;
        }
        addUserActivities(uid, cas);
    }

    /**
     * 新增活动
     *
     * @param uid
     * @param cas
     */
    private void addUserActivities(long uid, List<CfgActivityEntity> cas) {
        for (CfgActivityEntity ca : cas) {
            UserActivity ua;
            Long aId;
            int sid = this.gameUserService.getActiveSid(uid);
            if (ActivityScopeEnum.GAME.getValue() == ca.getScope()) {
                GameActivity ga = this.gameActivityService.getGa(sid, ca);
                if (null == ga) {
                    continue;
                }
                ua = getUserActivity(uid, ga.gainId(), ca.getId());
                aId = ga.gainId();
            } else {
                ServerActivity sa = this.serverActivityService.getSa(sid, ca);
                if (null == sa) {
                    continue;
                }
                ua = getUserActivity(uid, sa.gainId(), ca.getId());
                aId = sa.gainId();
            }
            if (null == ua) {
                ua = UserActivity.fromActivity(uid, aId, 0, ca);
                addUserActivity(uid, ua);
            }
        }
    }

    /**
     * 是否加入玩家回归活动
     *
     * @param gu
     * @return
     */
    public boolean hasJoinHeroBackActivity(GameUser gu) {
        Long guId = gu.getId();
        int sId = gu.getServerId();
        List<IActivity> activityies = getActivities(sId, ActivityParentTypeEnum.HERO_BACK_ACTIVITY);
        if (ListUtil.isEmpty(activityies)) {
            return false;
        }
        boolean timeout = false;
        for (IActivity ia : activityies) {
            List<UserActivity> uas = getUserActivities(guId, ia.gainId(), ActivityEnum.fromValue(ia.gainType()));
            if (ListUtil.isNotEmpty(uas) && !timeout) {
                long second10Days = 60 * 60 * 24 * 10;// 10天的秒数
                for (UserActivity uActivity : uas) {
                    long seconds = DateUtil.getSecondsBetween(uActivity.getDate(), new Date());
                    if (seconds >= second10Days) {
                        // 玩家活动过期
                        timeout = true;
                        break;
                    }
                    return true;
                }
            }
            if (timeout) {
                this.gameUserService.deleteItems(guId, uas);
                continue;
            }
        }
        return false;
    }

    /**
     * 当前是否在活动有效时间内
     *
     * @param type
     * @return
     */
    public boolean isInActivityTimes(long uid, int sid, ActivityParentTypeEnum type) {
        // 获取需要展示的所有活动
        List<IActivity> activities = getActivities(sid, type).stream().filter(tmp -> {
            IActivityProcessor processor = activityProcessorFactory.getActivityProcessor(ActivityEnum.fromValue(tmp.gainType()));
            return null != processor && processor.isShowInUi(uid);
        }).collect(Collectors.toList());
        // 没有需要展示的
        if (ListUtil.isEmpty(activities)) {
            return false;
        }
        // 判断时间
        Date begin = activities.stream().min(Comparator.comparing(IActivity::gainBegin)).get().gainBegin();
        Date end = activities.stream().max(Comparator.comparing(IActivity::gainEnd)).get().gainEnd();
        int todayInt = DateUtil.toDateInt(DateUtil.now());
        return todayInt >= DateUtil.toDateInt(begin) && todayInt <= DateUtil.toDateInt(end);
    }

    /**
     * 重新初始化玩家的回归任务
     *
     * @param guId
     * @param sid
     */
    public void initHeroback(long guId, int sid) {
        delHeroback(guId, sid);
        List<IActivity> activityies = getActivities(sid, ActivityParentTypeEnum.HERO_BACK_ACTIVITY);
        if (ListUtil.isNotEmpty(activityies)) {
            for (IActivity ia : activityies) {
                List<CfgActivityEntity> cas = ActivityTool.getActivitiesByType(ActivityEnum.fromValue(ia.gainType()));
                for (CfgActivityEntity ca : cas) {
                    UserActivity ua = getUserActivity(guId, ia.gainId(), ca.getId());
                    if (ua != null) {
                        this.gameUserService.deleteItem(ua);
                    }
                    ua = UserActivity.fromActivity(guId, ia.gainId(), 0, ca);
                    addUserActivity(guId, ua);
                }
            }
            this.heroBackTaskProcessor.initTasks(guId);
        }
    }

    public void delHeroback(long guId, int sid) {
        List<IActivity> activityies = getActivities(sid, ActivityParentTypeEnum.HERO_BACK_ACTIVITY);
        for (IActivity ia : activityies) {
            List<CfgActivityEntity> cas = ActivityTool.getActivitiesByType(ActivityEnum.fromValue(ia.gainType()));
            for (CfgActivityEntity ca : cas) {
                UserActivity ua = getUserActivity(guId, ia.gainId(), ca.getId());
                if (ua != null) {
                    this.gameUserService.deleteItem(ua);
                }
            }
        }
    }
}
