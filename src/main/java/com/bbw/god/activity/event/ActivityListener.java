package com.bbw.god.activity.event;

import com.bbw.common.DateUtil;
import com.bbw.common.ListUtil;
import com.bbw.god.activity.ActivityService;
import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.UserActivity;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.activity.config.ActivityTool;
import com.bbw.god.activity.holiday.processor.HolidayCombinedServiceLoginProcessor;
import com.bbw.god.activity.holiday.processor.HolidayDayPayDoubleProcessor;
import com.bbw.god.activity.holiday.processor.HolidayGiftPackProcessor;
import com.bbw.god.activity.processor.*;
import com.bbw.god.city.event.EPCityAdd;
import com.bbw.god.city.event.UserCityAddEvent;
import com.bbw.god.db.entity.CfgActivityEntity;
import com.bbw.god.event.EventParam;
import com.bbw.god.game.award.AwardStatus;
import com.bbw.god.game.config.CfgProductGroup.CfgProduct;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.city.CfgCityEntity;
import com.bbw.god.game.config.city.CityTool;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.guide.NewerGuideService;
import com.bbw.god.gameuser.mail.MailService;
import com.bbw.god.gameuser.pay.UserReceipt;
import com.bbw.god.gameuser.res.ResWayType;
import com.bbw.god.gameuser.res.copper.CopperAddEvent;
import com.bbw.god.gameuser.res.copper.EPCopperAdd;
import com.bbw.god.gameuser.res.exp.EPExpAdd;
import com.bbw.god.gameuser.res.exp.ExpAddEvent;
import com.bbw.god.gameuser.res.gold.EPGoldDeduct;
import com.bbw.god.gameuser.res.gold.GoldDeductEvent;
import com.bbw.god.gameuser.statistic.behavior.recharge.RechargeStatistic;
import com.bbw.god.gameuser.statistic.behavior.recharge.RechargeStatisticService;
import com.bbw.god.gameuser.treasure.UserTreasure;
import com.bbw.god.gameuser.treasure.UserTreasureService;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.login.LoginPlayer;
import com.bbw.god.login.event.EPFirstLoginPerDay;
import com.bbw.god.login.event.FirstLoginPerDayEvent;
import com.bbw.god.login.event.LoginEvent;
import com.bbw.god.mall.cardshop.CardPoolEnum;
import com.bbw.god.mall.cardshop.event.DrawEndEvent;
import com.bbw.god.mall.cardshop.event.EPDraw;
import com.bbw.god.pay.DeliverNotifyEvent;
import com.bbw.god.pay.ProductService;
import com.bbw.god.pay.ReceiptService;
import com.bbw.god.rd.RDCommon;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.bbw.god.activity.holiday.processor.HolidayDayAccRProcessor.AWARD_AMOUNT_LIMIT;

/**
 * ???????????????
 *
 * @author suhq
 * @date 2019???3???4??? ??????4:15:07
 */
@Component
@Slf4j
public class ActivityListener {
    @Autowired
    private ActivityService activityService;
    @Autowired
    private MailService mailService;
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private ProductService productService;
    @Autowired
    private ReceiptService receiptService;
    @Autowired
    private GongCLDProcessor gongCLDProcessor;
    @Autowired
    private MultipleRebateProcessor multipleRebateProcessor;
    @Autowired
    private UserTreasureService userTreasureService;
    @Autowired
    private RechargeStatisticService rechargeStatisticService;
    @Autowired
    private NewerGuideService newerGuideService;
    @Autowired
    private FirstRechargeProcessor firstRechargeProcessor;
    @Autowired
    private NightmareFirstRechargeProcessor nightmareFirstRechargeProcessor;
    @Autowired
    private PerDayAccProcessor combinedServicePerDayAccR10Processor;
    @Autowired
    private HolidayCombinedServiceLoginProcessor holidayCombinedServiceLoginProcessor;
    @Autowired
    private HolidayDayPayDoubleProcessor holidayDayPayDoubleProcessor;
    @Autowired
    private HolidayGiftPackProcessor holidayGiftPackProcessor;

    private static final List<Integer> FIRST_RECHARGE = Arrays.asList(60, 61, 62, 63);
    private static final List<Integer> NIGHTMARE_FIRST_RECHARGE = Arrays.asList(70, 71, 72, 73);

