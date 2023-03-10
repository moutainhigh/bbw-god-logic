package com.bbw.god.login;

import com.bbw.App;
import com.bbw.common.CloneUtil;
import com.bbw.common.DateUtil;
import com.bbw.common.ListUtil;
import com.bbw.common.StrUtil;
import com.bbw.god.activity.ActivityLogic;
import com.bbw.god.activity.ActivityService;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.activity.config.ActivityParentTypeEnum;
import com.bbw.god.activity.holiday.processor.HolidayDigForTreasureProcessor;
import com.bbw.god.activity.holiday.processor.holidayspcialyeguai.AbstractSpecialYeGuaiProcessor;
import com.bbw.god.activity.holiday.processor.holidayspcialyeguai.HolidaySpecialYeGuaiFactory;
import com.bbw.god.activity.processor.RechargeCardProcessor;
import com.bbw.god.activity.processor.SevenLoginProcessor;
import com.bbw.god.activity.processor.cardboost.CardBoostProcessor;
import com.bbw.god.city.UserCityService;
import com.bbw.god.city.chengc.UserCity;
import com.bbw.god.city.chengc.in.FaTanService;
import com.bbw.god.city.miaoy.hexagram.HexagramBuffService;
import com.bbw.god.city.mixd.nightmare.NightmareMiXianService;
import com.bbw.god.city.mixd.nightmare.UserNightmareMiXian;
import com.bbw.god.city.taiyf.TaiYFProcessor;
import com.bbw.god.city.taiyf.mytaiyf.MYTaiYFProcessor;
import com.bbw.god.city.yed.RDAdventures;
import com.bbw.god.city.yed.YeDProcessor;
import com.bbw.god.db.async.UpdateRoleInfoAsyncHandler;
import com.bbw.god.db.entity.InsRoleInfoEntity;
import com.bbw.god.db.service.InsAccountTagsService;
import com.bbw.god.detail.LogUtil;
import com.bbw.god.game.chanjie.service.ChanjieService;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.WorldType;
import com.bbw.god.game.config.god.GodEnum;
import com.bbw.god.game.config.mall.CfgMallEntity;
import com.bbw.god.game.config.mall.MallTool;
import com.bbw.god.game.config.server.ServerTool;
import com.bbw.god.game.config.special.SpecialTool;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.game.config.treasure.TreasureTool;
import com.bbw.god.game.dfdj.DfdjDateService;
import com.bbw.god.game.transmigration.GameTransmigrationService;
import com.bbw.god.game.transmigration.UserTransmigrationService;
import com.bbw.god.game.transmigration.entity.GameTransmigration;
import com.bbw.god.game.wanxianzhen.service.WanXianLogic;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.UserLoginInfo;
import com.bbw.god.gameuser.bag.UserBagBuyRecord;
import com.bbw.god.gameuser.bag.UserBagService;
import com.bbw.god.gameuser.businessgang.digfortreasure.DigTreasureService;
import com.bbw.god.gameuser.businessgang.digfortreasure.RDDigTreasureInfo;
import com.bbw.god.gameuser.businessgang.luckybeast.LuckyBeastService;
import com.bbw.god.gameuser.businessgang.luckybeast.RDLuckyBeastInfo;
import com.bbw.god.gameuser.card.*;
import com.bbw.god.gameuser.card.equipment.UserCardXianJueService;
import com.bbw.god.gameuser.card.equipment.UserCardZhiBaoService;
import com.bbw.god.gameuser.card.equipment.data.UserCardXianJue;
import com.bbw.god.gameuser.card.equipment.data.UserCardZhiBao;
import com.bbw.god.gameuser.config.GameUserConfig;
import com.bbw.god.gameuser.config.GameUserExpTool;
import com.bbw.god.gameuser.dice.IncDiceService;
import com.bbw.god.gameuser.dice.UserDiceService;
import com.bbw.god.gameuser.god.UserGod;
import com.bbw.god.gameuser.guide.INewerGuideLoginService;
import com.bbw.god.gameuser.guide.NewerGuideService;
import com.bbw.god.gameuser.guide.UserNewerGuide;
import com.bbw.god.gameuser.leadercard.UserLeaderCard;
import com.bbw.god.gameuser.leadercard.service.LeaderCardService;
import com.bbw.god.gameuser.limit.UserLimit;
import com.bbw.god.gameuser.pay.UserPayInfo;
import com.bbw.god.gameuser.pay.UserPayInfoService;
import com.bbw.god.gameuser.special.UserSpecial;
import com.bbw.god.gameuser.special.UserSpecialService;
import com.bbw.god.gameuser.statistic.behavior.recharge.RechargeStatisticService;
import com.bbw.god.gameuser.task.godtraining.GodTrainingTaskService;
import com.bbw.god.gameuser.task.grow.NewbieTaskService;
import com.bbw.god.gameuser.treasure.*;
import com.bbw.god.gameuser.yaozu.UserYaoZuInfo;
import com.bbw.god.gameuser.yaozu.UserYaoZuInfoService;
import com.bbw.god.gameuser.yaozu.YaoZuGenerateProcessor;
import com.bbw.god.gameuser.yaozu.YaoZuProgressEnum;
import com.bbw.god.gameuser.yuxg.UserYuXGService;
import com.bbw.god.gameuser.yuxg.rd.RDYuXGFuTu;
import com.bbw.god.login.RDGameUser.RDCard;
import com.bbw.god.login.RDGameUser.RDCardGroup;
import com.bbw.god.login.RDGameUser.RDEffectTreasure;
import com.bbw.god.login.RDGameUser.RDMallPrice;
import com.bbw.god.login.event.LoginEventPublisher;
import com.bbw.god.mall.MallService;
import com.bbw.god.mall.cardshop.CardPoolEnum;
import com.bbw.god.nightmarecity.chengc.UserNightmareCity;
import com.bbw.god.notify.rednotice.ModuleEnum;
import com.bbw.god.notify.rednotice.RedNoticeService;
import com.bbw.god.questionnaire.QuestionnaireService;
import com.bbw.god.rd.RDCommon.RDSpecail;
import com.bbw.god.rechargeactivities.processor.timelimit.RoleTimeLimitPackProcessor;
import com.bbw.god.security.param.SecurityParam;
import com.bbw.god.security.param.SecurityParamService;
import com.bbw.god.server.ServerUserTmpStatusService;
import com.bbw.god.server.fst.server.FstServerService;
import com.bbw.god.server.god.GodService;
import com.bbw.god.server.god.ServerGod;
import com.bbw.god.server.maou.bossmaou.ServerBossMaouService;
import com.bbw.god.server.monster.MonsterService;
import com.bbw.god.statistics.userstatistic.ActionStatisticTool;
import com.bbw.mc.broadcast.BroadcastService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-02-28 09:06
 */
