package com.bbw.god.activity.processor;

import com.bbw.common.DateUtil;
import com.bbw.common.ListUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.activity.ActivityService;
import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.UserActivity;
import com.bbw.god.activity.config.ActivityConfig;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.activity.config.ActivityStatusEnum;
import com.bbw.god.activity.config.ActivityTool;
import com.bbw.god.activity.rd.RDActivityItem;
import com.bbw.god.activity.rd.RDActivityList;
import com.bbw.god.db.entity.CfgActivityEntity;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardService;
import com.bbw.god.game.award.AwardStatus;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.res.ResChecker;
import com.bbw.god.gameuser.res.ResEventPublisher;
import com.bbw.god.rd.RDCommon;
import com.bbw.god.rd.RDSuccess;
import com.bbw.god.server.ServerService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 活动处理类
 */
public abstract class AbstractActivityProcessor implements IActivityProcessor {
    // 无时间
    protected static final long NO_TIME = -1;
    @Autowired
    protected GameUserService gameUserService;
    @Autowired
    protected ActivityService activityService;
    @Autowired
    protected ServerService serverService;
    @Autowired
    protected AwardService awardService;
    @Autowired
    protected ActivityConfig activityConfig;

    protected List<ActivityEnum> activityTypeList;

    public WayEnum getWay() {
        return WayEnum.ACTIVITY;
    }

    /**
     * 是否在ui中展示
     *
     * @return
     */
    @Override
    public boolean isShowInUi(long uid) {
        return true;
    }

    /**
     * 获取活动详情
     *
     * @param uid
     * @return
     */
    @Override
    public RDSuccess getActivities(long uid, int activityType) {
        int sid = this.gameUserService.getActiveSid(uid);
        ActivityEnum activityEnum = ActivityEnum.fromValue(activityType);
        IActivity a = this.activityService.getActivity(sid, activityEnum);
        if (a == null) {
            throw new ExceptionForClientTip("activity.not.exist");
        }
        RDActivityList rd = new RDActivityList();
        GameUser gu = this.gameUserService.getGameUser(uid);

        List<CfgActivityEntity> cas = ActivityTool.getActivitiesByType(activityEnum);
        List<UserActivity> uas = this.activityService.getUserActivities(uid, a.gainId(), activityEnum);

        List<RDActivityItem> rdActivities = new ArrayList<>();
        for (CfgActivityEntity ca : cas) {
            int caId = ca.getId();
            UserActivity ua = uas.stream().filter(tmp -> tmp.getBaseId() == caId).findFirst().orElse(null);
            // 获取活动的状态
            AwardStatus status = this.getUAStatus(gu, a, ua, ca);
            // 封装活动信息
            RDActivityItem rdActivity = this.toRdActivity(gu, ua, ca, status.getValue());
            if (rdActivity == null) {
                continue;
            }
            rdActivity.setSign(a.gainSign());
            rdActivities.add(rdActivity);
        }
        rd.setItems(rdActivities);

        //处理剩余时间、总进度
        long remainTime = this.getRemainTime(uid, sid, a);
        if (remainTime != 0) {
            rd.setRemainTime(remainTime);
            int totalProgress = this.getTotalProgress(uas);
            rd.setTotalProgress(totalProgress);
        }
        if (null != a.gainBegin() && null != a.gainEnd()) {
            rd.setDateInfo(DateUtil.toString(a.gainBegin(), "M月d日HH:mm") + "-" + DateUtil.toString(a.gainEnd(), "M月d日HH:mm"));
        }
        rd.setCurType(activityType);
        return rd;
    }

    /**
     * 当奖励是多选一时，通过该方法提前预设置最终获取的奖励是哪个
     *
     * @param uid
     * @param sId
     * @param ca
     * @param awardIndex 默认是第一个
     * @return
     */
    @Override
    public boolean setAwardItem(Long uid, int sId, CfgActivityEntity ca, int awardIndex) {
        ActivityEnum activityEnum = ActivityEnum.fromValue(ca.getType());
        IActivity a = this.activityService.getActivity(sId, activityEnum);
        // 活动是否过期
        if (a == null) {
            return false;
        }
        GameUser gu = this.gameUserService.getGameUser(uid);
        UserActivity ua = this.activityService.getUserActivity(uid, a.gainId(), ca.getId());
        // 如果有领取条件，且领取条件为0，且没有领取记录，则添加记录
        if (ua == null) {
            ua = UserActivity.fromActivity(gu.getId(), a.gainId(), 0, ca);
            this.activityService.addUserActivity(uid, ua);
        }
        // 活动状态
        AwardStatus status = this.getUAStatus(gu, a, ua, ca);
        if (AwardStatus.AWARDED.equals(status) || AwardStatus.TIME_OUT.equals(status)) {
            return false;
        }
        // 奖励，并支持选择奖励
        ua.setAwardIndex(awardIndex);
        this.gameUserService.updateItem(ua);
        return true;
    }