    private static final List<Integer> HOLIDAY_GIFT_PACK_51 = Arrays.asList(99001801, 99001802, 99001803, 99001804);
    /**
     * ??????????????????????????????????????????????????????
     *
     * @param event
     */
    @Async
    @EventListener
    public void firstLoginPerDay(FirstLoginPerDayEvent event) {
        EPFirstLoginPerDay ep = event.getEP();
        long guId = ep.getUid();
        int sId = this.gameUserService.getActiveSid(guId);
        this.activityService.handleUaProgress(guId, sId, 1, ActivityEnum.SEVEN_LOGIN);
        this.activityService.handleUaProgress(guId, sId, 1, ActivityEnum.MONTH_LOGIN);
        this.activityService.handleUaProgress(guId, sId, 1, ActivityEnum.LOGIN_AWARD);
        this.activityService.handleUaProgress(guId, sId, 1, ActivityEnum.HOLIDAY_BAG);
        this.activityService.handleUaProgress(guId, sId, 1, ActivityEnum.HOLIDAY_SIGN);
        this.activityService.handleUaProgress(guId, sId, 1, ActivityEnum.HOLIDAY_SIGN_51);
        this.activityService.handleUaProgress(guId, sId, 1, ActivityEnum.HOLIDAY_SIGN_52);
        this.activityService.handleUaProgress(guId, sId, 1, ActivityEnum.HERO_BACK_SIGIN);
        this.activityService.handleUaProgress(guId, sId, 1, ActivityEnum.RECHARGE_SIGN);
        this.activityService.handleUaProgress(guId, sId, 1, ActivityEnum.COMBINED_SERVICE_LOGIN);

    }

    /**
     * ??????????????????????????????
     *
     * @param event
     */
    @Async
    @EventListener
    @Order(1000)
    public void login(LoginEvent event) {
        LoginPlayer player = event.getLoginPlayer();
        Long uid = player.getUid();
        int sid = gameUserService.getActiveSid(uid);
        if (!holidayCombinedServiceLoginProcessor.isOpened(sid)) {
            return;
        }
        //??????????????????
        IActivity iActivity = activityService.getActivity(sid, ActivityEnum.COMBINED_SERVICE_LOGIN);
        List<UserActivity> userActivities = activityService.getUserActivities(uid, iActivity.gainId(), ActivityEnum.COMBINED_SERVICE_LOGIN);
        if (ListUtil.isEmpty(userActivities)) {
            this.activityService.handleUaProgress(uid, sid, 1, ActivityEnum.COMBINED_SERVICE_LOGIN);
            return;
        }
        //????????????
        Date beginTime = iActivity.gainBegin();
        Date now = DateUtil.now();
        int openDays = DateUtil.getDaysBetween(beginTime, now);
        if (openDays > userActivities.size()) {
            return;
        }

        UserActivity userActivity = userActivities.get(openDays);

        if (openDays != 0 || userActivity.getProgress() != 0) {
            return;
        }
        this.activityService.handleUaProgress(uid, sid, 1, ActivityEnum.COMBINED_SERVICE_LOGIN);
    }