@Slf4j
@Service
public class UserLoginService {
    @Autowired
    private GameUserService userService;
    @Autowired
    private NewerGuideService newerGuideService;
    @Autowired
    private INewerGuideLoginService newerGuideLoginService;
    @Autowired
    private UserTreasureService userTreasureService;
    @Autowired
    private GodService godService;
    @Autowired
    private NewbieTaskService growTaskService;
    @Autowired
    private FstServerService fstServerService;
    @Autowired
    private BroadcastService broadcastService;
    @Autowired
    private MonsterService monsterService;
    @Autowired
    private MallService mallService;
    @Autowired
    private ActivityLogic activityLogic;
    @Autowired
    private ActivityService activityService;
    @Autowired
    private RechargeCardProcessor rechargeCardProcessor;
    @Autowired
    private IncDiceService incDiceService;
    @Autowired
    private UserCardGroupService userCardGroupService;
    @Autowired
    private SecurityParamService securityParamService;
    @Autowired
    private UserCardService userCardService;
    @Autowired
    private ServerUserTmpStatusService userTmpStatusService;
    @Autowired
    private InsAccountTagsService insAccountTagsService;
    @Autowired
    private UserSpecialService userSpecialService;
    @Autowired
    private RedNoticeService redNoticeService;
    @Autowired
    private TaiYFProcessor taiYFProcessor;
    @Autowired
    private MYTaiYFProcessor myTaiYFProcessor;
    @Autowired
    private ServerBossMaouService serverBossMaouService;
    @Autowired
    private UserCityService userCityService;
    @Autowired
    private UserLoginRepairService loginRepairService;
    @Autowired
    private UserTreasureRecordService userTreasureRecordService;
    @Autowired
    private UserTreasureEffectService userTreasureEffectService;
    @Autowired
    private WanXianLogic wanXianLogic;
    @Autowired
    private YeDProcessor yeDProcessor;
    @Autowired
    private ChanjieService chanjieService;
    @Autowired
    private QuestionnaireService questionnaireService;
    @Autowired
    private RechargeStatisticService rechargeStatisticService;
    @Autowired
    private DfdjDateService dfdjDateService;
    @Autowired
    private App app;
    @Autowired
    private SevenLoginProcessor sevenLoginProcessor;
    @Autowired
    private GodTrainingTaskService godTrainingTaskService;
    @Autowired
    private HolidayDigForTreasureProcessor holidayDigForTreasureProcessor;
    @Autowired
    private LeaderCardService leaderCardService;
    @Autowired
    private HexagramBuffService hexagramBuffService;
    @Autowired
    private UserBagService userBagService;
    @Autowired
    private UserDiceService userDiceService;
    @Autowired
    private UserPayInfoService userPayInfoService;
    @Autowired
    private RoleTimeLimitPackProcessor roleTimeLimitPackProcessor;
    @Autowired
    private CardBoostProcessor cardBoostProcessor;
    @Autowired
    private GameTransmigrationService gameTransmigrationService;
    @Autowired
    private UserYaoZuInfoService userYaoZuInfoService;
    @Autowired
    private YaoZuGenerateProcessor yaoZuGenerateProcessor;
    @Autowired
    private UserTransmigrationService userTransmigrationService;
    @Autowired
    private UserYuXGService userYuXGService;
    @Autowired
    private FaTanService faTanService;
    @Autowired
    private UpdateRoleInfoAsyncHandler updateRoleInfoAsyncHandler;
    @Autowired
    private HolidaySpecialYeGuaiFactory holidaySpecialYeGuaiFactory;
    @Autowired
    private LuckyBeastService luckyBeastService;
    @Autowired
    private DigTreasureService digTreasureService;
    @Autowired
    private UserCardXianJueService userCardXianJueService;
    @Autowired
    private UserCardZhiBaoService userCardZhiBaoService;
    @Autowired
    private NightmareMiXianService nightmareMiXianService;

