package com.bbw.god.activity;

import com.bbw.common.ListUtil;
import com.bbw.common.Rst;
import com.bbw.common.StrUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.activity.holiday.processor.*;
import com.bbw.god.activity.holiday.processor.HolidayGroceryShop.HolidayGroceryShopProcessor;
import com.bbw.god.activity.holiday.processor.HolidayGroceryShop.rd.RdGrandPrixAward;
import com.bbw.god.activity.holiday.processor.HolidayGroceryShop.rd.RdGroceryShop;
import com.bbw.god.activity.holiday.processor.holidaybrocadegift.HolidayBrocadeGiftProcessor;
import com.bbw.god.activity.holiday.processor.holidaycelebration.HolidayCelebrationProcessor;
import com.bbw.god.activity.holiday.processor.holidaychinesezodiaccollision.HolidayChineseZodiacConllisionProcessor;
import com.bbw.god.activity.holiday.processor.holidaychristmaswish.HolidayChristmasWishProcessor;
import com.bbw.god.activity.holiday.processor.holidaycutetugermarket.HolidayCuteTigerMarketProcessor;
import com.bbw.god.activity.holiday.processor.holidayhalloweenRestaurant.HolidayHalloweenRestaurantProcessor;
import com.bbw.god.activity.holiday.processor.holidayhalloweenRestaurant.RdHalloweenRestaurantOrderInfo;
import com.bbw.god.activity.holiday.processor.holidaymagicwitch.HolidayMagicWitchProcessor;
import com.bbw.god.activity.holiday.processor.holidaymagicwitch.RDHolidayMagicWitch;
import com.bbw.god.activity.holiday.processor.holidayprayerskylantern.HolidayPrayerSkyLanternProcessor;
import com.bbw.god.activity.holiday.processor.holidayspecialcity.HolidayBuGeiTangProcessor;
import com.bbw.god.activity.holiday.processor.holidayspecialcity.HolidayKoiPrayProcessor;
import com.bbw.god.activity.holiday.processor.holidayspecialcity.HolidayLaborGloriousProcessor;
import com.bbw.god.activity.holiday.processor.holidayspecialcity.HolidayTreatOrTrickProcessor;
import com.bbw.god.activity.holiday.processor.holidaythankflowerlanguage.RdFlowerpotInfos;
import com.bbw.god.activity.holiday.processor.holidaythankflowerlanguage.ThankFlowerLanguageProcessor;
import com.bbw.god.activity.holiday.processor.holidaytreasuretrove.HolidayTreasureTroveProcessor;
import com.bbw.god.activity.holiday.processor.holidaytreasuretrovemap.HolidayTreasureTroveMapProcessor;
import com.bbw.god.activity.holiday.processor.holidaytreasuretrovemap.RDTreasureTroveMapAward;
import com.bbw.god.activity.processor.CombinedServiceDiscountChangeProcessor;
import com.bbw.god.activity.rd.*;
import com.bbw.god.activity.worldcup.processor.Droiyan8Processor;
import com.bbw.god.activity.worldcup.processor.ProphetProcessor;
import com.bbw.god.activity.worldcup.processor.QuizKingProcessor;
import com.bbw.god.activity.worldcup.processor.Super16Processor;
import com.bbw.god.activity.worldcup.rd.RdDayDateQuizKing;
import com.bbw.god.activity.worldcup.rd.RdDivideGroup;
import com.bbw.god.controller.AbstractController;
import com.bbw.god.exchange.XingJBKExchangeService;
import com.bbw.god.game.CR;
import com.bbw.god.gameuser.task.TaskTypeEnum;
import com.bbw.god.gameuser.task.timelimit.TimeLimitFightTaskService;
import com.bbw.god.gameuser.task.timelimit.newyearandchrist.NewYearAndChristTaskProcessor;
import com.bbw.god.gameuser.task.timelimit.qingming.QingMingTaskProcessor;
import com.bbw.god.gameuser.task.timelimit.springfestival.SpringFestivalTaskProcessor;
import com.bbw.god.login.LoginPlayer;
import com.bbw.god.rd.RDCommon;
import com.bbw.god.rd.RDSuccess;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ??????????????????????????????
 *
 * @author suhq
 * @date 2019???3???2??? ??????4:19:19
 */
