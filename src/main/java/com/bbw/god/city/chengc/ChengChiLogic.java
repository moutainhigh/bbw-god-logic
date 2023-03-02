package com.bbw.god.city.chengc;

import com.bbw.App;
import com.bbw.common.ListUtil;
import com.bbw.common.PowerRandom;
import com.bbw.exception.CoderException;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.activity.ActivityService;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.activity.holiday.processor.HolidayCookingFoodProcessor;
import com.bbw.god.activity.holiday.processor.HolidayDigForTreasureProcessor;
import com.bbw.god.activity.holiday.processor.HolidayLotteryProcessor;
import com.bbw.god.activity.holiday.processor.holidaycutetugermarket.HolidayCuteTigerMarketProcessor;
import com.bbw.god.activity.holiday.processor.holidayspecialcity.HolidayKoiPrayProcessor;
import com.bbw.god.activity.holiday.processor.holidayspecialcity.HolidayLaborGloriousProcessor;
import com.bbw.god.activity.holiday.processor.holidayspecialcity.HolidaySkyLanternWorkShopProcessor;
import com.bbw.god.activity.holiday.processor.holidayspecialcity.HolidayWZJZProcessor;
import com.bbw.god.cache.TimeLimitCacheUtil;
import com.bbw.god.city.CityChecker;
import com.bbw.god.city.UserCityService;
import com.bbw.god.city.chengc.in.RDBuildingInfo;
import com.bbw.god.city.chengc.in.RDCityInInfo;
import com.bbw.god.city.chengc.in.building.BuildingFactory;
import com.bbw.god.city.chengc.trade.BuyGoodInfo;
import com.bbw.god.city.chengc.trade.IChengChiTradeService;
import com.bbw.god.city.miaoy.hexagram.HexagramBuffEnum;
import com.bbw.god.city.miaoy.hexagram.HexagramBuffService;
import com.bbw.god.city.miaoy.hexagram.event.HexagramEventPublisher;
import com.bbw.god.city.yeg.YeGProcessor;
import com.bbw.god.city.yeg.YeGuaiEnum;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardService;
import com.bbw.god.game.combat.attackstrategy.StrategyConfig;
import com.bbw.god.game.combat.attackstrategy.chengc.ChengCStrategyLogic;
import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.combat.video.RDVideo;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.city.*;
import com.bbw.god.game.config.server.ServerTool;
import com.bbw.god.game.config.special.CfgSpecialEntity;
import com.bbw.god.game.config.special.SpecialTool;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.game.transmigration.GameTransmigrationService;
import com.bbw.god.game.transmigration.TransmigrationCityRecordService;
import com.bbw.god.game.transmigration.entity.GameTransmigration;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.achievement.AchievementServiceFactory;
import com.bbw.god.gameuser.achievement.BaseAchievementService;
import com.bbw.god.gameuser.achievement.UserAchievementInfo;
import com.bbw.god.gameuser.businessgang.BusinessGangService;
import com.bbw.god.gameuser.card.CardGroupWay;
import com.bbw.god.gameuser.card.RDCardGroups;
import com.bbw.god.gameuser.chamberofcommerce.server.UserCocInfoService;
import com.bbw.god.gameuser.guide.NewerGuideService;
import com.bbw.god.gameuser.leadercard.fashion.UserLeaderFashionService;
import com.bbw.god.gameuser.res.ResChecker;
import com.bbw.god.gameuser.res.ResEventPublisher;
import com.bbw.god.gameuser.res.ResWayType;
import com.bbw.god.gameuser.res.copper.EPCopperAdd;
import com.bbw.god.gameuser.special.UserSpecial;
import com.bbw.god.gameuser.special.UserSpecialService;
import com.bbw.god.gameuser.special.event.EPSpecialDeduct;
import com.bbw.god.gameuser.special.event.EVSpecialAdd;
import com.bbw.god.gameuser.special.event.SpecialEventPublisher;
import com.bbw.god.gameuser.treasure.TreasureChecker;
import com.bbw.god.gameuser.treasure.UserTreasureEffectService;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.nightmarecity.chengc.UserNightmareCity;
import com.bbw.god.rd.RDCommon;
import com.bbw.god.server.special.GameSpecialService;
import com.bbw.god.server.special.ServerSpecialService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author：lwb
 * @date: 2020/12/21 17:14
 * @version: 1.0
 */
@Service
public class ChengChiLogic {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private UserSpecialService userSpecialService;
    @Autowired
    private ServerSpecialService serverSpecialService;
    @Autowired
    private GameSpecialService gameSpecialService;
    @Autowired
    private ActivityService activityService;
    @Autowired
    private UserTreasureEffectService userTreasureEffectService;
    @Autowired
    private UserCocInfoService userCocInfoService;
    @Autowired
    private UserCityService userCityService;
    @Autowired
    private AwardService awardService;
    @Autowired
    private YeGProcessor yeGProcessor;
    @Autowired
    private NightmareLogic nightmareLogic;
    @Autowired
    private AchievementServiceFactory achievementServiceFactory;
    @Autowired
    private HexagramBuffService hexagramBuffService;
    @Autowired
    private ChengCStrategyLogic chengCStrategyLogic;
    @Autowired
    private GameTransmigrationService gameTransmigrationService;
    @Autowired
    private TransmigrationCityRecordService transmigrationCityRecordService;
    @Autowired
    private HolidayCookingFoodProcessor holidayCookingFoodProcessor;
    @Autowired
    private BusinessGangService businessGangService;
    @Autowired
    private HolidaySkyLanternWorkShopProcessor holidaySkyLanternWorkShopProcessor;
    @Autowired
    private HolidayCuteTigerMarketProcessor holidayCuteTigerMarketProcessor;
    @Autowired
    private NewerGuideService newerGuideService;
    @Autowired
    private HolidayLotteryProcessor holidayLotteryProcessor;
    @Autowired
    private List<IChengChiTradeService> tradeServices;
    @Autowired
    private HolidayDigForTreasureProcessor holidayDigForTreasureProcess;
    @Autowired
    private HolidayWZJZProcessor holidayWZJZProcessor;
    @Autowired
    private UserLeaderFashionService userLeaderFashionService;
    @Autowired
    private HolidayKoiPrayProcessor holidayKoiPrayProcessor;
    @Autowired
    private HolidayLaborGloriousProcessor holidayLaborGloriousProcessor;

    private static final int SPECIAL_ID_MAX = 10000;
    @Autowired
    private App app;

