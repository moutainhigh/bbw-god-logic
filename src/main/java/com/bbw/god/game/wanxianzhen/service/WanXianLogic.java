package com.bbw.god.game.wanxianzhen.service;

import com.bbw.App;
import com.bbw.cache.UserCacheService;
import com.bbw.common.*;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.db.entity.WanXianUserCardsEntity;
import com.bbw.god.db.service.WanXianUserCardsService;
import com.bbw.god.detail.async.WanXianUserCardsAsyncHandler;
import com.bbw.god.event.common.CommonEventPublisher;
import com.bbw.god.game.config.TypeEnum;
import com.bbw.god.game.config.card.CardEnum;
import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.game.config.card.CfgCardEntity;
import com.bbw.god.game.config.server.ServerTool;
import com.bbw.god.game.wanxianzhen.*;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.card.RDShareCardGroup;
import com.bbw.god.gameuser.card.UserCard;
import com.bbw.god.gameuser.card.UserCardGroupShareService;
import com.bbw.god.gameuser.card.UserCardService;
import com.bbw.god.gameuser.leadercard.UserLeaderCard;
import com.bbw.god.gameuser.leadercard.beast.UserLeaderBeastService;
import com.bbw.god.gameuser.leadercard.equipment.UserLeaderEquimentService;
import com.bbw.god.gameuser.leadercard.service.LeaderCardService;
import com.bbw.god.gameuser.task.TaskTypeEnum;
import com.bbw.god.login.DynamicMenuEnum;
import com.bbw.god.login.RDNoticeInfo;
import com.bbw.god.notify.rednotice.ModuleEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.bbw.god.game.wanxianzhen.WanXianTool.getThisSeason;

/**
 * @author lwb
 * @date 2020/4/22 10:18
 */
@Service
@Slf4j
public class WanXianLogic {
    @Autowired
    private UserCacheService userCacheService;
    @Autowired
    private WanXianScoreRankService wanXianRankService;
    @Autowired
    private WanXianWinRankService wanXianWinRankService;
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private UserCardService userCardService;
    @Autowired
    private App app;
    @Autowired
    private WanXianRobotService robotService;
    @Autowired
    private WanXianRaceFactory wanXianRaceFactory;
    @Autowired
    private WanXianFightLogsService wanXianFightLogsService;
    @Autowired
    private ChampionPredictionService championPredictionService;
    @Autowired
    private WanXianScoreRankService wanXianScoreRankService;
    @Autowired
    private WanXianSeasonService wanXianSeasonService;
    @Autowired
    private UserCardGroupShareService userCardGroupShareService;
    @Autowired
    private WanXianUserCardsService wanXianUserCardsService;
    @Autowired
    private LeaderCardService leaderCardService;
    @Autowired
    private UserLeaderBeastService userLeaderBeastService;
    @Autowired
    private UserLeaderEquimentService userLeaderEquimentService;
    @Autowired
    private WanXianUserCardsAsyncHandler wanXianUserCardsAsyncHandler;

    public static final int TYPE_REGULAR_RACE = 1000;
    public static final int TYPE_SPECIAL_RACE = 2000;
    public static final int MIN_SIGN_UP_CARDS = 20;
    private static final int[] TYPES = {TYPE_REGULAR_RACE, TYPE_REGULAR_RACE};

    /**
     * 获取万仙阵主页信息
     *
     * @return
     */
    public void getMainPageInfo(long uid, RDWanXian rd, Integer minPageSize, Integer maxPageSize, int type) {
        rd.setCurrentRace(WanXianTool.getCurrentSpecialType(gameUserService.getActiveGid(uid)));
        if (rd.getWxType() == WanXianPageType.SING_UP.getVal()) {
            getSignUpPageInfo(uid, type, rd);
        } else if (rd.getWxType() == WanXianPageType.GROUP_STAGE_CP.getVal() || rd.getWxType() == WanXianPageType.FINALS_RACE_CP.getVal()) {
            championPredictionService.getChampionPredictionPage(uid, type, rd);
        } else {
            int pageSize = maxPageSize;
            if (hasJionWanxian(uid, type)) {
                pageSize = minPageSize;
            }
            wanXianRaceFactory.match(rd.getWxType()).getMainPageInfo(uid, rd, pageSize, type);
            hideLogMenu(uid);
        }
    }

    /**
     * 报名页面信息
     *
     * @param uid
     * @return
     */
    private void getSignUpPageInfo(long uid, int type, RDWanXian rd) {
        //获取玩家的万仙阵状态
        UserWanXian userWanXian = getOrCreateUserWanXian(uid, type);
        boolean signUp = userWanXian.hasSignUpRegularRace();
        rd.setMyStatus(signUp ? 1 : 0);
        if (userWanXian.getRaceCards() != null) {
            rd.setCards(userWanXian.getRaceCards().stream().map(WanXianCard::getCardId).collect(Collectors.toList()));
        }
        //获取以往赛季信息
        int gid = gameUserService.getActiveGid(uid);
        List<RDWanXian.RDUser> list = getHistoryRank(gid, type, rd.getSeasonOrder());
        if (!list.isEmpty()) {
            rd.setRankList(list);
        }
        if (WanXianLogic.TYPE_SPECIAL_RACE == type) {
            rd.setNextRace(WanXianTool.getNextSpecialType(gid));
            rd.setPreRace(wanXianSeasonService.getSpecialTypeByOrder(gid, rd.getSeasonOrder()).getVal());
        }
    }

    public List<RDWanXian.RDUser> getHistoryRank(int gid, int type, Integer order) {
        List<Long> uids = wanXianScoreRankService.getHistorySeasonRank(gid, type, order);
        List<RDWanXian.RDUser> rdUsers = new ArrayList<>();
        if (!uids.isEmpty()) {
            for (Long uid : uids) {
                RDWanXian.RDUser user = RDWanXian.RDUser.instance(uid);
                getUserInfo(user);
                rdUsers.add(user);
            }
        }
        return rdUsers;
    }