@RestController
public class ActivityCtrl extends AbstractController {
    @Autowired
    private ActivityLogic activityLogic;
    @Autowired
    private XingJBKExchangeService xingJBKExchangeService;
    @Autowired
    private HolidayHorseRacingProcessor horseRacingProcessor;
    @Autowired
    private HolidayBuGeiTangProcessor holidayBuGeiTangProcessor;
    @Autowired
    private HolidayGratefulProcessor holidayGratefulProcessor;
    @Autowired
    private HolidayDiscountChangeProcessor holidayDiscountChangeProcessor;
    @Autowired
    private CombinedServiceDiscountChangeProcessor combinedServiceDiscountChangeProcessor;
    @Autowired
    private HolidayCookingFoodProcessor holidayCookingFoodProcessor;
    @Autowired
    private HolidayBuildingAltarProcessor holidayBuildingAltarProcessor;
    @Autowired
    private NewYearAndChristTaskProcessor newYearAndChristTaskProcessor;
    @Autowired
    private HolidayCelebrationProcessor holidayCelebrationProcessor;
    @Autowired
    private HolidayTreasureTroveProcessor holidaydsTreasureSecretProcessor;
    @Autowired
    private SpringFestivalTaskProcessor springFestivalTaskProcessor;
    @Autowired
    private TimeLimitFightTaskService timeLimitFightTaskService;
    @Autowired
    private HolidayTreasureTroveMapProcessor holidayTreasureTroveMapProcessor;
    @Autowired
    private HolidayThoughtsOfFlowersProcessor holidayThoughtsOfFlowersProcessor;
    @Autowired
    private HolidayPrayerSkyLanternProcessor holidayPrayerSkyLanternProcessor;
    @Autowired
    private HolidayBrocadeGiftProcessor holidayLanternGiftsProcessor;
    @Autowired
    private HolidayCuteTigerMarketProcessor holidayCuteTigerMarketProcessor;
    @Autowired
    private QingMingTaskProcessor qingMingTaskProcessorl;
    @Autowired
    private HolidayLaborGloriousProcessor holidayLaborGloriousProcessor;
    @Autowired
    private HolidayKoiPrayProcessor holidayKoiPrayProcessor;
    @Autowired
    private HolidayTreatOrTrickProcessor holidayTreatOrTrickProcessor;
    @Autowired
    private HolidayHalloweenRestaurantProcessor holidayHalloweenRestaurantProcessor;
    @Autowired
    private Super16Processor super16Processor;
    @Autowired
    private Droiyan8Processor droiyan8Processor;
    @Autowired
    private ProphetProcessor prophetProcessor;
    @Autowired
    private QuizKingProcessor quizKingProcessor;
    @Autowired
    private ThankFlowerLanguageProcessor thankFlowerLanguageProcessor;
    @Autowired
    private HolidayGroceryShopProcessor holidayGroceryShopProcessor;
    @Autowired
    private HolidayMagicWitchProcessor holidayMagicWitchProcessor;
    @Autowired
    private HolidayChristmasWishProcessor holidayChristmasWishProcessor;
    @Autowired
    private HolidayChineseZodiacConllisionProcessor holidayChineseZodiacConllisionProcessor;
    @Autowired
    private HolidayHappyTouchCupProcessor holidayHappyTouchCupProcessor;

    private static final String UNDEFINED = "undefined";

    /**
     * ?????????????????????
     *
     * @param type
     * @param kind
     * @return
     */
    @GetMapping(CR.Activity.GET_ACTIVITIES)
    public RDSuccess getActivities(String type, String kind) {
        if (UNDEFINED.equals(type) || UNDEFINED.equals(kind)) {
            throw ExceptionForClientTip.fromMsg("???????????????");
        }
        return this.activityLogic.getActivities(this.getUserId(), this.getServerId(), type, kind);
    }