    private static final List<String> CITY_AREA_LIST = Arrays.asList("西岐", "东鲁", "曹州", "南都", "朝歌");
    private static final int[] SHENG_YUAN_ZHU_IDS = {11440, 11450, 11460, 11470, 11480};//圣元珠Id集合
    private static final int[] achievementIds_1 = {520, 525, 530, 535, 536};
    private static final int[] achievementIds_2 = {541, 542, 543, 544, 545};
    private static final int[] nightmare_achievementIds_1 = {14740, 14750, 14760, 14770, 14780};
    private static final int[] nightmare_achievementIds_2 = {14840, 14850, 14860, 14870, 14880};
    private static final int[] nightmare_achievementIds_3 = {14790, 14800, 14810, 14820, 14830};

    /**
     * 客户端请求当前城池信息：城池id 成就相关内容 是否侦察 当前城池战斗次数 进度 是否展示攻略 攻城符数组
     *
     * @param uid
     * @return
     */
    public RDArriveChengC arriveCity(long uid) {
        GameUser gu = gameUserService.getGameUser(uid);
        if (gu.getStatus().ifInTransmigrateWord()) {
            return new RDArriveChengC();
        }
        ChengChiInfoCache cache = TimeLimitCacheUtil.getChengChiInfoCache(uid);
        RDArriveChengC rd = new RDArriveChengC();
        rd.setOwnCity(0);
        CfgCityEntity entity = gu.gainCurCity();
        int cityLv = entity.getLevel();
        if (cache.isOwnCity()) {
            rd.setOwnCity(1);
            rd.setManorInfo(getCityInInfo(uid, cache.getCityId()));
        } else if (gu.getStatus().intoNightmareWord()) {
            arriveNightmareCity(gu, cache, rd);
            rd.setShowStrategy(cityLv >= StrategyConfig.NIGHTMARE_CITY_MIN_LV ? 1 : 0);
        } else {
            arriveNormalCity(gu, cache, rd);
            rd.setShowStrategy(cityLv >= StrategyConfig.CITY_MIN_LV ? 1 : 0);
        }
        rd.setArriveCityId(entity.getId());
        rd.setInvestigated(cache.isInvestigated() ? 1 : 0);
        rd.setAttackTimes(cache.getAttackTimes());
        rd.setLevelProgress(cache.getLevelProgress());

        return rd;
    }

    private void arriveNormalCity(GameUser gu, ChengChiInfoCache cache, RDArriveChengC rd) {
        List<RDArriveChengC.AchievementInfo> achievementInfos = new ArrayList<>();
        //添加成就
        long uid = gu.getId();
        UserAchievementInfo info = gameUserService.getSingleItem(uid, UserAchievementInfo.class);
        //攻下所有城池
        BaseAchievementService attackAllCityAchievementService = achievementServiceFactory.getById(550);
        achievementInfos.add(RDArriveChengC.AchievementInfo.instance(550, attackAllCityAchievementService.getMyProgress(uid, info)));
        //攻下对应区域所有城池
        int index = cache.getArea() / 10 - 1;
        BaseAchievementService achievementService1 = achievementServiceFactory.getById(achievementIds_2[index]);
        if (!achievementService1.isAccomplished(info)) {
            achievementInfos.add(RDArriveChengC.AchievementInfo.instance(achievementIds_2[index], achievementService1.getMyProgress(uid, info)));
        } else {
            BaseAchievementService achievementService2 = achievementServiceFactory.getById(achievementIds_1[index]);
            achievementInfos.add(RDArriveChengC.AchievementInfo.instance(achievementIds_1[index], achievementService2.getMyProgress(uid, info)));
        }
        rd.setAchievementInfos(achievementInfos);
        rd.setBuffs(Arrays.asList(RunesEnum.TGCF.getRunesId()));
    }

    private void arriveNightmareCity(GameUser gu, ChengChiInfoCache cache, RDArriveChengC rd) {
        ChengC nightmareChengC = CityTool.getNightmareChengC(cache.getCityId());
        rd.setBuffs(Arrays.asList(nightmareChengC.getBuff()));
        List<RDArriveChengC.AchievementInfo> achievementInfos = new ArrayList<>();
        //添加成就
        long uid = gu.getId();
        UserAchievementInfo info = gameUserService.getSingleItem(uid, UserAchievementInfo.class);
        //攻下所有城池
        BaseAchievementService attackAllCityAchievementService = achievementServiceFactory.getById(14890);
        achievementInfos.add(RDArriveChengC.AchievementInfo.instance(14890, attackAllCityAchievementService.getMyProgress(uid, info)));
        //攻下对应区域所有城池
        int index = cache.getArea() / 10 - 1;

        int needVal = -1;
        RDArriveChengC.AchievementInfo achievementInfo = null;
        BaseAchievementService achievementService1 = achievementServiceFactory.getById(nightmare_achievementIds_1[index]);
        BaseAchievementService achievementService2 = achievementServiceFactory.getById(nightmare_achievementIds_2[index]);
        BaseAchievementService achievementService3 = achievementServiceFactory.getById(nightmare_achievementIds_3[index]);
        BaseAchievementService[] services = {achievementService1, achievementService2, achievementService3};
        for (BaseAchievementService service : services) {
            if (service.isAccomplished(info)) {
                continue;
            }
            int myProgress = service.getMyProgress(uid, info);
            int ineed = service.getMyNeedValue() - service.getMyProgress(uid, info);
            if (needVal == -1 || needVal > ineed) {
                needVal = ineed;
                achievementInfo = RDArriveChengC.AchievementInfo.instance(service.getMyAchievementId(), myProgress);
            }
        }
        if (achievementInfo == null) {
            achievementInfo = RDArriveChengC.AchievementInfo.instance(achievementService1.getMyAchievementId(), achievementService1.getMyProgress(uid, info));
        }
        achievementInfos.add(achievementInfo);
        rd.setAchievementInfos(achievementInfos);
        List<Integer> buffs = new ArrayList<>();
        buffs.add(RunesEnum.GYJL.getRunesId());
        buffs.add(RunesEnum.TDLH.getRunesId());
        buffs.add(cache.getCityBuff());
        buffs.add(RunesEnum.FWFS.getRunesId());
        buffs.add(RunesEnum.TGCF.getRunesId());
        rd.setBuffs(buffs);
        RDCardGroups attackCardGroup = nightmareLogic.getAttackCardGroup(gu.getId());
        rd.setHasCardGroup(0);
        if (!attackCardGroup.isEmpty(CardGroupWay.NIGHTMARE_HU_WEI) && !attackCardGroup.isEmpty(CardGroupWay.NIGHTMARE_JIN_WEI)) {
            rd.setHasCardGroup(1);
        }
    }

