package com.bbw.god.fight.fsfight;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bbw.common.*;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.activity.processor.cardboost.CardBoostProcessor;
import com.bbw.god.activity.processor.cardboost.UserBoostCards;
import com.bbw.god.db.entity.CfgServerEntity;
import com.bbw.god.db.entity.InsGamePvpDetailEntity;
import com.bbw.god.detail.async.PvpDetailAsyncHandler;
import com.bbw.god.fight.FightSubmitParam;
import com.bbw.god.fight.FightTypeEnum;
import com.bbw.god.game.chanjie.service.ChanjieUserService;
import com.bbw.god.game.combat.event.CombatEventPublisher;
import com.bbw.god.game.combat.event.EPFightEnd;
import com.bbw.god.game.combat.pvp.PVPService;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.server.ServerTool;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.game.dfdj.fight.DfdjFightDetail;
import com.bbw.god.game.dfdj.fight.DfdjFightService;
import com.bbw.god.game.dfdj.zone.DfdjZone;
import com.bbw.god.game.dfdj.zone.DfdjZoneService;
import com.bbw.god.game.sxdh.*;
import com.bbw.god.game.sxdh.SxdhRoboterService.SxdhMatchedRoboter;
import com.bbw.god.game.sxdh.config.CfgSxdh;
import com.bbw.god.game.sxdh.config.SxdhRankType;
import com.bbw.god.game.sxdh.config.SxdhRoboterType;
import com.bbw.god.game.sxdh.config.SxdhTool;
import com.bbw.god.game.sxdh.event.SxdhEventPublisher;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.card.UserCard;
import com.bbw.god.gameuser.card.UserCardGroup;
import com.bbw.god.gameuser.card.UserCardService;
import com.bbw.god.gameuser.leadercard.UserLeaderCard;
import com.bbw.god.gameuser.leadercard.service.LeaderCardService;
import com.bbw.god.gameuser.mail.MailService;
import com.bbw.god.gameuser.res.ResChecker;
import com.bbw.god.gameuser.res.ResEventPublisher;
import com.bbw.god.gameuser.treasure.UserTreasure;
import com.bbw.god.gameuser.treasure.UserTreasureService;
import com.bbw.god.gameuser.treasure.event.EVTreasure;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.login.RDGameUser;
import com.bbw.god.login.RDGameUser.RDCard;
import com.bbw.god.rd.RDCommon;
import com.bbw.god.rd.RDSuccess;
import com.bbw.god.server.ServerUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 玩家竞技逻辑
 *
 * @author suhq
 * @date 2019年3月14日 上午9:58:46
 */
@Slf4j
@Service
public class FsFightLogic {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private SxdhFightService sxdhFightService;
    @Autowired
    private SxdhFighterService sxdhFighterService;
    @Autowired
    private SxdhZoneService sxdhZoneService;
    @Autowired
    private SxdhRoboterService sxdhRoboterService;
    @Autowired
    private ServerUserService serverUserService;
    @Autowired
    private ChanjieUserService chanjieUserService;
    @Autowired
    private UserCardService userCardService;
    @Autowired
    private UserTreasureService userTreasureService;
    @Autowired
    private SxdhRankService sxdhRankService;
    @Autowired
    private PVPService pVPService;
    @Autowired
    private MailService mailService;
    @Autowired
    private DfdjFightService dfdjFightService;
    @Autowired
    private DfdjZoneService dfdjZoneService;
    @Autowired
    private LeaderCardService leaderCardService;
    @Autowired
    private CardBoostProcessor cardBoostProcessor;
    @Autowired
    private PvpDetailAsyncHandler pvpDetailAsyncHandler;
    @Autowired
    private SxdhMatchLimitService sxdhMatchLimitService;