    @GetMapping(CR.Activity.GET_ACTIVITIES_V2)
    public RDSuccess getActivityItems(String type, String kind) {
        if (UNDEFINED.equals(type) || UNDEFINED.equals(kind)) {
            throw ExceptionForClientTip.fromMsg("???????????????");
        }
        RDSuccess rd = this.activityLogic.getActivities(this.getUserId(), this.getServerId(), type, kind);
        if (type != null && !type.equals("0") && rd instanceof RDActivityList) {
            RDActivityList rdActivityList = (RDActivityList) rd;
            if (ListUtil.isEmpty(rdActivityList.getItems())){
                return rd;
            }
            return rdActivityList;
        }
        return rd;
    }

    /**
     * ?????????????????????????????????????????????
     *
     * @param id
     * @param awardIndex ?????????1??????
     * @return
     */
    @GetMapping(CR.Activity.SET_AWARD_ITEM)
    public Rst getActivities(int id, String awardIndex) {
        int index = 0;
        if (StrUtil.isNotNull(awardIndex)) {
            index = Integer.parseInt(awardIndex);
        }
        if (this.activityLogic.setAwardItem(getUserId(), getServerId(), id, index)) {
            return Rst.businessOK();
        }
        return Rst.businessFAIL("?????????????????????");
    }

    /**
     * ????????????
     *
     * @param id ????????????ID
     * @return
     */
    @GetMapping(CR.Activity.RECEIVE_AWARD)
    public RDCommon receiveAward(int id, Integer activityType, String awardIndex) {
        int awardIndexInt = -1;
        if (awardIndex != null) {
            awardIndexInt = Integer.parseInt(awardIndex);
        }
        return this.activityLogic.joinActivity(this.getUserId(), this.getServerId(), id, activityType, awardIndexInt);
    }

    @GetMapping(CR.Activity.RECEIVE_TASK_AWARD)
    public RDCommon receiveTaskAward(int taskId) {
        return activityLogic.receiveTaskAward(getUserId(), getServerId(), taskId);
    }

    /**
     * ????????????????????????
     *
     * @param item ??????
     * @param num  ??????
     * @return
     */
    @GetMapping(CR.Activity.EXCHANGE_GOOD)
    public RDCommon exchangeGood(int item, int num) {
        LoginPlayer player = this.getUser();
        return this.xingJBKExchangeService.exchange(player.getUid(), player.getServerId(), item, num);
    }

    /**
     * ??????
     *
     * @param id
     * @return
     */
    @GetMapping(CR.Activity.REPLENISH)
    public RDCommon replenish(int id) {
        return this.activityLogic.replenish(this.getUserId(), this.getServerId(), id);
    }

    /**
     * ????????????
     *
     * @return
     */
    @GetMapping(CR.Activity.HORSE_RACING_BET)
    public RDCommon horseRacingBet(Integer number, Integer multiple) {
        return horseRacingProcessor.bet(getUserId(), number, multiple);
    }

    /**
     * ???????????????
     *
     * @param activityType
     * @return
     */
    @GetMapping(CR.Activity.ACTIVITY_OPEN_BOX)
    public RDCommon activityOpenBox(Integer activityType) {
        if (activityType == ActivityEnum.LABOR_GLORIOUS.getValue()) {
            return holidayLaborGloriousProcessor.openBox(getUserId());
        }
        if (activityType == ActivityEnum.TREAT_OR_TRICK.getValue()) {
            return holidayTreatOrTrickProcessor.openBox(getUserId());
        }
        return holidayBuGeiTangProcessor.openBox(getUserId());
    }

    /**
     * ?????????????????????????????????????????????
     *
     * @return
     */
    @ApiOperation(value = "?????????????????????????????????????????????")
    @GetMapping(CR.Activity.THANKSGIVING_INFOS)
    public RDActivityExchangeInfo getThanksGivingInfo() {
        return holidayGratefulProcessor.getActivityInfo(getUserId());
    }

    /**
     * ??????????????????????????????
     *
     * @return
     */
    @ApiOperation(value = "??????????????????????????????")
    @GetMapping(CR.Activity.MALL_DISCOUNT)
    public RDDiscount getOrRefreshDiscount() {
        return holidayDiscountChangeProcessor.getOrRefreshDiscount(getUserId());
    }