    /**
     * 查看城池展示信息
     *
     * @param uid
     * @param cityId
     * @return
     */
    public RDArriveChengC getCityShowInfo(long uid, int cityId) {
        GameUser gu = gameUserService.getGameUser(uid);
        if (gu.getStatus().ifInTransmigrateWord()) {
            return new RDArriveChengC();
        }
        CfgCityEntity city = CityTool.getCityById(cityId);
        RDArriveChengC rd = new RDArriveChengC();
        Integer areaId = RoadTool.getRoadById(city.getAddress1()).getCountry();
        String cityArea = CITY_AREA_LIST.get((areaId / 10) - 1) + "城区";
        rd.setCityArea(cityArea);
        rd.setOwnCity(0);
        int showStrategyLv = 0;
        if (gu.getStatus().intoNightmareWord()) {
            UserNightmareCity nightmareCity = userCityService.getUserNightmareCity(uid, cityId);
            if (nightmareCity != null && nightmareCity.isOwn()) {
                rd.setOwnCity(1);
            }
            RDVideo rdVideo = chengCStrategyLogic.listBetterStrategy(uid, gameUserService.getActiveGid(uid), cityId);
            rd.setVideo(rdVideo.getStrategyVOList());
            rd.setBuffs(rd.getBuffList());
            rd.setCityBuff(CityTool.getCityBuff(cityId));
            showStrategyLv = StrategyConfig.NIGHTMARE_CITY_MIN_LV;
        } else {
            UserCity userCity = userCityService.getUserCity(uid, cityId);
            if (userCity != null && userCity.isOwn()) {
                rd.setOwnCity(1);
            }
            showStrategyLv = StrategyConfig.CITY_MIN_LV;
        }
        if (rd.getOwnCity() == 0) {
            // 攻城难度
            rd.setDifficult(getAttackDifficult(gu.getId(), city.getLevel()));
            rd.setShowStrategy(city.getLevel() >= showStrategyLv ? 1 : 0);
        }
        return rd;
    }

    /**
     * 获取城池关卡奖励
     *
     * @param uid
     * @param cityId
     * @param level
     * @return
     */
    public RDCommon gainLevelAward(long uid, int cityId, int level) {
        UserCity userCity = userCityService.getUserCity(uid, cityId);
        if (userCity == null || userCity.isOwn() || userCity.getProcess() == null || userCity.getProcess()[level - 1] != 1) {
            //没有奖励
            throw new ExceptionForClientTip("attack.city.level.awarded");
        }
        RDCommon rd = new RDCommon();
        userCity.getProcess()[level - 1] = 2;
        ChengChiInfoCache cache = TimeLimitCacheUtil.getChengChiInfoCache(uid);
        if (cache != null) {
            cache.setLevelProgress(userCity.getProcess());
            TimeLimitCacheUtil.setChengChiInfoCache(uid, cache);
        }
        List<Award> awards = null;
        if (level < 4) {
            //普通野怪宝箱 奖励
            awards = yeGProcessor.getRandomBoxAwards(uid, YeGuaiEnum.YG_NORMAL);
        } else {
            //精英野怪宝箱 奖励
            awards = yeGProcessor.getRandomBoxAwards(uid, YeGuaiEnum.YG_ELITE);
        }
        if (ListUtil.isNotEmpty(awards)) {
            awardService.fetchAward(uid, awards, WayEnum.ATTACK_LEVE_BOX, "开启攻城宝箱中", rd);
            gameUserService.updateItem(userCity);
        }
        return rd;
    }

    /**
     * 获取城池交易信息
     *
     * @param guId
     * @return
     */
    public RDTradeInfo listCityTradeInfo(long guId) {
        GameUser gu = this.gameUserService.getGameUser(guId);
        CfgCityEntity city = gu.gainCurCity();
        CityChecker.checkIsCC(city);
        RDTradeInfo rd = new RDTradeInfo();
        ChengChiInfoCache cache = TimeLimitCacheUtil.getChengChiInfoCache(guId);
        if (ListUtil.isEmpty(cache.getCitySpecials())) {
            initCityTradeInfo(cache, guId);
        }
        //获取本城出售的特产
        rd.setCitySpecials(cache.getCitySpecials());
        // 玩家拥有的特产在本城的售价
        List<UserSpecial> uSpecials = userSpecialService.getOwnSpecials(gu.getId());
        List<RDTradeInfo.RDSellingSpecial> sellingSpecials = toRdSellingSpecials(gu, uSpecials, cache.getPremiumRate());
        rd.setSellingSpecials(sellingSpecials);
        rd.setDiscount(cache.getDiscount());
        rd.setPremiumRate(cache.getPremiumRate());
        cache.setSellingSpecials(sellingSpecials);
        TimeLimitCacheUtil.setChengChiInfoCache(guId, cache);
        return rd;
    }