    /**
     * 返回登录玩家竞技的数据
     *
     * @return
     */
    public RDFsFighter getGuInfo(long uid, String bean, String ticket, String joinDate) {
        GameUser gu = gameUserService.getGameUser(uid);
        if (gu.getLevel() < SxdhTool.getSxdh().getPvpUnlockLevel()) {
            throw new ExceptionForClientTip("pvp.lock");
        }
        if (StrUtil.isNotBlank(bean)) {
            migrateFighter(uid, Integer.valueOf(bean), Integer.valueOf(ticket), joinDate);
        }
        RDFsFighter rd = new RDFsFighter();
        /** 玩家基本信息 **/
        rd.setRid(uid);
        rd.setName(gu.getRoleInfo().getNickname());
        rd.setPlat(gu.getRoleInfo().getChannelId());
        rd.setHead(gu.getRoleInfo().getHead());
        rd.setLevel(gu.getLevel());
        rd.setGold(gu.getGold());
        rd.setCopper(gu.getCopper());
        rd.setHeadName("封神召唤师");
        if (!DateUtil.isWeekDay(7)) {
            rd.setHeadName(chanjieUserService.getUserInfo(uid).getHeadName());
        }
        SxdhZone sxdhZone = sxdhZoneService.getCurOrLastZone(uid);
        int phaseScore = sxdhRankService.getScore(sxdhZone, SxdhRankType.PHASE_RANK, uid);
        rd.setTitle(SxdhTool.getSegmentByScore(phaseScore).getId());
        int sid = gameUserService.getActiveSid(uid);
        CfgServerEntity server = ServerTool.getServer(sid);
        rd.setServerName(server.getShortName());
        rd.setGroupId(server.getGroupId());

        // 玩家宝物
        List<UserTreasure> uTreasures = userTreasureService.getAllUserTreasures(gu.getId());
        if (ListUtil.isNotEmpty(uTreasures)) {
            rd.setProp(uTreasures.stream().map(ut -> new RDGameUser.RDTreasure(ut)).collect(Collectors.toList()));
        }
        // 玩家卡牌
        List<UserCard> uCards = userCardService.getUserCards(gu.getId());
        rd.setCard(uCards.stream().map(uc -> RDCard.instance(uc)).collect(Collectors.toList()));
        //助力10级卡牌
        if (cardBoostProcessor.getRemainTime(uid) > 0) {
            UserBoostCards bootCards = gameUserService.getSingleItem(uid, UserBoostCards.class);
            if (null != bootCards) {
                for (RDCard rdCard : rd.getCard()) {
                    if (rdCard.getLevel() < 10 && bootCards.ifBoostCard(rdCard.getBaseId())) {
                        rdCard.setLevel(10);
                    }
                }
            }
        }
        //获取封装主角卡
        Optional<UserLeaderCard> leaderCardOp = leaderCardService.getUserLeaderCardOp(uid);
        if (leaderCardOp.isPresent()) {
            UserLeaderCard leaderCard = leaderCardOp.get();
            rd.getCard().add(RDCard.instance(leaderCard));
        }
        //玩家卡组
        List<UserCardGroup> cardGroups = gameUserService.getMultiItems(uid, UserCardGroup.class);
        cardGroups = cardGroups.stream().filter(tmp -> ListUtil.isNotEmpty(tmp.getCards())).collect(Collectors.toList());
        rd.setCardGroups(cardGroups);
        return rd;
    }

    /**
     * 同步购买
     *
     * @param guId
     * @param gold
     * @param pros 810,2;820,5
     * @return
     */
    public RDSuccess syncBuy(long guId, int gold, String pros) {
        ResEventPublisher.pubGoldDeductEvent(guId, gold, WayEnum.EXCHANGE_FS_FIGHT, new RDCommon());
        List<EVTreasure> evTreasures = Stream.of(pros.split(";")).map(pro -> {
            String[] proInfo = pro.split(",");
            return new EVTreasure(Integer.valueOf(proInfo[0]), Integer.valueOf(proInfo[1]));
        }).collect(Collectors.toList());
        TreasureEventPublisher.pubTAddEvent(guId, evTreasures, WayEnum.EXCHANGE_FS_FIGHT, new RDCommon());
        return new RDSuccess();
    }

