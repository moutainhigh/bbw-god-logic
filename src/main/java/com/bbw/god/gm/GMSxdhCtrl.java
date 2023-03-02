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
import com.bbw.god.game.sxdh.*;
import com.bbw.god.game.sxdh.SxdhZoneService.ZoneDate;
import com.bbw.god.game.sxdh.config.SxdhRankType;
import com.bbw.god.game.sxdh.config.ZoneType;
import com.bbw.god.game.sxdh.store.SxdhStoreProgress;
import com.bbw.god.game.sxdh.store.SxdhZoneMallRecord;
import com.bbw.god.gameuser.UserDataType;
import com.bbw.god.gameuser.redis.UserRedisKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 神仙大会相关接口
 *
 * @author suhq
 * @date 2019年4月12日 上午11:55:43
 */
@Slf4j
@RestController
@RequestMapping("/gm")
public class GMSxdhCtrl extends AbstractController {
    @Autowired
    private SxdhService sxdhService;
    @Autowired
    private SxdhZoneService sxdhZoneService;
    @Autowired
    private SxdhRankService sxdhRankService;
    @Autowired
    private GameDataService gameDataService;
    @Autowired
    private SxdhRankAwardService sxdhRankAwardService;
    @Autowired
    private SxdhStoreProgress sxdhStoreProgress;

    /**
     * 修复战区
     *
     * @param serverGroup
     * @return
     */
    @GetMapping("server!repairZones")
    public Rst repairZones(int serverGroup, int seasonIndex) {
        List<SxdhZone> zones = gameDataService.getGameDatas(SxdhZone.class);
        Date now = DateUtil.now();
        zones = zones.stream().filter(tmp -> tmp.getServerGroup() == serverGroup && tmp.getBeginDate().before(now) && tmp.getEndDate().after(now)).collect(Collectors.toList());
        if (ListUtil.isEmpty(zones)) {
            ZoneDate zoneDate = sxdhZoneService.getZoneDate(seasonIndex);
            List<CfgServerEntity> groupServers = ServerTool.getGroupServers(serverGroup);
            List<SxdhZone> groupZones = sxdhZoneService.buildZones(serverGroup, groupServers, zoneDate);
            gameDataService.addGameDatas(groupZones);
            return Rst.businessOK("战区修复成功");
        }
        return Rst.businessFAIL("战区已存在，无需修复");
    }