    /**
     * 初始化城池交易
     *
     * @param cache
     */
    private void initCityTradeInfo(ChengChiInfoCache cache, long uid) {
        CfgCityEntity city = CityTool.getCityById(cache.getCityId());
        GameUser gu = gameUserService.getGameUser(uid);
        // 本城可出售特产
        if (!cache.isHadInitSpecials()) {
            String[] specials = city.getSpecials().split(",");
            int unlockIndex = SpecialTool.getCitySpecialUnlockIndexByTcpLv(city.getLevel(), cache.getTcpLv());
            List<RDTradeInfo.RDCitySpecial> citySpecialList = new ArrayList<>();
            //活动产出
            getActivityOutPuts(gu, city, citySpecialList);
            //特产铺不能产出10000以下的特产活动道具
            List<RDTradeInfo.RDCitySpecial> errorOutPut = citySpecialList.stream().filter(tmp -> tmp.getId() <= SPECIAL_ID_MAX).collect(Collectors.toList());
            if (ListUtil.isNotEmpty(errorOutPut)) {
                List<Integer> ids = errorOutPut.stream().map(RDTradeInfo.RDCitySpecial::getId).collect(Collectors.toList());
                throw CoderException.high(String.format("不能有id=%s的活动道具在特产铺产出", StringUtils.join(ids, ",")));
            }
            //产出好感度礼物，产出铜铲子
            List<RDTradeInfo.RDCitySpecial> businessGangOutput = businessGangService.getBusinessGangOutput(uid);
            if (!businessGangOutput.isEmpty()) {
                citySpecialList.addAll(businessGangOutput);
            }
            //除土区外，在梦魇世界其他区域城池有8%概率刷新
            int seed = 8;
            if (gu.getStatus().ifNotInFsdlWorld() && city.getCountry() != 50 && PowerRandom.hitProbability(seed)) {
                citySpecialList.add(new RDTradeInfo.RDCitySpecial(UserSpecialService.LING_ZHI_ID, 0, 0));
            }
            for (int i = 0; i < specials.length; i++) {
                int specialIdForClient = Integer.parseInt(specials[i]) + 1000 * i;
                int status = i <= unlockIndex ? 0 : -1;
                int needTcpLv = SpecialTool.getCitySpecialUnlockTcpLvByUnlockIndex(city.getLevel(), i);
                RDTradeInfo.RDCitySpecial special = new RDTradeInfo.RDCitySpecial(specialIdForClient, needTcpLv, status);
                //商帮效果特产升阶
                int specialUpgrade = businessGangService.specialUpgrade(gu, special.getId());
                //时装效果特产升阶
                specialUpgrade = userLeaderFashionService.specialUpgrade(gu, specialUpgrade);
                special.setId(specialUpgrade);
                citySpecialList.add(special);
            }
            cache.setCitySpecials(citySpecialList);
            if (hexagramBuffService.isHexagramBuff(uid, HexagramBuffEnum.HEXAGRAM_22.getId())) {
                cache.setDiscount(15);
                cache.setPremiumRate(15);
                HexagramEventPublisher.pubHexagramBuffDeductEvent(new BaseEventParam(uid, WayEnum.TRADE), HexagramBuffEnum.HEXAGRAM_22.getId(), 1);
            } else {
                int discount = getDiscount(cache.getTcpLv());
                int premiumRate = getPremiumRate(cache.getTcpLv());
                if (this.userCocInfoService.getCityTradeProfit(uid)) {
                    discount += 5;
                    premiumRate += 5;
                }
                cache.setDiscount(discount);
                cache.setPremiumRate(premiumRate);
            }
            cache.setHadInitSpecials(true);
            return;
        }
        for (RDTradeInfo.RDCitySpecial special : cache.getCitySpecials()) {
            if (special.getStatus() == 1) {
                special.setStatus(0);
            }
        }
        return;
    }

    /**
     * 活动产出
     *
     * @param gu
     * @param city
     * @param citySpecialList
     */
    private void getActivityOutPuts(GameUser gu, CfgCityEntity city, List<RDTradeInfo.RDCitySpecial> citySpecialList) {
        //新手引导未通过，不触发活动事件
        Long uid = gu.getId();
        boolean passNewerGuide = newerGuideService.isPassNewerGuide(uid);
        if (!passNewerGuide) {
            return;
        }
        //感恩节活动（烹饪美食）
        citySpecialList.addAll(holidayCookingFoodProcessor.getIngredients(gu, city));
        //萌虎集市
        citySpecialList.addAll(holidayCuteTigerMarketProcessor.specialExtraAwards(uid));
        //圣元珠。 25%概率刷新一个，5%概率刷新二个
        citySpecialList.addAll(holidayLotteryProcessor.specialExtraAwards(uid));
        //25%概率刷出挖宝铲子
        citySpecialList.addAll(holidayDigForTreasureProcess.specialExtraAwards(uid));
        //35%概率 符•百毒不侵
        citySpecialList.addAll(holidayWZJZProcessor.specialExtraAwards(uid));
        //40%概率刷出天灯材料
        citySpecialList.addAll(holidaySkyLanternWorkShopProcessor.specialExtraAwards(uid));
        //概率刷出锦鲤
        citySpecialList.addAll(holidayKoiPrayProcessor.specialExtraAwards(uid));
        //概率刷出种子包和肥料包
        citySpecialList.addAll(holidayLaborGloriousProcessor.specialExtraAwards(uid));
    }

    /**
     * 给客户端创造交易里的玩家的特产信息
     *
     * @param gu
     * @param uSpecials
     * @return
     */
    private List<RDTradeInfo.RDSellingSpecial> toRdSellingSpecials(GameUser gu, List<UserSpecial> uSpecials, int premiumRate) {
        CfgCityEntity city = gu.gainCurCity();
        List<Long> pockets = this.userSpecialService.getLockSpecialIds(gu.getId());
        //先获得特产所在区服或者平台的价格
        List<RDTradeInfo.RDSellingSpecial> sellingSpecials = uSpecials.stream().map(us -> {
            CfgSpecialEntity special = SpecialTool.getSpecialById(us.getBaseId());
            int price;
            if (special.getId() == UserSpecialService.LING_ZHI_ID) {
                //灵芝有特地的售价 在土区城池可出售15w，朝歌可出售18w。其余地区固定卖8W
                if (city.getCountry() == 50) {
                    price = city.getId() == 2725 ? 180000 : 150000;
                } else {
                    price = 80000;
                }
            } else if (special.isSyntheticSpecialty()) {
                //合成特产则有特定价格
                int serverGroup = ServerTool.getServerGroup(gu.getServerId());
                price = gameSpecialService.getSellPrice(special.getId(), serverGroup, city);
            } else {
                price = this.serverSpecialService.getSellingPrice(special, city);
            }
            return RDTradeInfo.RDSellingSpecial.fromUserSpecial(us, price, pockets.contains(us.getId()));
        }).collect(Collectors.toList());
        //判断是否有交易福利
        boolean tradeWelfare = activityService.getActivity(gu.getServerId(), ActivityEnum.TRADE_WELFARE) != null;
        //开始遍历分别计算售价
        for (RDTradeInfo.RDSellingSpecial rdSpecial : sellingSpecials) {
            if (rdSpecial.getSellPriceParam() != null) {
                continue;
            }
            SpecialSellPriceParam sellPriceParam = settleSpecialSellPrice(gu, premiumRate, tradeWelfare, rdSpecial);
            rdSpecial.setSellingPrice(sellPriceParam.getRealSellPrice());
            rdSpecial.setSellPriceParam(sellPriceParam);
        }
        List<RDTradeInfo.RDSellingSpecial> collect = new ArrayList<>();
        if (ListUtil.isNotEmpty(sellingSpecials)) {
            //排序规则   灵芝排序到尾部（盈利时 排在盈利的后面，亏本同样的排在亏本的后面）
            List<RDTradeInfo.RDSellingSpecial> lingZhi = new ArrayList<>();
            List<RDTradeInfo.RDSellingSpecial> profit = new ArrayList<>();
            List<RDTradeInfo.RDSellingSpecial> loss = new ArrayList<>();
            for (RDTradeInfo.RDSellingSpecial special : sellingSpecials) {
                if (special.getId() == UserSpecialService.LING_ZHI_ID) {
                    lingZhi.add(special);
                } else if (special.ifProfit()) {
                    profit.add(special);
                } else {
                    loss.add(special);
                }
            }
            lingZhi = lingZhi.stream().sorted(Comparator.comparing(RDTradeInfo.RDSellingSpecial::getSellingPrice).reversed()).collect(Collectors.toList());
            profit = profit.stream().sorted(Comparator.comparing(RDTradeInfo.RDSellingSpecial::getSellingPrice).reversed()).collect(Collectors.toList());
            loss = loss.stream().sorted(Comparator.comparing(RDTradeInfo.RDSellingSpecial::getSellingPrice).reversed()).collect(Collectors.toList());
            collect.addAll(profit);
            if (ListUtil.isNotEmpty(lingZhi) && lingZhi.get(0).ifProfit()) {
                collect.addAll(lingZhi);
                collect.addAll(loss);
            } else {
                collect.addAll(loss);
                collect.addAll(lingZhi);
            }
        }
        return collect;
    }