    /**
     * ????????????????????????????????????
     *
     * @return
     */
    @ApiOperation(value = "????????????????????????????????????")
    @GetMapping(CR.Activity.MALL_COMBINED_SERVICE_DISCOUNT)
    public RDDiscount getOrRefreshCombinedServiceDiscount() {
        return combinedServiceDiscountChangeProcessor.getOrRefreshDiscount(getUserId());
    }

    /**
     * ??????????????????????????????????????????
     *
     * @return
     */
    @ApiOperation(value = "??????????????????????????????????????????")
    @GetMapping(CR.Activity.GET_NPC_GRATITUDE)
    public RDActivityExchangeInfo getNpcGratitude(int npcId, int foodId, int num) {
        return holidayGratefulProcessor.getNpcGratitude(getUserId(), npcId, foodId, num);
    }

    /**
     * ????????????
     *
     * @return
     */
    @ApiOperation(value = "????????????")
    @GetMapping(CR.Activity.COOKING_FOODS)
    public RDCommon cookingFoods(int ingredientId, int seasoningId, int num) {
        return holidayCookingFoodProcessor.cookingFoods(getUserId(), ingredientId, seasoningId, num);
    }

    /**
     * ???????????????????????????
     *
     * @return
     */
    @ApiOperation(value = "???????????????????????????")
    @GetMapping(CR.Activity.USE_GRATITUDE)
    public RDActivityExchangeInfo useGratitudeExchange(int npcId) {
        return holidayGratefulProcessor.useGratitudeExchange(getUserId(), npcId);
    }

    /**
     * ??????????????????
     *
     * @return
     */
    @ApiOperation(value = "??????")
    @GetMapping(CR.Activity.DONATE)
    public RDCommon donate() {
        return holidayBuildingAltarProcessor.donate(getUserId());
    }

    /**
     * ????????????????????????
     *
     * @param dataId
     * @return
     */
    @ApiOperation(value = "????????????????????????")
    @GetMapping(CR.Activity.CONSUME_TREASURES)
    public RDCommon consumeTreasures(int taskType, long dataId) {
        if (taskType == TaskTypeEnum.NEW_YEAR_AND_CHRISTMAS_TASK.getValue()) {
            return newYearAndChristTaskProcessor.consumeTreasures(getUserId(), dataId);
        }
        return springFestivalTaskProcessor.consumeTreasures(getUserId(), dataId);
    }

    @ApiOperation(value = "????????????????????????")
    @GetMapping(CR.Activity.RECEIVE_CELEBRATION_AWARD)
    public RDCommon receiveCelebrationAward(int taskId) {
        return holidayCelebrationProcessor.receiveCelebrationAward(getUserId(), getServerId(), taskId);
    }

    /**
     * ????????????????????????
     *
     * @param cards
     * @param taskType ??????taskType??????
     * @return
     */
    @ApiOperation(value = "????????????????????????")
    @GetMapping(CR.Activity.CUNZ_YIYUN_CARD_GROUPING)
    public RDSuccess setCardGrouping(String cards, int taskType) {
        if (taskType == TaskTypeEnum.QING_MING_TASK.getValue()){
            return qingMingTaskProcessorl.editUserTimeLimitFightCards(getUserId(), cards);
        }
        return springFestivalTaskProcessor.editUserTimeLimitFightCards(getUserId(), cards);
    }

    /**
     * ????????????????????????
     *
     * @param taskType ??????taskType??????
     * @param dataId
     * @return
     */
    @ApiOperation(value = "????????????????????????")
    @GetMapping(CR.Activity.CUNZ_YIYUN_REFRESH_CARD_LIBRARY)
    public RDCommon RefreshCardLibrary(int taskType, long dataId) {
        if (taskType == TaskTypeEnum.QING_MING_TASK.getValue()){
            return qingMingTaskProcessorl.useColdRefreshCardLibrary(getUserId(), dataId);
        }
        return springFestivalTaskProcessor.useColdRefreshCardLibrary(getUserId(), dataId);
    }

