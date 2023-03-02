package com.bbw.god.game.sxdh;

import com.bbw.common.DateUtil;
import com.bbw.common.ListUtil;
import com.bbw.god.db.entity.CfgServerEntity;
import com.bbw.god.game.config.server.ServerTool;
import com.bbw.god.game.data.GameDataService;
import com.bbw.god.game.sxdh.config.CfgSxdh;
import com.bbw.god.game.sxdh.config.CfgSxdhZoneEntity;
import com.bbw.god.game.sxdh.config.SxdhTool;
import com.bbw.god.game.sxdh.config.ZoneType;
import com.bbw.god.gameuser.GameUserService;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 神仙大会战区服务
 *
 * @author suhq
 * @date 2019-06-25 15:37:14
 */
@Slf4j
@Service
public class SxdhZoneService {
    @Autowired
    private GameDataService gameDataService;
    @Autowired
    private SxdhDateService sxdhDateService;
    @Autowired
    private GameUserService gameUserService;

    /**
     * 加入战区,新服调用
     *
     * @param server
     */
    public void joinZone(CfgServerEntity server) {
        SxdhZone sxdhZone = getZoneByZoneType(ZoneType.ZONE_ONE, server.getGroupId());
        if (sxdhZone != null) {
            if (!sxdhZone.getSids().contains(server.getMergeSid())) {
                sxdhZone.getSids().add(server.getMergeSid());
                gameDataService.updateGameData(sxdhZone);
            }

        }
    }

    /**
     * 获得指定战区
     *
     * @param zoneType
     * @param serverGroup
     * @return
     */
    public SxdhZone getZoneByZoneType(ZoneType zoneType, int serverGroup) {
        List<SxdhZone> zones = getZones();
        SxdhZone sxdhZone = zones.stream().filter(tmp -> tmp.getServerGroup() == serverGroup && tmp.getZone() == zoneType.getValue()).findFirst().orElse(null);
        return sxdhZone;
    }

    /**
     * 获取所在战区
     *
     * @param server
     * @return
     */
    public SxdhZone getZoneByServer(CfgServerEntity server) {
        List<SxdhZone> zones = getZones();
        // 合服后保持本赛季的战区
        int sid = server.getId();
        Optional<SxdhZone> optional = zones.stream().filter(tmp -> tmp.getSids().contains(sid)).findFirst();
        if (!optional.isPresent()) {
            int mergeId = server.getMergeSid();
            optional = zones.stream().filter(tmp -> tmp.getSids().contains(mergeId)).findFirst();
        }
        return optional.orElse(null);
    }

    /**
     * 获取当前战区，如果没有，则返回上一期
     *
     * @param uid
     * @return
     */
    public SxdhZone getCurOrLastZone(long uid) {
        CfgServerEntity server = gameUserService.getOriServer(uid);
        SxdhZone sxdhZone = getZoneByServer(server);
        if (sxdhZone == null) {
            sxdhZone = getLastZone(server);
        }
        return sxdhZone;
    }

    /**
     * 上个赛季的战区
     *
     * @param server
     * @return
     */
    public SxdhZone getLastZone(CfgServerEntity server) {
        List<SxdhZone> sxdhZones = gameDataService.getGameDatas(SxdhZone.class);
        ZoneDate zoneDate = getLastZoneDate();
        sxdhZones = sxdhZones.stream().filter(tmp -> tmp.ifMatch(zoneDate.getMonthInt())).collect(Collectors.toList());
        if (ListUtil.isNotEmpty(sxdhZones)) {
            // 合服后区服获取上赛季赛季的战区已合服前为准
            int sid = server.getId();
            SxdhZone sxdhZone = sxdhZones.stream().filter(tmp -> tmp.getSids().contains(sid)).findFirst().orElse(null);
            if (sxdhZone == null) {
                int mergeId = server.getMergeSid();
                sxdhZone = sxdhZones.stream().filter(tmp -> tmp.getSids().contains(mergeId)).findFirst().orElse(null);
            }
            return sxdhZone;
        }
        return null;
    }

    public SxdhZone getLastZone(int serverGroup, int zoneType) {
        List<SxdhZone> sxdhZones = gameDataService.getGameDatas(SxdhZone.class);
        ZoneDate zoneDate = getLastZoneDate();
        sxdhZones = sxdhZones.stream().filter(tmp -> tmp.ifMatch(zoneDate.getMonthInt())).collect(Collectors.toList());
        if (ListUtil.isNotEmpty(sxdhZones)) {
            SxdhZone sxdhZone = sxdhZones.stream().filter(tmp -> tmp.getServerGroup() == serverGroup && tmp.getZone() == zoneType).findFirst().orElse(null);
            return sxdhZone;
        }
        return null;
    }

    /**
     * 获得上个赛季的时间信息
     *
     * @return
     */
    private ZoneDate getLastZoneDate() {
        ZoneDate zoneDate = getZoneDate(-1);
        Date now = DateUtil.now();
        Date monthEnd = DateUtil.getMonthEnd(now, 0);
        monthEnd = DateUtil.getDateBegin(monthEnd);
        monthEnd = DateUtil.addHours(monthEnd, 21);//TODO 优化
        if (now.after(monthEnd)) {
            zoneDate = getZoneDate(0);
        }
        return zoneDate;
    }