    /**
     * 报名常规赛:报名时仅记录卡牌ID不记录卡牌详细信息
     * 报名条件：玩家拥有的卡牌收藏数达到60张
     *
     * @param uid
     * @return
     */
    public boolean signUpRace(int type, long uid, int gid) {
        //检查是否满足报名条件=》玩家拥有的卡牌收藏数达到60张
        int count = userCardService.getUserCards(uid).size();
        if (app.runAsProd()) {
            if (count < MIN_SIGN_UP_CARDS) {
                throw new ExceptionForClientTip("wanxian.not.permit");
            }
        }
        UserWanXian userWanXian = getOrCreateUserWanXian(uid, type);
        //特色赛
        WanXianSpecialType specialType = wanXianSeasonService.getCurrentSpecialType(gid);
        if (!(WanXianLogic.TYPE_SPECIAL_RACE == type && specialType.getVal() == WanXianSpecialType.SHEN_XIAN.getVal()) && (ListUtil.isEmpty(userWanXian.getRaceCards()) || !validSpecialCardGroupByCards(type, uid, userWanXian.getRaceCards(), gid))) {
            throw new ExceptionForClientTip("wanxian.card.error");
        }
        int season = getThisSeason();
        if (userWanXian.getRegularRace() != season) {
            //玩家报名
            userWanXian.setRegularRace(season);
            wanXianRankService.jionWanXianRegularRace(wanXianRankService.getBaseKey(gid, type), uid);
            wanXianWinRankService.incVal(wanXianWinRankService.getBaseKey(gid, type), uid, 0);
            gameUserService.updateItem(userWanXian);
            CommonEventPublisher.pubAccomplishEvent(uid, ModuleEnum.WANXIAN, TaskTypeEnum.MAP_TIP.getValue(), 1);
        }
        return true;
    }

    /**
     * 保存卡组
     *
     * @param uid
     * @param cardIds
     * @return
     */
    public boolean saveCardGroup(long uid, int type, String cardIds, int gid) {
        List<Integer> cardIdList = StrUtil.toList(cardIds, ",");
        if (cardIdList.isEmpty()) {
            //报名卡组为空
            throw new ExceptionForClientTip("wanxian.card.error");
        }
        //卡牌上限20张
        if (cardIdList.size() > 20) {
            throw new ExceptionForClientTip("card.grouping.outOfLimit");
        }
        if (!isSignUpTime()) {
            //非参赛时间
            throw new ExceptionForClientTip("wanxian.close.signup");
        }
        if (!validSpecialCardGroup(type, cardIdList, gid)) {
            throw new ExceptionForClientTip("wanxian.card.error");
        }
        if (checkCardGroupHasUsing(cardIdList, uid, type)) {
            throw new ExceptionForClientTip("wanxian.card.using");
        }
        if (WanXianLogic.TYPE_SPECIAL_RACE == type) {
            //特色赛
            WanXianSpecialType specialType = wanXianSeasonService.getCurrentSpecialType(gid);
            if (specialType.getVal() == WanXianSpecialType.SHEN_XIAN.getVal()) {
                //神仙战 不允许修改卡牌
                return false;
            }
        }
        UserWanXian userWanXian = getOrCreateUserWanXian(uid, type);
        //修改卡组
        List<WanXianCard> cards = new ArrayList<>();
        for (Integer cardId : cardIdList) {
            if (cardId.intValue() == CardEnum.LEADER_CARD.getCardId()) {
                UserLeaderCard leaderCard = leaderCardService.getUserLeaderCard(uid);
                WanXianCard card = WanXianCard.instance(cardId);
                card.updateCardInfo(leaderCard, userLeaderBeastService.getSkills(uid));
                card.setCardName(gameUserService.getGameUser(uid).getRoleInfo().getNickname());
                card.setBeasts(userLeaderBeastService.getTakedBeasts(uid));
                card.setEquips(userLeaderEquimentService.getTakedEquipments(uid));
                leaderCardService.updateWanXianCard(leaderCard, card);
                cards.add(card);
            } else {
                cards.add(WanXianCard.instance(cardId, CardTool.getCardById(cardId)));
            }
        }
        userWanXian.setRaceCards(cards);
        userWanXian.setSaveSeasonCard(getThisSeason());
        gameUserService.updateItem(userWanXian);
        if (!userWanXian.hasSignUpRegularRace()) {
            signUpRace(type, uid, gid);
        }
        return true;
    }

    /**
     * 截止报名，将玩家此时卡牌状态锁定
     *
     * @param
     * @return
     */
    public void updateWanXianCardInfo(int gid, int type, RDWanXianJob rd) {
        List<Long> uids = wanXianRankService.getAllItemKeys(wanXianRankService.getBaseKey(gid, type));
        int season = getThisSeason();
        for (Long uid : uids) {
            UserWanXian wanXian = getOrCreateUserWanXian(uid, type);
            if (wanXian.getRaceCards() == null || wanXian.getRaceCards().isEmpty()) {
                GameUser gu = gameUserService.getGameUser(uid);
                log.error("报名错误的玩家：UID:" + uid + "名字：" + gu.getRoleInfo().getNickname() + ",区服" + ServerTool.getServerShortName(gu.getServerId()));
                continue;
            }
            List<UserCard> cards = userCardService.getUserCards(uid);
            for (WanXianCard card : wanXian.getRaceCards()) {
                int cardId = card.getCardId();
                int deifyCard = CardTool.getDeifyCardId(cardId);
                Optional<UserCard> userCardOp = cards.stream().filter(p -> p.getBaseId() == cardId || p.getBaseId() == deifyCard).findFirst();
                if (userCardOp.isPresent()) {
                    card.updateCardInfo(userCardOp.get());
                } else if (cardId == CardEnum.LEADER_CARD.getCardId()) {
                    //主角卡
                    Optional<UserLeaderCard> cardOp = leaderCardService.getUserLeaderCardOp(uid);
                    if (cardOp.isPresent()) {
                        UserLeaderCard leaderCard = cardOp.get();
                        card.updateCardInfo(leaderCard, userLeaderBeastService.getSkills(uid));
                        card.setCardName(gameUserService.getGameUser(uid).getRoleInfo().getNickname());
                        card.setBeasts(userLeaderBeastService.getTakedBeasts(uid));
                        card.setEquips(userLeaderEquimentService.getTakedEquipments(uid));
                        leaderCardService.updateWanXianCard(leaderCard, card);
                    }
                }
            }
            int lv = gameUserService.getGameUser(uid).getLevel();
            WanXianUserCardsEntity entity = WanXianUserCardsEntity.instance(uid, lv, JSONUtil.toJson(wanXian.getRaceCards()), season, type);
            entity.setGid(gid);
            wanXianUserCardsAsyncHandler.log(entity);
            gameUserService.updateItem(wanXian);
        }
        if (TYPE_SPECIAL_RACE == type) {
            wanXianSeasonService.addSpecialType(gid);
        }
        int specialType = WanXianTool.getCurrentSpecialType(gid);
        checkPeopleNums(specialType, gid, 64 - uids.size(), type == TYPE_SPECIAL_RACE);
    }

