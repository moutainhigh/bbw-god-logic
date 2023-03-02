package com.bbw.god.server;

import com.bbw.common.DateUtil;
import com.bbw.common.ListUtil;
import com.bbw.god.activityrank.RankAwardRecord;
import com.bbw.god.db.entity.CfgServerEntity;
import com.bbw.god.detail.LogUtil;
import com.bbw.god.game.config.server.ServerTool;
import com.bbw.god.server.god.ServerGod;
import com.bbw.god.server.maou.alonemaou.ServerAloneMaou;
import com.bbw.god.server.maou.alonemaou.ServerAloneMaouService;
import com.bbw.god.server.maou.bossmaou.ServerBossMaou;
import com.bbw.god.server.maou.bossmaou.ServerBossMaouService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 删除旧数据服务
 *
 * @author suhq
 * @date 2020-10-23 15:08
 **/
@Component
public class DelHistoryServerDataService {
    private static final int dayBefore15 = -15;
    private static final int dayBefore10 = -10;
    private static final int dayBefore5 = -5;

    @Autowired
    private ServerDataService serverDataService;
    @Autowired
    private ServerBossMaouService serverBossMaouService;
    @Autowired
    private ServerAloneMaouService serverAloneMaouService;

    public void delExpiredServerData(CfgServerEntity server) {
        Date beforeDate15 = DateUtil.addDays(DateUtil.now(), dayBefore15);
        Date beforeDate10 = DateUtil.addDays(DateUtil.now(), dayBefore10);
        Date beforeDate5 = DateUtil.addDays(DateUtil.now(), dayBefore5);
        //排行奖励
        List<RankAwardRecord> rankAwardRecords = serverDataService.getServerDatas(server.getId(), RankAwardRecord.class);
        List<ServerData> toDels = rankAwardRecords.stream().filter(tmp -> tmp.getSendTime().before(beforeDate5)).collect(Collectors.toList());
        deleteServerDatas(server.getId(), toDels);
        //区服神仙
        delServerGodEntity(server, beforeDate5);
        //区服魔王
        delServerMaou(server, beforeDate10);
    }

    /**
     * 删除过期serverGod实例
     *
     * @param server
     * @param beforeDate
     */
    private void delServerGodEntity(CfgServerEntity server, Date beforeDate) {
        int maxDate = 10;
        while (maxDate > 0) {
            Date date = DateUtil.addDays(beforeDate, -maxDate);
            String loopKey = DateUtil.toDateInt(date) + "";
            List<ServerGod> toDels = serverDataService.getServerDatas(server.getId(), ServerGod.class, loopKey);
            deleteServerDatas(server.getId(), toDels, loopKey);
            maxDate--;
        }
    }

    /**
     * 删除魔王数据，不包括ServerMaou实例
     *
     * @param server
     * @param beforeDate
     */
    private void delServerMaou(CfgServerEntity server, Date beforeDate) {
        int maxDate = 10;
        List<ServerAloneMaou> allAloneMaou = new ArrayList<>();
        List<ServerBossMaou> allBossMaou = new ArrayList<>();
        while (maxDate > 0) {
            Date date = DateUtil.addDays(beforeDate, -maxDate);
            String loopKey = DateUtil.toDateInt(date) + "";
            List<ServerAloneMaou> aloneMaous = serverDataService.getServerDatas(server.getId(), ServerAloneMaou.class, loopKey);
            allAloneMaou.addAll(aloneMaous);
            List<ServerBossMaou> bossMaous = serverDataService.getServerDatas(server.getId(), ServerBossMaou.class, loopKey);
            allBossMaou.addAll(bossMaous);
            maxDate--;
        }
        allAloneMaou = allAloneMaou.stream().filter(tmp -> tmp.getBeginTime().before(beforeDate)).collect(Collectors.toList());
        allBossMaou = allBossMaou.stream().filter(tmp -> tmp.getBeginTime().before(beforeDate)).collect(Collectors.toList());

        for (ServerAloneMaou aloneMaou : allAloneMaou) {
            serverAloneMaouService.timeOutTmpData(aloneMaou);
        }
        for (ServerBossMaou bossMaou : allBossMaou) {
            serverBossMaouService.timeOutTmpData(bossMaou);
        }
    }

    /**
     * 删除过期魔王实例
     */
    public void delServerMaouEntity() {
        Date today = DateUtil.now();
        for (int i = 300; i > 20; i--) {
            Date removeDate = DateUtil.addDays(today, -i);
            //魔王
            String bossLoopKey = ServerBossMaou.getLoopKey(removeDate);
            String aloneLoopKey = ServerAloneMaou.getLoopKey(removeDate);
            List<CfgServerEntity> servers = ServerTool.getServers();
            for (CfgServerEntity server : servers) {
                List<ServerBossMaou> bossMaous = serverDataService.getServerDatas(server.getId(), ServerBossMaou.class, bossLoopKey);
                if (ListUtil.isNotEmpty(bossMaous)) {
                    deleteServerDatas(server.getId(), bossMaous, bossLoopKey);
                }
                List<ServerAloneMaou> aloneMaous = serverDataService.getServerDatas(server.getId(), ServerAloneMaou.class, aloneLoopKey);
                if (ListUtil.isNotEmpty(aloneMaous)) {
                    deleteServerDatas(server.getId(), aloneMaous, aloneLoopKey);
                }
            }
        }
    }


    public <T extends ServerData> void deleteServerDatas(int sid, List<T> dels, String... loopKey) {
        if (ListUtil.isNotEmpty(dels)) {
            // 备忘
            LogUtil.logDeletedServerDatas(dels, "过期数据");
            // 删除
            List<Long> delIds = dels.stream().map(ServerData::getId).collect(Collectors.toList());
            this.serverDataService.deleteServerDatas(sid, delIds, dels.get(0).getClass(), loopKey);
        }
    }


}