    /**
     * 计算特产最终售价
     *
     * @param gu
     * @param tcpRate
     * @param tradeWelfare
     * @param rdSpecial
     * @return
     */
    private SpecialSellPriceParam settleSpecialSellPrice(GameUser gu, int tcpRate, boolean tradeWelfare, RDTradeInfo.RDSellingSpecial rdSpecial) {
        if (rdSpecial.getSellPriceParam() != null) {
            return rdSpecial.getSellPriceParam();
        }
        int cszCopper = 0;// 财神珠铜钱加量
        //利润：售价-买入价，负数则为0
        if (rdSpecial.getBoughtPrice() == null) {
            int buyPrice = SpecialTool.getSpecialById(rdSpecial.getId()).getBuyPrice(rdSpecial.getBoughtDiscount());
            rdSpecial.setBoughtPrice(buyPrice);
        }
        int profit = Math.max(rdSpecial.getSellingPrice() - rdSpecial.getBoughtPrice(), 0);
        // 财神珠溢价:加一份利润
        if (this.userTreasureEffectService.isTreasureEffect(gu.getId(), TreasureEnum.CSZ.getValue())) {
            cszCopper = profit;
        }
        // 特产铺溢价：特产铺溢价百分比*利润
        int tcpCopper = profit * tcpRate / 100;
        int earnCopper = rdSpecial.getSellingPrice() + tcpCopper;// 常规获得的铜钱  原售价+特产铺
        int weekCopper = profit + tcpCopper;// 富豪榜铜钱加量  纯利润+特产铺
        // 交易福利活动加成: 利润*0.5
        int activityCopper = 0;// 活动铜钱加量
        if (tradeWelfare) {
            activityCopper = profit / 2;
        }
        SpecialSellPriceParam param = SpecialSellPriceParam.instance(rdSpecial.getId(), weekCopper, earnCopper, cszCopper, activityCopper);
        return param;
    }


    /**
     * 刷新本城可购买的特产
     *
     * @param guId
     * @return
     */
    public RDTradeRefresh refreshTradeSpecials(long guId) {
        RDTradeRefresh rd = new RDTradeRefresh();
        // 特许交易卷检查与扣除
        TreasureChecker.checkIsEnough(TreasureEnum.TXJYQ.getValue(), 1, guId);
        TreasureEventPublisher.pubTDeductEvent(guId, TreasureEnum.TXJYQ.getValue(), 1, WayEnum.TRADE_REFRESH, rd);
        ChengChiInfoCache cache = TimeLimitCacheUtil.getChengChiInfoCache(guId);
        initCityTradeInfo(cache, guId);
        //获取城池产出
        List<RDTradeInfo.RDCitySpecial> businessOutPut = businessGangService.getCityOutPut(guId);
        List<RDTradeInfo.RDCitySpecial> citySpecials = cache.getCitySpecials();
        if (!businessOutPut.isEmpty()) {
            citySpecials.addAll(0, businessOutPut);
        }
        //获取本城出售的特产
        rd.setCitySpecials(citySpecials);
        TimeLimitCacheUtil.setChengChiInfoCache(guId, cache);
        return rd;
    }

    /**
     * 一键购买特产
     *
     * @param uid
     * @return
     */
    public RDTradeBuy autoBuySpecial(long uid) {
        List<Integer> autoBuySpecialIds = userSpecialService.getFilterAutoBuySpecialIds(uid);
        ChengChiInfoCache cache = TimeLimitCacheUtil.getChengChiInfoCache(uid);
        List<Integer> buySpecialIds = new ArrayList<>();
        for (RDTradeInfo.RDCitySpecial special : cache.getCitySpecials()) {
            if (special.getStatus() != 0) {
                continue;
            }
            //特产id过滤
            int specialId = special.getId();
            if (special.getStatus() == 0 && specialId < SPECIAL_ID_MAX) {
                //是特产需要%100
                specialId = specialId % 1000;
            }

            //不含在一键购买的特产
            if (!autoBuySpecialIds.contains(specialId)) {
                continue;
            }
            //添加一键购买的特产id
            buySpecialIds.add(special.getId());
        }
        if (ListUtil.isEmpty(buySpecialIds)) {
            return new RDTradeBuy();
        }
        return buySpecial(uid, buySpecialIds);
    }

