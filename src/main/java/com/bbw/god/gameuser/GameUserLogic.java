package com.bbw.god.gameuser;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bbw.common.*;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.ConsumeType;
import com.bbw.god.activity.ActivityService;
import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.config.ActivityParentTypeEnum;
import com.bbw.god.activityrank.ActivityRankEnum;
import com.bbw.god.activityrank.ActivityRankService;
import com.bbw.god.cache.ShareCacheUtil;
import com.bbw.god.cache.ShareCacheUtil.ShareStatus;
import com.bbw.god.city.UserCityService;
import com.bbw.god.city.chengc.UserCity;
import com.bbw.god.city.taiyf.TaiYFProcessor;
import com.bbw.god.city.taiyf.mytaiyf.MYTaiYFProcessor;
import com.bbw.god.db.async.UpdateRoleInfoAsyncHandler;
import com.bbw.god.db.entity.InsRoleInfoEntity;
import com.bbw.god.detail.async.MallDetailAsyncHandler;
import com.bbw.god.detail.async.MallDetailEventParam;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.award.AwardService;
import com.bbw.god.game.award.AwardStatus;
import com.bbw.god.game.chanjie.ChanjieUserInfo;
import com.bbw.god.game.chanjie.service.ChanjieUserService;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.game.config.server.ServerTool;
import com.bbw.god.game.config.special.CfgSpecialEntity;
import com.bbw.god.game.config.special.SpecialTool;
import com.bbw.god.game.config.treasure.CfgTreasureEntity;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.game.config.treasure.TreasureTool;
import com.bbw.god.game.config.treasure.TreasureType;
import com.bbw.god.game.sxdh.SxdhRankService;
import com.bbw.god.game.sxdh.SxdhZone;
import com.bbw.god.game.sxdh.SxdhZoneService;
import com.bbw.god.game.sxdh.config.SxdhRankType;
import com.bbw.god.game.transmigration.TransmigrationGlobalEnterService;
import com.bbw.god.game.transmigration.cfg.TransmigrationTool;
import com.bbw.god.gameuser.achievement.AchievementTool;
import com.bbw.god.gameuser.achievement.UserAchievementService;
import com.bbw.god.gameuser.buddy.BuddyLogic;
import com.bbw.god.gameuser.card.OppCardService;
import com.bbw.god.gameuser.card.UserCard;
import com.bbw.god.gameuser.card.UserCardService;
import com.bbw.god.gameuser.card.UserShowCard;
import com.bbw.god.gameuser.config.GameUserConfig;
import com.bbw.god.gameuser.dice.IncDiceService;
import com.bbw.god.gameuser.dice.RDGainDice;
import com.bbw.god.gameuser.dice.UserDiceInfo;
import com.bbw.god.gameuser.dice.UserDiceService;
import com.bbw.god.gameuser.guide.NewerGuideService;
import com.bbw.god.gameuser.knapsack.GameUserAssetService;
import com.bbw.god.gameuser.privilege.PrivilegeService;
import com.bbw.god.gameuser.res.ResChecker;
import com.bbw.god.gameuser.res.ResEventPublisher;
import com.bbw.god.gameuser.shake.ShakeService;
import com.bbw.god.gameuser.task.grow.NewbieTaskService;
import com.bbw.god.gameuser.task.grow.UserGrowTask;
import com.bbw.god.gameuser.treasure.TreasureChecker;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.login.DynamicMenuEnum;
import com.bbw.god.login.RDGameUser;
import com.bbw.god.nightmarecity.chengc.UserNightmareCity;
import com.bbw.god.rd.*;
import com.bbw.god.server.ServerUserService;
import com.bbw.god.server.fst.FstRanking;
import com.bbw.god.server.fst.server.FstServerService;
import com.bbw.god.server.guild.service.GuildUserService;
import com.bbw.god.server.maou.ServerMaouStatus;
import com.bbw.god.server.maou.ServerMaouStatusInfo;
import com.bbw.god.server.maou.bossmaou.ServerBossMaou;
import com.bbw.god.server.maou.bossmaou.ServerBossMaouProcessor;
import com.bbw.god.server.maou.bossmaou.ServerBossMaouService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class GameUserLogic {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private ServerUserService serverUserService;
    @Autowired
    private FstServerService fstServerService;
    @Autowired
    private ServerBossMaouProcessor bossMaouProcessor;
    @Autowired
    private ServerBossMaouService bossMaouService;
    @Autowired
    private ActivityRankService activityRankService;
    @Autowired
    private TaiYFProcessor taiYFProcessor;
    @Autowired
    private MYTaiYFProcessor nightmareTaiYFProcessor;
    @Autowired
    private AwardService awardService;
    @Autowired
    private IncDiceService incDiceService;
    @Autowired
    private PrivilegeService privilegeService;
    @Autowired
    private ShakeService shakeService;
    @Autowired
    private GuildUserService guildUserService;
    @Autowired
    private UserCityService userCityService;
    @Autowired
    private ChanjieUserService chanjieUserService;
    @Autowired
    private UserCardService userCardService;
    @Autowired
    private SxdhRankService sxdhRankService;
    @Autowired
    private UserAchievementService userAchievementService;
    @Autowired
    private BuddyLogic buddyLogic;
    @Autowired
    private NewbieTaskService newbieTaskService;
    @Autowired
    private GameUserAssetService gameUserAssetService;
    @Autowired
    private SxdhZoneService sxdhZoneService;
    @Autowired
    private NewerGuideService newerGuideService;
    @Autowired
    private ActivityService activityService;
    @Autowired
    private UserDiceService userDiceService;
    @Autowired
    private TransmigrationGlobalEnterService globalEnterService;
    @Autowired
    private MallDetailAsyncHandler mallDetailAsyncHandler;
    @Autowired
    private UpdateRoleInfoAsyncHandler updateRoleInfoAsyncHandler;
    @Autowired
    private OppCardService oppCardService;

    /**
     * 购买体力
     *
     * @param guId
     * @return
     */
    public RDDiceBuy buyDice(long guId) {
        GameUser gu = this.gameUserService.getGameUser(guId);
        if (gu.ifMaxDice()) {
            throw new ExceptionForClientTip("gu.dice.outOfLimit");
        }
        UserDiceInfo userDiceInfo = userDiceService.getUserDiceInfo(guId);
        int diceBuyTimes = userDiceInfo.gainDiceBuyNum();
        diceBuyTimes++;
        int needGold = this.getNeedGoldForTlBuy(diceBuyTimes);
        ResChecker.checkGold(gu, needGold);
        int addDice = 80;
        RDDiceBuy rd = new RDDiceBuy();
        ResEventPublisher.pubGoldDeductEvent(guId, needGold, WayEnum.BUY_DICE, rd);
        ResEventPublisher.pubDiceAddEvent(guId, addDice, WayEnum.BUY_DICE, rd);
        mallDetailAsyncHandler.log(new MallDetailEventParam(guId, AwardEnum.TL.getValue(), 0, "体力", needGold, addDice, needGold, ConsumeType.GOLD, gu.getGold()));
        userDiceInfo.updateDiceBuyTimes(diceBuyTimes);
        gameUserService.updateItem(userDiceInfo);
        rd.setDiceBuyTimes(diceBuyTimes);
        return rd;
    }

    /**
     * 更改漫步靴使用状态
     */
    public RDCommon changeStatusForMBX(long guId, int useShoeStatus) {
        RDCommon rd = new RDCommon();
        // 更新漫步靴的状态
        GameUser gu = this.gameUserService.getGameUser(guId);
        gu.getSetting().setActiveMbx(useShoeStatus);
        gu.updateSetting();
        // 充值漫步靴
        TreasureEventPublisher.pubMBXEffectSetEvent(guId, 0, WayEnum.NONE);
        return rd;
    }

    private int getNeedGoldForTlBuy(int useTimes) {
        int needGold = 0;
        if (useTimes <= 6) {
            needGold = 50 + (--useTimes / 2 * 10);
        } else if (useTimes < 12) {
            needGold = (++useTimes) * 10;
        } else {
            needGold = 120;
        }
        return needGold;
    }

    /**
     * 领取俸禄
     *
     * @param guId
     * @return
     */
    public RDSalaryCopper getCopper(long guId) {
        GameUser gu = this.gameUserService.getGameUser(guId);
        // 获取未领取俸禄的天数
        int unAwardDays = gu.gainSalaryUnawardDays();
        if (unAwardDays == 0) {
            throw new ExceptionForClientTip("gu.salary.copper.already.got");
        }
        // 等级俸禄
        int levelCopper = gu.getLevel() * 1000 + 10000;
        levelCopper *= unAwardDays;// 按未领取俸禄的天数做成倍数
        int manorCopper = 0;
        // 封地俸禄
        List<UserCity> userCities = userCityService.getUserCities(guId);
        if (ListUtil.isNotEmpty(userCities)) {
            for (UserCity uc : userCities) {
                manorCopper += uc.gainCity().getLevel() * 1000;
            }
        }
        manorCopper *= unAwardDays;

        // 总俸禄
        int addedCopper = levelCopper + manorCopper;

        RDSalaryCopper rd = new RDSalaryCopper();
        rd.setLevelCopper(levelCopper);
        rd.setManorCopper(manorCopper);
        ResEventPublisher.pubCopperAddEvent(guId, addedCopper, WayEnum.SALARY_COPPER, rd);
        gu.getStatus().setSalaryCopperTime(DateUtil.now());
        gu.updateStatus();
        return rd;
    }

    /**
     * 获取特殊头像
     *
     * @param guId
     * @return
     */
    public RDGameUser getHeadList(long guId) {
        RDGameUser rd = new RDGameUser();
        rd.setListHeads(gameUserAssetService.getAllHeadIds(guId));
        return rd;
    }

    /**
     * 获取头像框
     *
     * @param guId
     * @return
     */
    public RDGameUser getHeadIconList(long guId) {
        RDGameUser rd = new RDGameUser();
        rd.setListHeadIcons(gameUserAssetService.getAllHeadIcons(guId));
        return rd;
    }

    /**
     * 设置头像
     *
     * @param guId
     * @return
     */
    public RDSuccess setHead(long guId, int head) {
        List<Integer> iconList = gameUserAssetService.getAllHeadIds(guId);
        if (head > 10000) {
            UserCard userCard = userCardService.getUserCard(guId, head);
            if (userCard == null) {
                throw new ExceptionForClientTip("role.head.not.own");
            }
        } else if (head > 3000 && !iconList.contains(head)) {
            // 非法设置头像 卡牌头像低于3000
            throw new ExceptionForClientTip("role.head.not.own");
        }
        GameUser gu = this.gameUserService.getGameUser(guId);
        gu.getRoleInfo().setHead(head);
        gu.updateRoleInfo();
        return new RDSuccess();
    }

    /**
     * 设置头像框
     *
     * @param guId
     * @return
     */
    public RDSuccess setHeadIcon(long guId, Integer icon) {
        GameUser gu = this.gameUserService.getGameUser(guId);
        //玩家拥有的头像ID
        List<Integer> iconList = gameUserAssetService.getAllHeadIcons(guId);
        if (!iconList.contains(icon)) {
            // 非法设置外框
            throw new ExceptionForClientTip("role.headicon.not.own");
        }
        gu.getRoleInfo().setHeadIcon(icon);
        gu.updateRoleInfo();
        return new RDSuccess();
    }

    /**
     * 修改昵称
     *
     * @param guId
     * @param nickname
     * @return
     */
    public RDSuccess rename(long guId, String nickname) {
        GameUser gu = this.gameUserService.getGameUser(guId);
        // 昵称必须在合服区里保持唯一，这里必须使用合服ID
        Optional<Long> uid = this.serverUserService.getUidByNickName(gu.getServerId(), nickname);
        if (uid.isPresent()) {
            throw new ExceptionForClientTip("createrole.nickname.is.exist");
        }

        int needGold = GameUserConfig.bean().getNumRenameGold();
        ResChecker.checkGold(gu, needGold);
        ResEventPublisher.pubGoldDeductEvent(guId, needGold, WayEnum.RENAME, new RDCommon());

        gu.getRoleInfo().setNickname(nickname);
        gu.updateRoleInfo();
        InsRoleInfoEntity role = new InsRoleInfoEntity();
        role.setUid(guId);
        role.setNickname(nickname);
        updateRoleInfoAsyncHandler.setRoleInfo(role, 2);
        return new RDSuccess();
    }

    /**
     * 获得玩家统计信息
     *
     * @param guId
     * @return
     */
    public RDGuStatistic gainUserStatisticInfo(long guId) {
        RDGuStatistic rd = new RDGuStatistic();
        GameUser gu = this.gameUserService.getGameUser(guId);
        // 富豪榜排行
        int weekCopperRank = this.activityRankService.getRank(guId, ActivityRankEnum.FUHAO_RANK);
        int fstRank = fstServerService.getFstRank(guId);// 封神台排行
        int satisfaction = gu.getStatus().getSatisfaction();// 女娲好感度
        // 已捐赠特产数
        int tyfFillCount = this.taiYFProcessor.getFilledSpecialIds(gu).size();

        rd.setWeekCopperRank(weekCopperRank);
        rd.setPvpRank(fstRank);
        rd.setSatisfaction(satisfaction);
        rd.setTyfFillCount(tyfFillCount);
        return rd;
    }

    /**
     * 获取未捐赠的特产
     *
     * @param guId
     * @return
     */
    public RDGuStatistic gainUnfilledSpecials(long guId) {
        GameUser gu = this.gameUserService.getGameUser(guId);
        List<Integer> filledSpecialIds = this.taiYFProcessor.getFilledSpecialIds(gu);
        //梦魇太一府已捐特产
        List<Integer> nightmareFilledSpecialIds = this.nightmareTaiYFProcessor.getFillRecord(gu.getId()).getSpecialIds();
        //梦魇太一府本轮特产
        List<Integer> nightmareRoundSpecialIds = this.nightmareTaiYFProcessor.getRoundSpecialIds(gu.getId(), false);
        List<CfgSpecialEntity> lowHighSpecials = SpecialTool.getLowHighSpecials().stream().filter(s -> !s.isUpdateSpecial()).collect(Collectors.toList());
        List<Integer> unfilledSpecialIds;
        List<Integer> nightmareUnfilledSpecialIds;
        if (ListUtil.isNotEmpty(filledSpecialIds)) {
            unfilledSpecialIds = lowHighSpecials.stream().filter(special -> !filledSpecialIds.contains(special.getId())).map(CfgSpecialEntity::getId).collect(Collectors.toList());
        } else {
            unfilledSpecialIds = lowHighSpecials.stream().map(CfgSpecialEntity::getId).collect(Collectors.toList());
        }
        if (ListUtil.isNotEmpty(nightmareFilledSpecialIds)) {
            nightmareUnfilledSpecialIds = nightmareRoundSpecialIds.stream().filter(roundSpecialIds -> !nightmareFilledSpecialIds.contains(roundSpecialIds)).collect(Collectors.toList());
        } else {
            nightmareUnfilledSpecialIds = nightmareRoundSpecialIds;
        }
        RDGuStatistic rd = new RDGuStatistic();
        rd.setUnfilledSpecialIds(unfilledSpecialIds);
        rd.setNightmareUnfilledSpecialIds(nightmareUnfilledSpecialIds);
        return rd;
    }

    /**
     * 获得战斗信息
     *
     * @param guId
     * @return
     */
    public RDFightInfo gainFightInfo(long guId, int sid) {
        Optional<ServerBossMaou> optional = this.bossMaouService.getCurBossMaou(guId, sid);

        if (!optional.isPresent()) {
            RDFightInfo rd = new RDFightInfo();
            rd.setMaouStatus(ServerMaouStatus.OVER.getValue());
            return rd;
        }
        ServerMaouStatusInfo maouStatus = this.bossMaouProcessor.getMaouStatus(optional.get());
        RDFightInfo rd = new RDFightInfo();
        rd.setMaouRemainTime(maouStatus.getRemainTime());
        rd.setMaouStatus(maouStatus.getStatus());
        return rd;
    }

    /**
     * 获得分享奖励
     *
     * @param guId
     * @return
     */
    public RDCommon getShareAward(long guId, String type, String cardIdP) {
        RDCommon rd = new RDCommon();
        if (type != null && type.equals("newCard")) {
            Integer cardId = Integer.valueOf(cardIdP);
            ShareStatus shareStatus = ShareCacheUtil.getShareableCard(guId, cardId);
            if (shareStatus == ShareStatus.ENABLE_AWARD) {
                // 更新缓存
                ShareCacheUtil.setShareableCard(guId, cardId, ShareStatus.NO_AWARD);
                // 发放元宝
                int awardGold = GameUserConfig.bean().getShareCardAwardGold();
                ResEventPublisher.pubGoldAddEvent(guId, awardGold, WayEnum.SHARE, rd);
            }
        } else {
            ShareStatus shareStatus = ShareCacheUtil.getShareableAttack(guId);
            if (shareStatus == ShareStatus.ENABLE_AWARD) {
                // 更新缓存
                ShareCacheUtil.setShareableAttack(guId, ShareStatus.NO_AWARD);
                // 发放宝箱
                TreasureEventPublisher.pubTAddEvent(guId, TreasureEnum.BX.getValue(), 1, WayEnum.SHARE, rd);
            }
        }
        return rd;
    }

    /**
     * 兑换码兑换
     *
     * @param guId
     * @param url
     * @return
     */
    public RDCommon gainPacks(long guId, String url, String exchangeCode) {
        String returnData = HttpClientUtil.doGet(url);
        JSONObject data = JSON.parseObject(returnData);
        if (data == null) {
            throw new ExceptionForClientTip("exchange.pack.error", exchangeCode);
        }
        int res = data.getIntValue("res");
        if (res == 1) {
            throw ExceptionForClientTip.fromMsg(data.getString("message"));
        }
        JSONArray awardsArray = data.getJSONArray("awards");
        RDCommon rd = new RDCommon();
        this.awardService.fetchAward(guId, awardsArray.toString(), WayEnum.EXCHANGE_DH, "", rd);
        return rd;
    }

    /**
     * 客户端定时获取体力
     *
     * @param uid
     * @return
     */
    public RDGainDice gainDice(long uid) {
        RDGainDice rd = new RDGainDice();
        GameUser gu = this.gameUserService.getGameUser(uid);
        this.incDiceService.limitIncDice(gu);
        rd.setDice(gu.getDice());
        rd.setLastDiceIncTime(userDiceService.getUserDiceInfo(uid).getDiceLastIncTime().getTime() / 1000);
        return rd;

    }

    /**
     * 获得灵印的状态
     *
     * @param uid
     * @return
     */
    public RDLingYinStatus getTianlingStatus(long uid) {
        GameUser gu = this.gameUserService.getGameUser(uid);
        AwardStatus status = this.privilegeService.getTianlingStatus(gu);
        RDLingYinStatus rd = new RDLingYinStatus();
        rd.setStatus(status.getValue());
        return rd;
    }

    /**
     * 获取玩家信息
     *
     * @param myuid 我的uid
     * @param uid   需要查询信息的uid
     * @return
     */
    public RDUserInfo getUserInfoByUid(long myuid, long uid) {
        int sid = this.gameUserService.getActiveSid(uid);
        RDUserInfo rd = new RDUserInfo();
        rd.setShortServerName(ServerTool.getServerShortName(sid));
        GameUser gu = this.gameUserService.getGameUser(uid);
        rd.setNickname(gu.getRoleInfo().getNickname());
        rd.setLevel(gu.getLevel());
        String guildName = this.guildUserService.getGuildName(sid, uid);
        if (StrUtil.isBlank(guildName)) {
            rd.setGuild("无");
        } else {
            rd.setGuild(guildName);
        }
        // 封神台
        int fstRank = fstServerService.getFstRank(uid);
        Optional<FstRanking> optional = fstServerService.getFstRanking(uid);
        String windRate = "0%";
        if (optional.isPresent()) {
            FstRanking ranking = optional.get();
            windRate = MathTool.getRate(ranking.getWinTimes(), ranking.getChallengeTotalTimes());
        }
        RDUserInfo.ItemInfo fstInfo = RDUserInfo.ItemInfo.instance(UserInfoEnum.FST, fstRank, windRate);
        rd.addItemInfo(fstInfo);
        //卡牌集
        List<UserCard> cards = oppCardService.getOppAllCards(uid);
        String rate = MathTool.getRate(cards.size(), CardTool.allCardsNum());
        RDUserInfo.ItemInfo cardsInfo = RDUserInfo.ItemInfo.instance(UserInfoEnum.CARDS, cards.size(), rate);
        rd.addItemInfo(cardsInfo);
        //神仙大会
        SxdhZone sxdhZone = sxdhZoneService.getCurOrLastZone(uid);
        if (sxdhZone == null) {
            RDUserInfo.ItemInfo sxdhInfo = RDUserInfo.ItemInfo.instance(UserInfoEnum.SXDH, 0, 0 + "");
            rd.addItemInfo(sxdhInfo);
        } else {
            Integer sxdhScore = this.sxdhRankService.getScore(sxdhZone, SxdhRankType.RANK, uid);
            int rank = this.sxdhRankService.getRank(sxdhZone, SxdhRankType.RANK, uid);
            RDUserInfo.ItemInfo sxdhInfo = RDUserInfo.ItemInfo.instance(UserInfoEnum.SXDH, rank, sxdhScore + "");
            rd.addItemInfo(sxdhInfo);
        }
        //阐截斗法
        ChanjieUserInfo cjdf = this.chanjieUserService.getUserInfo(uid);
        Long cjdfRank = 0L;
        if (cjdf.hasReligiousNowSeason()) {
            int gid = ServerTool.getServerGroup(sid);
            cjdfRank = this.chanjieUserService.getRank(cjdf.getReligiousType(), uid, gid);
        }
        RDUserInfo.ItemInfo cjdfInfo = RDUserInfo.ItemInfo.instance(UserInfoEnum.CJDF, cjdfRank, this.chanjieUserService.getUserInfo(uid).getHeadName());
        rd.addItemInfo(cjdfInfo);
        //城池集
        List<UserCity> cityList = this.userCityService.getUserOwnCities(uid);
        List<UserNightmareCity> nightmareCities = this.userCityService.getUserOwnNightmareCities(uid);
        int myCityNum = cityList.size() + nightmareCities.size();
        String citys = MathTool.getRate(myCityNum, 170);
        RDUserInfo.ItemInfo cityInfo = RDUserInfo.ItemInfo.instance(UserInfoEnum.CITIES, myCityNum, citys);
        rd.addItemInfo(cityInfo);
        //成就集
        int count = AchievementTool.getAllAchievements().size();
        int finished = this.userAchievementService.getUserFinishedAchievementsNum(uid);
        String achiveRate = MathTool.getRate(finished, count);
        RDUserInfo.ItemInfo achiveInfo = RDUserInfo.ItemInfo.instance(UserInfoEnum.ACHIEVEMENT, finished, achiveRate);
        rd.addItemInfo(achiveInfo);
        //展示的卡信息
        List<UserCard> showCards = new ArrayList<>();
        UserShowCard showCard = userCardService.getShowCard(uid);
        if (showCard != null && ListUtil.isNotEmpty(showCard.getCardIds())) {
            showCards = cards.stream().filter(p -> showCard.getCardIds().contains(p.getBaseId())).collect(Collectors.toList());
        }
        rd.setCards(showCards);
        rd.setHead(gu.getRoleInfo().getHead());
        rd.setHeadIcon(gu.getRoleInfo().getHeadIcon());
        rd.setIsMyFriend(this.buddyLogic.myFriendUid(myuid, uid) ? 1 : 0);
        return rd;
    }

    public RDOpenMenu getOpenMenuList(long uid) {
        RDOpenMenu rd = new RDOpenMenu();
        GameUser gu = gameUserService.getGameUser(uid);
        if (gu.getLevel() >= 5) {
            rd.addMenu(DynamicMenuEnum.FRIENDS.getVal());
        } else if (gu.getLevel() >= 9) {
            rd.addMenu(DynamicMenuEnum.JIZHANG.getVal());
        } else {
            //好友图标，新手任务添加好友前一个任务 获得1次战斗胜利40 出现
            Optional<UserGrowTask> op = newbieTaskService.getUserGrowTask(uid, 40);
            if (op.isPresent() && op.get().ifAccomplished()) {
                rd.addMenu(DynamicMenuEnum.FRIENDS.getVal());
            }
            //激战图标，打友怪任务90 后出现
            Optional<UserGrowTask> opJZ = newbieTaskService.getUserGrowTask(uid, 90);
            if (opJZ.isPresent() && opJZ.get().ifAwarded()) {
                rd.addMenu(DynamicMenuEnum.JIZHANG.getVal());
            }
        }
        //开启合服活动界面
        List<IActivity> iActivities = activityService.getActivities(gameUserService.getActiveSid(uid), ActivityParentTypeEnum.COMBINED_SERVICE);
        if (ListUtil.isNotEmpty(iActivities)) {
            rd.addMenu(DynamicMenuEnum.COMBINED_SERVICE.getVal());
        }
        //开启世界杯系列活动界面
        List<IActivity> seriesOfActivities = activityService.getActivities(gameUserService.getActiveSid(uid), ActivityParentTypeEnum.WORLD_CUP_ACTIVITY);
        if (ListUtil.isNotEmpty(seriesOfActivities)) {
            rd.addMenu(DynamicMenuEnum.SERIES_OF_ACTIVITIE.getVal());
        }
        //开启普通节日活动51活动界面
        List<IActivity> holidayActivity51 = activityService.getActivities(gameUserService.getActiveSid(uid), ActivityParentTypeEnum.HOLIDAY_ACTIVITY_51);
        if (ListUtil.isNotEmpty(holidayActivity51)) {
            rd.addMenu(DynamicMenuEnum.NORMAL_JRHD_51.getVal());
        }
        //开启普通节日活动52活动界面
        List<IActivity> holidayActivity52 = activityService.getActivities(gameUserService.getActiveSid(uid), ActivityParentTypeEnum.HOLIDAY_ACTIVITY_52);
        if (ListUtil.isNotEmpty(holidayActivity52)) {
            rd.addMenu(DynamicMenuEnum.NORMAL_JRHD_52.getVal());
        }

        if (gu.getLevel() >= 20) {
            //轮回世界图标
            boolean showTransmigratonIcon = true;
            boolean hasEverEnter = globalEnterService.isEnter(uid);
            if (!hasEverEnter) {
                int nightmareCityOwnNum = userCityService.getUserOwnNightmareCities(uid).size();
                if (nightmareCityOwnNum < TransmigrationTool.getCfg().getShowIconCityNum()) {
                    showTransmigratonIcon = false;
                }
            }
            if (showTransmigratonIcon) {
                rd.addMenu(DynamicMenuEnum.TRANSMIGRATION.getVal());
            }
        }

        // 节日活动
        int sid = gu.getServerId();
        if (newerGuideService.isPassNewerGuide(uid)) {
            if (activityService.isInActivityTimes(uid, sid, ActivityParentTypeEnum.HOLIDAY_ACTIVITY)) {
                rd.addMenu(DynamicMenuEnum.JRHD.getVal());
            }
            if (activityService.isInActivityTimes(uid, sid, ActivityParentTypeEnum.NORMAL_HOLIDAY_ACTIVITY)) {
                rd.addMenu(DynamicMenuEnum.NORMAL_JRHD.getVal());
            }
        }
        return rd;
    }

    /**
     * 设置表情包
     *
     * @param guId
     * @return
     */
    public RDSuccess setEmoticon(long guId, Integer id) {
        GameUser gu = this.gameUserService.getGameUser(guId);
        //玩家拥有的头像ID
        if (id == null || id < 0) {
            id = 0;
        }
        if (id > 0) {
            CfgTreasureEntity treasure = TreasureTool.getTreasureById(id);
            if (treasure == null || treasure.getType() != TreasureType.EMOTICON.getValue() || !TreasureChecker.hasTreasure(guId, id)) {
                throw new ExceptionForClientTip("role.emoticon.not.own");
            }
        }
        gu.getRoleInfo().setEmoticon(id);
        gu.updateRoleInfo();
        return new RDSuccess();
    }
}
