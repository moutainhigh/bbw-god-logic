package com.bbw.god.server.fst.server;

import com.bbw.common.DateUtil;
import com.bbw.common.ListUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.game.combat.data.param.CCardParam;
import com.bbw.god.game.combat.data.param.CPCardGroup;
import com.bbw.god.game.combat.data.param.CPlayerInitParam;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.game.config.CfgFst;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.game.data.GameDataService;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.card.*;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.server.ServerDataService;
import com.bbw.god.server.ServerDataType;
import com.bbw.god.server.fst.*;
import com.bbw.god.server.fst.event.FstEventPublisher;
import com.bbw.god.server.fst.event.FstIncrementPoint;
import com.bbw.god.server.fst.game.FstGameRanking;
import com.bbw.god.server.fst.game.FstGameService;
import com.bbw.god.server.fst.game.FstRankingType;
import com.bbw.god.server.fst.robot.FstGameRobot;
import com.bbw.god.server.fst.robot.FstRobotService;
import com.bbw.god.server.redis.ServerRedisKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.bbw.god.server.fst.FstTool.REDIS_KEY;
import static com.bbw.god.server.fst.robot.FstRobotService.getServerRobotId;

/**
 * 说明：区服封神台
 *
 * @author lwb
 * date 2021-06-29
 */
@Service
public class FstServerService extends FstService {
    @Autowired
    private ServerDataService serverDataService;
    @Autowired
    private GameUserService userService;
    @Autowired
    private UserCardLogic userCardLogic;
    @Autowired
    private UserCardGroupLogic userCardGroupLogic;
    @Autowired
    private OppCardService oppCardService;
    @Autowired
    private FstGameService fstGameService;
    @Autowired
    private GameDataService gameDataService;
    private static byte[] lock = new byte[0];

    @Override
    public FstType getFstType() {
        return FstType.SERVER;
    }

    /**
     * 封神台榜单key
     *
     * @param serverId
     * @return
     */
    private String getFstKey(int serverId) {
        return ServerRedisKey.getDataTypeKey(serverId, ServerDataType.FSTPVPRanking, REDIS_KEY);
    }

    /**
     * 封神台区服战斗状态
     *
     * @param sid
     * @return
     */
    private String getFstFightStateKey(int sid) {
        return ServerRedisKey.getDataTypeKey(sid, ServerDataType.FSTPVPRanking, REDIS_KEY, "fightState");
    }

    @Override
    public boolean isUnlock(long uid) {
        GameUser gu = gameUserService.getGameUser(uid);
        return FstTool.getCfg().getUnlockLevel() <= gu.getLevel();
    }

    @Override
    public boolean checkCardGroupState(long uid) {
        //需要配置攻防卡组
        RDCardGroups rd = userCardGroupLogic.getFierceFightingCards(uid);
        if (rd.isEmpty(CardGroupWay.FIERCE_FIGHTING_ATTACK) || rd.isEmpty(CardGroupWay.FIERCE_FIGHTING_DEFENSE)) {
            return false;
        }
        return true;
    }

    @Override
    public RDFst intoFst(long uid) {
        RDFst rd = RDFst.getIntoFst();
        rd.setIsJoinGameFst(0);
        // 获取封神台 实际排名
        int myRealRanking = getFstRankWithIntoRanking(uid);
        rd.setMyRank(myRealRanking);
        Optional<FstRanking> optional = getFstRanking(uid);
        List<FstFightMsg> msgs = new ArrayList<>();
        rd.setRankingType(FstRankingType.SERVER.getType());
        if (myRealRanking > 0 && optional.isPresent()) {
            FstRanking myFstRanking = optional.get();
            //设置玩家自身信息
            rd.setMyAblePoint(getPointByRank(myRealRanking, FstRankingType.SERVER));
            //封神台积分
            rd.setMyPoint(userTreasureService.getTreasureNum(uid, TreasureEnum.FST_POINT.getValue()));
            rd.setPvpTimes(FstTool.getCfg().getFreeTimes() - myFstRanking.getTodayFightTimes());
            rd.setCurrentPoint(myFstRanking.getIncrementPoints());
            if (ListUtil.isNotEmpty(myFstRanking.getVideoLogs())) {
                for (int i = myFstRanking.getVideoLogs().size() - 1; i >= 0; i--) {
                    FstVideoLog log = myFstRanking.getVideoLogs().get(i);
                    msgs.add(FstFightMsg.getInstance(log, getRankingUserNickName(log.getOppo())));
                }
            }
        }
        //获取榜单
        rd.setRanks(getRankList(myRealRanking, uid));
        for (FstRankerParam param : rd.getRanks()) {
            param.setAblePoints(getPointByRank(param.getPvpRanking(), FstRankingType.fromVal(rd.getRankingType())));
        }
        rd.setPreRanks(getRangeRankList(1, 3, uid));
        rd.setFightMsgs(msgs);
        Optional<FstGameRanking> op = fstGameService.getFstGameRankingOp(uid);
        rd.setIsJoinGameFst(1 <= rd.getMyRank() && rd.getMyRank() <= 100 ? 1 : 0);
        if (op.isPresent()) {
            FstGameRanking ranking = op.get();
            rd.setShowPopType(ranking.getShowPop());
            if (ranking.getShowPop() == FstPopType.JOIN_TO_GAME_FST.getType()) {
                ranking.setShowPop(FstPopType.NONE.getType());
                gameDataService.updateGameData(ranking);
            }
            if (rd.getIsJoinGameFst() == 0) {
                rd.setIsJoinGameFst(op.get().getRankingType() > 0 ? 1 : 0);
            }
        }
        return rd;
    }