    /**
     * 是否时报名阶段 报名时间：上周天20:00-周一11:30
     *
     * @return
     */
    private boolean isSignUpTime() {
        if (app.runAsDev()) {
            return true;
        }
        int weekDay = DateUtil.getToDayWeekDay();
        if (weekDay == 7) {
            //周日16:00-24:00
            Date beginDate = DateUtil.toDate(new Date(), 16, 0, 0);
            return DateUtil.getSecondsBetween(new Date(), beginDate) < 0;
        } else if (weekDay == 1) {
            //周一 00:00-12:00
            Date beginDate = DateUtil.toDate(new Date(), 12, 00, 0);
            return DateUtil.getSecondsBetween(new Date(), beginDate) > 0;
        }
        return false;
    }

    /**
     * 获取玩家万仙阵信息
     *
     * @param uid
     * @return
     */
    public Optional<UserWanXian> getUserWanXian(long uid, int type) {
        UserWanXian userWanXian = userCacheService.getCfgItem(uid, type, UserWanXian.class);
        if (userWanXian == null) {
            if (TYPE_REGULAR_RACE == type) {
                List<UserWanXian> userWanXians = userCacheService.getUserDatas(uid, UserWanXian.class);
                if (userWanXians == null || userWanXians.isEmpty()) {
                    return Optional.empty();
                }
                Optional<UserWanXian> op = userWanXians.stream().filter(p -> p.getBaseId() == null).findFirst();
                if (!op.isPresent()) {
                    return Optional.empty();
                }
                userWanXian = op.get();
                userWanXian.setBaseId(1000);
                gameUserService.updateItem(userWanXian);
            } else {
                return Optional.empty();
            }
        }
        if (userWanXian.getRegularRace() != 0 && userWanXian.getRegularRace() != getThisSeason()) {
            userWanXian.restNewSeason();
            gameUserService.updateItem(userWanXian);
        } else if (userWanXian.getWanXianLastUpdateTime() != DateUtil.getTodayInt()) {
            userWanXian.restTodayData();
            gameUserService.updateItem(userWanXian);
        }
        if (ListUtil.isNotEmpty(userWanXian.getRaceCards())) {
            Optional<WanXianCard> optional = userWanXian.getRaceCards().stream().filter(p -> p.getCardId() == CardEnum.LEADER_CARD.getCardId()).findFirst();
            if (optional.isPresent()) {
                WanXianCard wanXianCard = optional.get();
                if (wanXianCard.getCardName() == null || wanXianCard.getType() == null) {
                    GameUser user = gameUserService.getGameUser(uid);
                    wanXianCard.setCardName(user.getRoleInfo().getNickname());
                    wanXianCard.setEquips(userLeaderEquimentService.getTakedEquipments(uid));
                    wanXianCard.setBeasts(userLeaderBeastService.getTakedBeasts(uid));
                    Optional<UserLeaderCard> cardOp = leaderCardService.getUserLeaderCardOp(uid);
                    wanXianCard.setType(TypeEnum.fromValue(cardOp.get().getProperty()));
                    gameUserService.updateItem(userWanXian);
                }
            }
        }
        return Optional.of(userWanXian);
    }

    /**
     * 获取万仙阵玩家信息，不存在时则创建
     *
     * @param uid
     * @return
     */
    public UserWanXian getOrCreateUserWanXian(long uid, int type) {
        Optional<UserWanXian> userWanXianOp = getUserWanXian(uid, type);
        if (userWanXianOp.isPresent()) {
            return userWanXianOp.get();
        }
        UserWanXian userWanXian = UserWanXian.instance(uid);
        userWanXian.setBaseId(type);
        userCacheService.addUserData(userWanXian);
        return userWanXian;
    }

    /**
     * 获取万仙阵常规赛卡牌集合
     *
     * @param uid
     * @return
     */
    public List<WanXianCard> getUserRegularRaceCards(long uid, int type) {
        if (type == TYPE_SPECIAL_RACE) {
            int specialType = WanXianTool.getCurrentSpecialType(gameUserService.getActiveGid(uid));
            if (specialType == WanXianSpecialType.SHEN_XIAN.getVal()) {
                UserWanXian userWanXian = getOrCreateUserWanXian(uid, TYPE_REGULAR_RACE);
                List<CfgCardEntity> cards = CardTool.getAllCards();
                List<CfgCardEntity> collect = cards;
                if (userWanXian.hasSignUpRegularRace() && ListUtil.isNotEmpty(userWanXian.getRaceCards())) {
                    List<Integer> list = userWanXian.getRaceCards().stream().map(WanXianCard::getCardId).collect(Collectors.toList());
                    collect = cards.stream().filter(p -> !list.contains(p.getId()) && !list.contains(p.getId() + 10000)).collect(Collectors.toList());
                }
                List<CfgCardEntity> randoms = PowerRandom.getRandomsFromList(collect, 20);
                List<UserCard> userCards = userCardService.getUserCards(uid);
                List<WanXianCard> rd = new ArrayList<>();
                for (CfgCardEntity random : randoms) {
                    WanXianCard wanXianCard = WanXianCard.instance(random.getId(), random);
                    Optional<UserCard> cardOp = userCards.stream().filter(p -> p.getBaseId().equals(random.getId()) || p.getBaseId().equals(random.getId() + 10000)).findFirst();
                    if (cardOp.isPresent()) {
                        wanXianCard.updateCardInfo(cardOp.get());
                    } else {
                        wanXianCard.setHv(0);
                        wanXianCard.setLv(10);
                    }
                    rd.add(wanXianCard);
                }
                return rd;
            }
        }
        if (uid > 0) {
            UserWanXian userWanXian = getOrCreateUserWanXian(uid, type);
            return userWanXian.getRaceCards();
        }
        return robotService.getRobotCards(uid);
    }