    /**
     * 交易-购买特产
     *
     * @param guId
     * @param specialIds specialId,specialId (specialId = id + 100 *i)
     * @return
     */
    public RDTradeBuy buySpecial(long guId, List<Integer> specialIds) {
        int cardinality = 1000;
        //特产总数
        int specialtotal = specialIds.size();
        ChengChiInfoCache cache = TimeLimitCacheUtil.getChengChiInfoCache(guId);
        //判断本城是否有卖
        List<RDTradeInfo.RDCitySpecial> buyList = cache.getCitySpecials().stream().filter(p -> specialIds.contains(p.getId()) && p.getStatus() == 0).collect(Collectors.toList());
        if (buyList.isEmpty() || buyList.size() < specialIds.size()) {
            throw new ExceptionForClientTip("city.trade.special.not.valid");
        }
        RDTradeBuy rd = new RDTradeBuy();
        //处理非特产物品
        String handleResult = handleTradeBuyWithoutSpecial(guId, specialIds, cache, rd);
        String[] handleResults = handleResult.split(",");
        //产出道具数量
        int treasureNum = Integer.parseInt(handleResults[0]);
        //灵芝数量
        int linZhiNum = (int) specialIds.stream().filter(p -> p == UserSpecialService.LING_ZHI_ID).count();
        //计算购买特产数量
        int buySpecialNum = specialtotal - treasureNum - linZhiNum;
        //剩余空间
        int freeSize = userSpecialService.getSpecialFreeSize(guId);
        //普通特产id
        List<Integer> commonSpecialIds = specialIds.stream().filter(tmp -> tmp != UserSpecialService.LING_ZHI_ID && tmp <= SPECIAL_ID_MAX).collect(Collectors.toList());
        //获得折扣
        int discount = 100 - cache.getDiscount();
        //按价格进行降序排序
        commonSpecialIds.sort((commonSpecialId1, commonSpecialId2) -> {
            int firstSpecialPrice = SpecialTool.getSpecialById(commonSpecialId1 % cardinality).getBuyPrice(discount);
            int secondSpecialPrice = SpecialTool.getSpecialById(commonSpecialId2 % cardinality).getBuyPrice(discount);
            return secondSpecialPrice - firstSpecialPrice;
        });
        //本次购买花费的铜钱
        int sumPrint = 0;
        //最终购买特产id
        List<Integer> finalBuyCommonSpecialIds = new ArrayList<>();
        GameUser gu = gameUserService.getGameUser(guId);
        //灵芝购买处理
        int lingZhiPrice = SpecialTool.getSpecialById(UserSpecialService.LING_ZHI_ID).getPrice();
        boolean isBuyLingZhi = isBuyLingZhi(specialtotal, linZhiNum, sumPrint, lingZhiPrice, gu);
        if (isBuyLingZhi) {
            sumPrint += lingZhiPrice;
            finalBuyCommonSpecialIds.add(UserSpecialService.LING_ZHI_ID);
        }
        if (!isBuyLingZhi && linZhiNum > 0) {
            linZhiNum--;
        }
        //获得铜钱不足，不能购买的特产id（从高价格买起）
        List<Integer> notBuySpecial = new ArrayList<>();
        for (Integer specialId : commonSpecialIds) {
            int specialPrice = SpecialTool.getSpecialById(specialId % cardinality).getBuyPrice(discount);
            if (sumPrint + specialPrice > gameUserService.getGameUser(guId).getCopper()) {
                notBuySpecial.add(specialId);
                continue;
            }
            sumPrint += specialPrice;
        }
        //移除不能购买的特产
        commonSpecialIds.removeAll(notBuySpecial);
        //只购买特产，铜钱不够买任一特产，提示铜钱不足
        if (ListUtil.isEmpty(commonSpecialIds) && 0 == treasureNum && 0 == linZhiNum) {
            throw new ExceptionForClientTip("gu.copper.not.enough");
        }
        //最终购买数量
        int finalBuySpecialNum = Math.min(freeSize, buySpecialNum - notBuySpecial.size());
        //特产包剩余空间不足
        List<Integer> canBuyCommonSpecialIds = commonSpecialIds;
        if (finalBuySpecialNum < commonSpecialIds.size()) {
            //能购买的特产
            canBuyCommonSpecialIds = commonSpecialIds.subList(0, finalBuySpecialNum);
        }
        //只购买特产，背包满空间，提示空间不足
        if (ListUtil.isEmpty(canBuyCommonSpecialIds) && 0 == treasureNum && 0 == linZhiNum) {
            throw new ExceptionForClientTip("special.is.full", userSpecialService.getSpecialLimit(guId) + "");
        }
        //最终购买的特产id集合
        finalBuyCommonSpecialIds.addAll(canBuyCommonSpecialIds);
        long needCopper = ListUtil.sumInt(buyList.stream()
                .filter(p -> p.getId() < SPECIAL_ID_MAX && finalBuyCommonSpecialIds.contains(p.getId()))
                .map(p -> SpecialTool.getSpecialById(p.getId() % cardinality).getBuyPrice(discount))
                .collect(Collectors.toList()));
        WayEnum way = WayEnum.TRADE;
        if (needCopper > 0) {
            // 铜钱是否足够
            if (treasureNum > 0 && gu.getCopper() < needCopper) {
                return rd;
            }
            ResChecker.checkCopper(gu, needCopper);
            ResEventPublisher.pubCopperDeductEvent(guId, needCopper, way, rd);
            List<EVSpecialAdd> specialAdds = finalBuyCommonSpecialIds.stream()
                    .map(specialId -> new EVSpecialAdd(specialId % cardinality, discount))
                    .collect(Collectors.toList());
            SpecialEventPublisher.pubSpecialAddEvent(guId, specialAdds, way, rd);
            List<UserSpecial> userSpecials = this.userSpecialService.getOwnSpecials(guId);
            //新买的特产计算售价
            List<UserSpecial> newBoughtSpecials = userSpecials.subList(Math.max(0, userSpecials.size() - finalBuySpecialNum - linZhiNum), userSpecials.size());
            List<RDTradeInfo.RDSellingSpecial> boughtSpecials = toRdSellingSpecials(gu, newBoughtSpecials, cache.getPremiumRate());
            //更新到缓存中
            for (RDTradeInfo.RDCitySpecial special : cache.getCitySpecials()) {
                if (finalBuyCommonSpecialIds.contains(special.getId())) {
                    special.setStatus(1);
                }
            }
            cache.getSellingSpecials().addAll(boughtSpecials);
            TimeLimitCacheUtil.setChengChiInfoCache(guId, cache);
            rd.setBoughtSpecials(boughtSpecials);
        }
        return rd;
    }

    /**
     * 是否购买灵芝
     *
     * @param specialtotal
     * @param linZhiNum
     * @param sumPrint
     * @param lingZhiPrice
     * @param gu
     */
    private boolean isBuyLingZhi(int specialtotal, Integer linZhiNum, Integer sumPrint, Integer lingZhiPrice, GameUser gu) {
        if (0 == linZhiNum) {
            return false;
        }
        //是否有足够铜钱购买
        if (sumPrint + lingZhiPrice > gu.getCopper()) {
            return false;
        }
        List<UserSpecial> userSpecial = userSpecialService.getOwnUnLockSpecialsByBaseId(gu.getRoleInfo().getUid(), UserSpecialService.LING_ZHI_ID);
        if (userSpecial.size() >= UserSpecialService.LING_ZHI_BUY_LIMIY && specialtotal == linZhiNum) {
            throw new ExceptionForClientTip("special.buy.limit");
        }
        if (userSpecial.size() >= UserSpecialService.LING_ZHI_BUY_LIMIY) {
            return false;
        }
        return true;
    }

