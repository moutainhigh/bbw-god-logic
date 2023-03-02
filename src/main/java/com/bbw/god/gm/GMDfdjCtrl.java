package com.bbw.god.gm;

import com.alibaba.fastjson.JSONObject;
import com.bbw.common.DateUtil;
import com.bbw.common.ListUtil;
import com.bbw.common.Rst;
import com.bbw.common.SpringContextUtil;
import com.bbw.god.controller.AbstractController;
import com.bbw.god.db.entity.CfgServerEntity;
import com.bbw.god.db.entity.InsUserDataEntity;
import com.bbw.god.db.pool.PlayerDataDAO;
import com.bbw.god.game.config.server.ServerTool;
import com.bbw.god.game.data.GameDataService;
import com.bbw.god.game.dfdj.DfdjService;
import com.bbw.god.game.dfdj.config.DfdjRankType;
import com.bbw.god.game.dfdj.config.ZoneType;
import com.bbw.god.game.dfdj.rank.DfdjRankAwardService;
import com.bbw.god.game.dfdj.rank.DfdjRankService;
import com.bbw.god.game.dfdj.zone.DfdjZone;
import com.bbw.god.game.dfdj.zone.DfdjZoneService;
import com.bbw.god.gameuser.UserDataType;
import com.bbw.god.gameuser.redis.UserRedisKey;
import com.bbw.god.server.ServerUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 巅峰对决相关接口
 *
 * @author suhq
 * @date 2019年4月12日 上午11:55:43
 */
@Slf4j
@RestController
@RequestMapping("/gm")
public class GMDfdjCtrl extends AbstractController {
    @Autowired
    private DfdjService dfdjService;
    @Autowired
    private DfdjZoneService dfdjZoneService;
    @Autowired
    private DfdjRankService dfdjRankService;
    @Autowired
    private GameDataService gameDataService;
    @Autowired
    private DfdjRankAwardService dfdjRankAwardService;
    @Autowired
    private ServerUserService serverUserService;

    /**
     * 修复战区
     *
     * @param serverGroup
     * @return
     */
    @GetMapping("server!repairDfdjZones")
    public Rst repairZones(int serverGroup, int seasonIndex) {
        List<DfdjZone> zones = gameDataService.getGameDatas(DfdjZone.class);
        Date now = DateUtil.now();
        zones = zones.stream().filter(tmp -> tmp.getServerGroup() == serverGroup && tmp.getBeginDate().before(now) && tmp.getEndDate().after(now)).collect(Collectors.toList());
        if (ListUtil.isEmpty(zones)) {
            DfdjZoneService.ZoneDate zoneDate = dfdjZoneService.getZoneDate(seasonIndex);
            List<CfgServerEntity> groupServers = ServerTool.getGroupServers(serverGroup);
            List<DfdjZone> groupZones = dfdjZoneService.buildZones(serverGroup, groupServers, zoneDate);
            gameDataService.addGameDatas(groupZones);
            return Rst.businessOK("战区修复成功");
        }
        return Rst.businessFAIL("战区已存在，无需修复");
    }

    /**
     * @param seasonDate **-**-** 20:30:00
     * @return
     */
    @GetMapping("server!makeUpDfdjRankAward")
    public Rst makeupRankAward(String seasonDate) {
        Date dateToSend = DateUtil.fromDateTimeString(seasonDate);
        dfdjRankAwardService.sendAward(dateToSend);
        return Rst.businessOK();
    }

    /**
     * @param serverGroup
     * @param zone
     * @param rankType    RANK("赛季", 30),MIDDLE_RANK("季中", 31),LAST_PHASE_RANK("赛季前一个阶段排行", 33),
     * @param seasonDate  **-**-** 20:30:00
     * @return
     * @see ZoneType
     */
    @GetMapping("server!makeUpDfdjZoneRankAward")
    public Rst makeupRankAward(int serverGroup, int zone, int rankType, String seasonDate) {
        if (rankType != 30 && rankType != 31 && rankType != 33) {
            return Rst.businessFAIL("无效的rankType");
        }
        Date dateToSend = DateUtil.fromDateTimeString(seasonDate);
        dfdjRankAwardService.sendAward(serverGroup, zone, rankType, dateToSend);
        return Rst.businessOK();
    }

    /**
     * 将参与者加入到Redis记录集
     *
     * @param serverGroup
     * @return
     */
    @GetMapping("server!fixDfdjJoiners")
    public Rst fixDfdjJoiners(int serverGroup) {
        List<CfgServerEntity> servers = ServerTool.getGroupServers(serverGroup);
        int fixNum = 0;
        for (CfgServerEntity server : servers) {
            try {
                fixNum += fixDfdjJoiners(server);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }

        }
        return Rst.businessOK("修正成功，加入数量：" + fixNum);
    }

    /**
     * 修复巅峰对决总榜
     *
     * @param serverGroup
     * @return
     */
    @GetMapping("server!fixDfdjRanker")
    public Rst fixDfdjRanker(int serverGroup) {
        List<CfgServerEntity> groupServers = ServerTool.getGroupServers(serverGroup);
        if (ListUtil.isEmpty(groupServers)) {
            return Rst.businessOK("该区服组没有任何服务器");
        }
        fixDfdjRank(serverGroup, groupServers, ZoneType.ZONE_ONE);
        fixDfdjRank(serverGroup, groupServers, ZoneType.ZONE_TWO);
        fixDfdjRank(serverGroup, groupServers, ZoneType.ZONE_THREE);
        return Rst.businessOK("修正成功");
    }