    /**
     * ?????????????????????????????????????????????????????????
     *
     * @param event
     */
    @EventListener
    @Order(2)
    public void deliverNotify(DeliverNotifyEvent event) {
        UserReceipt userReceipt = event.getParam();
        long guId = userReceipt.getGameUserId();
        int sId = this.gameUserService.getActiveSid(guId);
        CfgProduct product = this.productService.getCfgProduct(userReceipt.getProductId());
        boolean isTodayFirstReceipt = this.receiptService.isTodayFirstGoldPackReceipt(guId);
        boolean isFirstDiamondPackReceipt = this.receiptService.isTodayFirstDiamondPackReceipt(guId);
        int rmbProgress = product.getPrice();
        int goldProgress = product.getQuantity();
        if (goldProgress == 0 || productService.ifPayForDiamond(userReceipt.getProductId())) {
            goldProgress = rmbProgress * 10;
        }

        this.activityService.handleUaProgress(guId, sId, rmbProgress, ActivityEnum.MULTIPLE_REBATE);
        this.activityService.handleUaProgress(guId, sId, rmbProgress, ActivityEnum.LIMIT_CARD);
        this.activityService.handleUaProgressAsRound(guId, sId, rmbProgress, ActivityEnum.ACC_R);
        this.multipleRebateProcessor.handleRechargeProgress(guId, sId, rmbProgress);
        this.activityService.handleUaProgress(guId, sId, rmbProgress, ActivityEnum.TODAY_ACC_R);
        this.activityService.handleUaProgress(guId, sId, rmbProgress, ActivityEnum.TODAY_ACC_R_2);
        this.activityService.handleUaProgress(guId, sId, rmbProgress, ActivityEnum.TODAY_ACC_R_3);
        this.activityService.handleUaProgress(guId, sId, rmbProgress, ActivityEnum.MULTI_DAY_ACC_R);
        this.activityService.handleUaProgress(guId, sId, rmbProgress, ActivityEnum.MULTI_DAY_ACC_R2);
        this.activityService.handleUaProgress(guId, sId, rmbProgress, ActivityEnum.MULTI_DAY_ACC_R3);
        this.activityService.handleUaProgress(guId, sId, rmbProgress, ActivityEnum.SUNDAY_ACC);
        this.activityService.handleUaProgress(guId, sId, goldProgress, ActivityEnum.HOLIDAY_ACC_R);
        this.activityService.handleUaProgress(guId, sId, goldProgress, ActivityEnum.HOLIDAY_ACTIVITY_ACC_R);
        this.activityService.handleUaProgress(guId, sId, rmbProgress, ActivityEnum.HOLIDAY_PER_DAY_ACC_R);
        this.activityService.handleUaProgress(guId, sId, rmbProgress, ActivityEnum.HOLIDAY_ACC_R_2);
        this.activityService.handleUaProgress(guId, sId, rmbProgress, ActivityEnum.HOLIDAY_DAY_ACC_R);
        this.activityService.handleUaProgress(guId, sId, rmbProgress, ActivityEnum.HOLIDAY_DAY_ACC_R_1_51);
        this.activityService.handleUaProgress(guId, sId, rmbProgress, ActivityEnum.HOLIDAY_DAY_ACC_R_1_52);
        this.activityService.handleUaProgress(guId, sId, rmbProgress, ActivityEnum.HOLIDAY_DAY_ACC_R2);
        handlePerDayAccR10(guId, sId, rmbProgress, ActivityEnum.HOLIDAY_PER_ACC_R_10);
        handlePerDayAccR10(guId, sId, rmbProgress, ActivityEnum.HOLIDAY_PER_ACC_R_10_51);
        handlePerDayAccR10(guId, sId, rmbProgress, ActivityEnum.HOLIDAY_PER_ACC_R_10_52);
        combinedServicePerDayAccR10Processor.handleCombinedServicePerDayAccR10(guId, sId, rmbProgress);
        // ????????????
        if (isTodayFirstReceipt) {
            this.activityService.handleUaProgress(guId, sId, 1, ActivityEnum.ACC_R_DAYS_7);
            // ??????????????????
            holidayDayPayDoubleProcessor.firstGoldPayDouble(guId, product, userReceipt);
        }
        //????????????
        if (isFirstDiamondPackReceipt) {
            holidayDayPayDoubleProcessor.diamondBenefits(guId, product, userReceipt);
        }

        if (product.isYueKa()) {
            this.activityService.handleUaProgress(guId, sId, 1, ActivityEnum.HERO_BACK_RECHARGE);
        }
        if (product.isRechargeSign()) {
            this.activityService.handleUaProgress(guId, sId, 1, ActivityEnum.RECHARGE_SIGN);
        }
        if (FIRST_RECHARGE.contains(product.getId())) {
            // ??????
            firstRechargeProcessor.rechargeItem(userReceipt);
            this.activityService.handleUaProgress(guId, sId, rmbProgress, ActivityEnum.FIRST_R);
        }
        if (NIGHTMARE_FIRST_RECHARGE.contains(product.getId())) {
            //??????????????????
            nightmareFirstRechargeProcessor.rechargeItem(userReceipt);
            this.activityService.handleUaProgress(guId, sId, rmbProgress, ActivityEnum.NIGHTMARE_FIRST_R);
        }
        if (HOLIDAY_GIFT_PACK_51.contains(product.getId())) {
            //??????????????????
            this.activityService.handleUaProgress(guId, sId, rmbProgress, ActivityEnum.HOLIDAY_GIFT_PACK_51);
        }
    }