    /**
     * 同步元宝
     *
     * @param guId
     * @param refreshCardNum
     * @param refreshTimes   刷新次数
     * @return
     */
    public RDCommon syncSxdhCardRefresh(long guId, int refreshCardNum, int refreshTimes) {
        int needGold = 5 * refreshCardNum;
        if (refreshCardNum > 1) {
            needGold = 50;
        }
        GameUser gu = gameUserService.getGameUser(guId);
        ResChecker.checkGold(gu, needGold);
        RDCommon rd = new RDCommon();
        ResEventPublisher.pubGoldDeductEvent(guId, needGold, WayEnum.SXDH_REFRESH_CARD, rd);
        //TODO 临时处理，下个版本更新时优化
        rd.setAddedGold(-rd.getAddedGold());
        SxdhEventPublisher.pubCardRefreshEvent(guId, refreshCardNum);
        return rd;
    }

    /**
     * 同步扣法宝，兑换门派、使用战斗法宝
     *
     * @param guId
     * @param treasures 300,2;310,1
     * @return
     */
    public RDSuccess syncDeductTreasures(long guId, String treasures) {
        Arrays.asList(treasures.split(";")).stream().forEach(treasure -> {
            String[] tInfo = treasure.split(",");
            UserTreasure uTreasure = userTreasureService.getUserTreasure(guId, Integer.valueOf(tInfo[0]));
            uTreasure.setOwnNum(Integer.valueOf(tInfo[1]));
            gameUserService.updateItem(uTreasure);
        });

        return new RDSuccess();
    }

    /**
     * 神仙大会匹配同步法宝
     *
     * @param uid
     * @return
     */
    public RDSuccess syncTicket(long uid) {
        SxdhFighter sxdhFighter = sxdhFighterService.getFighter(uid);
        if (sxdhFighter.getFreeTimes() > 0) {
            sxdhFighter.deductFreeTimes();
            gameUserService.updateItem(sxdhFighter);
            return new RDSuccess();
        }
        int needTicket = SxdhTool.getSxdh().getNeedTicket();
        TreasureEventPublisher.pubTDeductEvent(uid, TreasureEnum.SXDH_TICKET.getValue(), needTicket,
                WayEnum.SXDH_FIGHT, new RDCommon());
        return new RDSuccess();
    }

    /**
     * 神仙大会加入匹配队列前调用
     *
     * @param uid
     * @return
     */
    public RDToMatch toMatch(long uid) {
        CfgServerEntity server = gameUserService.getOriServer(uid);
        SxdhZone sxdhZone = sxdhZoneService.getZoneByServer(server);
        if (sxdhZone == null) {
            throw new ExceptionForClientTip("sxdh.season.not.start");
        }
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        CfgSxdh cfgSxdh = SxdhTool.getSxdh();
        if (hour < cfgSxdh.getOpenBeginHour() && hour >= cfgSxdh.getOpenEndHour()) {
            throw new ExceptionForClientTip("sxdh.season.not.open");
        }
        // 特殊赛季，每个阶段最多进行50次匹配
        if (SxdhTool.isSpecialSeason() && !sxdhMatchLimitService.isAbleMatch(uid, sxdhZone)) {
            throw new ExceptionForClientTip("sxdh.season.special.phase.outOfLimit");
        }
        SxdhFighter sxdhFighter = sxdhFighterService.getFighter(uid);
        if (sxdhFighter.getFreeTimes() == 0) {
            int needTicket = SxdhTool.getSxdh().getNeedTicket();
            int ticket = userTreasureService.getTreasureNum(uid, TreasureEnum.SXDH_TICKET.getValue());
            if (ticket < needTicket) {
                throw new ExceptionForClientTip("sxdh.shop.not.enought.ticket");
            }
        }

        RDToMatch rd = new RDToMatch();
        // 战区信息
        rd.setZone(sxdhZone.getZone());
        rd.setZoneSids(sxdhZone.getSids());
        // 丹药信息
        List<UserTreasure> uTreasures = userTreasureService.getAllUserTreasures(uid);
        if (ListUtil.isNotEmpty(uTreasures)) {
            List<Integer> medicines = new ArrayList<Integer>();
            medicines.add(TreasureEnum.BuSD.getValue());
            medicines.add(TreasureEnum.YuanQD.getValue());
            medicines.add(TreasureEnum.ChangSD.getValue());
            medicines.add(TreasureEnum.HeLD.getValue());
            medicines.add(TreasureEnum.YangWD.getValue());
            medicines = medicines.stream().map(medicine -> {
                Optional<UserTreasure> optional = uTreasures.stream().filter(tmp -> tmp.getBaseId().intValue() == medicine).findFirst();
                if (optional.isPresent()) {
                    return optional.get().gainTotalNum();
                }
                return 0;
            }).collect(Collectors.toList());
            rd.setMedicines(medicines);
        } else {
            rd.setMedicines(Arrays.asList(0, 0, 0, 0, 0));
        }
        return rd;
    }