    /**
     * ????????????
     *
     * @param flowersNum
     * @param recipientId
     * @param message
     * @return
     */
    @ApiOperation(value = "????????????")
    @GetMapping(CR.Activity.SEND_FLOWERS)
    public RDCommon sendFlowers(Integer type, int flowersNum, long recipientId, String message) {
        if (ActivityEnum.HAPPY_TOUCH_CUP.getValue() == type) {
            return holidayHappyTouchCupProcessor.sendCarefreeBrewing(getUserId(), flowersNum, recipientId, message);
        }
        return holidayThoughtsOfFlowersProcessor.sendFlowers(getUserId(), flowersNum, recipientId, message);
    }

    /**
     * ????????????
     *
     * @param nickName
     * @return
     */
    @ApiOperation(value = "????????????")
    @GetMapping(CR.Activity.SEARCH_USER_INFO)
    public RDUserListInfos sendFlowers(Integer type, String nickName) {
        if (ActivityEnum.HAPPY_TOUCH_CUP.getValue() == type) {
            return holidayHappyTouchCupProcessor.searchUserInfo(nickName);
        }
        return holidayThoughtsOfFlowersProcessor.searchUserInfo(nickName);
    }

    /**
     * ????????????
     *
     * @param skyLanternNum
     * @param message
     * @return
     */
    @ApiOperation(value = "????????????")
    @GetMapping(CR.Activity.PUT_SKY_LANTERN)
    public RDCommon sendFlowers(int skyLanternNum, String message) {
        return holidayPrayerSkyLanternProcessor.putSkyLantern(getUserId(), skyLanternNum, message);
    }

    /**
     * ??????
     *
     * @param type
     * @return
     */
    @ApiOperation(value = "??????")
    @GetMapping(CR.Activity.LANTERN_BET)
    public RDCommon lanternBet(int type) {
        return holidayLanternGiftsProcessor.bet(getUserId(), type);
    }

    /**
     * ??????????????????
     *
     * @param treasureTroveMapLevel
     * @return
     */
    @GetMapping(CR.Activity.REFRESH_FLOP_CHALLENGE)
    public RDCommon refreshFlopChallenge(Integer treasureTroveMapLevel) {
        return holidayTreasureTroveMapProcessor.refreshFlopChallenge(getUserId(), treasureTroveMapLevel);
    }

    /**
     * ??????
     *
     * @param flopIndex
     * @return
     */
    @GetMapping(CR.Activity.FLOP)
    public RDCommon flop(Integer flopIndex) {
        return holidayTreasureTroveMapProcessor.flop(getUserId(), flopIndex);
    }

    /**
     * ??????????????????
     *
     * @param connectionAwardId
     * @return
     */
    @GetMapping(CR.Activity.RECEIVE_CONNECTION_AWARDS)
    public RDCommon receiveConnectionAwards(Integer connectionAwardId) {
        return holidayTreasureTroveMapProcessor.receiveConnectionAwards(getUserId(), connectionAwardId);
    }

    /**
     * ????????????????????????
     *
     * @return
     */
    @GetMapping(CR.Activity.RECEIVE_FLOP_TARGET_AWARDS)
    public RDCommon receiveFlopTargetAwards(Integer targetId) {
        return holidayTreasureTroveMapProcessor.receiveFlopTargetAwards(getUserId(), targetId);
    }

    /**
     * ?????????????????????????????????
     *
     * @return
     */
    @GetMapping(CR.Activity.GET_TREASURE_TROVE_MAP_TO_SHOW)
    public RDTreasureTroveMapAward getTreasureTroveMapAwardToShow(Integer treasureTroveMapLevel) {
        return holidayTreasureTroveMapProcessor.getTreasureTroveMapAwardToShow(getUserId(), treasureTroveMapLevel);
    }

    /**
     * ?????????????????????
     *
     * @return
     */
    @GetMapping(CR.Activity.RECEIVE_TREASURE_TROVE_MAP_AWARDS)
    public RDCommon receiveTreasureTroveMapAwards(Integer treasureTroveMapLevel) {
        return holidayTreasureTroveMapProcessor.receiveTreasureTroveMapAwards(getUserId(), treasureTroveMapLevel);
    }