    @Override
    public List<FstRankerParam> getRankList(int myRank, long uid) {
        CfgFst config = FstTool.getCfg();
        Integer showCount = config.getShowCount();
        List<FstRankerParam> list = new ArrayList<>();
        int sid = gameUserService.getActiveSid(uid);
        int fireRange = config.getFireRange();
        if ((showCount + config.getFireRange()) < myRank) {
            int step = getStep(myRank);
            for (int i = fireRange; i >= 0; i--) {
                int tmpRanking = myRank - step * i;
                Long tUid = getUidByRank(sid, tmpRanking);
                if (tUid == uid) {
                    continue;
                }
                list.add(getFstRankerParam(tUid, tmpRanking, getFightAble(tmpRanking, myRank), FstRankingType.SERVER));
            }
        } else {
            int begin = 1;
            int end = showCount + 1;
            if (myRank > showCount) {
                begin = myRank - showCount;
                end = myRank - 1;
            }
            list = getRangeRankList(begin, end, uid);
            list = list.stream().filter(p -> p.getId() != uid).collect(Collectors.toList());
        }
        return list;
    }


    private Long getUidByRank(int sid, int rank) {
        return rankingList.rankObject(getFstKey(sid), rank);
    }

    /**
     * 获取区间
     *
     * @param begin
     * @param end
     * @param uid
     * @return
     */
    @Override
    public List<FstRankerParam> getRangeRankList(int begin, int end, Long uid) {
        int sid = gameUserService.getActiveSid(uid);
        int myRank = getFstRank(uid);
        List<Long> rangeRank = getRangeRank(sid, begin, end);
        List<FstRankerParam> rankerParams = new ArrayList<>();
        for (int i = 0; i < rangeRank.size(); i++) {
            long id = rangeRank.get(i);
            rankerParams.add(getFstRankerParam(id, begin + i, getFightAble(begin + i, myRank), FstRankingType.SERVER));
        }
        return rankerParams;
    }

    @Override
    public int getPointByRank(int pvpRank, FstRankingType type) {
        if (pvpRank == 1) {
            return 400;
        } else if (pvpRank == 2) {
            return 360;
        } else if (pvpRank == 3) {
            return 350;
        } else if (pvpRank < 11) {
            return 330 - (pvpRank - 4) * 5;
        } else if (pvpRank < 101) {
            return 288 - (pvpRank - 11) / 3 * 3;
        } else if (pvpRank < 401) {
            return 199 - (pvpRank - 101) / 10;
        } else if (pvpRank < 1001) {
            return 169 - (pvpRank - 401) / 25;
        } else if (pvpRank < 1601) {
            return 144 - (pvpRank - 1001) / 40;
        } else if (pvpRank < 3201) {
            return 129 - (pvpRank - 1601) / 100;
        } else if (pvpRank < 25001) {
            return 113 - (pvpRank - 3201) / 200;
        }
        return 0;
    }

    @Override
    public int getRemainChallengeNum(long guId) {
        Optional<FstRanking> optional = getFstRanking(guId);
        if (optional.isPresent()) {
            return FstTool.getCfg().getFreeTimes() - optional.get().getTodayFightTimes();
        }
        return 0;
    }