    /**
     * 修复日排行，限本月
     *
     * @param serverGroup
     * @param zone        10|20|30
     * @param date        yyyyMMddHH
     * @param rankers     uid,score;uid,score;...
     * @return
     */
    @GetMapping("server!fixDfdjDayRanker")
    public Rst fixDfdjDayRanker(int serverGroup, int zone, String date, String rankers) {
        ZoneType zoneType = ZoneType.fromValue(zone);
        if (zoneType == null) {
            return Rst.businessFAIL("无效的战区");
        }
        DfdjZone dfdjZone = dfdjZoneService.getZoneByZoneType(zoneType, serverGroup);
        if (dfdjZone == null) {
            return Rst.businessFAIL("该区服组没有该战区");
        }
        dfdjRankService.removeDayRank(dfdjZone, date);
        String[] rankerList = rankers.split(";");
        for (String ranker : rankerList) {
            String[] rankerInfo = ranker.split(",");
            long uid = Long.valueOf(rankerInfo[0]);
            int score = Integer.valueOf(rankerInfo[1]);
            dfdjRankService.incrementDayRankValue(dfdjZone, date, uid, score);
        }
        return Rst.businessOK("修正成功");
    }

    /**
     * 将参与者加入到Redis记录集
     *
     * @param server
     * @return 加入数量
     */
    private int fixDfdjJoiners(CfgServerEntity server) {
        int fixNum = 0;
        int sid = server.getMergeSid();
        int serverGroup = server.getGroupId();
        PlayerDataDAO playerDataDAO = SpringContextUtil.getBean(PlayerDataDAO.class, sid);
        List<InsUserDataEntity> dfdjFighters = playerDataDAO.dbSelectUserDataByType(UserDataType.DFDJ_FIGHTER.getRedisKey());
        if (ListUtil.isNotEmpty(dfdjFighters)) {

            for (InsUserDataEntity dfdjFighter : dfdjFighters) {
                String fighterKey = UserRedisKey.getUserDataKey(dfdjFighter);
                if (!dfdjService.isJoinedDfdj(fighterKey, serverGroup)) {
                    dfdjService.joinDfdj(fighterKey, serverGroup);
                    fixNum++;
                }
            }
        }
        return fixNum;
    }

    /**
     * 修复巅峰对决总榜
     *
     * @param serverGroup
     * @param groupServers
     * @param zoneType
     */
    private void fixDfdjRank(int serverGroup, List<CfgServerEntity> groupServers, ZoneType zoneType) {
        DfdjZone zone = dfdjZoneService.getZoneByZoneType(zoneType, serverGroup);
        if (zone == null) {
            return;
        }
        dfdjRankService.removeRank(zone, DfdjRankType.RANK);
        for (CfgServerEntity server : groupServers) {
            int sid = server.getMergeSid();
            if (zone.getSids().contains(sid)) {
                PlayerDataDAO playerDataDAO = SpringContextUtil.getBean(PlayerDataDAO.class, sid);
                List<InsUserDataEntity> dfdjFighters = playerDataDAO.dbSelectUserDataByType(UserDataType.DFDJ_FIGHTER.getRedisKey());
                if (ListUtil.isNotEmpty(dfdjFighters)) {

                    for (InsUserDataEntity dfdjFighter : dfdjFighters) {
                        JSONObject data = JSONObject.parseObject(dfdjFighter.getDataJson());
                        int score = data.getIntValue("score");
                        if (score != 0) {
                            dfdjRankService.incrementRankValue(zone, DfdjRankType.RANK, dfdjFighter.getUid(), score);
                        }

                    }
                }
            }

        }
    }

    @GetMapping("game!updateDfdjZoneEndTime")
    public Rst changeTime(String end) {
        List<DfdjZone> zones = dfdjZoneService.getZones();
        Date endDate = DateUtil.fromDateTimeString(end);
        for (DfdjZone zone : zones) {
            if (endDate.after(zone.getEndDate())) {
                zone.setEndDate(endDate);
            }
        }
        gameDataService.updateGameDatas(zones);
        return Rst.businessOK();
    }

    @GetMapping("server!sendScore")
    public Rst sendScore(String serverNames) {
        List<CfgServerEntity> servers = ServerTool.getServers(serverNames);
        for (CfgServerEntity server : servers) {
            Set<Long> uids = serverUserService.getUidsInDays(server.getMergeSid(), 3);
            for (Long uid : uids) {
                DfdjZone zone = dfdjZoneService.getCurOrLastZone(uid);
                int score = dfdjRankService.getScore(zone, DfdjRankType.RANK, uid);
                if (score <= 0) {
                    continue;
                }
                // 积分翻3倍
                int addedScore = score * 2;
                dfdjRankService.incrementRankValue(zone, DfdjRankType.RANK, uid, addedScore);
                dfdjRankService.incrementRankValue(zone, DfdjRankType.PHASE_RANK, uid, addedScore);
            }
        }
        return Rst.businessOK();
    }

    @GetMapping("server!repairScore")
    public Rst repairScore(String serverNames, int days) {
        List<CfgServerEntity> servers = ServerTool.getServers(serverNames);
        for (CfgServerEntity server : servers) {
            Set<Long> uids = serverUserService.getUidsInDays(server.getMergeSid(), days);
            for (Long uid : uids) {
                DfdjZone zone = dfdjZoneService.getCurOrLastZone(uid);
                int totalScore = dfdjRankService.getScore(zone, DfdjRankType.RANK, uid);
                int score = dfdjRankService.getScore(zone, DfdjRankType.PHASE_RANK, uid);
                if (score >= 0 && totalScore >= 0) {
                    continue;
                }
                if (score < 0) {
                    dfdjRankService.incrementRankValue(zone, DfdjRankType.PHASE_RANK, uid, score * -1);
                }
                if (totalScore < 0) {
                    dfdjRankService.incrementRankValue(zone, DfdjRankType.RANK, uid, totalScore * -1);
                }
            }
        }
        return Rst.businessOK();
    }
}