    @ApiOperation(value = "????????????")
    @GetMapping(CR.Activity.CUTE_TIGER_MARKET_PASTRY_SOLD)
    public RDCommon sellPastries(int pastryId) {
        return holidayCuteTigerMarketProcessor.sellPastries(getUserId(), pastryId);
    }

    @ApiOperation(value = "??????????????????")
    @GetMapping(CR.Activity.ENTER_LITTLE_TIGER_STORE)
    public RDLittleTigerStoreInfo enterLittleTigerStore() {
        return holidayCuteTigerMarketProcessor.enterLittleTigerStore(getUserId());
    }

    @ApiOperation(value = "????????????????????????")
    @GetMapping(CR.Activity.LITTLE_TIGER_STORE_REFRESH)
    public RDCommon refreshAwards(int number) {
        return holidayCuteTigerMarketProcessor.refreshAwards(getUserId(), number);
    }

    @ApiOperation(value = "????????????")
    @GetMapping(CR.Activity.LITTLE_TIGER_STORE_REPLACE)
    public RDSuccess refreshAwards(int number, int awardId, int item, int num) {
        return holidayCuteTigerMarketProcessor.replaceAward(getUserId(), number, awardId, item, num);
    }

    @ApiOperation(value = "????????????????????????")
    @GetMapping(CR.Activity.SEND_TOTAL_REFRESH_AWARDS)
    public RDCommon sendGrandTotalAward(int refreshTimes) {
        return holidayCuteTigerMarketProcessor.sendGrandTotalAward(getUserId(), refreshTimes);
    }

    @ApiOperation(value = "????????????????????????")
    @GetMapping(CR.Activity.RECEIVE_LITTLE_TIGER_STORE_AWARDS)
    public RDCommon receiveAward(int number) {
        return holidayCuteTigerMarketProcessor.receiveAward(getUserId(), number);
    }

    @ApiOperation(value = "????????????")
    @GetMapping(CR.Activity.KOI_PRAY)
    public RDCommon pray(String koiStr) {
        return holidayKoiPrayProcessor.pray(getUserId(), koiStr);
    }

    @ApiOperation(value = "??????????????????")
    @GetMapping(CR.Activity.HALLOWEEN_RESTAURANT_COMPOUND)
    public RDCommon compound(String foodPos) {
        return holidayHalloweenRestaurantProcessor.compound(getUserId(), foodPos);
    }

    @ApiOperation(value = "????????????????????????")
    @GetMapping(CR.Activity.HALLOWEEN_RESTAURANT_UNLOCK_PLATE)
    public RDCommon unlockPlate(Integer foodPos) {
        return holidayHalloweenRestaurantProcessor.unlockPlate(getUserId(), foodPos);
    }

    @ApiOperation(value = "??????????????????????????????")
    @GetMapping(CR.Activity.GET_HALLOWEEN_RESTAURANT_ORDER_INFO)
    public RdHalloweenRestaurantOrderInfo getOrderInfos() {
        return holidayHalloweenRestaurantProcessor.getOrderInfos(getUserId());
    }

    @ApiOperation(value = "????????????????????????")
    @GetMapping(CR.Activity.HALLOWEEN_RESTAURANT_ACCEPT_ORDER)
    public RDSuccess acceptOrder(long orderId) {
        return holidayHalloweenRestaurantProcessor.acceptOrder(getUserId(), orderId);
    }

    @ApiOperation(value = "????????????????????????")
    @GetMapping(CR.Activity.HALLOWEEN_RESTAURANT_COMPLETE_ORDER)
    public RDCommon completeOrder(long orderId, String foodPos) {
        return holidayHalloweenRestaurantProcessor.completeOrder(getUserId(), orderId, foodPos);
    }

    @ApiOperation(value = "??????????????????????????????")
    @GetMapping(CR.Activity.RECEIVE_HALLOWEEN_RESTAURANT_OFFLINE_REVENUE)
    public RDCommon receiveOfflineRevenue() {
        return holidayHalloweenRestaurantProcessor.receiveOfflineRevenue(getUserId());
    }