    /**
     * 获取参赛人员的昵称
     *
     * @param uid
     * @return
     */
    public String getNickname(long uid) {
        if (uid > 0) {
            int sid = gameUserService.getActiveSid(uid);
            return ServerTool.getServerShortName(sid) + "." + gameUserService.getGameUser(uid).getRoleInfo().getNickname();
        }
        return "机器人" + (-uid % 10);
    }

    /**
     * 获取万仙阵卡组
     *
     * @param uid
     * @return
     */
    public RDWanXian getUserCardGroup(long uid, int type) {
        RDWanXian rd = new RDWanXian();
        UserWanXian userWanXian = getOrCreateUserWanXian(uid, type);
        rd.setMyStatus(userWanXian.hasSignUpRegularRace() ? 1 : 0);
        rd.setCardGroup(new ArrayList<WanXianCard>());
        if (type == TYPE_SPECIAL_RACE) {
            int specialType = WanXianTool.getCurrentSpecialType(gameUserService.getActiveGid(uid));
            rd.setCurrentWxType(specialType);
            if (specialType == WanXianSpecialType.SHEN_XIAN.getVal()) {
                return rd;
            }
        } else {
            rd.setCurrentWxType(TYPE_REGULAR_RACE);
        }
        if (ListUtil.isNotEmpty(userWanXian.getRaceCards()) || !isSignUpTime()) {
            rd.setCardGroup(userWanXian.getRaceCards());
            return rd;
        }
        return rd;
    }

    public RDWanXian getUserHistoryCardGroup(long uid, int type, Integer order) {
        RDWanXian rd = new RDWanXian();
        int gid = gameUserService.getActiveGid(uid);
        if (order == null || order < 0) {
            rd.setCardGroup(new ArrayList<WanXianCard>());
        } else {
            rd.setCardGroup(wanXianSeasonService.getHistorySeasonCardGroup(gid, type, order, uid));
        }
        return rd;
    }

    /**
     * 获取奖励预览
     *
     * @return
     */
    public RDWanXian listAward(long uid, int type) {
        RDWanXian rd = new RDWanXian();
        List<RDWanXian.RDUser> listRd = championPredictionService.getChampionPredictionList(uid, type, 8);
        List<RDWanXian.RDUser> bets4 = championPredictionService.getChampionPredictionList(uid, type, 4);
        if (!listRd.isEmpty() && !bets4.isEmpty()) {
            //说明8强和4强都有预测
            for (RDWanXian.RDUser user : bets4) {
                Optional<RDWanXian.RDUser> userOp = listRd.stream().filter(p -> p.getUid().equals(user.getUid())).findFirst();
                if (userOp.isPresent()) {
                    userOp.get().addMultiple(user.getMultiple());
                } else {
                    listRd.add(user);
                }
            }
        } else {
            listRd.addAll(bets4);
        }
        if (!listRd.isEmpty()) {
            listRd = listRd.stream().sorted(Comparator.comparing(RDWanXian.RDUser::getStatus).reversed()).collect(Collectors.toList());
            rd.setBetLogs(listRd);
        }
        rd.setRankAwards(WanXianTool.getRankAwards(type));
        return rd;
    }

    /**
     * 为RDUser 补充玩家的详细信息
     *
     * @param user
     */
    public void getUserInfo(RDWanXian.RDUser user, boolean needPreNicknam) {
        if (user == null) {
            return;
        }
        if (user.getUid() > 0) {
            GameUser gu = gameUserService.getGameUser(user.getUid());
            GameUser.RoleInfo info = gu.getRoleInfo();
            user.setHead(info.getHead());
            if (needPreNicknam) {
                user.setNickname(ServerTool.getServerShortName(gu.getServerId()) + "." + info.getNickname());
            } else {
                user.setNickname(info.getNickname());
            }
            user.setHeadIcon(info.getHeadIcon());
        } else {
            user.setNickname(getNickname(user.getUid()));
        }
    }

    public void getUserInfo(RDWanXian.RDUser user) {
        getUserInfo(user, false);
    }

    /**
     * 获取玩家对战信息
     */
    public RDWanXian getFightLogs(long uid, int type) {
        RDWanXian rd = new RDWanXian();
        Optional<UserWanXian> userWanXianOp = getUserWanXian(uid, type);
        if (userWanXianOp.isPresent()) {
            rd.setLogs(getFightLogs(userWanXianOp.get(), type, 7, 6, 5, 4, 3, 2, 1));
        }
        return rd;
    }