    /**
     * ?????????????????????????????????
     *
     * @param loginInfo
     * @param player
     * @return
     */
    public RDGameUser getGameUserInfo(LoginInfo loginInfo, LoginPlayer player) {
        long begin = System.currentTimeMillis();
        GameUser gu = loginInfo.getUser();
        long guId = gu.getId();
        String roleInfo = "???" + gu.getRoleInfo().getUserName() + "??????" + gu.getId() + "???";
        log.info(roleInfo + "??????getGameUserInfo");

        // ????????????????????????????????????
        this.newerGuideLoginService.handleNewerGuideAsLogin(gu);
        log.info(roleInfo + "handleNewerGuideAsLogin,???????????????" + (System.currentTimeMillis() - begin));

        // ???????????????????????????
        loginRepairService.repairAsLogin(gu);
        log.info(roleInfo + "repairAsLogin,???????????????" + (System.currentTimeMillis() - begin));

        // ????????????
        RDGameUser rd = this.getGuInfo(gu);
        log.info(roleInfo + "getGuInfo,???????????????" + (System.currentTimeMillis() - begin));

        // ??????????????????
        this.updateLoginInfo(loginInfo, rd);
        log.info(roleInfo + "updateLoginInfo,???????????????" + (System.currentTimeMillis() - begin));

        // ????????????
        List<String> notices = this.redNoticeService.getNoticeForLogin(gu, gu.getServerId());
        rd.addRedNotices(notices);
        this.getNoticeInfo(gu, notices, rd);
        log.info(roleInfo + "??????????????????,???????????????" + (System.currentTimeMillis() - begin));

        // ??????????????????
        this.setSecurityParams(player, rd);

        // ????????????????????????
        this.setAccountTagsList(player.getAccount(), rd);
        // ??????????????????
        this.setBagLimit(gu, rd);
        this.setBagBuyTimes(gu, rd);
        rd.setTransmigrationSuccessNum(userTransmigrationService.getSuccessNum(guId));
        rd.setFuTus(RDYuXGFuTu.getInstance(userYuXGService.getUserAllFuTus(guId)));

        // ?????????????????????????????????????????????????????????
        if (gu.getRoleInfo().getChannelId() == 203) {
            rd.setYgBoxOpenTimes(ActionStatisticTool.getUserActionStatistic(guId, WayEnum.YG_OPEN_BOX.getName()));
            rd.setBxOpenTimes(ActionStatisticTool.getUserActionStatistic(guId, WayEnum.OPEN_BaoX.getName()));
            List<Integer> cardPoolOpenTimes = new ArrayList<>();
            cardPoolOpenTimes.add(ActionStatisticTool.getUserActionStatistic(guId, CardPoolEnum.GOLD_CP.getName()));
            cardPoolOpenTimes.add(ActionStatisticTool.getUserActionStatistic(guId, CardPoolEnum.WOOD_CP.getName()));
            cardPoolOpenTimes.add(ActionStatisticTool.getUserActionStatistic(guId, CardPoolEnum.WATER_CP.getName()));
            cardPoolOpenTimes.add(ActionStatisticTool.getUserActionStatistic(guId, CardPoolEnum.FIRE_CP.getName()));
            cardPoolOpenTimes.add(ActionStatisticTool.getUserActionStatistic(guId, CardPoolEnum.EARTH_CP.getName()));
            cardPoolOpenTimes.add(ActionStatisticTool.getUserActionStatistic(guId, CardPoolEnum.WANWU_CP.getName()));
            rd.setCardPoolOpenTimes(cardPoolOpenTimes);
        }
        log.info(roleInfo + "??????getGameUserInfo,???????????????" + (System.currentTimeMillis() - begin));

        return rd;
    }

    /**
     * @param player
     * @param rd
     */
    private void setSecurityParams(LoginPlayer player, RDGameUser rd) {
        SecurityParam securityParam = this.securityParamService.build(player.getUid(), player.getTokenVersion());
        rd.setEids(new ArrayList<>(securityParam.getTokens().size()));
        rd.getEids().addAll(securityParam.getTokens());
    }

    private void setAccountTagsList(String account, RDGameUser rd) {
        List<String> accountTags = this.insAccountTagsService.getAllTagsByAccount(account);
        rd.setAccountTagsList(accountTags);
    }

    public void setBagLimit(GameUser gu, RDGameUser rd) {
        Integer limit = this.userSpecialService.getSpecialLimit(gu);
        rd.setBagLimit(limit);
    }

    public void setBagBuyTimes(GameUser gu, RDGameUser rd) {
        UserBagBuyRecord buyRecord = userBagService.getCurBuyRecord(gu.getId());
        rd.setBagBuyTimes(buyRecord.getBuyTimes());
    }