    @ApiOperation(value = "????????????????????????")
    @GetMapping(CR.Activity.HALLOWEEN_RESTAURANT_SELL_FOOD)
    public RDCommon sellFood(int mallId, int buyNum, int foodPos) {
        return holidayHalloweenRestaurantProcessor.sellFood(getUserId(), mallId, buyNum, foodPos);
    }

    @ApiOperation(value = "????????????????????????")
    @GetMapping(CR.Activity.HALLOWEEN_RESTAURANT_BUY_FOOD)
    public RDCommon buyFood(int mallId, int buyNum) {
        return holidayHalloweenRestaurantProcessor.buyFood(getUserId(), mallId, buyNum);
    }

    @ApiOperation(value = "??????????????????????????????")
    @GetMapping(CR.Activity.HALLOWEEN_RESTAURANT_SWAP_FOOD_POS)
    public RDSuccess swapFoodPos(String foodPos) {
        return holidayHalloweenRestaurantProcessor.swapFoodPos(getUserId(), foodPos);
    }
    @ApiOperation(value = "???????????????16?????????")
    @GetMapping(CR.Activity.WORLD_CUP_SUPER16_BET)
    public RDCommon worldCupSuper16Bet(String group,String betCountry) {
        return super16Processor.super16Bet(getUserId(),group,betCountry);
    }

    @ApiOperation(value = "???????????????16???????????????")
    @GetMapping(CR.Activity.WORLD_CUP_SUPER16_DIVIDE_GROUP)
    public RdDivideGroup worldCupSuper16DivideGroup(String group) {
        return super16Processor.super16DivideGroup(getUserId(),group);
    }

    @ApiOperation(value = "???????????????8?????????")
    @GetMapping(CR.Activity.WORLD_CUP_DROIYAN8P_BET)
    public RDCommon worldCupDroiyan8PBet(String id,String betCountry) {
        return droiyan8Processor.droiyan8Bet(getUserId(),id,betCountry);
    }
    @ApiOperation(value = "???????????????8?????????????????????")
    @GetMapping(CR.Activity.WORLD_CUP_DROIYAN8P_BET_DIVIDE_GROUP)
    public RdDivideGroup worldCupDroiyan8PBet(String id) {
        return droiyan8Processor.droiyan8DivideGroup(getUserId(),id);
    }

    @ApiOperation(value = "??????????????????????????????")
    @GetMapping(CR.Activity.WORLD_CUP_PROPHET_BET)
    public RDCommon worldCupProphetBet(String identAndBetCountry) {
       return  prophetProcessor.prophetBet(getUserId(),identAndBetCountry);
    }

    @ApiOperation(value = "??????????????????????????????")
    @GetMapping(CR.Activity.WORLD_CUP_QUIZ_KING_BET)
    public RDCommon worldCupQuizKingBet(String id, String betCountryAndNums) {
        return quizKingProcessor.quizKingBet(getUserId(), id, betCountryAndNums);
    }

    @ApiOperation(value = "????????????????????????????????????????????????")
    @GetMapping(CR.Activity.world_Cup_Quiz_King_Day_Date)
    public RdDayDateQuizKing quizKingDayDate(String dayDate) {
        return quizKingProcessor.quizKingDayDate(getUserId(), dayDate);
    }

    @ApiOperation(value = "??????????????????")
    @GetMapping(CR.Activity.THANK_FLOWER_LANGUAGE_PLANT)
    public RdFlowerpotInfos plant(Integer flowerpotId, Integer seedBagId) {
        return thankFlowerLanguageProcessor.plant(getUserId(), flowerpotId, seedBagId);
    }

    @ApiOperation(value = "????????????????????????")
    @GetMapping(CR.Activity.THANK_FLOWER_LANGUAGE_ONE_CLICK_PLANT)
    public RdFlowerpotInfos oneClickPlant() {
        return thankFlowerLanguageProcessor.oneClickPlant(getUserId());
    }

    @ApiOperation(value = "??????????????????")
    @GetMapping(CR.Activity.THANK_FLOWER_LANGUAGE_APPLY_FERTILIZER)
    public RdFlowerpotInfos applyFertilizer(Integer flowerpotId, Integer fertilizerId) {
        return thankFlowerLanguageProcessor.applyFertilizer(getUserId(), flowerpotId, fertilizerId);
    }