    public RDWanXian getFightLogs(int gid, int type, int logType) {
        RDWanXian rd = new RDWanXian();
        List<String> logKeys = new ArrayList<>();
        WanXianEmailEnum maxKeyVal = WanXianEmailEnum.getMaxShowEnumVal(gid, type);
        if (maxKeyVal == null) {
            return rd;
        }
        if (logType == 8) {
            //小组赛
            List<WanXianEmailEnum> keys = WanXianEmailEnum.getEnumValByWeekday(6);
            if (maxKeyVal != null) {
                keys = keys.stream().filter(p -> (p.getVal() % 10 < maxKeyVal.getVal() % 10) || (p.getVal() % 10 == maxKeyVal.getVal() % 10 && p.getSeq() <= maxKeyVal.getSeq())).collect(Collectors.toList());
            }
            String[] groupNames = {"A", "B"};
            for (WanXianEmailEnum val : keys) {
                for (int i = 1; i <= 6; i++) {
                    for (String group : groupNames) {
                        logKeys.add(val.getVal() + "N" + group + "N" + i);
                    }
                }
            }
        } else if (logType == 9) {
            //总决赛
            List<WanXianEmailEnum> keys = WanXianEmailEnum.getEnumValByWeekday(7);
            if (maxKeyVal != null) {
                keys = keys.stream().filter(p -> (p.getVal() % 10 < maxKeyVal.getVal() % 10) || (p.getVal() % 10 == maxKeyVal.getVal() % 10 && p.getSeq() <= maxKeyVal.getSeq())).collect(Collectors.toList());
            }
            for (WanXianEmailEnum val : keys) {
                for (int i = 1; i <= 3; i++) {
                    logKeys.add(val.getVal() + "N" + i);
                }
            }
        }
        List<RDWanXian.RDFightLog> logs = wanXianFightLogsService.getFightLogsForUserInfo(gid, type, logKeys);
        if (!logs.isEmpty()) {
            rd.setLogs(logs);
        }
        return rd;
    }

    public RDWanXian getMyHistoryFightLogs(long uid, int type) {
        RDWanXian rd = new RDWanXian();
        Optional<UserWanXian> userWanXianOp = getUserWanXian(uid, type);
        List<List<RDWanXian.RDFightLog>> rdlist = new ArrayList<>();
        WanXianEmailEnum maxKeyVal = WanXianEmailEnum.getMaxShowEnumVal(gameUserService.getActiveGid(uid), type);
        int max = maxKeyVal == null ? DateUtil.getToDayWeekDay() - 1 : DateUtil.getToDayWeekDay();
        if (userWanXianOp.isPresent()) {
            List<RDWanXian.RDFightLog> list = null;
            for (int i = 1; i <= max; i++) {
                list = getFightLogs(userWanXianOp.get(), type, i);
                if (!list.isEmpty()) {
                    rdlist.add(list);
                }

            }
        }
        rd.setMyHistoryLogs(rdlist);
        return rd;
    }

    public List<RDWanXian.RDFightLog> getFightLogs(UserWanXian userWanXian, int type, List<Integer> keys) {
        long uid = userWanXian.getGameUserId();
        int gid = gameUserService.getActiveGid(uid);
        List<String> logKeys = userWanXian.getFightLogsKeys(keys);
        if (!logKeys.isEmpty()) {
            List<RDWanXian.RDFightLog> logs = wanXianFightLogsService.getFightLogsForUserInfo(gid, type, logKeys);
            for (RDWanXian.RDFightLog log : logs) {
                if (log.getP2().getScore() != null) {
                    if (log.getP2().getUid() == uid) {
                        log.setScore("+" + log.getP2().getScore());
                    } else {
                        log.setScore("+" + log.getP1().getScore());
                    }
                }
                if (log.getWinnerUid() == uid) {
                    log.setWin(1);
                } else {
                    log.setWin(0);
                }
            }
            return logs;
        }
        return new ArrayList<>();
    }

    public List<RDWanXian.RDFightLog> getFightLogs(UserWanXian userWanXian, int type, int... weekdays) {
        long uid = userWanXian.getGameUserId();
        int gid = gameUserService.getActiveGid(uid);
        List<WanXianEmailEnum> keys = new ArrayList<>();
        for (int wd : weekdays) {
            keys.addAll(WanXianEmailEnum.getEnumValByWeekday(wd));
        }
        WanXianEmailEnum maxKeyVal = WanXianEmailEnum.getMaxShowEnumVal(gid, type);
        if (maxKeyVal != null) {
            keys = keys.stream().filter(p -> (p.getVal() % 10 < maxKeyVal.getVal() % 10) || (p.getVal() % 10 == maxKeyVal.getVal() % 10 && p.getSeq() <= maxKeyVal.getSeq())).collect(Collectors.toList());
        }
        List<Integer> keysVals = keys.stream().map(WanXianEmailEnum::getVal).collect(Collectors.toList());
        List<String> logKeys = userWanXian.getFightLogsKeys(keysVals);
        if (!logKeys.isEmpty()) {
            List<RDWanXian.RDFightLog> logs = wanXianFightLogsService.getFightLogsForUserInfo(gid, type, logKeys);
            for (RDWanXian.RDFightLog log : logs) {
                if (log.getWinnerUid() == uid) {
                    log.setScore("+" + log.getWinnerRDUser().getScore());
                    log.setWin(1);
                } else {
                    log.setScore("+0");
                    log.setWin(0);
                }
            }
            return logs;
        }
        return new ArrayList<>();
    }