    /**
     * 获得奖励
     *
     * @param uid
     * @param sId
     * @param ca
     * @param awardIndex
     * @return
     */
    @Override
    public RDCommon joinActivity(Long uid, int sId, int caId, CfgActivityEntity ca, int awardIndex) {

        ActivityEnum activityEnum = ActivityEnum.fromValue(ca.getType());
        IActivity a = this.activityService.getActivity(sId, activityEnum);
        // 活动是否过期
        if (a == null) {
            throw new ExceptionForClientTip("activity.is.timeout");
        }
        RDCommon rd = new RDCommon();
        // 防止并发请求
        // TODO 待重构
        GameUser gu = this.gameUserService.getGameUser(uid);
        UserActivity ua;
        List<Award> awards;
        synchronized (uid) {
            ua = this.activityService.getUserActivity(uid, a.gainId(), ca.getId());
            // 如果有领取条件，且领取条件为0，且没有领取记录，则添加记录
            if (ua == null && ca.getNeedValue() != null && ca.getNeedValue() == 0) {
                ua = UserActivity.fromActivity(gu.getId(), a.gainId(), 0, ca);
                this.activityService.addUserActivity(uid, ua);
            }
            // 活动状态
            AwardStatus status = this.getUAStatus(gu, a, ua, ca);
            this.checkStatusForJoin(status);

            // 奖励，并支持选择奖励
            awards = this.getAwardsToSend(gu, ua, ca);
            if (awardIndex > awards.size()) {
                throw new ExceptionForClientTip("activity.not.valid.choose");
            }
            if (awardIndex >= 1) {
                awards = awards.subList(awardIndex - 1, awardIndex);
            }

            this.updateAwardedStatus(gu, a.gainId(), awardIndex, ca);
        }
        WayEnum way = getWay();
        if (ca.isJiKa()) {
            way = WayEnum.JK;
        } else if (ca.isYueKa()) {
            way = WayEnum.YK;
        }
        this.deliver(gu.getId(), way, ca.getName(), awards, rd);
        return rd;
    }

    /**
     * 补领
     *
     * @param uid
     * @return
     */
    @Override
    public RDCommon replenish(long uid, int sId, CfgActivityEntity ca) {
        ActivityEnum activityEnum = ActivityEnum.fromValue(ca.getType());
        GameUser gu = this.gameUserService.getGameUser(uid);
        // 需要的元宝、途径
        int needGold = this.activityConfig.getNeedGoldToReplenishDice();
        RDCommon rd = new RDCommon();
        IActivity a = this.activityService.getActivity(sId, activityEnum);
        UserActivity ua = this.activityService.getUserActivity(uid, a.gainId(), ca.getId());
        if (ua == null && ca.getNeedValue() != null && ca.getNeedValue() == 0) {
            ua = UserActivity.fromActivity(gu.getId(), a.gainId(), 0, ca);
            this.activityService.addUserActivity(uid, ua);
        }
        // 活动状态
        AwardStatus status = this.getUAStatus(gu, a, ua, ca);
        this.checkStatus(status);

        WayEnum way = WayEnum.REPLENISH_DICE;
        if (activityEnum == ActivityEnum.MONTH_LOGIN) {
            needGold = this.activityConfig.getNeedGoldToReplenishMonthLogin();
            way = WayEnum.REPLENISH_MONTH_LOGIN;
        } else if (activityEnum == ActivityEnum.RECHARGE_SIGN) {
            needGold = this.activityConfig.getNeedGoldToReplenishRechargeSign();
            way = WayEnum.REPLENISH_RECHARGE_SIGN;
        }
        // 检查元宝
        ResChecker.checkGold(gu, needGold);
        // 扣除元宝
        ResEventPublisher.pubGoldDeductEvent(uid, needGold, way, rd);

        this.updateAwardedStatus(gu, a.gainId(), 0, ca);

        List<Award> awards = this.getAwardsToSend(gu, ua, ca);
        this.awardService.fetchAward(uid, awards, WayEnum.ACTIVITY, "在【" + ca.getName() + "】活动中", rd);
        return rd;
    }