    /**
     * 神仙大会战斗提交
     *
     * @param param
     * @return
     */
    public RDSxdhFightResult submitSxdhFightResult(CPFsFightSubmit param) {
        RDSxdhFightResult rd = new RDSxdhFightResult();
        long winner = param.getWinner();
        long loser = param.getLoser();
        int winnerZcNum = pVPService.getZcTimes(param.getCombatId(), winner);
        // 总得分，额外分数
        int[] winnerAddScore = {0, 0};
        int[] loserAddScore = {0, 0};
        JSONObject extraJs = null;
        SxdhRoboterType roboterType = SxdhRoboterType.TWO;
        if (StrUtil.isNotEmpty(param.getExtra())) {
            extraJs = JSON.parseObject(param.getExtra());
            roboterType = SxdhRoboterType.fromValue(extraJs.getInteger("roboterType"));
        }
        long realPlayerUid = winner > 0 ? winner : loser;
        CfgServerEntity server = gameUserService.getOriServer(realPlayerUid);
        SxdhZone sxdhZone = sxdhZoneService.getZoneByServer(server);
        winnerAddScore = sxdhFightService.handleAsSxdhWin(sxdhZone, winner, loser, param.getRoomId(), roboterType, winnerZcNum);
        loserAddScore[0] = sxdhFightService.handleAsSxdhFail(sxdhZone, loser, winner, param.getRoomId(), roboterType);
        rd.setWinnerAddedScore(winnerAddScore[0] + winnerAddScore[1]);
        rd.setLoserAddedScore(loserAddScore[0] + loserAddScore[1]);
        InsGamePvpDetailEntity detailData = new InsGamePvpDetailEntity();
        detailData.setId(ID.INSTANCE.nextId());
        detailData.setServerGroup(server.getGroupId());
        detailData.setFightType(FightTypeEnum.SXDH.getValue());
        detailData.setFightTypeName(FightTypeEnum.SXDH.getName());
        detailData.setRoomId(param.getRoomId());
        detailData.setUser1(winner);
        detailData.setUser2(loser);
        detailData.setWinner(winner);
        detailData.setFightTime(DateUtil.toDateTimeLong());
        SxdhFightDetail sxdhDetail = new SxdhFightDetail();
        if (sxdhZone != null) {
            sxdhDetail.setZoneType(sxdhZone.getZone());

        }
        if (extraJs != null) {
            sxdhDetail.setRoboterType(SxdhRoboterType.fromValue(extraJs.getInteger("roboterType")));
            sxdhDetail.setLevel1(extraJs.getInteger("level1"));
            sxdhDetail.setLevel2(extraJs.getInteger("level2"));
        }
        sxdhDetail.setAddScore1(winnerAddScore[0] + winnerAddScore[1]);
        sxdhDetail.setAddScore2(loserAddScore[0] + loserAddScore[1]);
        detailData.setDataJson(JSONUtil.toJson(sxdhDetail));
        pvpDetailAsyncHandler.log(detailData);

        FightSubmitParam fightSubmitParam = new FightSubmitParam();
        fightSubmitParam.setCombatId(param.getCombatId());
        if (winner > 0) {
            GameUser winUser = gameUserService.getGameUser(winner);
            CombatEventPublisher.pubWinEvent(EPFightEnd.instance(winner, winUser.getLocation().getPosition(), FightTypeEnum.SXDH, true, fightSubmitParam, new RDCommon()));
            if (param.getWinnerOnline() == 0) {
                String title = LM.I.getMsgByUid(winner, "mail.sxdh.fight.results.title");
                String content = LM.I.getMsgByUid(winner, "mail.sxdh.fight.results.win.content", param.getLoserNickname(), rd.getWinnerAddedScore());
                mailService.sendSystemMail(title, content, winner);
            }
            CombatEventPublisher.pubSxdhAddPointEvent(winner, rd.getWinnerAddedScore());
        }
        if (loser > 0) {
            GameUser failUser = gameUserService.getGameUser(loser);
            CombatEventPublisher.pubFailEvent(EPFightEnd.instance(loser, failUser.getLocation().getPosition(), FightTypeEnum.SXDH, true, fightSubmitParam, new RDCommon()));
            if (param.getLoserOnline() == 0) {
                String title = LM.I.getMsgByUid(loser, "mail.sxdh.fight.results.title");
                String content = LM.I.getMsgByUid(loser, "mail.sxdh.fight.results.lose.content", param.getWinnerNickname(), rd.getLoserAddedScore());
                mailService.sendSystemMail(title, content, loser);
            }
            CombatEventPublisher.pubSxdhAddPointEvent(winner, rd.getLoserAddedScore());
        }
        return rd;
    }