    /**
     * ?????????????????????????????????????????????????????????
     *
     * @param event
     */
    @EventListener
    public void deductGold(GoldDeductEvent event) {
        EPGoldDeduct ep = event.getEP();
        WayEnum way = ep.getWay();
        if (way == WayEnum.DJT || way == WayEnum.FLX_SG) {
            return;
        }
        long guId = ep.getGuId();
        int sId = this.gameUserService.getActiveSid(guId);
        int deductGold = ep.getDeductGold();
        this.activityService.handleUaProgress(guId, sId, deductGold, ActivityEnum.ACC_GOLD_CONSUME);
        // ????????????
        IActivity a = this.activityService.getActivity(sId, ActivityEnum.GOLD_CONSUME);
        if (a == null) {
            return;
        }
        UserTreasure ut = this.userTreasureService.getUserTreasure(guId, TreasureEnum.GOLD_CONSUME_POINT.getValue());
        if (ut != null && !DateUtil.isBetweenIn(ut.getLastGetTime(), a.gainBegin(), a.gainEnd())) {
            // ????????????
            userTreasureService.doDelTreasure(ut);
        }
        TreasureEventPublisher.pubTAddEvent(guId, TreasureEnum.GOLD_CONSUME_POINT.getValue(), deductGold, way, new RDCommon());
        this.activityService.handleUaProgress(guId, sId, deductGold, ActivityEnum.GOLD_CONSUME);
        this.activityService.handleUaProgress(guId, sId, deductGold, ActivityEnum.PER_DAY_GOLD_CONSUME);
    }

    @EventListener
    @Order(0)
    public void addUserCity(UserCityAddEvent event) {
        EventParam<EPCityAdd> ep = (EventParam<EPCityAdd>) event.getSource();
        long guId = ep.getGuId();
        int sId = this.gameUserService.getActiveSid(guId);
        CfgCityEntity city = CityTool.getCityById(ep.getValue().getCityId());
        this.gongCLDProcessor.handleAttackProgress(guId, sId, city);
        this.multipleRebateProcessor.handleAttackProgress(guId, sId, city);
    }

    /**
     * ???????????????????????????????????????
     *
     * @param event
     */
    @EventListener
    @Order(1)
    public void addCopper(CopperAddEvent event) {
        EPCopperAdd ep = event.getEP();
        WayEnum way = ep.getWay();
        long guId = ep.getGuId();
        int sId = this.gameUserService.getActiveSid(guId);
        // ??????!!!??????switch????????????????????????
        switch (way) {
            case FIGHT_ATTACK:
            case FIGHT_TRAINING:
            case FIGHT_PROMOTE:
            case YG_OPEN_BOX:
            case YG_ELITE_OPEN_BOX:
                // ??????????????????
                // ??????????????????????????????
                // ?????????????????????????????????????????????
                if (this.activityService.isActive(sId, ActivityEnum.DOUBLE_FIGHT_COPPER)) {
                    long normalCopper = ep.gainCopper(ResWayType.Normal);
                    ep.addCopper(ResWayType.Activity, normalCopper);
                    ep.setWeekCopper(ep.getWeekCopper() + normalCopper);
                }
                return;
            default:
                return;
        }
    }

    /**
     * ?????????????????????????????????
     *
     * @param event
     */
    @EventListener
    @Order(1)
    public void addExp(ExpAddEvent event) {
        EPExpAdd ep = event.getEP();
        WayEnum way = ep.getWay();
        long guId = ep.getGuId();
        int sId = this.gameUserService.getActiveSid(guId);
        switch (way) {
            case FIGHT_ATTACK:
            case FIGHT_TRAINING:
            case FIGHT_PROMOTE:
            case FIGHT_YG:
                // ??????????????????
                long addedExp = ep.gainNormalExp();
                // ?????????????????????????????????
                if (this.activityService.isActive(sId, ActivityEnum.DOUBLE_FIGHT_EXP)
                        && this.newerGuideService.isPassNewerGuide(ep.getGuId())) {
                    ep.addExp(ResWayType.Activity, addedExp);
                }
                return;
            default:
                return;
        }
    }

