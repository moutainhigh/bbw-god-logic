package com.bbw.god.server.fst;

import com.bbw.common.CloneUtil;
import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.card.*;
import com.bbw.god.gameuser.leadercard.UserLeaderCard;
import com.bbw.god.gameuser.leadercard.beast.UserLeaderBeastService;
import com.bbw.god.gameuser.leadercard.equipment.UserLeaderEquimentService;
import com.bbw.god.gameuser.leadercard.service.LeaderCardService;
import com.bbw.god.server.fst.game.FstGameRanking;
import com.bbw.god.server.fst.game.FstGameService;
import com.bbw.god.server.fst.game.FstRankingType;
import com.bbw.god.server.fst.robot.FstGameRobot;
import com.bbw.god.server.fst.robot.FstRobotService;
import com.bbw.god.server.fst.server.FstServerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author liuwenbin
 * 封神台
 */
@Slf4j
@Service
public class FstLogic {
    @Autowired
    private FstServerService fstServerService;
    @Autowired
    private FstGameService fstGameService;
    @Autowired
    private OppCardService oppCardService;
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private UserCardService userCardService;
    @Autowired
    private FstRobotService fstRobotService;
    @Autowired
    private UserCardGroupService userCardGroupService;
    @Autowired
    private LeaderCardService leaderCardService;
    @Autowired
    private UserLeaderBeastService userLeaderBeastService;
    @Autowired
    private UserLeaderEquimentService userLeaderEquimentService;

    /**
     * 匹配封神台service
     *
     * @param fstType
     * @return
     */
    public FstService matchFstService(FstType fstType) {
        if (fstServerService.getFstType().equals(fstType)) {
            return fstServerService;
        }
        return fstGameService;
    }

    /**
     * 进入封神台
     *
     * @param uid
     * @param type
     * @return
     */
    public RDFst intoFst(long uid, FstType type) {
        return matchFstService(type).intoFst(uid);
    }

    /**
     * 领取积分
     *
     * @param uid
     * @return
     */
    public RDFst gainIncrementPoints(long uid) {
        return fstServerService.gainIncrementPoints(uid);
    }

    /**
     * 显示榜单
     *
     * @param isPreRanking 是否显示的是之前的榜单  false表示实时的
     * @return
     */
    public RDFst ranking(long uid, boolean isPreRanking, FstRankingType rankingType) {
        return fstGameService.ranking(uid, isPreRanking, rankingType);
    }

    /**
     * 初始化跨服封神台
     *
     * @param gid
     * @return
     */
    public boolean initGameFst(int gid) {
        return fstGameService.initFst(gid);
    }

    /**
     * 初始化区服封神台
     *
     * @param sid
     * @return
     */
    public boolean initServerFst(int sid) {
        return fstServerService.initFst(sid);
    }


    /**
     * 获取战斗信息
     *
     * @param uid
     * @param log
     * @return
     */
    public List<RDFst.FightLog> getLog(long uid, FstVideoLog log) {
        List<RDFst.FightLog> list = new ArrayList<>();
        if (log == null) {
            return list;
        }
        RDFst.Player p1 = getPlayer(uid);
        RDFst.Player p2 = getPlayer(log.getOppo());
        for (FstVideoLog.Log logLog : log.getLogs()) {
            list.add(RDFst.FightLog.getInstance(CloneUtil.clone(p1), CloneUtil.clone(p2), logLog));
        }
        return list;
    }

    /**
     * 获取玩家信息
     *
     * @param uid
     * @return
     */
    private RDFst.Player getPlayer(long uid) {
        RDFst.Player player = RDFst.Player.getInstance(uid);
        if (uid < 0) {
            FstGameRobot info = fstRobotService.getRobotInfo(uid);
            player.setHead(info.getHead());
            player.setNickname(info.getNickname());
        } else {
            GameUser user = gameUserService.getGameUser(uid);
            player.setHead(user.getRoleInfo().getHead());
            player.setNickname(user.getRoleInfo().getNickname());
            player.setHeadIcon(user.getRoleInfo().getHeadIcon());
        }
        return player;
    }