    /**
     * 巅峰对决战斗提交
     *
     * @param param
     * @return
     */
    public RDDfdjFightResult submitDfdjFightResult(CPFsFightSubmit param) {
        RDDfdjFightResult rd = new RDDfdjFightResult();
        long winner = param.getWinner();
        long loser = param.getLoser();
        int[] loserAddScore = {0, 0};
        long realPlayerUid = winner > 0 ? winner : loser;
        CfgServerEntity server = gameUserService.getOriServer(realPlayerUid);
        DfdjZone zone = dfdjZoneService.getZoneByServer(server);
        int[] winnerAddScore = dfdjFightService.handleAsDfdjWin(zone, winner, loser, param.getRoomId());
        loserAddScore[0] = dfdjFightService.handleAsDfdjFail(zone, loser, winner, param.getRoomId());
        rd.setWinnerAddedScore(winnerAddScore[0] + winnerAddScore[1]);
        rd.setLoserAddedScore(loserAddScore[0] + loserAddScore[1]);
        rd.setWinnerExtraAward(dfdjFightService.addEleAward(winner));
        rd.setLoserExtraAward(dfdjFightService.addEleAward(loser));

        FightSubmitParam fightSubmitParam = new FightSubmitParam();
        fightSubmitParam.setCombatId(param.getCombatId());
        if (winner > 0) {
            GameUser winUser = gameUserService.getGameUser(winner);
            CombatEventPublisher.pubWinEvent(EPFightEnd.instance(winner, winUser.getLocation().getPosition(), FightTypeEnum.DFDJ, true, fightSubmitParam, new RDCommon()));
            if (param.getWinnerOnline() == 0) {
                mailService.sendSystemMail("巅峰对决结算通知", "您离线时参与的巅峰对决已结束，您击败了【" + param.getLoserNickname() + "】,获得积分*" + rd.getWinnerAddedScore() + "。", winner);
            }
        }
        if (loser > 0) {
            GameUser failUser = gameUserService.getGameUser(loser);
            CombatEventPublisher.pubFailEvent(EPFightEnd.instance(loser, failUser.getLocation().getPosition(), FightTypeEnum.DFDJ, true, fightSubmitParam, new RDCommon()));
            if (param.getLoserOnline() == 0) {
                mailService.sendSystemMail("巅峰对决结算通知", "您离线时参与的巅峰对决已结束，您被【" + param.getWinnerNickname() + "】击败了,获得积分*" + rd.getLoserAddedScore() + "。", loser);
            }
        }
        // 记录明细
        InsGamePvpDetailEntity detailData = new InsGamePvpDetailEntity();
        detailData.setId(ID.INSTANCE.nextId());
        detailData.setServerGroup(server.getGroupId());
        detailData.setFightType(FightTypeEnum.DFDJ.getValue());
        detailData.setFightTypeName(FightTypeEnum.DFDJ.getName());
        detailData.setRoomId(param.getRoomId());
        detailData.setUser1(winner);
        detailData.setUser2(loser);
        detailData.setWinner(winner);
        detailData.setFightTime(DateUtil.toDateTimeLong());
        DfdjFightDetail dfdjFightDetail = new DfdjFightDetail(
                zone.getZone(),
                gameUserService.getGameUser(winner).getLevel(),
                gameUserService.getGameUser(loser).getLevel(),
                winnerAddScore[0] + winnerAddScore[1],
                loserAddScore[0] + loserAddScore[1]);
        detailData.setDataJson(JSONUtil.toJson(dfdjFightDetail));
        pvpDetailAsyncHandler.log(detailData);
        return rd;
    }

