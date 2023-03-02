package com.bbw.god.activity;

import com.bbw.common.DateUtil;
import com.bbw.common.ListUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.activity.config.ActivityParentTypeEnum;
import com.bbw.god.activity.config.ActivityTool;
import com.bbw.god.activity.holiday.config.CfgHolidayTaskEntity;
import com.bbw.god.activity.holiday.config.HolidayTaskTool;
import com.bbw.god.activity.holiday.processor.HolidayTaskProcessor;
import com.bbw.god.activity.processor.ActivityProcessorFactory;
import com.bbw.god.activity.processor.IActivityProcessor;
import com.bbw.god.activity.processor.cardboost.CardBoostProcessor;
import com.bbw.god.activity.rd.RDActivityTypeList;
import com.bbw.god.activity.rd.RDActivityTypeList.RDActivityType;
import com.bbw.god.city.UserCityService;
import com.bbw.god.db.entity.CfgActivityEntity;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.rd.RDCommon;
import com.bbw.god.rd.RDSuccess;
import com.bbw.god.statistics.ServerStatistic;
import com.bbw.god.statistics.StatisticKeyEnum;
import com.bbw.god.statistics.serverstatistic.GodServerStatisticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ActivityLogic {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private UserCityService userCityService;
    @Autowired
    private ActivityService activityService;
    @Autowired
    private ActivityProcessorFactory activityProcessorFactory;
    @Autowired
    private GodServerStatisticService godServerStatisticService;
    @Autowired
    @Lazy
    private List<HolidayTaskProcessor> holidayTaskProcessors;

    /**
     * 获取活动数据
     *
     * @param guId
     * @param sId
     * @param type null或者0，获取活动类型；非0，获取活动列表
     * @param kind 非20，获取福利；20，获取活动
     * @return
     */
    public RDSuccess getActivities(long guId, int sId, String type, String kind) {
        // 活动/福利列表
        if (type != null && !type.equals("0")) {
            int typeInt = Integer.parseInt(type);
            ActivityEnum activityType = ActivityEnum.fromValue(typeInt);
            IActivityProcessor activityProcessor = this.activityProcessorFactory.getActivityProcessor(activityType);
            return activityProcessor.getActivities(guId, typeInt);
        }
        int parentType = ActivityParentTypeEnum.BUILD_IN_WELFARE.getValue();
        if (kind != null) {
            parentType = Integer.parseInt(kind);
        }
        ActivityParentTypeEnum activityParentType = ActivityParentTypeEnum.fromValue(parentType);
        List<RDActivityType> types = this.getActivityTypes(guId, sId, activityParentType);
        RDActivityTypeList rdActivityTypeList = new RDActivityTypeList();
        rdActivityTypeList.setTypes(types);
        return rdActivityTypeList;
    }

    /**
     * 获取当前多日累充活动类型
     *
     * @return
     */
    public ActivityEnum getCurMultiAccR() {
        int weekDay = DateUtil.getToDayWeekDay();
        switch (weekDay) {
            case 1:
            case 2:
                return ActivityEnum.MULTI_DAY_ACC_R;
            case 3:
            case 4:
                return ActivityEnum.MULTI_DAY_ACC_R2;
            case 5:
            case 6:
                return ActivityEnum.MULTI_DAY_ACC_R3;
            case 7:
                return ActivityEnum.SUNDAY_ACC;
        }
        return null;
    }

    /**
     * 当奖励是多选一时，通过该方法提前预设置最终获取的奖励是哪个
     *
     * @param guId
     * @param sId
     * @param caId
     * @param awardIndex 默认是第一个
     * @return
     */
    public boolean setAwardItem(Long guId, int sId, int caId, int awardIndex) {
        CfgActivityEntity ca = ActivityTool.getActivity(caId);
        // 活动是否存在
        if (ca == null) {
            throw new ExceptionForClientTip("activity.not.exist");
        }
        ActivityEnum activityType = ActivityEnum.fromValue(ca.getType());
        IActivityProcessor activityProcessor = this.activityProcessorFactory.getActivityProcessor(activityType);
        activityProcessor.setAwardItem(guId, sId, ca, awardIndex);
        return true;
    }

    /**
     * 领取奖励
     *
     * @param guId
     * @param sId
     * @param caId
     * @param awardIndex
     * @return
     */
    public RDCommon joinActivity(Long guId, int sId, int caId, Integer activityType, int awardIndex) {
        ActivityEnum activityEnum = null;
        if (null != activityType) {
            activityEnum = ActivityEnum.fromValue(activityType);
        }
        CfgActivityEntity ca = ActivityTool.getActivity(caId);
        // 活动是否存在
        if (null == ca && null == activityEnum) {
            throw new ExceptionForClientTip("activity.not.exist");
        }
        if (null != activityType) {
            activityEnum = ActivityEnum.fromValue(activityType);
        } else {
            activityEnum = ActivityEnum.fromValue(ca.getType());
        }
        IActivityProcessor activityProcessor = this.activityProcessorFactory.getActivityProcessor(activityEnum);
        return activityProcessor.joinActivity(guId, sId, caId, ca, awardIndex);
    }

    public RDCommon receiveTaskAward(long uid, int sid, int taskId) {
        CfgHolidayTaskEntity taskEntity = HolidayTaskTool.getTaskById(taskId);
        Integer type = taskEntity.getType();
        HolidayTaskProcessor holidayTaskProcessor = holidayTaskProcessors.stream().filter(s ->
                s.isMatch(ActivityEnum.fromValue(type))).findFirst().orElse(null);
        return holidayTaskProcessor.receiveTaskAward(uid, sid, taskId, type);
    }

    /**
     * 补领
     *
     * @param uid
     * @return
     */
    public RDCommon replenish(long uid, int sId, int caId) {
        CfgActivityEntity ca = ActivityTool.getActivity(caId);
        ActivityEnum activityType = ActivityEnum.fromValue(ca.getType());
        IActivityProcessor activityProcessor = this.activityProcessorFactory.getActivityProcessor(activityType);
        return activityProcessor.replenish(uid, sId, ca);
    }

    /**
     * 获取活动可领取数
     *
     * @param gu
     * @param pType
     * @return
     */
    public int getAwardNum(GameUser gu, int sId, ActivityParentTypeEnum pType) {
        int awardNum = 0;
        List<RDActivityType> types = this.getActivityTypes(gu, sId, pType);
        if (ListUtil.isNotEmpty(types)) {
            awardNum = types.stream().mapToInt(RDActivityType::getNum).sum();
        }
        return awardNum;
    }

    /**
     * 是否领取了所有活动
     *
     * @param guId
     * @param sId
     * @param pType
     * @return
     */
    public boolean ifAwardAllActivities(long guId, int sId, ActivityParentTypeEnum pType) {

        // 获得特定父类型当前所有的活动实例
        List<IActivity> activityies = this.activityService.getActivities(sId, pType);
        for (IActivity activity : activityies) {
            ActivityEnum activityType = ActivityEnum.fromValue(activity.gainType());
            IActivityProcessor activityProcessor = this.activityProcessorFactory.getActivityProcessor(activityType);
            if (activityProcessor == null) {
//                System.out.println(activityType);
                continue;
            }
            if (!activityProcessor.isJoinAllActivities(guId, activity)) {
                return false;
            }
        }

        return true;
    }

    public boolean isShowFirstRecharge(GameUser gu, int sid) {
        List<RDActivityType> types = this.getActivityTypes(gu, sid, ActivityParentTypeEnum.FIRST_RECHARGE_ACTIVITY);
        return ListUtil.isNotEmpty(types);
    }

    /**
     * 获得活动类型领取信息
     *
     * @param guId
     * @param sId
     * @param pType
     * @return
     */
    public List<RDActivityType> getActivityTypes(long guId, int sId, ActivityParentTypeEnum pType) {
        GameUser gu = this.gameUserService.getGameUser(guId);
        return getActivityTypes(gu, sId, pType);
    }

    /**
     * 获得活动类型领取信息
     *
     * @param gu
     * @param sId
     * @param pType
     * @return
     */
    public List<RDActivityType> getActivityTypes(GameUser gu, int sId, ActivityParentTypeEnum pType) {
        long guId = gu.getId();
        // 获得特定父类型当前所有的活动实例
        List<IActivity> activities = this.activityService.getActivities(sId, pType);
        List<RDActivityType> types = new ArrayList<>();
        for (IActivity activity : activities) {
            ActivityEnum activityType = ActivityEnum.fromValue(activity.gainType());
            IActivityProcessor activityProcessor = this.activityProcessorFactory.getActivityProcessor(activityType);
            if (activityProcessor == null || !activityProcessor.isShowInUi(guId)) {
                continue;
            }
            int ableAwardNum = activityProcessor.getAbleAwardedNum(gu, activity);

            if (ableAwardNum == 0) {
                // 七日之约都已领取则不显示
                List<UserActivity> userActivities = activityService.getUserActivities(guId, activity.gainId(),
                        activityType);
                switch (activityType) {
                    case SEVEN_LOGIN:
                    case TODAY_ACC_R:
                    case TODAY_ACC_R_2:
                    case TODAY_ACC_R_3:
                    case FIRST_R:
                    case LIMIT_CARD:
                    case MULTIPLE_REBATE:
                    case DRAW_CARD_TH:
                    case NEWER_PACKAGE:
                    case GOD_BLESS:
                        if (activityProcessor.isAwardedAllAwards(guId, userActivities, activityType)) {
                            continue;
                        }
                        break;
                    case NIGHTMARE_FIRST_R:
                        if (!userCityService.isOwnAllCity(guId, false) || activityProcessor.isAwardedAllAwards(guId, userActivities, activityType)) {
                            continue;
                        }
                        break;
                    case NEWER_BOOST:
                    case CARD_LEVEL_BOOST:
                    case CARD_EXP_BOOST:
                        if (((CardBoostProcessor) activityProcessor).getRemainTime(guId) <= 0) {
                            continue;
                        }
                    default:
                        break;
                }
            }
            //判断是否全部领取完了
//			if (ActivityEnum.NEWER_PACKAGE.equals(activityType) && activityProcessor.isJoinAllActivities(guId,activity)){
//				continue;
//			}
            if (Arrays.asList(ActivityEnum.GongCLD, ActivityEnum.DRAW_CARD_TH, ActivityEnum.NEWER_PACKAGE,
                    ActivityEnum.GOD_FAVOR).contains(activityType)) {
                Date regTime = gu.getRoleInfo().getRegTime();
                // 判断注册时间到现在是否过了7天
                if (DateUtil.addSeconds(regTime, 7 * 24 * 60 * 60 - 1).before(DateUtil.now())) {
                    continue;
                }
            }

            types.add(new RDActivityType(activityType.getValue(), ableAwardNum));
        }

        types = filterActivityTypes(gu, pType, types);
        return types;
    }

    private List<RDActivityType> filterActivityTypes(GameUser gu, ActivityParentTypeEnum pType,
                                                     List<RDActivityType> types) {
        if (ActivityParentTypeEnum.HERO_BACK_ACTIVITY == pType) {
            //回归活动需要按顺序返回
            if (ListUtil.isNotEmpty(types)) {
                types = types.stream().sorted(Comparator.comparing(RDActivityType::getType)).collect(Collectors.toList());
            }
        }
        // 精彩活动要筛选玩家对应的活动
        if (ActivityParentTypeEnum.WONDERFUL_ACTIVITY == pType) {
            if (ListUtil.isNotEmpty(types)) {
                List<Integer> list = new ArrayList<>();
                Date regTime = gu.getRoleInfo().getRegTime();
                int regDates = 4;
                Date now = DateUtil.now();
                if (regTime != null) {
                    regDates = DateUtil.getDaysBetween(regTime, now);
                }
                if (regDates >= 3) {
                    if (DateUtil.isWeekEndDate(now)) {
                        //周日
                        list.add(ActivityEnum.TODAY_ACC_R.getValue());
                        list.add(ActivityEnum.TODAY_ACC_R_2.getValue());
                    } else {
                        //周一到周六
                        list.add(ActivityEnum.TODAY_ACC_R.getValue());
                        list.add(ActivityEnum.TODAY_ACC_R_3.getValue());
                    }
                } else {
                    //注册前三天
                    list.add(ActivityEnum.TODAY_ACC_R_2.getValue());
                    list.add(ActivityEnum.TODAY_ACC_R_3.getValue());
                }
                types = types.stream().filter(t -> !list.contains(t.getType())).collect(Collectors.toList());
            }
        }
        return types;
    }

    /**
     * 主界面是否显示新手福利
     *
     * @param gu
     * @return
     */
    public boolean isShowNewerWelfare(GameUser gu) {
        List<RDActivityType> types = getActivityTypes(gu, gu.getServerId(),
                ActivityParentTypeEnum.NEWER_WELFARE);
        return ListUtil.isNotEmpty(types);
    }

    /**
     * 是否领取了哼哈二将的礼包
     *
     * @param uid
     * @return
     */
    public boolean isGainHengHaPack(long uid) {
        GameUser gu = gameUserService.getGameUser(uid);
        List<IActivity> activities = this.activityService.getActivities(gu.getServerId(), ActivityParentTypeEnum.FIRST_RECHARGE_ACTIVITY);
        for (IActivity activity : activities) {
            ActivityEnum activityType = ActivityEnum.fromValue(activity.gainType());
            if (!ActivityEnum.FIRST_R.equals(activityType)) {
                continue;
            }
            IActivityProcessor activityProcessor = this.activityProcessorFactory.getActivityProcessor(activityType);
            if (activityProcessor == null) {
                continue;
            }
            int ableAwardNum = activityProcessor.getAbleAwardedNum(gu, activity);
            if (ableAwardNum == 0) {
                List<UserActivity> userActivities = activityService.getUserActivities(uid, activity.gainId(), activityType);
                if (activityProcessor.isAwardedAllAwards(uid, userActivities, activityType)) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    /**
     * 是否是本区第一个攻下五级城的玩家
     *
     * @param gu
     * @return
     */
    private boolean isFirstDefeatCity5(GameUser gu) {
        // 第一个攻下五级城的玩家
        ServerStatistic ss = this.godServerStatisticService.getStatistic(gu.getServerId(), StatisticKeyEnum.FIRST_CC5);
        if (ss != null) {
            long firstCC5GuId = ss.getUid();
            GameUser firstCC5Gu = this.gameUserService.getGameUser(firstCC5GuId);
            return gu.getId().equals(firstCC5Gu.getId());
        }
        return false;
    }
}
