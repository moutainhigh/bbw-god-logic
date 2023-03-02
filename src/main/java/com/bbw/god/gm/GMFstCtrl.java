package com.bbw.god.gm;

import com.alibaba.fastjson.JSONObject;
import com.bbw.common.DateUtil;
import com.bbw.common.ListUtil;
import com.bbw.common.Rst;
import com.bbw.db.redis.RedisHashUtil;
import com.bbw.db.redis.RedisValueUtil;
import com.bbw.db.redis.RedisZSetUtil;
import com.bbw.god.controller.AbstractController;
import com.bbw.god.db.entity.CfgServerEntity;
import com.bbw.god.game.config.server.ServerTool;
import com.bbw.god.game.data.GameDataService;
import com.bbw.god.server.ServerDataService;
import com.bbw.god.server.ServerDataType;
import com.bbw.god.server.ServerService;
import com.bbw.god.server.ServerUserService;
import com.bbw.god.server.fst.*;
import com.bbw.god.server.fst.game.FstGameRanking;
import com.bbw.god.server.fst.game.FstGameService;
import com.bbw.god.server.fst.game.FstRankingType;
import com.bbw.god.server.fst.robot.FstRobotService;
import com.bbw.god.server.fst.server.FstServerService;
import com.bbw.god.server.redis.ServerRedisKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 封神台相关接口
 *
 * @author suhq
 * @date 2019年4月12日 上午11:55:43
 */
@Slf4j
@RestController
@RequestMapping("/gm/fst")
public class GMFstCtrl extends AbstractController {
    @Autowired
    private ServerService serverService;
    @Autowired
    private FstServerService fstServerService;
    @Autowired
    private FstGameService fstGameService;
    @Autowired
    private FstLogic fstLogic;
    @Autowired
    private ServerUserService serverUserService;
    @Autowired
    private ServerDataService serverDataService;
    @Autowired
    private RedisHashUtil<Long, Long> hashUtil;

    @Autowired
    protected RedisZSetUtil<Long> rankingList;
    @Autowired
    private RedisValueUtil<FstRanking> serverDataRedis;

    /**
     * 修复封神台榜单
     *
     * @param serverNames
     * @return
     */
    @GetMapping("server!fixFst")
    public Object fixFst(String serverNames) {
        List<CfgServerEntity> servers = ServerTool.getServers(serverNames);
        JSONObject jsonObject = new JSONObject();
        for (CfgServerEntity server : servers) {
            try {
                String fixResultInfo = fixFst(server);
                jsonObject.put(server.getName(), fixResultInfo);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }

        }
        return jsonObject;
    }

    /**
     * 重置封神台，此操作会清空榜单
     *
     * @param sid
     * @return
     */
    @GetMapping("server!resetFst")
    public Rst resetFst(int sid) {
        fstServerService.initFst(sid);
        return Rst.businessOK();
    }

    @GetMapping("game!resetFst")
    public Rst resetGameFst(int gid) {
        fstLogic.initGameFst(gid);
        return Rst.businessOK();
    }

    @GetMapping("game!joinToFst")
    public Rst joinToFst(int sid, String nickname) {
        Optional<Long> uidOptional = this.serverUserService.getUidByNickName(sid, nickname);
        fstGameService.joinToGameFst(uidOptional.get());
        return Rst.businessOK();
    }

    /**
     * 修复跨服封神台最近一次战斗时间
     *
     * @return
     */
    @GetMapping("game!fixFstLastFightTime")
    public Rst fixFstLastFightTime() {
        List<FstGameRanking> gameRankers = gameDataService.getGameDatas(FstGameRanking.class);
        List<FstGameRanking> updateRankers = new ArrayList<>();
        for (FstGameRanking gameRanker : gameRankers) {
            if (null == gameRanker || gameRanker.getId() < 0) {
                continue;
            }
            gameRanker.setLastChallengeDate(DateUtil.now());
            updateRankers.add(gameRanker);
        }
        gameDataService.updateGameDatas(updateRankers);
        return Rst.businessOK();
    }

    private String fixFst(CfgServerEntity server) {
        String repairLogInfo = "";
        int sid = server.getMergeSid();
        Set<ZSetOperations.TypedTuple<Long>> rankers = fstServerService.getAllRankers(sid);
        int rank = 0;
        for (ZSetOperations.TypedTuple<Long> ranker : rankers) {
            rank++;
            long uid = Optional.ofNullable(ranker.getValue()).orElse(0L);
            fstServerService.updateRank(sid, uid, rank);
        }
        return repairLogInfo;
    }