    /**
     * 报名界面：上周天16：:00-周一12:00
     * 8强下注界面时间：周五20:00-周六11:30
     * 4强下注界面时间：周六20:00-周天11:30
     *
     * @return 万仙阵图标
     */
    public RDNoticeInfo.ActivityShow getMenuIcon(long uid) {
        RDNoticeInfo.ActivityShow regularSignUpIcon = signUpIcon(uid, TYPE_REGULAR_RACE);
        if (regularSignUpIcon != null) {
            int num = regularSignUpIcon.getAwardNum();
            if (num == 0 && !hasJionWanxian(uid, TYPE_SPECIAL_RACE)) {
                num = 1;
            }
            return RDNoticeInfo.ActivityShow.instance(DynamicMenuEnum.WANXIAN_SIGN_UP.getVal(), num);
        }
        int gid = gameUserService.getActiveGid(uid);
        Optional<CfgWanXian.RaceConfig> configOptional = WanXianTool.getRaceConfig(gid);
        if (!configOptional.isPresent() || !configOptional.get().ifSeasonOpen(getThisSeason()) || getThisSeason() < configOptional.get().getFirstSeason()) {
            //该平台没有开放万仙阵
            return null;
        }
        int weekday = DateUtil.getToDayWeekDay();
        Date endDate = null;
        int icon = 0;
        switch (weekday) {
            case 5:
                endDate = DateUtil.toDate(new Date(), 16, 0, 0);
                if (DateUtil.getSecondsBetween(endDate, new Date()) > 0) {
                    icon = DynamicMenuEnum.WANXIAN_CHAMPION_PREDICTION.getVal();
                }
                break;
            case 6:
                endDate = DateUtil.toDate(new Date(), 12, 0, 0);
                if (DateUtil.getSecondsBetween(endDate, new Date()) < 0) {
                    icon = DynamicMenuEnum.WANXIAN_CHAMPION_PREDICTION.getVal();
                } else {
                    endDate = DateUtil.toDate(new Date(), 16, 0, 0);
                    if (DateUtil.getSecondsBetween(endDate, new Date()) > 0) {
                        icon = DynamicMenuEnum.WANXIAN_CHAMPION_PREDICTION.getVal();
                    }
                }
                break;
            case 7:
                endDate = DateUtil.toDate(new Date(), 12, 0, 0);
                if (DateUtil.getSecondsBetween(endDate, new Date()) < 0) {
                    icon = DynamicMenuEnum.WANXIAN_CHAMPION_PREDICTION.getVal();
                }
        }
        return RDNoticeInfo.ActivityShow.instance(icon, 0);

    }

    /**
     * 是否显示报名图标
     *
     * @param
     * @return
     */
    public RDNoticeInfo.ActivityShow signUpIcon(long uid, int wxType) {
        int gid = gameUserService.getActiveGid(uid);
        Optional<CfgWanXian.RaceConfig> configOptional = WanXianTool.getRaceConfig(gid);
        if (!configOptional.isPresent() || !configOptional.get().ifSeasonOpen(getThisSeason()) || getThisSeason() < configOptional.get().getFirstSeason()) {
            //该平台没有开放万仙阵
            return null;
        }
        int weekday = DateUtil.getToDayWeekDay();
        boolean inTheTime = false;
        Date endDate = null;
        if (weekday == 1) {
            endDate = DateUtil.toDate(new Date(), 12, 0, 0);
            if (DateUtil.getSecondsBetween(endDate, new Date()) < 0) {
                inTheTime = true;
            }
        } else if (weekday == 7) {
            endDate = DateUtil.toDate(new Date(), 16, 0, 0);
            if (DateUtil.getSecondsBetween(endDate, new Date()) > 0) {
                inTheTime = true;
            }
        }
        if (!inTheTime) {
            return null;
        }
        int count = userCardService.getUserCards(uid).size();
        if (count < MIN_SIGN_UP_CARDS) {
            return RDNoticeInfo.ActivityShow.instance(wxType, 0);
        }
        return RDNoticeInfo.ActivityShow.instance(wxType, hasJionWanxian(uid, wxType) ? 0 : 1);
    }

    /**
     * 是否加入了本赛季
     *
     * @param uid
     * @return
     */
    public boolean hasJionWanxian(long uid, int type) {
        Optional<UserWanXian> op = getUserWanXian(uid, type);
        if (op.isPresent()) {
            UserWanXian userWanXian = op.get();
            return userWanXian.hasSignUpRegularRace();
        }
        return false;
    }

    /**
     * 特色赛 卡牌校验
     *
     * @param
     * @return
     */
    public boolean validSpecialCardGroupByCards(int type, long uid, List<WanXianCard> cards, int gid) {
        if (WanXianLogic.TYPE_SPECIAL_RACE != type) {
            return true;
        }
        if (cards.isEmpty()) {
            return false;
        }
        List<Integer> cardIds = cards.stream().map(WanXianCard::getCardId).collect(Collectors.toList());
        return validSpecialCardGroup(type, cardIds, gid);
    }