    /**
     * ?????????????????????
     *
     * @param event
     */
    @EventListener
    public void drawEnd(DrawEndEvent event) {
        EventParam<EPDraw> ep = (EventParam<EPDraw>) event.getSource();
        long guId = ep.getGuId();
        EPDraw epDraw = ep.getValue();
        Integer cardPoolType = epDraw.getCardPoolType();
        if (CardPoolEnum.JUX_CP.getValue() == cardPoolType) {
            return;
        }
        int sId = this.gameUserService.getActiveSid(guId);
        this.activityService.handleUaProgress(guId, sId, epDraw.getDrawTimes(), ActivityEnum.DRAW_CARD_TH);
    }

    /**
     * ???????????????????????????
     *
     * @param uid
     * @return
     */
    public Date getFirstRechargeDate(long uid) {
        // ????????????
        IActivity a = this.activityService.getActivity(this.gameUserService.getActiveSid(uid), ActivityEnum.FIRST_R);
        if (a == null) {
            return null;
        }
        List<UserReceipt> receipts = this.receiptService.getAllReceipts(uid);
        // ???????????????????????????????????????
        Optional<UserReceipt> chongzhi = receipts.stream().filter(r -> r.getDeliveryTime().after(a.gainBegin())).findFirst();
        if (chongzhi.isPresent()) {

            return chongzhi.get().getDeliveryTime();
        }
        return null;
    }

    /**
     * ????????????
     *
     * @param uid
     * @return
     */
    public boolean isFirstRecharge(long uid) {
        // ????????????
        IActivity a = this.activityService.getActivity(this.gameUserService.getActiveSid(uid), ActivityEnum.FIRST_R);
        if (a == null) {
            return false;
        }
        List<UserReceipt> receipts = this.receiptService.getAllReceipts(uid);
        long rechareNumInActivity = receipts.stream().filter(r -> r.getDeliveryTime().after(a.gainBegin())).count();
        return rechareNumInActivity == 1;
    }

    public void handlePerDayAccR10(long uid, int sid, int progress, ActivityEnum activityEnum) {
        IActivity a = this.activityService.getActivity(sid, activityEnum);
        if (null == a) {
            return;
        }
        //????????????????????????????????????
        CfgActivityEntity cfgActivityEntity = ActivityTool.getActivityByType(activityEnum);
        if (null == cfgActivityEntity) {
            return;
        }
        //??????id
        int activityId = cfgActivityEntity.getId();
        UserActivity userActivity = activityService.getUserActivity(uid, a.gainId(), activityId);
        if (null == userActivity) {
            userActivity = UserActivity.fromActivity(uid, a.gainId(), 0, ActivityTool.getActivity(activityId));
            activityService.addUserActivity(uid, userActivity);
        }
        // ??????10??????????????????100???
        if (userActivity.getProgress() >= AWARD_AMOUNT_LIMIT) {
            return;
        }
        RechargeStatistic statistic = rechargeStatisticService.fromRedis(uid, DateUtil.getTodayInt());
        Integer todayRecharge = statistic.getToday();
        int afterProgress = Math.min(todayRecharge, AWARD_AMOUNT_LIMIT);
        boolean isUpdateStatus = afterProgress / cfgActivityEntity.getNeedValue() > userActivity.getProgress() / cfgActivityEntity.getNeedValue();
        int status = isUpdateStatus ? AwardStatus.ENABLE_AWARD.getValue() : AwardStatus.UNAWARD.getValue();
        userActivity.setStatus(status);
        // ???????????????????????????
        if (AwardStatus.UNAWARD.getValue() == userActivity.getStatus()
                && userActivity.getProgress() == 0 && todayRecharge >= cfgActivityEntity.getNeedValue()) {
            log.error("????????????{}??????uid={}???sid={}", progress, uid, sid);
            userActivity.setStatus(AwardStatus.ENABLE_AWARD.getValue());
        }
        gameUserService.updateItem(userActivity);
    }
}