    /**
     * @param seasonDate **-**-** 20:30:00
     * @return
     */
    @GetMapping("server!makeupRankAward")
    public Rst makeupRankAward(String seasonDate) {
        Date dateToSend = DateUtil.fromDateTimeString(seasonDate);
        sxdhRankAwardService.sendAward(dateToSend);
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
    @GetMapping("server!makeupZoneRankAward")
    public Rst makeupRankAward(int serverGroup, int zone, int rankType, String seasonDate) {
        if (rankType != 30 && rankType != 31 && rankType != 33) {
            return Rst.businessFAIL("无效的rankType");
        }
        Date dateToSend = DateUtil.fromDateTimeString(seasonDate);
        sxdhRankAwardService.sendAward(serverGroup, zone, rankType, dateToSend);
        return Rst.businessOK();
    }

    /**
     * 将参与者加入到Redis记录集
     *
     * @param serverGroup
     * @return
     */
    @GetMapping("server!fixSxdhJoiners")
    public Rst fixSxdhJoiners(int serverGroup) {
        List<CfgServerEntity> servers = ServerTool.getGroupServers(serverGroup);
        int fixNum = 0;
        for (CfgServerEntity server : servers) {
            try {
                fixNum += fixSxdhJoiners(server);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }

        }
        return Rst.businessOK("修正成功，加入数量：" + fixNum);
    }

    /**
     * 修复神仙大会总榜
     *
     * @param serverGroup
     * @return
     */
    @GetMapping("server!fixSxdhRanker")
    public Rst fixSxdhRanker(int serverGroup) {
        List<CfgServerEntity> groupServers = ServerTool.getGroupServers(serverGroup);
        if (ListUtil.isEmpty(groupServers)) {
            return Rst.businessOK("该区服组没有任何服务器");
        }
        fixSxdhRank(serverGroup, groupServers, ZoneType.ZONE_ONE);
        fixSxdhRank(serverGroup, groupServers, ZoneType.ZONE_TWO);
        fixSxdhRank(serverGroup, groupServers, ZoneType.ZONE_THREE);
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
    @GetMapping("server!fixDayRanker")
    public Rst fixDayRanker(int serverGroup, int zone, String date, String rankers) {
        ZoneType zoneType = ZoneType.fromValue(zone);
        if (zoneType == null) {
            return Rst.businessFAIL("无效的战区");
        }
        SxdhZone sxdhZone = sxdhZoneService.getZoneByZoneType(zoneType, serverGroup);
        if (sxdhZone == null) {
            return Rst.businessFAIL("该区服组没有该战区");
        }
        sxdhRankService.removeDayRank(sxdhZone, date);
        String[] rankerList = rankers.split(";");
        for (String ranker : rankerList) {
            String[] rankerInfo = ranker.split(",");
            long uid = Long.valueOf(rankerInfo[0]);
            int score = Integer.valueOf(rankerInfo[1]);
            sxdhRankService.incrementDayRankValue(sxdhZone, date, uid, score);
        }
        return Rst.businessOK("修正成功");
    }

    /**
     * 将参与者加入到Redis记录集
     *
     * @param server
     * @return 加入数量
     */
    private int fixSxdhJoiners(CfgServerEntity server) {
        int fixNum = 0;
        int sid = server.getMergeSid();
        int serverGroup = server.getGroupId();
        PlayerDataDAO playerDataDAO = SpringContextUtil.getBean(PlayerDataDAO.class, sid);
        List<InsUserDataEntity> sxdhFighters = playerDataDAO.dbSelectUserDataByType(UserDataType.SXDH_FIGHTER.getRedisKey());
        if (ListUtil.isNotEmpty(sxdhFighters)) {

            for (InsUserDataEntity sxdhFighter : sxdhFighters) {
                String fighterKey = UserRedisKey.getUserDataKey(sxdhFighter);
                if (!sxdhService.isJoinedSxdh(fighterKey, serverGroup)) {
                    sxdhService.joinSxdh(fighterKey, serverGroup);
                    fixNum++;
                }
            }
        }
        return fixNum;
    }

    /**
     * 修复神仙大会总榜
     *
     * @param serverGroup
     * @param groupServers
     * @param zoneType
     */
    private void fixSxdhRank(int serverGroup, List<CfgServerEntity> groupServers, ZoneType zoneType) {
        SxdhZone sxdhZone = sxdhZoneService.getZoneByZoneType(zoneType, serverGroup);
        if (sxdhZone == null) {
            return;
        }
        sxdhRankService.removeRank(sxdhZone, SxdhRankType.RANK);
        for (CfgServerEntity server : groupServers) {
            int sid = server.getMergeSid();
            if (sxdhZone.getSids().contains(sid)) {
                PlayerDataDAO playerDataDAO = SpringContextUtil.getBean(PlayerDataDAO.class, sid);
                List<InsUserDataEntity> sxdhFighters = playerDataDAO.dbSelectUserDataByType(UserDataType.SXDH_FIGHTER.getRedisKey());
                if (ListUtil.isNotEmpty(sxdhFighters)) {

                    for (InsUserDataEntity sxdhFighter : sxdhFighters) {
                        JSONObject data = JSONObject.parseObject(sxdhFighter.getDataJson());
                        int score = data.getIntValue("score");
                        if (score != 0) {
                            sxdhRankService.incrementRankValue(sxdhZone, SxdhRankType.RANK, sxdhFighter.getUid(), score);
                        }

                    }
                }
            }

        }
    }

    @GetMapping("game!updateZoneEndTime")
    public Rst changeTime(String end) {
        List<SxdhZone> zones = sxdhZoneService.getZones();
        Date endDate = DateUtil.fromDateTimeString(end);
        for (SxdhZone zone : zones) {
            if (endDate.after(zone.getEndDate())) {
                zone.setEndDate(endDate);
            }
        }
        gameDataService.updateGameDatas(zones);
        return Rst.businessOK();
    }

    /**
     * 添加神仙大会战区商品记录
     *
     * @param serverGroup
     * @param mallId
     * @return
     */
    @GetMapping("game!addSxdhZoneMallRecord")
    public Rst changeTime(int serverGroup, int mallId) {
        List<SxdhZoneMallRecord> records = gameDataService.getGameDatas(SxdhZoneMallRecord.class);
        List<SxdhZoneMallRecord> newRecords = new ArrayList<>();
        for (ZoneType zoneType : ZoneType.values()) {
            if (mallId == SxdhStoreProgress.MiZ_MALL_ID) {
                newRecords.add(sxdhStoreProgress.buildMiZMallRecord(serverGroup, zoneType.getValue(), records));
            } else {
                SxdhZoneMallRecord record = SxdhZoneMallRecord.instance(serverGroup, zoneType.getValue(), mallId);
                newRecords.add(record);
            }
        }
        gameDataService.addGameDatas(newRecords);
        return Rst.businessOK();
    }
}