    /**
     * 获取当前战区
     *
     * @return
     */
    @NonNull
    public List<SxdhZone> getZones() {
        return getZones(DateUtil.now());
    }

    /**
     * 获取指定日期的神仙大会
     *
     * @param date
     * @return
     */
    public List<SxdhZone> getZones(Date date) {
        List<SxdhZone> zones = gameDataService.getGameDatas(SxdhZone.class);
        zones = zones.stream().filter(tmp -> tmp.getBeginDate().before(date) && tmp.getEndDate().after(date)).collect(Collectors.toList());
        return zones;
    }

    /**
     * 昨日是否是上赛季
     *
     * @return
     */
    public boolean isLastZoneAsYesterday(SxdhZone lastZone) {
        if (lastZone == null) {
            return false;
        }
        Date yesterday = sxdhDateService.getSxdhDateEnd(-1);
        Date lastZoneEnd = lastZone.getEndDate();
        if (DateUtil.getDaysBetween(yesterday, lastZoneEnd) == 1) {
            return true;
        }

        return false;
    }

    /**
     * 生成新的战区
     *
     * @return
     */
    public List<SxdhZone> newZones(int seasonIndex) {
        ZoneDate zoneDate = getZoneDate(seasonIndex);
        log.info("生成新的战区：" + zoneDate.toString());
        List<CfgServerEntity> allServer = ServerTool.getAvailableServers();
        Map<Integer, List<CfgServerEntity>> groupServers = allServer.stream().collect(Collectors.groupingBy(CfgServerEntity::getGroupId));
        Set<Integer> groupSet = groupServers.keySet();

        List<SxdhZone> sxdhZones = new ArrayList<>();
        for (Integer serverGroup : groupSet) {
            List<SxdhZone> groupZones = buildZones(serverGroup, groupServers.get(serverGroup), zoneDate);
            sxdhZones.addAll(groupZones);
        }
        List<SxdhZone> preZones = gameDataService.getGameDatas(SxdhZone.class);
        preZones = preZones.stream().filter(tmp -> null != tmp.getSeason()).collect(Collectors.toList());
        List<SxdhZone> toAdds = new ArrayList<>();
        for (SxdhZone sxdhZone : sxdhZones) {
            boolean isAdded = preZones.stream().anyMatch(tmp -> tmp.getZone().intValue() == sxdhZone.getZone() && tmp.getSeason().intValue() == sxdhZone.getSeason());
            if (isAdded) {
                continue;
            }
            toAdds.add(sxdhZone);
        }
        gameDataService.addGameDatas(toAdds);
        return sxdhZones;
    }

    /**
     * 生成新的区服组战区
     *
     * @return
     */
    public List<SxdhZone> buildZones(int serverGroup, List<CfgServerEntity> servers, ZoneDate zoneDate) {
        Date now = DateUtil.now();
        List<SxdhZone> sxdhZones = new ArrayList<>();
        int maxOpenDay = DateUtil.getDaysBetween(servers.get(0).getBeginTime(), now);
        List<CfgSxdhZoneEntity> zones = SxdhTool.getZones(maxOpenDay + 1);
        List<Integer> joinedSids = new ArrayList<>();
        for (int i = zones.size() - 1; i >= 0; i--) {
            CfgSxdhZoneEntity zone = zones.get(i);
            List<Integer> sids = servers.stream().filter(tmp -> !joinedSids.contains(tmp.getId()) && DateUtil.getDaysBetween(tmp.getBeginTime(), now) + 1 >= zone.getMinOpenDay()).map(CfgServerEntity::getId).collect(Collectors.toList());
            joinedSids.addAll(sids);
            SxdhZone sxdhZone = SxdhZone.instance(serverGroup, zone.getId(), sids, zoneDate);
            sxdhZones.add(sxdhZone);
        }
        return sxdhZones;
    }

    /**
     * <pre>
     * 0，新赛季,[上赛季结束天，新赛季结束前一天]
     * -1上赛季、1下赛季时间，非月最后一天均可获得
     * </pre>
     *
     * @param seasonIndex
     * @return
     */
    public ZoneDate getZoneDate(int seasonIndex) {
        CfgSxdh cfgSxdh = SxdhTool.getSxdh();
        Date now = DateUtil.now();
        // 赛季开始时间
        Date monthBegin = DateUtil.getMonthBegin(now, seasonIndex);
        Date beginDate = DateUtil.addHours(monthBegin, cfgSxdh.getSeasonBeginHour());

        // 赛季结束时间
        Date endDate = DateUtil.getMonthEnd(now, seasonIndex);
        endDate = DateUtil.getDateBegin(endDate);
        endDate = DateUtil.addHours(endDate, cfgSxdh.getSeasonEndHour());
        return new ZoneDate(beginDate, endDate);
    }

    @Getter
    @Setter
    @ToString
    public static class ZoneDate {
        private Date beginDate;// 开始时间
        private Date endDate;// 结束时间
        private int monthInt;//月份

        public ZoneDate(Date beginDate, Date endDate) {
            this.beginDate = beginDate;
            this.endDate = endDate;
            int daysBewteen = DateUtil.getDaysBetween(beginDate, endDate);
            Date middleDate = DateUtil.addDays(beginDate, daysBewteen / 2);
            this.monthInt = DateUtil.toMonthInt(middleDate);
        }
    }

}