    /**
     * 领取封神台 增值积分
     *
     * @param guId
     */
    public RDFst gainIncrementPoints(long guId) {
        RDFst rd = new RDFst();
        Optional<FstRanking> optional = getFstRanking(guId);
        if (optional.isPresent()) {
            FstRanking fstRanking = optional.get();
            int points = fstRanking.getIncrementPoints();
            if (points <= 0) {
                return rd;
            }
            fstRanking.deductIncrementPoints(points);
            serverDataService.updateServerData(fstRanking);
            TreasureEventPublisher.pubTAddEvent(guId, TreasureEnum.FST_POINT.getValue(), points, WayEnum.FST_GAIN_POINT, rd);
            FstIncrementPoint param = FstIncrementPoint.instance(new BaseEventParam(guId), points);
            FstEventPublisher.pubFstIncrementPointEvent(param);
        }
        return rd;
    }

    /**
     * 加入榜单，失败为false
     *
     * @param uid
     * @return
     */
    public boolean intoFstRanking(long uid) {
        int myRank = getFstRank(uid);
        if (myRank > 0) {
            return true;
        }
        //确认  解锁 和 卡组状态
        if (!isUnlock(uid) || !checkCardGroupState(uid)) {
            return false;
        }
        int sid = gameUserService.getActiveSid(uid);
        int rank = addToRank(uid, sid);
        if (rank > 0) {
            getOrCreateFstRanking(uid);
            FstEventPublisher.pubIntoFstEvent(uid);
            return true;
        }
        return false;
    }

    /**
     * 添加到榜单
     *
     * @param id
     * @param sid
     * @return
     */
    public int addToRank(long id, int sid) {
        int fstRank = getFstRank(id);
        if (fstRank > 0) {
            return fstRank;
        }
        String fsKey = getFstKey(sid);
        synchronized (lock) {
            Long newRank = rankingList.size(fsKey) + 1;
            Boolean success = rankingList.add(fsKey, id, newRank);
            if (success) {
                return newRank.intValue();
            }
        }
        return -1;
    }


    /**
     * 仅仅获的排行，如果没有排行，不加入排行，并返回0 <br>
     * <fonte>机器人无法通过该方法获取排名</fonte>
     *
     * @param uid
     * @return
     */
    @Override
    public int getFstRank(Long uid) {
        int sid = uid < 0 ? FstRobotService.getServerId(uid) : userService.getActiveSid(uid);
        String fstKey = getFstKey(sid);
        Long rank = rankingList.rank(fstKey, uid);
        if (rank != null) {
            return rank.intValue() + 1;
        }
        return 0;
    }

    /**
     * <pre>
     * 获得某个排行区间的玩家
     * 排行从1开始。
     * </pre>
     *
     * @param sid
     * @param start
     * @param end
     * @return
     */
    public List<Long> getRangeRank(int sid, int start, int end) {
        Set<Long> set = rankingList.range(getFstKey(sid), start - 1, end - 1);
        return set.stream().collect(Collectors.toList());
    }

    /**
     * 交换两个玩家的封神台排行
     *
     * @param uid1
     * @param uid2
     * @return
     */
    @Override
    public boolean swapRanking(Long uid1, FstVideoLog log1, Long uid2, FstVideoLog log2) {
        String fsKey = getFstKey(userService.getActiveSid(uid1));
        Long rank1 = rankingList.rank(fsKey, uid1);
        Long rank2 = rankingList.rank(fsKey, uid2);
        if (rank1 == null || rank2 == null) {
            return false;
        }
        if (rank1 < rank2) {
            return false;
        }
        rank2++;
        rank1++;
        rankingList.add(fsKey, uid1, rank2);
        rankingList.add(fsKey, uid2, rank1);
        log1.setRank(rank1 > rank2 ? rank2.intValue() : -rank2.intValue());
        log2.setRank(rank1 > rank2 ? -rank1.intValue() : rank1.intValue());
        if (Math.abs(log1.getRank()) <= 100 && uid1 > 0) {
            fstGameService.joinToGameFst(uid1);
        }
        if (Math.abs(log2.getRank()) <= 100 && uid2 > 0) {
            fstGameService.joinToGameFst(uid2);
        }
        return true;
    }