    /**
     * ?????????????????????????????? ????????????????????????????????????????????????????????????????????????
     */
    // TODO:???????????????????????????UserLoginService?????????????????????????????????????????????try catch???????????????????????????
    public RDNoticeInfo getNoticeInfo(GameUser gu, List<String> allNotices, RDNoticeInfo rd) {
        try {
            Long guId = gu.getId();
            rd.setDice(gu.getDice());
            rd.setGold(gu.getGold());
            rd.setDiamond(gu.getDiamond());

            // ??????
            // ???????????????
            if (this.serverBossMaouService.isMaouBossTime()) {
                rd.addOpenMenu(DynamicMenuEnum.MAOU_ATTACK.getVal(), 1);
            }

            boolean isShowFirstRechargeIcon = false;
            //?????????????????????????????????
            if (this.newerGuideService.isPassNewerGuide(guId)) {
                isShowFirstRechargeIcon = this.activityLogic.isShowFirstRecharge(gu, gu.getServerId());
            }
            if (isShowFirstRechargeIcon) {
                int firstRechargeActivityNum = this.activityLogic.getAwardNum(gu, gu.getServerId(), ActivityParentTypeEnum.FIRST_RECHARGE_ACTIVITY);
                rd.addOpenMenu(DynamicMenuEnum.ZYNS.getVal(), firstRechargeActivityNum);
            }

            // ??????????????????????????????????????????
            if (questionnaireService.isShowIcon(guId)) {
                rd.addOpenMenu(DynamicMenuEnum.QUESTIONNAIRE.getVal(), 1);
                rd.setIsJoinQuestionnaire(questionnaireService.isJoinQuestionnaire(guId));
            }
            //???????????????????????????
            if (digTreasureService.isShowDigTreasure(guId)) {
                rd.addOpenMenu(DynamicMenuEnum.DIG_FOR_TREASURE.getVal(), 1);
            }
            // ?????????????????????????????????
            if (this.activityService.hasJoinHeroBackActivity(gu)) {
                int gain = this.activityLogic.getAwardNum(gu, gu.getServerId(),
                        ActivityParentTypeEnum.HERO_BACK_ACTIVITY);
                rd.addOpenMenu(DynamicMenuEnum.YXGL.getVal(), gain);
            }

            // ????????????
            rd.setTotalRecharge(rechargeStatisticService.getTotalRecharge(guId));

            // ?????????????????????
            if (gu.gainSalaryUnawardDays() > 0) {
                rd.addOpenMenu(DynamicMenuEnum.SALARY.getVal());
            }
            // ??????
            rd.setBroadcast(this.broadcastService.getBroadcastInfo(gu.getServerId(), gu.getId()));

            rd.setDiceBuyTimes(userDiceService.getUserDiceInfo(guId).gainDiceBuyNum());
            rd.setDfzUseTimes(this.userTreasureRecordService.getUseTimes(guId, TreasureEnum.DFZ.getValue()));
            UserPayInfo userPayInfo = userPayInfoService.getUserPayInfo(gu.getId());
            // ?????????
            int endFightLevel = GameUserConfig.bean().getSzkUnlockLevel();
            int ableEndFight = userPayInfo.getEndFightBuyTime() != null ? 1 : 0;
            rd.setEndFightLevel(endFightLevel);
            rd.setAbleEndFight(ableEndFight);
            // ??????
            int ykRemainDays = this.rechargeCardProcessor.getYkRemainDays(userPayInfo);
            rd.setYkRemainDays(ykRemainDays);
            // ??????
            rd.setJkRemainDays(this.rechargeCardProcessor.getJkRemainDays(userPayInfo));
            List<Integer> list = mallService.getAbleBuyActivityMalls(guId);
            if (list != null && !list.isEmpty()) {
                for (Integer id : list) {
                    int type = DynamicMenuEnum.getGiftPack(id);
                    if (type == 0) {
                        continue;
                    }
                    rd.addOpenMenu(type, 1);
                }
            }
            // ????????????????????????
            List<UserLimit> userLimits = this.userService.getMultiItems(guId, UserLimit.class);
            // ????????????????????????????????????????????????????????????
            Optional<UserLimit> limitOptional = userLimits.stream().sorted(Comparator.comparing(UserLimit::getLimitBegin).reversed()).findFirst();
            // ??????????????????????????????????????????????????????????????????????????????????????????
            if (limitOptional.isPresent() && null != limitOptional.get().getLimitEnd() && DateUtil.toDateTimeLong(limitOptional.get().getLimitEnd()) > DateUtil.toDateTimeLong()) {
                rd.setUserLimitType(limitOptional.get().getType());
            }

            //??????  ???????????????
            int satisfaction = gu.getStatus().getSatisfaction();// ???????????????
            rd.setNvWaProgress(satisfaction);
            // ??????????????????
            int tyfFillCount = this.taiYFProcessor.getFilledSpecialIds(gu).size();
            rd.setTaiYiProgress(tyfFillCount);
            // ?????????????????????????????????
            int nightmareTyfFillCount = this.myTaiYFProcessor.getFillRecord(gu.getId()).getSpecialIds().size();
            rd.setNightmareTaiYiProgress(nightmareTyfFillCount);
            // ??????????????????
            this.userTmpStatusService.clearTmpStatusOnLogin(gu.getServerId(), guId);
            // ????????????
            if (gu.getLevel() >= 8) {
                int achievementAccomplishNum = this.redNoticeService.getNoticeNum(allNotices, ModuleEnum.ACHIEVEMENT);
                if (achievementAccomplishNum > 0) {
                    rd.addOpenMenu(DynamicMenuEnum.ACHIEVE.getVal(), achievementAccomplishNum);
                }
            }
            // ?????????????????????
//            if (this.redNoticeService.getNoticeNum(allNotices, ModuleEnum.PRIVILEGE, Privilege.TianlingYin.getValue()) > 0) {
//                rd.addOpenMenu(DynamicMenuEnum.LYLB.getVal(), 1);
//            }

            // ???????????????????????????
            int monsterCount = this.redNoticeService.getNoticeNum(allNotices, ModuleEnum.BUDDY_MONSTER, 10);
            if (monsterCount > 0) {
                rd.addOpenMenu(DynamicMenuEnum.MG.getVal(), monsterCount);
            }
            //?????????????????????
            RDNoticeInfo.ActivityShow show = wanXianLogic.getMenuIcon(guId);
            if (show != null && show.getShowType() > 0) {
                rd.addOpenMenu(show.getShowType(), show.getAwardNum());
            }
            //???????????????????????????
            if (wanXianLogic.isShowLogMenu(guId)) {
                rd.addOpenMenu(DynamicMenuEnum.WANXIAN_LOG.getVal(), 1);
            }
            // ????????????????????????
//            if (dfdjDateService.ifOpenDfdj(guId)) {
//                rd.addOpenMenu(DynamicMenuEnum.DFDJ.getVal(), 1);
//            }
            // ????????????
            if (godTrainingTaskService.ifShowTrainingTask(gu)) {
                rd.addOpenMenu(DynamicMenuEnum.GOD_TRAINING_TASK.getVal(), 1);
            }
            // ????????????
            RDAdventures rdAdventures = yeDProcessor.listAdventures(gu.getId());
            List<RDAdventures.RDAdventureInfo> arriveYeDList = rdAdventures.getAdventureList();
            if (ListUtil.isNotEmpty(arriveYeDList)) {
                rd.addOpenMenu(DynamicMenuEnum.ADVENTURE.getVal(), arriveYeDList.size());
            }
            //??????????????????
            AbstractSpecialYeGuaiProcessor yeGuaiProcessor = holidaySpecialYeGuaiFactory.getSpecialYeGuaiProcessor(guId);
            if (null != yeGuaiProcessor) {
                String youHunPos = yeGuaiProcessor.getYouHunPos(guId);
                rd.setYouHun(youHunPos);
            }
            //??????????????????????????????
            int gid = ServerTool.getServerGroup(gu.getServerId());
            boolean isChanjieOpen = chanjieService.hasFightingTime(gid);
            if (isChanjieOpen) {
                rd.addOpenMenu(DynamicMenuEnum.CHAN_JIE.getVal(), 1);
            }
            /*if (gu.getLevel() >= 20 && (app.runAsDev() || !isChanjieOpen)) {
                rd.addOpenMenu(DynamicMenuEnum.CHAN_JIE2.getVal(), 1);
            }*/
            //????????????7?????????
            if (newerGuideService.isPassNewerGuide(guId)) {
                RDNoticeInfo.ActivityShow icon = sevenLoginProcessor.showMenuIcon(gu);
                if (icon != null) {
                    rd.addOpenMenu(icon);
                }
            }
            /**
             * ??????????????????
             */
            if (holidayDigForTreasureProcessor.opened(gu.getServerId())) {
                rd.addOpenMenu(DynamicMenuEnum.DIG_FOR_TREASURE.getVal());
            }

            /**
             * ??????????????????????????????
             *
             */
            if (leaderCardService.showIcon(gu.getId())) {
                rd.addOpenMenu(DynamicMenuEnum.SHEN_WU_XIAN_SHI.getVal());
            }

            //???????????????????????????????????????
            if (roleTimeLimitPackProcessor.isShowIcon(gu.getId())) {
                rd.addOpenMenu(DynamicMenuEnum.ROLE_TIME_LIMIT_BAG.getVal());
            }

            //????????????????????????
            rd.setCurrentHexagram(hexagramBuffService.getHexagramBuffInfo(gu.getId()).getCurrentHexagram());

            //??????????????????
            rd.setBoostRemainTime(cardBoostProcessor.getRemainTime(guId));

            // ????????????????????????????????????
            UserNightmareMiXian nightmareMiXian = userService.getSingleItem(guId, UserNightmareMiXian.class);
            if (nightmareMiXian != null){
                int layers = nightmareMiXianService.incChallengeLayers(nightmareMiXian);
                rd.setRemainChallengeLayers(layers);
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return rd;
    }

    /**
     * ???0???|?????????????????????????????????????????????
     *
     * @param loginInfo
     * @return
     */
    public RDGameUser getSimpleGuInfo(LoginInfo loginInfo) {
        // ???????????????????????????
        RDGameUser rd = new RDGameUser();
        // ???????????????
        rd.setServerTime(System.currentTimeMillis() - 8 * 1000);

        GameUser usr = loginInfo.getUser();
        // ????????????
        rd.setDices(usr.getDice());
        // ?????????
        rd.setPvpTimes(fstServerService.getRemainChallengeNum(usr.getId()));

        // ???????????????
        rd.setNextBeatTime(this.monsterService.getRemainTimeToBeat(usr.getId()));

        // ????????????
        Optional<UserGod> userGod = this.godService.getAttachGod(usr);
        if (userGod.isPresent()) {
            UserGod god = userGod.get();
            rd.setGod(god.getBaseId());
            GodEnum godEnum = GodEnum.fromValue(god.getBaseId());
            if (godEnum.getType() == 10 || godEnum.getType() == 30) {
                rd.setGodRemainStep(god.getRemainStep());
            }
            if (godEnum.getType() == 20) {
                rd.setGodRemainTime(god.getAttachEndTime().getTime() - System.currentTimeMillis());
            }
            if (godEnum.equals(GodEnum.XZ)) {
                rd.setGodExt(god.getAttachWay().getValue() == WayEnum.HEXAGRAM.getValue() ? 4 : 1);
            }
        }
        //?????????????????????
        RDLuckyBeastInfo rdLuckyBeastInfo = luckyBeastService.getluckyBeastPosByLogin(usr.getId());
        if (null != rdLuckyBeastInfo) {
            rd.setLuckyBeasts(Arrays.asList(new RDGameUser.RDLuckyBeastPos(rdLuckyBeastInfo)));
        }

        // ??????????????????
        List<ServerGod> todayGods = this.godService.getUnAttachGods(usr);
        if (ListUtil.isNotEmpty(todayGods)) {
            rd.setGods(todayGods.stream().map(sg -> new RDGameUser.RDGod(sg)).collect(Collectors.toList()));
        }
        // ??????????????????
        if (yaoZuGenerateProcessor.isPassYaoZu(usr.getId())) {
            rd.setPassYaoZu(true);
        } else {
            List<UserYaoZuInfo> yaoZuInfos = this.userYaoZuInfoService.getUserYaoZu(usr.getId());
            rd.setYaoZus(yaoZuInfos.stream().filter(yaoZuInfo -> yaoZuInfo.getProgress() != YaoZuProgressEnum.BEAT_ONTOLOGY.getType())
                    .map(yz -> new RDGameUser.RDYaoZu(yz)).collect(Collectors.toList()));
        }

        // ?????????????????????????????????
        rd.setMallPrices(this.toRdMallPrice(loginInfo.getUser().getId()));
        //????????????????????????
        int defaultDeck = this.userCardGroupService.getUsingGroup(usr.getId(), CardGroupWay.Normal_Fight).getGroupNumber();
        rd.setDefaultDeck(defaultDeck);
        // ??????????????????
        this.updateLoginInfo(loginInfo, rd);
        this.incDiceService.limitIncDice(usr);
        rd.setLastDiceIncTime(userDiceService.getUserDiceInfo(usr.getId()).getDiceLastIncTime().getTime() / 1000);
        rd.setDices(usr.getDice());
        return rd;
    }

    private RDGameUser getGuInfo(GameUser gu) {
        String roleInfo = "???" + gu.getRoleInfo().getUserName() + "??????" + gu.getId() + "???";
        // ???????????????????????????
        RDGameUser rd = new RDGameUser();
        long uid = gu.getId();
        // ???????????????
        rd.setServerTime(System.currentTimeMillis() - 8 * 1000);
        rd.setShortServerName(userService.getOriServer(gu.getId()).getShortName());
        // ????????????
        rd.setGuId(uid);
        rd.setNickname(gu.getRoleInfo().getNickname());
        rd.setSex(gu.getRoleInfo().getSex());

        rd.setHead(gu.getRoleInfo().getHead());
        rd.setHeadIcon(gu.getRoleInfo().getHeadIcon());
        rd.setEmoticon(gu.getRoleInfo().getEmoticon());
        rd.setLevel(gu.getLevel());
        // ????????????
        rd.setExpRate(GameUserExpTool.getLevelExpRateByExp(gu.getExperience()).getExpRate());
        rd.setExperience(gu.getExperience() - GameUserExpTool.getExpByLevel(gu.getLevel()));
        rd.setCopper(gu.getCopper());
        rd.setGold(gu.getGold());
        rd.setDiamond(gu.getDiamond());
        this.incDiceService.limitIncDice(gu);
        log.info("{}after limitIncDice", roleInfo);
        rd.setLastDiceIncTime(userDiceService.getUserDiceInfo(uid).getDiceLastIncTime().getTime() / 1000);
        rd.setDices(gu.getDice());
        rd.setGoldEle(gu.getGoldEle());
        rd.setWoodEle(gu.getWoodEle());
        rd.setWaterEle(gu.getWaterEle());
        rd.setFireEle(gu.getFireEle());
        rd.setEarthEle(gu.getEarthEle());
        rd.setPosition(gu.getLocation().getPosition());
        rd.setDirection(gu.getLocation().getDirection());
        // ?????????
        rd.setMyInvitationCode(gu.getRoleInfo().getMyInvitationCode());
        // ????????????
        UserNewerGuide userNewerGuide = this.newerGuideService.getUserNewerGuide(gu.getId());
        rd.setGuideStatus(userNewerGuide.getNewerGuide());
        rd.setIsPassNewerGuide(userNewerGuide.getIsPassNewerGuide() ? 1 : 0);
        // ?????????
        rd.setPvpTimes(fstServerService.getRemainChallengeNum(uid));
        // ???????????????
        rd.setNextBeatTime(this.monsterService.getRemainTimeToBeat(uid));
        log.info("{}after next beat time", roleInfo);
        // ????????????
        List<UserTreasureEffect> utEffects = this.userTreasureEffectService.getTreasureEffects(uid);
        if (ListUtil.isNotEmpty(utEffects)) {
            List<RDEffectTreasure> usedTreasures = utEffects.stream().filter(ute -> ute.getRemainEffect() > 0 || ute.getRemainEffect() == -1 || (ute.getBaseId() == TreasureEnum.MBX.getValue() && gu.ifMbxOpen())).map(ute -> new RDGameUser.RDEffectTreasure(ute)).collect(Collectors.toList());
            rd.setUsedTreasures(usedTreasures);
        }
        // ???????????????
        rd.setUseShoe(gu.getSetting().getActiveMbx());
        // ????????????????????????0?????????????????????????????????????????????????????????????????????????????????????????????????????????
        UserTreasureEffect mbxEffect = utEffects.stream().filter(tmp -> tmp.getBaseId() == TreasureEnum.MBX.getValue()).findFirst().orElse(null);
        if (this.userTreasureEffectService.isTreasureEffect(mbxEffect)) {
            rd.setMbxRemainForcross(1);
        } else {
            rd.setMbxRemainForcross(0);
        }
        log.info("{}after treasure effect", roleInfo);
        // ????????????
        Optional<UserGod> userGod = this.godService.getAttachGod(gu);
        if (userGod.isPresent()) {
            UserGod god = userGod.get();
            rd.setGod(god.getBaseId());
            GodEnum godEnum = GodEnum.fromValue(god.getBaseId());
            if (godEnum.getType() == 10 || godEnum.getType() == 30) {
                rd.setGodRemainStep(god.getRemainStep());
            } else if (godEnum.getType() == 20) {
                rd.setGodRemainTime(god.getAttachEndTime().getTime() - System.currentTimeMillis());
            }
            if (godEnum.equals(GodEnum.XZ)) {
                rd.setGodExt(god.getAttachWay().equals(WayEnum.HEXAGRAM) ? 4 : 1);
            }
        }
        log.info("{}after user god", roleInfo);
        //?????????????????????
        RDLuckyBeastInfo rdLuckyBeastInfo = luckyBeastService.getluckyBeastPosByLogin(uid);
        if (null != rdLuckyBeastInfo) {
            rd.setLuckyBeasts(Arrays.asList(new RDGameUser.RDLuckyBeastPos(rdLuckyBeastInfo)));
        }

        //????????????
        if (digTreasureService.isShowDigTreasure(uid)) {
            RDDigTreasureInfo rdDigForTreasureInfo = digTreasureService.refreshMyDigStatusToNewPalac(uid, gu.getLocation().getPosition());
            rd.setDigTreasureInfo(rdDigForTreasureInfo);
        }
        log.info("{}after dig treasure", roleInfo);
        // ??????????????????
        List<ServerGod> todayGods = this.godService.getUnAttachGods(gu);
        rd.setGods(todayGods.stream().map(sg -> new RDGameUser.RDGod(sg)).collect(Collectors.toList()));

        // ??????????????????
        if (yaoZuGenerateProcessor.isPassYaoZu(uid)) {
            rd.setPassYaoZu(true);
        } else {
            List<UserYaoZuInfo> yaoZuInfos = this.userYaoZuInfoService.getUserYaoZu(uid);
            rd.setYaoZus(yaoZuInfos.stream().filter(yaoZuInfo -> yaoZuInfo.getProgress() != YaoZuProgressEnum.BEAT_ONTOLOGY.getType())
                    .map(yz -> new RDGameUser.RDYaoZu(yz)).collect(Collectors.toList()));
        }
        log.info("{}after yao zu", roleInfo);
        // ???????????????
        //????????????
        List<UserSpecial> uSpecials = this.userSpecialService.getUnLockSpecials(uid);
        if (ListUtil.isNotEmpty(uSpecials)) {
            List<Integer> excludeBagSpecialIds = SpecialTool.getAllExcludeBagSpecialIds();
            List<UserSpecial> list = uSpecials.stream().filter(p -> !excludeBagSpecialIds.contains(p.getBaseId())).collect(Collectors.toList());
            if (list.size() > 65) {
                list = list.subList(0, list.size() - 65);
                this.userService.deleteItems(uid, list);
                LogUtil.logDeletedUserDatas(list, "????????????????????????");
                uSpecials = this.userSpecialService.getOwnSpecials(gu.getId());
            }
        }
        //????????????
        List<UserSpecial> pocketSpecials = this.userSpecialService.getLockSpecials(uid);
        List<RDSpecail> rdSpecails = new ArrayList<RDSpecail>();
        for (UserSpecial us : uSpecials) {
            rdSpecails.add(RDSpecail.fromUserSpecail(us));
        }
        for (UserSpecial ups : pocketSpecials) {
            rdSpecails.add(RDSpecail.instancePocketSpecail(ups));
        }
        rd.setSpecials(rdSpecails);
        log.info("{}after special", roleInfo);
        // ????????????
        List<UserTreasure> uTreasures = this.userTreasureService.getAllUserTreasures(uid);
        if (ListUtil.isNotEmpty(uTreasures)) {
            List<UserTreasure> toDelsList = uTreasures.stream().filter(tmp -> tmp.gainTotalNum() == 0 || TreasureTool.getTreasureById(tmp.getBaseId()) == null).collect(Collectors.toList());
            if (ListUtil.isNotEmpty(toDelsList)) {
                LogUtil.logDeletedUserDatas(toDelsList, "???????????????0?????????????????????");
                this.userService.deleteItems(uid, toDelsList);
                uTreasures = this.userTreasureService.getAllUserTreasures(uid);
            }
            toDelsList = new ArrayList<>();
            Map<Integer, List<UserTreasure>> utMaps = uTreasures.stream().collect(Collectors.groupingBy(UserTreasure::getBaseId));
            Set<Integer> utBaseIds = utMaps.keySet();
            for (Integer utBaseId : utBaseIds) {
                List<UserTreasure> uts = utMaps.get(utBaseId);
                if (uts.size() > 1) {
                    int num = uts.stream().mapToInt(UserTreasure::gainTotalNum).max().orElse(0);
                    toDelsList.addAll(uts.subList(1, uts.size()));
                    UserTreasure uTreasure = uts.get(0);
                    uTreasure.setOwnNum(num);
                    this.userService.updateItem(uTreasure);
                }
            }
            if (ListUtil.isNotEmpty(toDelsList)) {
                LogUtil.logDeletedUserDatas(toDelsList, "?????????????????????");
                this.userService.deleteItems(uid, toDelsList);
                uTreasures = this.userTreasureService.getAllUserTreasures(uid);
            }
            rd.setUserTreasures(uTreasures.stream().map(ut -> new RDGameUser.RDTreasure(ut)).collect(Collectors.toList()));
        }
        log.info("{}after treasure", roleInfo);
        // ????????????
        List<UserCard> uCards = this.userCardService.getUserCardsAsLogin(gu.getId());
        List<RDCardGroup> decks = new ArrayList<RDGameUser.RDCardGroup>();
        int defaultDeck = 1;
        List<RDCard> rdCards = new ArrayList<>();
        //?????????????????????
        Optional<UserLeaderCard> leaderCardOp = leaderCardService.getUserLeaderCardOp(uid);
        if (leaderCardOp.isPresent()) {
            UserLeaderCard leaderCard = leaderCardOp.get();
            rdCards.add(RDCard.instance(leaderCard));
        }
        //??????????????????????????????
        List<RDGameUser.RdCardZhiBao> rdCardZhiBaos = new ArrayList<>();
        List<UserCardZhiBao> userCardZhiBaos = userCardZhiBaoService.getUserCardZhiBaosAsLogin(uid);
        if (ListUtil.isNotEmpty(userCardZhiBaos)) {
            rdCardZhiBaos.addAll(userCardZhiBaos.stream().map(RDGameUser.RdCardZhiBao::instance).collect(Collectors.toList()));
        }
        rd.setCardZhiBaos(rdCardZhiBaos);
        //???????????????????????????
        if (ListUtil.isNotEmpty(uCards)) {
            if (app.runAsDevFZJ()) {
                rdCards.addAll(uCards.stream().filter(p -> p.getBaseId() < 258 || p.getBaseId() > 260).map(RDCard::instance).collect(Collectors.toList()));
            } else {
                //??????????????????????????????
                List<UserCardZhiBao> allZhiBaos = userCardZhiBaoService.getUserCardZhiBaos(uid);
                //??????????????????????????????
                List<UserCardXianJue> allXianJues = userCardXianJueService.getUserCardXianJues(uid);

                for (UserCard userCard : uCards) {
                    RDCard rdCard = RDCard.instance(userCard);
                    //?????????????????????
                    if (ListUtil.isNotEmpty(allZhiBaos)) {
                        //????????????????????????
                        List<UserCardZhiBao> singleCardZhiBaos = allZhiBaos.stream().filter(tmp -> tmp.ifPutOnCard(userCard.getBaseId())).collect(Collectors.toList());
                        //?????????????????????
                        if (ListUtil.isNotEmpty(singleCardZhiBaos)) {
                            //????????????????????????
                            List<RDGameUser.RdCardZhiBao> rdZhiBaos = singleCardZhiBaos.stream().map(RDGameUser.RdCardZhiBao::instance).collect(Collectors.toList());
                            List<RDGameUser.RdCardZhiBao> cloneZhiBaos = CloneUtil.cloneList(rdZhiBaos);
                            rdCard.setZhiBaos(cloneZhiBaos);

                        }
                    }
                    //?????????????????????
                    if (ListUtil.isNotEmpty(allXianJues)) {
                        //????????????????????????
                        List<UserCardXianJue> singleCardXianJues = allXianJues.stream().filter(tmp -> tmp.ifPutOnCard(userCard.getBaseId())).collect(Collectors.toList());
                        //?????????????????????
                        if (ListUtil.isNotEmpty(singleCardXianJues)) {
                            //????????????????????????
                            rdCard.setXianJues(singleCardXianJues.stream().map(RDGameUser.RdCardXianJue::instance).collect(Collectors.toList()));

                        }
                    }
                    rdCards.add(rdCard);
                }
            }
            defaultDeck = this.userCardGroupService.getUsingGroup(uid, CardGroupWay.Normal_Fight).getGroupNumber();
        }
        rd.setCards(rdCards);
        List<UserCardGroup> userCardGroups = this.userCardGroupService.getUserCardGroups(uid, CardGroupWay.Normal_Fight);
        for (UserCardGroup cardGroup : userCardGroups) {
            RDCardGroup rdCardGroup = new RDCardGroup();
            rdCardGroup.setCardGroupId(cardGroup.getId());
            rdCardGroup.setName(cardGroup.getName());
            rdCardGroup.setDeck(cardGroup.getGroupNumber());
            rdCardGroup.setCardIds(cardGroup.getCards());
            rdCardGroup.setFuCeId(cardGroup.getFuCe());
            decks.add(rdCardGroup);
        }
        rd.setDecks(decks);
        rd.setDefaultDeck(defaultDeck);
        log.info("{}after card", roleInfo);
        // ??????????????????
        List<UserCity> uCities = userCityService.getUserCities(uid);
        if (ListUtil.isNotEmpty(uCities)) {
            rd.setManors(uCities.stream().filter(p -> p.isOwn()).map(RDGameUser.RDChengc::new).collect(Collectors.toList()));
        }
        rd.setFaTanTotalLv(faTanService.getUserFaTanDaTa(uid, uCities));
        // ????????????
        if (gu.getStatus().getCurWordType() == WorldType.TRANSMIGRATION.getValue()) {
            GameTransmigration curTransmigration = gameTransmigrationService.getCurTransmigration(userService.getActiveGid(uid));
            if (null != curTransmigration) {
                rd.setMainCityDefenderTypes(curTransmigration.getMainCityDefenderTypes());
            } else {
                //?????????????????????????????????
                gu.getStatus().setCurWordType(gu.getStatus().getPreWordType());
                gu.updateStatus();
            }
        }
        rd.setPreWorldType(gu.getStatus().getPreWordType());
        rd.setCurWorldType(gu.getStatus().getCurWordType());
        // ????????????
        List<UserNightmareCity> nightmareCities = userCityService.getUserOwnNightmareCities(uid);
        if (ListUtil.isNotEmpty(nightmareCities)) {
            List<Integer> idList = nightmareCities.stream().map(UserNightmareCity::getBaseId).collect(Collectors.toList());
            List<RDGameUser.RDChengc> list = new ArrayList<>();
            for (Integer id : idList) {
                Optional<RDGameUser.RDChengc> op = rd.getManors().stream().filter(p -> p.getId().equals(id)).findFirst();
                if (op.isPresent()) {
                    list.add(CloneUtil.clone(op.get()));
                }
            }
            rd.setNightmareManors(list);
        }
        log.info("{}after city", roleInfo);
        // ?????????????????????????????????
        rd.setMallPrices(this.toRdMallPrice(uid));


        // ??????
        // ?????????????????????????????????????????????????????????????????????
        boolean growTaskCompleted = gu.getStatus().isGrowTaskCompleted();
        // ????????????????????????????????????????????????????????????????????????
        if (!growTaskCompleted) {
            growTaskCompleted = this.growTaskService.isPassGrowTask(gu);
            if (growTaskCompleted) {
                gu.getStatus().setGrowTaskCompleted(true);
                gu.updateStatus();
            }
        } else {
            // ????????????
            this.growTaskService.delGrowTasks(uid);
        }

        return rd;
    }

    /**
     * ??????????????????,???0???
     *
     * @param loginInfo
     * @param rd
     */
    private void updateLoginInfo(LoginInfo loginInfo, RDGameUser rd) {

        Date now = DateUtil.now();
        int days = 0;
        UserLoginInfo uLoginInfo = this.userService.getSingleItem(loginInfo.getUser().getId(), UserLoginInfo.class);
        if (null == uLoginInfo) {
            // ?????????????????????
            uLoginInfo = UserLoginInfo.instance(loginInfo.getUser().getId());
            this.userService.addItem(loginInfo.getUser().getId(), uLoginInfo);
            days = 1;
        } else {
            // ????????????????????????????????????
            days = DateUtil.getDaysBetween(uLoginInfo.getLastLoginTime(), now);
        }
        if (days > 0) {
            rd.setFirstTodayLogin(1);
            LoginEventPublisher.pubFirstLoginEvent(loginInfo, rd);
        }
        InsRoleInfoEntity role = new InsRoleInfoEntity();
        role.setUid(loginInfo.getUser().getId());
        role.setLastLoginDate(DateUtil.getTodayInt());
        updateRoleInfoAsyncHandler.setRoleInfo(role, 3);
        uLoginInfo.setLastLoginTime(now);
        // ip
        if (StrUtil.isNotBlank(loginInfo.getIp())) {
            uLoginInfo.setLastLoginIp(loginInfo.getIp());
        }

        // ???????????????
        if (StrUtil.isNotBlank(loginInfo.getToken())) {
            uLoginInfo.setToken(loginInfo.getToken());
        }
        // ?????????????????????
        if (StrUtil.isNotBlank(loginInfo.getDeviceId())) {
            String[] deviceInfo = loginInfo.getDeviceId().split("&");
            uLoginInfo.setDeviceId(deviceInfo[0]);
            if (deviceInfo.length >= 2) {
                uLoginInfo.setPhone(deviceInfo[1]);
            }
        }
        this.userService.updateItem(uLoginInfo);
    }

    /**
     * ?????????????????????????????????
     *
     * @param guId
     * @return
     */
    private List<RDMallPrice> toRdMallPrice(long guId) {
        int sId = this.userService.getActiveSid(guId);
        boolean isDiscount = this.activityService.isActive(sId, ActivityEnum.MALL_DISCOUNT);
        List<CfgMallEntity> treasureMall = MallTool.getMallConfig().getTreasureMalls();
        return treasureMall.stream().map(tm -> new RDGameUser.RDMallPrice(tm.getGoodsId(), tm.getPrice(isDiscount))).collect(Collectors.toList());

    }
}