    /**
     * 处理非特产购买
     *
     * @param uid
     * @param specialIds
     * @param cache
     * @param rd
     * @return 处理结果 需要处理的,已处理的
     */
    private String handleTradeBuyWithoutSpecial(long uid, List<Integer> specialIds, ChengChiInfoCache cache, RDTradeBuy rd) {
        GameUser gu = gameUserService.getGameUser(uid);
        // 获取购买信息
        List<BuyGoodInfo> allBuyGoodInfos = new ArrayList<>();
        for (IChengChiTradeService tradeService : tradeServices) {
            List<BuyGoodInfo> buyGoodInfos = tradeService.getTradeBuyInfo(uid, specialIds);
            if (ListUtil.isEmpty(buyGoodInfos)) {
                continue;
            }
            allBuyGoodInfos.addAll(buyGoodInfos);
        }
        if (ListUtil.isEmpty(allBuyGoodInfos)) {
            return "0,0";
        }

        //获取可出售的物品
        List<BuyGoodInfo> goodToBuy = new ArrayList<>();
        long sumPrice = 0;
        for (BuyGoodInfo buyGoodInfo : allBuyGoodInfos) {
            int price = buyGoodInfo.getPrice();
            if (sumPrice + price >= gu.getCopper()) {
                continue;
            }
            sumPrice += price;
            goodToBuy.add(buyGoodInfo);
        }
        // 铜钱是否足够
        ResChecker.checkCopper(gu, sumPrice);
        //扣除铜钱
        ResEventPublisher.pubCopperDeductEvent(gu.getId(), sumPrice, WayEnum.TRADE, rd);
        //处理物品
        for (BuyGoodInfo buyGoodInfo : goodToBuy) {
            //发放物品
            TreasureEventPublisher.pubTAddEvent(gu.getId(), buyGoodInfo.getGoodId(), 1, WayEnum.TRADE, rd);
            specialIds.removeIf(s -> s.equals(buyGoodInfo.getGoodId()));
            if (null == cache.getCitySpecials()) {
                continue;
            }
            // 从缓存中移除数据
            RDTradeInfo.RDCitySpecial rdCitySpecialToRemove = cache.getCitySpecials().stream().filter(p -> buyGoodInfo.getGoodId().equals(p.getId())).findFirst().get();
            cache.getCitySpecials().remove(rdCitySpecialToRemove);
            TimeLimitCacheUtil.setChengChiInfoCache(gu.getId(), cache);
        }
        // 物品如果无法全购买，不处理特产逻辑。
        return allBuyGoodInfos.size() + "," + goodToBuy.size();
    }

    /**
     * 一键出售特产
     *
     * @param uid
     * @return
     */
    public RDTradeSell autoSellSpecial(long uid) {
        ChengChiInfoCache cache = TimeLimitCacheUtil.getChengChiInfoCache(uid);
        List<RDTradeInfo.RDSellingSpecial> specials = cache.getSellingSpecials();
        if (ListUtil.isEmpty(specials)) {
            throw new ExceptionForClientTip("special.can.not.auto.sell");
        }
        List<RDTradeInfo.RDSellingSpecial> collect = specials.stream().filter(p -> p.getSellingPrice() > p.getBoughtPrice()).collect(Collectors.toList());
        collect = userSpecialService.getFilterAutoSellSpecialIds(uid, collect);
        if (ListUtil.isEmpty(collect)) {
            throw new ExceptionForClientTip("special.can.not.auto.sell");
        }
        List<Long> dataIds = collect.stream().map(RDTradeInfo.RDSellingSpecial::getUsId).collect(Collectors.toList());
        return sellSpecial(uid, dataIds);
    }

    /**
     * 出售特产
     *
     * @param uid
     * @param specialDataIds
     * @return
     */
    public RDTradeSell sellSpecial(long uid, List<Long> specialDataIds) {
        RDTradeSell rd = new RDTradeSell();
        GameUser gu = this.gameUserService.getGameUser(uid);
        //检查卖出的特产

        List<UserSpecial> userSpecials = this.userSpecialService.getAllSpecialsById(uid, specialDataIds);
        if (userSpecials.size() < specialDataIds.size()) {
            throw new ExceptionForClientTip("special.not.exist");
        }
        int earnCopper = 0;// 常规获得的铜钱
        int weekCopper = 0;// 富豪榜铜钱加量
        int cszCopper = 0;// 财神珠铜钱加量
        int activityCopper = 0;// 活动铜钱加量
        Map<Integer, Integer> specialsCount = new HashMap<>();// 特产出售数记录
        List<EPSpecialDeduct.SpecialInfo> specialInfoList = new ArrayList<>();// 出售的特产信息集合
        //获取要卖的特产
        ChengChiInfoCache cache = TimeLimitCacheUtil.getChengChiInfoCache(uid);
        List<RDTradeInfo.RDSellingSpecial> sellingSpecials = cache.getSellingSpecials().stream().filter(p -> specialDataIds.contains(p.getUsId())).collect(Collectors.toList());
        for (RDTradeInfo.RDSellingSpecial rdSpecial : sellingSpecials) {
            int specialId = rdSpecial.getId();
            // 统计批量卖出的特产的数量
            specialsCount.merge(specialId, 1, Integer::sum);
            // 添加数据到特产出售信息集合
            SpecialSellPriceParam sellPriceParam = rdSpecial.getSellPriceParam();
            EPSpecialDeduct.SpecialInfo info = EPSpecialDeduct.SpecialInfo.getInstance(rdSpecial.getUsId(), specialId, rdSpecial.getBoughtPrice(), rdSpecial.getSellingPrice());
            specialInfoList.add(info);
            earnCopper += sellPriceParam.getEarnCopper();
            weekCopper += sellPriceParam.getWeekCopper();
            cszCopper += sellPriceParam.getCszCopper();
            activityCopper += sellPriceParam.getActivityCopper();
        }
        // 扣除特产
        EPSpecialDeduct ep = EPSpecialDeduct.instance(new BaseEventParam(uid, WayEnum.TRADE, rd), gu.getLocation().getPosition(), specialInfoList);
        SpecialEventPublisher.pubSpecialDeductEvent(ep);
        // 铜钱入库
        EPCopperAdd copperInfo = new EPCopperAdd(new BaseEventParam(uid, WayEnum.TRADE, rd), earnCopper, weekCopper);
        copperInfo.addCopper(ResWayType.CaiSZ, cszCopper);
        copperInfo.addCopper(ResWayType.Activity, activityCopper);
        ResEventPublisher.pubCopperAddEvent(copperInfo);
        // 返回特产价格
        userSpecials = this.userSpecialService.getOwnSpecials(gu.getId());
        // 排序
        this.userSpecialService.sortUserSpecialAsPriceRate(gu, userSpecials);
        sellingSpecials = toRdSellingSpecials(gu, userSpecials, cache.getPremiumRate());
        // 更新缓存
        cache.setSellingSpecials(sellingSpecials);
        TimeLimitCacheUtil.setChengChiInfoCache(uid, cache);
        rd.setSellingSpecials(sellingSpecials);
        return rd;
    }