    /**
     * 是否领取的该类活动的所有奖励
     *
     * @param uid
     * @return
     */
    @Override
    public Boolean isJoinAllActivities(long uid, IActivity a) {
        ActivityEnum activityEnum = ActivityEnum.fromValue(a.gainType());
        // 活动未生效，不做任何处理
        if (a == null) {
            return true;
        }
        List<CfgActivityEntity> cas = ActivityTool.getActivitiesByType(activityEnum);
        GameUser gu = this.gameUserService.getGameUser(uid);
        List<UserActivity> uas = this.activityService.getUserActivities(uid, a.gainId(), activityEnum);
        for (CfgActivityEntity ca : cas) {
            UserActivity ua = uas.stream().filter(tmp -> tmp.getBaseId().equals(ca.getId())).findFirst().orElse(null);
            if (ua == null) {
                return false;
            }
            AwardStatus awardStatus = this.getUAStatus(gu, a, ua, ca);
            if (awardStatus != AwardStatus.AWARDED) {
                return false;
            }
        }
        return true;
    }


    /**
     * 发放奖励
     *
     * @param uid
     * @param way
     * @param caName
     * @param awards
     * @param rd
     */
    protected void deliver(long uid, WayEnum way, String caName, List<Award> awards, RDCommon rd) {
        this.awardService.fetchAward(uid, awards, way, "【" + caName + "】活动", rd);
    }

    /**
     * 更新领奖后的状态
     *
     * @param gu
     * @param aId
     * @param awardIndex 非选择传0
     * @param ca
     */
    protected void updateAwardedStatus(GameUser gu, long aId, int awardIndex, CfgActivityEntity ca) {
        UserActivity ua = this.activityService.getUserActivity(gu.getId(), aId, ca.getId());
        ua.setStatus(AwardStatus.AWARDED.getValue());
        if (awardIndex >= 1) {
            ua.setAwardIndex(awardIndex);
        }
        this.gameUserService.updateItem(ua);
    }

    /**
     * 该活动类别有多少个可领取的
     *
     * @param gu
     * @param a
     * @return
     */
    @Override
    public int getAbleAwardedNum(GameUser gu, IActivity a) {
        ActivityEnum activityEnum = ActivityEnum.fromValue(a.gainType());
        List<UserActivity> uas = this.activityService.getUserActivities(gu.getId(), a.gainId(), activityEnum);
        List<CfgActivityEntity> cas = ActivityTool.getActivitiesByType(activityEnum);
        int ableAwardNum = 0;
        for (CfgActivityEntity ca : cas) {
            UserActivity ua = uas.stream().filter(uaTmp -> uaTmp.getBaseId().intValue() == ca.getId()).findFirst().orElse(null);
            AwardStatus status = this.getUAStatus(gu, a, ua, ca);
            if (status == AwardStatus.ENABLE_AWARD) {
                ableAwardNum++;
            }
        }
        return ableAwardNum;

    }

    /**
     * 获得活动奖励
     *
     * @param gu
     * @param ua
     * @param ca
     * @return
     */
    @Override
    public List<Award> getAwardsToSend(GameUser gu, UserActivity ua, CfgActivityEntity ca) {
        return getAwardsToShow(gu, ua, ca);
    }

    @Override
    public List<Award> getAwardsToShow(GameUser gu, UserActivity ua, CfgActivityEntity ca) {
        ActivityEnum type = ActivityEnum.fromValue(ca.getType());
        final int awardWeek = this.getAwardWeek(gu.getServerId(), ua, type);

        List<Award> awards = this.awardService.parseAwardJson(ca.getAwards(), Award.class);
        if (awards == null || awards.isEmpty()) {
            return new ArrayList<>();
        }
        List<Award> subAwards = awards.stream().filter(award -> award.getWeek() == awardWeek).collect(Collectors.toList());
        if (ListUtil.isEmpty(subAwards)) {
            subAwards = awards.stream().filter(award -> award.getWeek() == 0).collect(Collectors.toList());
        }

        return subAwards;
    }