    /**
     * 该方法用于核对特色赛上牌是否符合要求，未写在里面的说明上牌没有要求
     *
     * @param type    万仙阵类型
     * @param cardIds 上牌的ID集合
     * @return true为符合要求 false为不符合要求
     */
    public boolean validSpecialCardGroup(int type, List<Integer> cardIds, int gid) {
        if (WanXianLogic.TYPE_SPECIAL_RACE != type) {
            return true;
        }
        if (app.runAsDev()) {
            return true;
        }
        WanXianSpecialType specialType = wanXianSeasonService.getCurrentSpecialType(gid);
        switch (specialType) {
            case JIN://金系赛（仅能使用金系神将参赛）
                Optional<Integer> opJin = cardIds.stream().filter(p -> p % 10000 > 199).findFirst();
                return !opJin.isPresent();
            case MU_YE://牧野赛（参赛队伍必须拥有20张神将）
                return cardIds.size() == 20;
            case MU://木系（仅能使用木系神将参赛）
                Optional<Integer> opMu = cardIds.stream().filter(p -> p % 10000 < 200 || p % 10000 > 299).findFirst();
                return !opMu.isPresent();
            case XIAO_BING://小兵赛（仅能使用1星神将和2星神将参赛，玩家等级调为40级）
                for (Integer id : cardIds) {
                    CfgCardEntity cardEntity = CardTool.getCardById(id);
                    if (cardEntity.getStar() > 2) {
                        return false;
                    }
                }
                break;
            case SHUI://水系（仅能使用水系神将参赛）
                Optional<Integer> opShui = cardIds.stream().filter(p -> (p % 10000 < 300 || p % 10000 > 399)).findFirst();
                return !opShui.isPresent();
            case ZHONG_JIAN://中坚赛（仅能使用3星神将和4星神将参赛，玩家等级调为80级）
                for (Integer id : cardIds) {
                    CfgCardEntity cardEntity = CardTool.getCardById(id);
                    if (cardEntity.getStar() != 3 && cardEntity.getStar() != 4) {
                        return false;
                    }
                }
                break;
            case HUO://火系赛（仅能使用火系神将参赛）
                Optional<Integer> opHuo = cardIds.stream().filter(p -> p % 10000 < 400 || p % 10000 > 499).findFirst();
                return !opHuo.isPresent();
            case PING_MIN://平民赛（无法使用五张王者神将参与比赛）
                List<Integer> wzCards = Arrays.asList(126, 226, 325, 425, 525, 10325);
                Optional<Integer> opWz = cardIds.stream().filter(p -> wzCards.contains(p)).findFirst();
                return !opWz.isPresent();
            case TU://土系赛（仅能使用土系神将参赛）
                Optional<Integer> opTu = cardIds.stream().filter(p -> p % 10000 < 500).findFirst();
                return !opTu.isPresent();
            case JIN_LUAN_DOU://金乱斗（仅能使用金系神将参赛，至少上阵15名神将，不能上姬昌。）
                if (cardIds.size() < 15) {
                    return false;
                }
                return !cardIds.stream().filter(p -> p % 10000 > 199 || p == 126 || p == 10126).findFirst().isPresent();
            case MU_LUAN_DOU://木乱斗（仅能使用木系神将参赛，至少上阵15名神将，不能上姜桓楚。）
                if (cardIds.size() < 15) {
                    return false;
                }
                return !cardIds.stream().filter(p -> p % 10000 < 200 || p % 10000 > 299 || p == 226 || p == 10226).findFirst().isPresent();
            case SHUI_LUAN_DOU://水乱斗（仅能使用水系神将参赛，至少上阵15名神将，不能上崇侯虎。）
                if (cardIds.size() < 15) {
                    return false;
                }
                return !cardIds.stream().filter(p -> p % 10000 < 300 || p % 10000 > 399 || p == 325 || p == 10325).findFirst().isPresent();
            case HUO_LUAN_DOU://火乱斗（仅能使用火系神将参赛，至少上阵15名神将，不能上鄂崇禹。）
                if (cardIds.size() < 15) {
                    return false;
                }
                return !cardIds.stream().filter(p -> p % 10000 < 400 || p % 10000 > 499 || p == 425 || p == 10425).findFirst().isPresent();
            case TU_LUAN_DOU://土乱斗（仅能使用土系神将参赛，至少上阵15名神将，不能上纣王。）
                if (cardIds.size() < 15) {
                    return false;
                }
                return !cardIds.stream().filter(p -> p % 10000 < 500 || p == 525 || p == 10525).findFirst().isPresent();
            case ALL_LUAN_DOU://金乱斗（每种属性的卡牌各上4张。）
                if (cardIds.size() != 20) {
                    return false;
                }
                //计算各个属性的卡牌数量
                Map<Integer, Long> countMap = cardIds.stream().map(p -> p % 10000 / 100).collect(Collectors.groupingBy(Integer::intValue, Collectors.counting()));
                for (int i = 1; i <= 5; i++) {
                    if (countMap.get(i) == null || countMap.get(i) != 4) {
                        //1~5 代表卡牌的属性 以为金属性卡ID 为100~199 之间
                        return false;
                    }
                }
                break;
        }
        return true;
    }

    public void collectCardGroup(long fromUid, long uid, Integer type, Integer season) {
        List<WanXianCard> list = null;
        int seasonDate = getThisSeason();
        if (season != null) {
            list = getUserHistoryCardGroup(fromUid, type, season).getCardGroup();
            int gid = gameUserService.getActiveGid(fromUid);
            seasonDate = WanXianTool.getSeasonByOrder(type, season, gid);
        } else {
            list = getUserCardGroup(fromUid, type).getCardGroup();
        }
        if (list == null || list.isEmpty()) {
            throw new ExceptionForClientTip("wanxian.collect.cardGroup.expire");
        }
        List<RDShareCardGroup.RDCard> cards = new ArrayList<>();
        for (WanXianCard card : list) {
            cards.add(RDShareCardGroup.RDCard.instance(card));
        }
        RDShareCardGroup shareCardGroup = new RDShareCardGroup();
        shareCardGroup.setCards(cards);
        shareCardGroup.setUid(fromUid);
        shareCardGroup.setName("万仙阵常规赛阵容");
        shareCardGroup.setShareId(seasonDate + "WX_" + fromUid);
        if (WanXianLogic.TYPE_SPECIAL_RACE == type) {
            shareCardGroup.setName("万仙阵特色赛阵容");
            shareCardGroup.setShareId(seasonDate + "WX_TS_" + fromUid);
        }
        userCardGroupShareService.collectCardGroup(shareCardGroup, uid);
    }

    public int getTypeRace(int type, int gid) {
        if (TYPE_SPECIAL_RACE == type) {
            return wanXianSeasonService.getCurrentSpecialType(gid).getVal();
        }
        return type;
    }