    /**
     * 更新排行
     *
     * @param uid
     * @param rank
     */
    public void updateRank(int sid, Long uid, int rank) {
        String fsKey = getFstKey(sid);
        rankingList.add(fsKey, uid, rank);
    }

    @Override
    public boolean checkFightState(long p1, long p2) {
        //先检查是否在同一个榜单
        Integer sid = gameUserService.getActiveSid(p1);
        Optional<FstRanking> op = getFstRanking(p1);
        if (!op.isPresent()) {
            //自身必须要加入到榜单中
            throw new ExceptionForClientTip("fst.fighter.not.in.ranking");
        }
        FstRanking ranking = op.get();
        if (FstTool.getCfg().getFreeTimes() - ranking.getTodayFightTimes() <= 0) {
            //没有挑战次数
            throw new ExceptionForClientTip("fst.not.fight.times");
        }
        int fstRank1 = getFstRank(p1);
        int fstRank2 = getFstRank(p2);
        if (fstRank1 == 0 || fstRank2 == 0) {
            throw new ExceptionForClientTip("fst.fighter.not.in.ranking");
        }
        if (!getFightAble(fstRank2, fstRank1)) {
            throw new ExceptionForClientTip("fst.cant.attack.this.rank");
        }
        String fightStateKey = getFstFightStateKey(sid);
        synchronized (sid) {
            Long p1State = hasFightStateHUtil.getField(fightStateKey, p1);
            long millis = System.currentTimeMillis();
            if (p1State != null && (millis - p1State) < FIGHT_TIME_OUT) {
                //5分钟内说明 还在战斗
                return false;
            }
            Long p2State = hasFightStateHUtil.getField(fightStateKey, p2);
            if (p2State != null && (millis - p2State) < FIGHT_TIME_OUT) {
                //5分钟内说明 还在战斗
                return false;
            }
            hasFightStateHUtil.putField(fightStateKey, p1, millis);
            hasFightStateHUtil.putField(fightStateKey, p2, millis);
        }
        ranking.deductChallengeNum();
        serverDataService.updateServerData(ranking);
        return true;
    }

    /**
     * 获取某一区服玩家封神台排行数据
     *
     * @param uid
     * @return
     */
    public Optional<FstRanking> getFstRanking(long uid) {
        int sid = gameUserService.getActiveSid(uid);
        FstRanking ranking = serverDataService.getServerData(sid, FstRanking.class, uid);
        if (ranking != null) {
            int todayInt = DateUtil.getTodayInt();
            ranking.setVideoLogs(ranking.getVideoLogs().stream().filter(p -> p.ifValid()).collect(Collectors.toList()));
            if ((ranking.getLastUpdateDate() == null || ranking.getLastUpdateDate() != todayInt) && !DateUtil.isToday(ranking.getLastChallengeTime())) {
                ranking.setTodayFightTimes(0);
                ranking.setLastUpdateDate(todayInt);
            }
            serverDataService.updateServerData(ranking);
        }
        return Optional.ofNullable(ranking);
    }

    /**
     * 获取封神台信息 不存在则创建默认对象,不会主动加入榜单
     *
     * @param uid
     * @return
     */
    public FstRanking getOrCreateFstRanking(long uid) {
        Optional<FstRanking> optional = getFstRanking(uid);
        if (optional.isPresent()) {
            return optional.get();
        }
        int sid = gameUserService.getActiveSid(uid);
        FstRanking fstRanking = FstRanking.instance(uid, sid, -1);
        serverDataService.updateServerData(fstRanking);
        return fstRanking;
    }

    /**
     * 移除榜单
     *
     * @param sId
     */
    public void removeRanks(int sId) {
        String fstKey = getFstKey(sId);
        rankingList.remove(fstKey);
    }

    /**
     * 获取整个封神台的榜单
     *
     * @param sId
     * @return
     */
    public Set<ZSetOperations.TypedTuple<Long>> getAllRankers(int sId) {
        String fstKey = getFstKey(sId);
        Set<ZSetOperations.TypedTuple<Long>> rankersSet = rankingList.rangeWithScores(fstKey);
        return rankersSet;
    }


    @Override
    public int getFstRankWithIntoRanking(Long uid) {
        if (!intoFstRanking(uid)) {
            //不在封神台内
            return 0;
        }
        return getFstRank(uid);
    }