    /**
     * 补偿封神台积分
     *
     * @param
     * @return
     */
    @GetMapping("server!makeUpFstPoints")
    public Rst makeUpFstPoints(String serverNames, String sinceDate) {
        List<CfgServerEntity> servers = ServerTool.getServers(serverNames);
        Date date = DateUtil.fromDateTimeString(sinceDate);
        Rst rst = Rst.businessOK();
        List<Integer> sids = new ArrayList<>();
        for (CfgServerEntity server : servers) {
            try {
                if (sids.contains(server.getMergeSid())) {
                    continue;
                }
                sids.add(server.getMergeSid());
                makeUpPoints(server, date);
                rst.put(server.getName() + "_" + server.getMergeSid(), "补偿成功");
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }

        }
        return rst;
    }

    public void makeUpPoints(CfgServerEntity server, Date sinceDate) {
        Set<ZSetOperations.TypedTuple<Long>> rankers = fstServerService.getAllRankers(server.getMergeSid());
        long times = DateUtil.getMinutesBetween(sinceDate, DateUtil.now()) / 20;
        List<Long> uids = new ArrayList<>();
        //获取排行信息
        Map<Long, Integer> uidsWithRank = new HashMap<>();
        int rank = 0;
        for (ZSetOperations.TypedTuple<Long> ranker : rankers) {
            rank++;
            Long uid = Optional.ofNullable(ranker.getValue()).orElse(-1L);
            if (uid > 0) {
                uids.add(uid);
            }
            uidsWithRank.put(uid, rank);
        }
        //获取封神台数据
        Set<String> keys = new HashSet<>();
        for (Long uid : uids) {
            keys.add(ServerRedisKey.getServerDataKey(server.getMergeSid(), ServerDataType.FSTPVPRanking, uid));
        }
        List<FstRanking> fstRankers = serverDataRedis.getBatch(keys);
        // 没有排名不做任何处理
        if (ListUtil.isEmpty(fstRankers)) {
            return;
        }
        List<FstRanking> needToUpdate = new ArrayList<>();
        for (FstRanking ranker : fstRankers) {
            int myRank = uidsWithRank.get(ranker.getId());
            // 获得排名积分奖励
            Long addedPoint = (FstTool.getPoinByRank(myRank) - 179) * times;
            if (addedPoint <= 0) {
                continue;
            }
            log.info(ranker.getId() + "before makeUpPoints,value: " + ranker.getIncrementPoints());
            ranker.addIncrementPoints(addedPoint.intValue());
            log.info(ranker.getId() + "after makeUpPoints,value: " + ranker.getIncrementPoints());

            needToUpdate.add(ranker);
        }
        // 更新需要更新积分的玩家
        if (ListUtil.isNotEmpty(needToUpdate)) {
            serverDataService.updateServerData(needToUpdate);
        }
    }

    /**
     * 补充结算
     *
     * @param groupId
     * @return
     */
    @GetMapping("game!doPromotion")
    public Rst doPromotion(int groupId) {
        String result = fstGameService.doPromotion(groupId);
        return Rst.businessOK(result);
    }

    /**
     * 清理旧服机器人
     *
     * @return
     */
    @GetMapping("clear!oldRobot")
    public Rst clearOldRobot() {
        for (CfgServerEntity server : ServerTool.getServers()) {
            String key = ServerRedisKey.getDataTypeKey(server.getId(), ServerDataType.FST_ROBOT, "ids");
            List<Long> list = hashUtil.getFieldValueList(key);
            if (ListUtil.isEmpty(list)) {
                continue;
            }
            serverDataService.deleteServerDatas(server.getId(), list, FstRobot.class);
        }
        return Rst.businessOK();
    }

    @GetMapping("game!addToRen")
    public Rst addToRen(int num) {
        String key = "game:fstRanking:1:fstzset:100";
        Set<Long> range = rankingList.range(key);
        List<Long> list = range.stream().collect(Collectors.toList());
        int begin = 120;
        for (int i = 0; i < 120; i++) {
            if (list.get(i) < 0) {
                for (int j = begin; j < list.size(); j++) {
                    if (list.get(j) > 0) {
                        fstGameService.swapRanking(list.get(j), new FstVideoLog(), list.get(i), new FstVideoLog());
                        num--;
                        begin = j + 1;
                        break;
                    }
                }
            }
            if (num <= 0) {
                return Rst.businessOK();
            }
        }
        return Rst.businessOK();
    }