    private boolean checkCardGroupHasUsing(List<Integer> saveCardIds, long uid, int saveType) {
        if (app.runAsDev()) {
            return false;
        }
        int checkType = saveType;
        if (WanXianLogic.TYPE_REGULAR_RACE == saveType) {
            checkType = WanXianLogic.TYPE_SPECIAL_RACE;
        } else {
            checkType = WanXianLogic.TYPE_REGULAR_RACE;
        }
        Optional<UserWanXian> op = getUserWanXian(uid, checkType);
        if (op.isPresent()) {
            UserWanXian userWanXian = op.get();
            if (userWanXian.getRaceCards() != null && !userWanXian.getRaceCards().isEmpty()) {
                Optional<WanXianCard> cardOp = userWanXian.getRaceCards().stream().filter(p -> saveCardIds.contains(p.getCardId())).findFirst();
                if (cardOp.isPresent()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 修复卡牌数据 从数据库获取
     *
     * @param uid
     * @param gid
     * @param type
     * @return
     */
    public List<WanXianCard> repairUserCards(long uid, int gid, int type) {
        int season = getThisSeason();
        List<WanXianCard> cards = wanXianUserCardsService.getCardsFromDb(uid, season, type);
        Optional<UserWanXian> op = getUserWanXian(uid, type);
        if (op.isPresent()) {
            UserWanXian userWanXian = op.get();
            userWanXian.setRaceCards(cards);
            gameUserService.updateItem(userWanXian);
        }
        return cards;
    }

    /**
     * 设置不是特别重要的数据的过期时间
     *
     * @param gid
     * @param season
     */
    public void setWanxianRedisExpire(int gid, int season) {
        wanXianScoreRankService.expire(gid, season, 3, TimeUnit.DAYS);
        wanXianFightLogsService.expire(gid, season, 3, TimeUnit.DAYS);
        wanXianWinRankService.expire(gid, season, 3, TimeUnit.DAYS);
        wanXianSeasonService.expire("game:wanXian:" + season + ":" + gid + ":group", 3, TimeUnit.DAYS);
        wanXianSeasonService.expire("game:wanXian:" + season + ":" + gid + ":2000_group", 30, TimeUnit.DAYS);
    }

    /**
     * 是否显示世界右侧的战报图标
     *
     * @param uid
     * @return
     */
    public boolean isShowLogMenu(long uid) {
        for (int type : TYPES) {
            Optional<UserWanXian> op = getUserWanXian(uid, type);
            if (op.isPresent() && op.get().isShowLogMenu()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 隐藏战报菜单
     *
     * @param uid
     */
    private void hideLogMenu(long uid) {
        for (int type : TYPES) {
            Optional<UserWanXian> op = getUserWanXian(uid, type);
            if (op.isPresent() && op.get().isShowLogMenu()) {
                UserWanXian userWanXian = op.get();
                userWanXian.setShowLogMenu(false);
                gameUserService.updateItem(userWanXian);
            }
        }
    }

    public void updateDeifyCard(long uid, int oldId, int deifyCardId) {
        if (!isSignUpTime()) {
            return;
        }
        UserCard userCard = userCardService.getUserCard(uid, deifyCardId);
        if (userCard == null) {
            return;
        }
        int[] types = {TYPE_SPECIAL_RACE, TYPE_REGULAR_RACE};
        for (int type : types) {
            Optional<UserWanXian> optional = getUserWanXian(uid, type);
            if (optional.isPresent()) {
                UserWanXian wanXian = optional.get();
                if (ListUtil.isNotEmpty(wanXian.getRaceCards())) {
                    for (WanXianCard card : wanXian.getRaceCards()) {
                        if (card.getCardId() == oldId) {
                            card.updateCardInfo(userCard);
                            gameUserService.updateItem(wanXian);
                            break;
                        }
                    }
                }
            }
        }
    }

    /**
     * 更新多张封神卡牌
     *
     * @param uid
     * @param oldCardIds
     * @param newCardIds
     */
    public void updateDeifyCards(long uid, List<Integer> oldCardIds, List<Integer> newCardIds) {
        if (!isSignUpTime()) {
            return;
        }
        List<UserCard> userCards = userCardService.getUserCards(uid, newCardIds);
        if (ListUtil.isEmpty(userCards) && oldCardIds.size() == userCards.size()) {
            return;
        }
        int[] types = {TYPE_SPECIAL_RACE, TYPE_REGULAR_RACE};
        for (int type : types) {
            Optional<UserWanXian> optional = getUserWanXian(uid, type);
            if (optional.isPresent()) {
                UserWanXian wanXian = optional.get();
                if (ListUtil.isNotEmpty(wanXian.getRaceCards())) {
                    for (WanXianCard card : wanXian.getRaceCards()) {
                        if (oldCardIds.contains(card.getCardId())) {
                            UserCard userCard = userCards.stream().filter(tmp -> CardTool.getNormalCardId(tmp.getBaseId()) == card.getCardId()).findFirst().orElse(null);
                            if (null == userCard) {
                                continue;
                            }
                            card.updateCardInfo(userCard);

                        }
                    }
                    gameUserService.updateItem(wanXian);
                }
            }
        }
    }

    /**
     * 检查人数是否够
     *
     * @param specialType
     * @param num
     * @param isSpecial
     */
    public void checkPeopleNums(int specialType, int gid, int num, boolean isSpecial) {
        if (num <= 0) {
            return;
        }
        List<CfgWanXianRobot.RobotInfo> robotInfos = WanXianTool.getRandomList(specialType, num);
        int type = isSpecial ? TYPE_SPECIAL_RACE : TYPE_REGULAR_RACE;
        int season = WanXianTool.getThisSeason();
        for (CfgWanXianRobot.RobotInfo info : robotInfos) {
            long uid = info.getUid();
            UserWanXian userWanXian = getOrCreateUserWanXian(uid, type);
            userWanXian.restNewSeason();
            if (WanXianSpecialType.SHEN_XIAN.getVal() != specialType || !isSpecial) {
                //不是神仙赛才要配卡
                List<WanXianCard> cards = new ArrayList<>();
                if (isSpecial) {
                    for (String cardName : info.getCards()) {
                        try {
                            CfgCardEntity card = CardTool.getCardByName(cardName);
                            cards.add(WanXianCard.instance(card.getId(), card));
                        } catch (Exception e) {
                            log.error(specialType + "万仙阵卡牌名称配错：" + cardName);
                        }
                    }
                } else {
                    //随机6到15张的1星卡
                    List<CfgCardEntity> randomNotSpecialCards = CardTool.getRandomCards(1, PowerRandom.getRandomBetween(6, 15));
                    for (CfgCardEntity card : randomNotSpecialCards) {
                        cards.add(WanXianCard.instance(card.getId(), card));
                    }
                }
                userWanXian.setRaceCards(cards);
            }
            userWanXian.setSaveSeasonCard(season);
            if (!userWanXian.hasSignUpRegularRace()) {
                userWanXian.setRegularRace(season);
                wanXianRankService.jionWanXianRegularRace(wanXianRankService.getBaseKey(gid, type), uid);
                wanXianWinRankService.incVal(wanXianWinRankService.getBaseKey(gid, type), uid, 0);
            }
            gameUserService.updateItem(userWanXian);
        }
    }
}