    public RDFst fightLog(long uid, boolean isGameFst, Long id) {
        FstVideoLog log = null;
        if (isGameFst) {
            Optional<FstGameRanking> op = fstGameService.getFstGameRankingOp(uid);
            if (op.isPresent()) {
                Optional<FstVideoLog> logOptional = op.get().getVideoLogs().stream().filter(p -> p.getId().equals(id)).findFirst();
                if (logOptional.isPresent()) {
                    log = logOptional.get();
                }
            }
        } else {
            Optional<FstRanking> op = fstServerService.getFstRanking(uid);
            if (op.isPresent()) {
                Optional<FstVideoLog> logOptional = op.get().getVideoLogs().stream().filter(p -> p.getId().equals(id)).findFirst();
                if (logOptional.isPresent()) {
                    log = logOptional.get();
                }
            }
        }
        RDFst rst = new RDFst();
        rst.setLogs(getLog(uid, log));
        return rst;
    }

    /**
     * 获取卡组信息
     *
     * @param uid
     * @param isGameFst
     * @param isAttack
     * @return
     */
    public RDFst getUserCardGroup(Long uid, boolean isGameFst, boolean isAttack) {
        RDFst rd = new RDFst();
        List<List<RDCardStrengthen>> carGroup = new ArrayList<>();
        rd.setCardGroup(carGroup);
        if (uid == null || uid < 0) {
            return rd;
        }
        List<CardGroupWay> cardGroupWays = null;
        if (isAttack) {
            if (isGameFst) {
                cardGroupWays = Arrays.asList(CardGroupWay.GAME_FST_ATTACK1, CardGroupWay.GAME_FST_ATTACK2, CardGroupWay.GAME_FST_ATTACK3);
            } else {
                cardGroupWays = Arrays.asList(CardGroupWay.FIERCE_FIGHTING_ATTACK);
            }
        } else {
            if (isGameFst) {
                cardGroupWays = Arrays.asList(CardGroupWay.GAME_FST_DEFENSE1, CardGroupWay.GAME_FST_DEFENSE2, CardGroupWay.GAME_FST_DEFENSE3);
            } else {
                cardGroupWays = Arrays.asList(CardGroupWay.FIERCE_FIGHTING_DEFENSE);
            }
        }
        List<UserCard> userCards = oppCardService.getOppAllCards(uid);
        for (CardGroupWay way : cardGroupWays) {
            CardGroup cGroup = userCardGroupService.getFierceFightingCards(uid, way);
            List<Integer> cards = cGroup.getCardIds();
            List<UserCard> group = userCards.stream().filter(p -> cards.contains(p.getBaseId()) || cards.contains(p.getBaseId() - CardTool.DeifyBase)).collect(Collectors.toList());
            List<RDCardStrengthen> list = new ArrayList<>();
            for (UserCard userCard : group) {
                list.add(RDCardStrengthen.getInstance(userCard));
            }
            if (cGroup.hasLeaderCard()) {
                Optional<UserLeaderCard> op = leaderCardService.getUserLeaderCardOp(uid);
                if (op.isPresent()) {
                    UserLeaderCard leaderCard = op.get();
                    RDCardStrengthen instance = RDCardStrengthen.getInstance(leaderCard);
                    instance.setAtk(leaderCard.settleTotalAtkWithEquip());
                    instance.setHp(leaderCard.settleTotalHpWithEquip());
                    instance.setEquips(userLeaderEquimentService.getTakedEquipments(uid));
                    instance.setBeasts(userLeaderBeastService.getTakedBeasts(uid));
                    instance.setCardName(gameUserService.getGameUser(uid).getRoleInfo().getNickname());
                    list.add(instance);
                }
            }
            carGroup.add(list);
        }
        return rd;
    }
}