    @Autowired
    private GameDataService gameDataService;

    @GetMapping("game!repair")
    public Rst repair() {
        String key = "game:fstRanking:1:fstzset:100";
        for (int i = 1; i <= 120; i++) {
            rankingList.add(key, FstRobotService.getGameRobotId(i), i);
        }
        return Rst.businessOK();
    }

    @GetMapping("game!ren")
    public Rst getRanRanking() {
        String key = "game:fstRanking:1:fstzset:100";
        Set<Long> range = rankingList.range(key, 0, 119);
        int count = 0;
        int prom = 0;
        List<Long> collect = range.stream().collect(Collectors.toList());
        for (int i = 0; i < collect.size(); i++) {
            if (collect.get(i) > 0) {
                if (i < 50) {
                    prom++;
                }
                count++;
            }
        }
        return Rst.businessOK().put("总的", count).put("晋级区", prom);
    }

    @GetMapping("game!resetAll")
    public Rst resetAll() {
        String key = "game:fstRanking:1:fstzset:100";
        Set<Long> score = rankingList.rangeByScore(key, 0, 120);
        for (Long id : score) {
            rankingList.add(key, id, 121);
            if (id > 0) {
                Optional<FstGameRanking> op = fstGameService.getFstGameRankingOp(id);
                if (op.isPresent()) {
                    FstGameRanking ranking = op.get();
                    ranking.setRankingType(FstRankingType.REN.getType());
                    gameDataService.updateGameData(ranking);
                }
            }
        }
        String huangKey = "game:fstRanking:1:fstzset:110";
        Set<Long> range = rankingList.range(huangKey);
        for (Long id : range) {
            rankingList.add(key, id, 121);
            if (id > 0) {
                Optional<FstGameRanking> op = fstGameService.getFstGameRankingOp(id);
                if (op.isPresent()) {
                    FstGameRanking ranking = op.get();
                    ranking.setRankingType(FstRankingType.REN.getType());
                    gameDataService.updateGameData(ranking);
                }
            }
        }
        rankingList.delete(huangKey);
        String xuanKey = "game:fstRanking:1:fstzset:120";
        Set<Long> xuan = rankingList.range(xuanKey);
        for (Long id : xuan) {
            rankingList.add(key, id, 121);
            if (id > 0) {
                Optional<FstGameRanking> op = fstGameService.getFstGameRankingOp(id);
                if (op.isPresent()) {
                    FstGameRanking ranking = op.get();
                    ranking.setRankingType(FstRankingType.REN.getType());
                    gameDataService.updateGameData(ranking);
                }
            }
        }
        rankingList.delete(xuanKey);

        String diKey = "game:fstRanking:1:fstzset:130";
        Set<Long> di = rankingList.range(diKey);
        for (Long id : di) {
            rankingList.add(key, id, 121);
            if (id > 0) {
                Optional<FstGameRanking> op = fstGameService.getFstGameRankingOp(id);
                if (op.isPresent()) {
                    FstGameRanking ranking = op.get();
                    ranking.setRankingType(FstRankingType.REN.getType());
                    gameDataService.updateGameData(ranking);
                }
            }
        }
        rankingList.delete(diKey);
        String tianKey = "game:fstRanking:1:fstzset:140";
        Set<Long> tian = rankingList.range(tianKey);
        for (Long id : tian) {
            rankingList.add(key, id, 121);
            if (id > 0) {
                Optional<FstGameRanking> op = fstGameService.getFstGameRankingOp(id);
                if (op.isPresent()) {
                    FstGameRanking ranking = op.get();
                    ranking.setRankingType(FstRankingType.REN.getType());
                    gameDataService.updateGameData(ranking);
                }
            }
        }
        rankingList.delete(tianKey);
        return Rst.businessOK();
    }


    @GetMapping("game!checkRanking")
    public Rst checkRanking(int gid, int type) {
        FstRankingType val = FstRankingType.fromVal(type);
        String key = "game:fstRanking:" + gid + ":fstzset:" + val.getType();
        Set<Long> range = rankingList.range(key);
        for (Long uid : range) {
            if (uid > 0) {
                FstGameRanking ranking = fstGameService.getOrCreateFstGameRanking(uid);
                ranking.setRankingType(val.getType());
                gameDataService.updateGameData(ranking);
            }
        }
        return Rst.businessOK();
    }

}