    @ApiOperation(value = "????????????????????????")
    @GetMapping(CR.Activity.THANK_FLOWER_LANGUAGE_ONE_CLICK_APPLY_FERTILIZER)
    public RdFlowerpotInfos oneClickApplyFertilizer() {
        return thankFlowerLanguageProcessor.oneClickApplyFertilizer(getUserId());
    }

    @ApiOperation(value = "??????????????????")
    @GetMapping(CR.Activity.THANK_FLOWER_LANGUAGE_PICK)
    public RdFlowerpotInfos pick(Integer flowerpotId) {
        return thankFlowerLanguageProcessor.pick(getUserId(), flowerpotId);
    }

    @ApiOperation(value = "????????????????????????")
    @GetMapping(CR.Activity.THANK_FLOWER_LANGUAGE_ONE_CLICK_PICK)
    public RdFlowerpotInfos oneClickPick() {
        return thankFlowerLanguageProcessor.oneClickPick(getUserId());
    }

    @ApiOperation(value = "????????????????????????")
    @GetMapping(CR.Activity.THANK_FLOWER_LANGUAGE_MAKE_BOUQUET)
    public RDCommon makeBouquet(String folwerInfos, Integer flowerNum) {
        return thankFlowerLanguageProcessor.makeBouquet(getUserId(), folwerInfos, flowerNum);
    }
    @ApiOperation(value = "????????????????????????")
    @GetMapping(CR.Activity.CHOICE_GRAND_PRIX)
    public RdGroceryShop choiceGrandPrix(Integer treasureId){
        return holidayGroceryShopProcessor.choiceGrandPrix(getUserId(),treasureId);
    }
    @ApiOperation(value = "????????????????????????")
    @GetMapping(CR.Activity.BUT_BLIND_BOX)
    public RDCommon butBlindBox(String positions){
        return holidayGroceryShopProcessor.butBlindBox(getUserId(),positions);
    }
    @ApiOperation(value = "????????????????????????")
    @GetMapping(CR.Activity.GET_GRAND_PRIX_AWARD)
    public RdGrandPrixAward getGrandPrixAward(){
        return holidayGroceryShopProcessor.getGrandPrixAward(getUserId());
    }
    @ApiOperation(value = "????????????????????????")
    @GetMapping(CR.Activity.RESE_LECT_GRAND_PRIX)
    public RDSuccess reselectGrandPrix(){
        return holidayGroceryShopProcessor.reselectGrandPrix(getUserId());

    }

    @ApiOperation(value = "????????????????????????")
    @GetMapping(CR.Activity.GET_HOLIDAY_REFIN_INFO)
    public RDHolidayMagicWitch getHolidayRefinInfo() {
        return holidayMagicWitchProcessor.getHolidayRefinInfo(getUserId());
    }

    @ApiOperation(value = "??????????????????")
    @GetMapping(CR.Activity.REFIN)
    public RDCommon refin(String magicMaterial) {
        return holidayMagicWitchProcessor.refin(getUserId(), magicMaterial);
    }

    @ApiOperation(value = "????????????????????????")
    @GetMapping(CR.Activity.COMPLETE_WISH)
    public RDCommon completeWish(Integer wishGift, long wishId) {
        return holidayChristmasWishProcessor.completeWish(getUserId(), wishGift, wishId);
    }

    @ApiOperation(value = "??????????????????")
    @GetMapping(CR.Activity.REFRESH_CHINESE_ZODIAC_MAP)
    public RDCommon refreshChineseZodiacMap(Integer mapLevel) {
        return holidayChineseZodiacConllisionProcessor.refreshChineseZodiacMap(getUserId(), mapLevel);
    }

    @ApiOperation(value = "????????????")
    @GetMapping(CR.Activity.CHINESE_ZODIAC_FLIP)
    public RDCommon chineseZodiacFlip(int index) {
        return holidayChineseZodiacConllisionProcessor.chineseZodiacFlip(getUserId(), index);
    }
}