    /**
     * 获取战斗卡组
     *
     * @param guId
     * @return
     */
    public List<CCardParam> getMyAttackInfo(long guId) {
        //获取玩家激战防守卡组
        List<CCardParam> userCards = userCardGroupLogic.getFierceFightingUserCards(guId, CardGroupWay.FIERCE_FIGHTING_ATTACK);
        if (userCards.isEmpty()) {
            throw new ExceptionForClientTip("fst.not.deploy.cardGroup");
        }
        return userCards;
    }

    /**
     * 获取防御卡组
     *
     * @param guId
     * @return
     */
    public CPlayerInitParam getMyDefenseInfo(long guId) {
        //获取玩家激战防守卡组
        if (guId < 0) {
            //机器人
            return fstRobotService.getRobotInitParam(guId);
        }
        CPCardGroup cp = oppCardService.getDefenceCards(guId);
        GameUser gameUser = userService.getGameUser(guId);
        return CPlayerInitParam.initParam(gameUser, cp.getCards(), cp.getBuffs(), new ArrayList<>());
    }


    /**
     * 加入到榜单
     *
     * @param sid
     * @param ranks
     * @param ids
     * @return
     */
    public boolean addToRanking(int sid, double[] ranks, Long[] ids) {
        String fstKey = getFstKey(sid);
        return rankingList.add(fstKey, ranks, ids);
    }

    /**
     * 获得最低排行
     *
     * @param sid
     * @return
     */
    public int getLowestRank(int sid) {
        return Long.valueOf(rankingList.size(getFstKey(sid))).intValue();
    }

    /**
     * 在[begin, end]排名区间内随机非自己的num个不重复排的玩家ID
     *
     * @param guId
     * @param myRank
     * @param begin
     * @param end
     * @param num
     * @return
     */
    public long[] getRandomRankerIds(long guId, int myRank, int begin, int end, int num) {
        end = end + 1;
        String key = getFstKey(userService.getActiveSid(guId));
        List<Integer> randomRanks = new Random().ints(begin, end).filter(rank -> rank != myRank).distinct().limit(num).boxed().collect(Collectors.toList());
        long[] randomRankerIds = randomRanks.stream().mapToLong(rank -> rankingList.rankObject(key, rank)).toArray();
        return randomRankerIds;
    }

    /**
     * 初始化区服封神台
     *
     * @param sid
     */
    @Override
    public boolean initFst(int sid) {
        FstGameRobot info = fstRobotService.getRobotInfo(getServerRobotId(1, sid));
        if (info == null) {
            fstRobotService.initRobot();
        }
        Integer robotsNum = FstTool.getCfg().getServerRobotsNum();
        double[] ranks = new double[robotsNum];
        Long[] ids = new Long[robotsNum];
        for (Integer i = 0; i < robotsNum; i++) {
            ranks[i] = i + 1;
            ids[i] = getServerRobotId(i + 1, sid);
        }
        return addToRanking(sid, ranks, ids);
    }

    @Override
    public void removeFightState(long p1, long p2) {
        String fightStateKey = getFstFightStateKey(gameUserService.getActiveSid(p1));
        hasFightStateHUtil.removeField(fightStateKey, p1);
        hasFightStateHUtil.removeField(fightStateKey, p2);
    }

    @Override
    public boolean hasJoinFst(long uid) {
        return getFstRank(uid) > 0;
    }

    /**
     * 对应rank排名的是否可挑战
     *
     * @param rank
     * @param myRank
     * @return
     */
    private boolean getFightAble(int rank, int myRank) {
        // 不可与自己战斗
        if (rank == myRank) {
            return false;
        }
        CfgFst config = Cfg.I.getUniqueConfig(CfgFst.class);
        int fireRange = config.getFireRange();
        if (myRank <= fireRange + 1 && rank <= fireRange + 1) {
            return true;
        }

        // 竞技场基数
        int stepFactor = config.getStepFactor();
        int step = myRank / stepFactor > 0 ? myRank / stepFactor : 1;
        int addFireRange = fireRange * step;
        int rankDif = myRank - rank;
        if (rankDif <= addFireRange && rankDif > 0) {
            return true;
        }
        return false;
    }

    /**
     * 获取步进值，玩家排名越低，步进值越大，冲的越快
     *
     * @param myRank
     * @return
     */
    private int getStep(int myRank) {
        CfgFst config = Cfg.I.getUniqueConfig(CfgFst.class);
        int stepFactor = config.getStepFactor();
        int step = myRank / stepFactor > 0 ? myRank / stepFactor : 1;
        return step;
    }

}