    /**
     * 迁移旧数据
     *
     * @param uid
     * @param bean
     * @param ticket
     * @param joinDate
     */
    private void migrateFighter(long uid, int bean, int ticket, String joinDate) {
        SxdhFighter sxdhFighter = gameUserService.getSingleItem(uid, SxdhFighter.class);
        if (sxdhFighter == null) {
            TreasureEventPublisher.pubTAddEvent(uid, TreasureEnum.XIAN_DOU.getValue(), bean,
                    WayEnum.SXDH_GAIN, new RDCommon());
            TreasureEventPublisher.pubTAddEvent(uid, TreasureEnum.SXDH_TICKET.getValue(), ticket,
                    WayEnum.SXDH_GAIN, new RDCommon());
            sxdhFighter = SxdhFighter.instance(uid);
            sxdhFighter.setJoinDate(DateUtil.fromDateTimeString(joinDate));
            gameUserService.addItem(uid, sxdhFighter);
            log.info("{}神仙大会成功迁移", uid);
        }
    }

    /**
     * 返回机器人数据
     *
     * @param uid
     * @return
     */
    public RDFsRoboter getRobotInfo(long uid) {
        SxdhZone sxdhZone = sxdhZoneService.getCurOrLastZone(uid);
        int phaseScore = sxdhRankService.getScore(sxdhZone, SxdhRankType.PHASE_RANK, uid);
        int title = SxdhTool.getSegmentByScore(phaseScore).getId();
        SxdhMatchedRoboter roboter = sxdhRoboterService.matchRoboter(uid, title);
        GameUser robotUser = roboter.getGu();
        RDFsRoboter rd = new RDFsRoboter();
        rd.setRoboterType(roboter.getType().getValue());
        // 玩家昵称
        String nickname = robotUser.getRoleInfo().getNickname();
        int head = robotUser.getRoleInfo().getHead();
        if (roboter.getType() == SxdhRoboterType.THREE) {
            nickname = serverUserService.getRandomNickName();
            head = PowerRandom.getRandomFromList(roboter.getCards()).getBaseId();
        }
        rd.setRoboterId(robotUser.getId());
        rd.setName(nickname);
        CfgServerEntity server = ServerTool.getServer(roboter.getGu().getServerId());
        rd.setServerName(server.getShortName());
        rd.setGroupId(server.getGroupId());
        // 头像
        rd.setHead(head);
        // 等级
        rd.setLevel(robotUser.getLevel());
        // 性别
        rd.setSex(robotUser.getRoleInfo().getSex());
        SxdhFighter sxdhFighter = sxdhFighterService.getFighter(uid);
        // 前两把必定匹配一个较弱的机器人
        if (sxdhFighter.getWinTimes() <= 1) {
            roboter.getCards().forEach(tmp -> {
                tmp.setLevel(0);
                tmp.setHierarchy(0);
            });
        }
        List<RDCard> rdcCards = roboter.getCards().stream().map(uc -> RDCard.instanceForPvpRoboter(uc)).collect(Collectors.toList());
        rd.setCard(rdcCards);
        //获取封装主角卡
        Optional<UserLeaderCard> leaderCardOp = leaderCardService.getUserLeaderCardOp(uid);
        if (leaderCardOp.isPresent()) {
            UserLeaderCard leaderCard = leaderCardOp.get();
            rd.getCard().add(RDCard.instance(leaderCard));
        }
//        log.info(JSONUtil.toJson(rd));
        return rd;
    }
}