    /**
     * 活动剩余时间
     *
     * @param uid
     * @param sid
     * @param a
     * @return
     */
    protected long getRemainTime(long uid, int sid, IActivity a) {
        return NO_TIME;
    }

    /**
     * 获得活动状态
     *
     * @param gu
     * @param a
     * @param ua
     * @param ca
     * @return
     */
    @Override
    public AwardStatus getUAStatus(GameUser gu, IActivity a, UserActivity ua, CfgActivityEntity ca) {
        if (ua != null) {
            return AwardStatus.fromValue(ua.getStatus());
        }
        // 如果有领取条件，且领取条件为0，且没有领取记录，则返回可领取
        if (ca.getNeedValue() != null && ca.getNeedValue() == 0) {
            return AwardStatus.ENABLE_AWARD;
        }
        return AwardStatus.UNAWARD;
    }

    /**
     * 组装返给客户端的单个活动的数据
     *
     * @param gu
     * @param ua
     * @param ca
     * @param status
     * @return
     */
    protected RDActivityItem toRdActivity(GameUser gu, UserActivity ua, CfgActivityEntity ca, int status) {
        RDActivityItem rdActivity = new RDActivityItem();
        rdActivity.setId(ca.getId());
        rdActivity.setTotalProgress(ca.getNeedValue());
        if (null != ca.getSeries()) {
            rdActivity.setSeries(ca.getSeries());
        }
        rdActivity.setStatus(status);
        List<Award> awards = this.getAwardsToShow(gu, ua, ca);
        rdActivity.setAwards(awards);
        rdActivity.setTitle(ca.getName());
        return rdActivity;
    }

    /**
     * 获得总进度
     *
     * @param uas
     * @return
     */
    protected int getTotalProgress(List<UserActivity> uas) {
        int totalProgress = 0;
        if (ListUtil.isNotEmpty(uas)) {
            totalProgress = uas.stream().mapToInt(UserActivity::getProgress).max().getAsInt();
        }
        return totalProgress;
    }

    /**
     * 获得奖励期数
     *
     * @param sid
     * @param ua
     * @param type
     * @return
     */
    private int getAwardWeek(int sid, UserActivity ua, ActivityEnum type) {
        Date now = DateUtil.now();
        int awardWeek = this.serverService.getOpenWeek(sid, now);
        // 轮次活动
        if (this.activityConfig.getRoundActivities().contains(type)) {
            awardWeek = 1;
            if (ua != null) {
                awardWeek = ua.getRound();
            }
        }
        return awardWeek;
    }

    /**
     * 检查活动状态
     *
     * @param status
     */
    protected void checkStatusForJoin(AwardStatus status) {
        if (status == AwardStatus.ENABLE_REPLENISH || status == AwardStatus.READY_REPLENISH) {
            throw new ExceptionForClientTip("activity.not.join");
        }
        this.checkStatus(status);
    }

    private void checkStatus(AwardStatus status) {
        if (status == AwardStatus.AWARDED) {
            throw new ExceptionForClientTip("activity.awarded");
        } else if (status == AwardStatus.UNAWARD) {
            throw new ExceptionForClientTip("activity.unaward");
        } else if (status == AwardStatus.TIME_OUT) {
            throw new ExceptionForClientTip("activity.award.timeout");
        }
    }


    /**
     * 是否匹配特定的活动
     *
     * @param activityType
     * @return
     */
    @Override
    public boolean isMatch(ActivityEnum activityType) {
        return this.activityTypeList.contains(activityType);
    }


    /**
     * 是否领取所有奖励
     *
     * @param userActivities
     * @param activityEnum
     * @return
     */
    @Override
    public boolean isAwardedAllAwards(long uid, List<UserActivity> userActivities, ActivityEnum activityEnum) {
        int totalSize = ActivityTool.getActivitiesByType(activityEnum).size();
        if (totalSize != userActivities.size()) {
            return false;
        }
        return userActivities.stream().allMatch(ac -> ac.getStatus().equals(ActivityStatusEnum.AWARDED0.getValue()));
    }
}