    /**
     * 获得城内建筑信息
     *
     * @param uid
     * @param cityId
     * @return
     */
    public RDCityInInfo getCityInInfo(long uid, int cityId) {
        UserCity userCity = userCityService.getUserCity(uid, cityId);
        RDCityInInfo rd = new RDCityInInfo();
        CfgCityEntity city = userCity.gainCity();
        // 城内建筑设置
        UserCitySetting citySetting = this.gameUserService.getSingleItem(uid, UserCitySetting.class);
        int useDefaultKcEles = citySetting == null ? 0 : citySetting.getUseDefaultKcEles();
        List<Integer> defaultKcEles = citySetting == null ? Arrays.asList(10, 20, 30, 40, 50) :
                citySetting.getDefaultKcEles();
        Integer ldfCard = citySetting == null ? 0 : citySetting.getLdfCard();
        rd.setUseDefaultKcEles(useDefaultKcEles);
        rd.setDefaultKcEles(defaultKcEles);
        rd.setLdfCard(ldfCard);
        // 建筑信息

        List<RDBuildingInfo> info = new ArrayList<>();
        info.add(BuildingFactory.I.create(userCity, BuildingEnum.FY).getBuildingInfo());
        info.add(BuildingFactory.I.create(userCity, BuildingEnum.KC).getBuildingInfo());
        info.add(BuildingFactory.I.create(userCity, BuildingEnum.QZ).getBuildingInfo());
        info.add(BuildingFactory.I.create(userCity, BuildingEnum.TCP).getBuildingInfo());
        info.add(BuildingFactory.I.create(userCity, BuildingEnum.JXZ).getBuildingInfo());
        info.add(BuildingFactory.I.create(userCity, BuildingEnum.LBL).getBuildingInfo());
        info.add(BuildingFactory.I.create(userCity, BuildingEnum.DC).getBuildingInfo());
        info.add(BuildingFactory.I.create(userCity, BuildingEnum.LDF).getBuildingInfo());
        info.add(BuildingFactory.I.create(userCity, BuildingEnum.FT).getBuildingInfo());
        rd.setInfo(info);
        // 兼容旧版
        rd.setRemainUpdateTimes(1);
        rd.setCityId(city.getId());
        rd.setIsAblePromote(userCity.ifAblePromote() ? 1 : 0);
        //轮回挑战评分
        int sgId = gameUserService.getActiveGid(uid);
        GameTransmigration curTransmigration = gameTransmigrationService.getCurTransmigration(sgId);
        if (null != curTransmigration) {
            Integer score = transmigrationCityRecordService.getScore(curTransmigration, uid, city.getId());
            rd.setTransmigrationScore(score);
        }

        ChengChiInfoCache cache = TimeLimitCacheUtil.getChengChiInfoCache(uid);
        if (ListUtil.isEmpty(cache.getCitySpecials())) {
            initCityTradeInfo(cache, uid);
        }
        TimeLimitCacheUtil.setChengChiInfoCache(uid, cache);
        return rd;
    }

    /**
     * 获取城池溢价(不包括商会溢价)
     *
     * @param tcpLv
     * @return
     */
    public int getPremiumRate(Integer tcpLv) {
        if (tcpLv == null) {
            return 0;
        }
        if (tcpLv >= 9) {
            return 10;
        }
        if (tcpLv >= 7) {
            return 5;
        }
        return 0;
    }

    /**
     * 获取城池溢价(不包括商会溢价)
     *
     * @param guId
     * @param cityId
     * @return
     */
    public int getPremiumRate(long guId, int cityId) {
        UserCity userCity = userCityService.getUserCity(guId, cityId);
        if (userCity == null) {
            return 0;
        }
        return getPremiumRate(userCity.getTcp());
    }

    /**
     * 获取城池折扣(不包括商会折扣)
     *
     * @return
     */
    public int getDiscount(Integer tcpLv) {
        if (tcpLv == null) {
            return 0;
        }
        if (tcpLv >= 9) {
            return 10;
        }
        if (tcpLv >= 7) {
            return 5;
        }
        return 0;
    }

    /**
     * 获取城池折扣(不包括商会折扣)
     *
     * @param guId
     * @param cityId
     * @return
     */
    public int getDiscount(long guId, int cityId) {
        UserCity userCity = userCityService.getUserCity(guId, cityId);
        if (userCity == null) {
            return 0;
        }
        return getDiscount(userCity.getTcp());
    }

    /**
     * 获得对应城池的攻城难度
     *
     * @param guId
     * @param cityLevel
     * @return
     */
    public double getAttackDifficult(long guId, int cityLevel) {
        int ownCityNum = userCityService.getOwnCityNumAsLevel(guId, cityLevel);
        double start = 0;
        switch (cityLevel) {
            case 1:
                start = 1;
                break;
            case 2:
                start = 1.5;
                break;
            case 3:
                start = 2;
                break;
            case 4:
                start = 3.5;
                break;
            case 5:
                start = 4;
                break;
            default:
                break;
        }
        if (5 == cityLevel) {
            return start + (ownCityNum >> 1) * 0.5;
        }
        return start + (ownCityNum - 1) / 5 * 0.5;
    }

    private boolean isLongZhu(int id) {
        for (int zhuId : SHENG_YUAN_ZHU_IDS) {
            if (zhuId == id) {
                return true;
            }
        }
        return false;
    }

}
