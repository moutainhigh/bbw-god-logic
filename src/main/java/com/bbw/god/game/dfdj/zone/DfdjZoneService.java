package com.bbw.god.game.dfdj.zone;

import com.bbw.common.DateUtil;
import com.bbw.common.ListUtil;
import com.bbw.god.db.entity.CfgServerEntity;
import com.bbw.god.game.config.server.ServerTool;
import com.bbw.god.game.data.GameDataService;
import com.bbw.god.game.dfdj.DfdjDateService;
import com.bbw.god.game.dfdj.config.CfgDfdj;
import com.bbw.god.game.dfdj.config.CfgDfdjZoneEntity;
import com.bbw.god.game.dfdj.config.DfdjTool;
import com.bbw.god.game.dfdj.config.ZoneType;
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
 * @author suchaobin
 * @description 巅峰对决战区服务
 * @date 2021/1/5 14:28
 **/
@Slf4j
@Service
public class DfdjZoneService {
    @Autowired
    private GameDataService gameDataService;
    @Autowired
    private DfdjDateService dfdjDateService;
    @Autowired
    private GameUserService gameUserService;

    /**
     * 加入战区,新服调用
     *
     * @param server
     */
    public void joinZone(CfgServerEntity server) {
        DfdjZone zone = getZoneByZoneType(ZoneType.ZONE_ONE, server.getGroupId());
        if (zone != null) {
            if (!zone.getSids().contains(server.getMergeSid())) {
                zone.getSids().add(server.getMergeSid());
                gameDataService.updateGameData(zone);
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
    public DfdjZone getZoneByZoneType(ZoneType zoneType, int serverGroup) {
        List<DfdjZone> zones = getZones();
        return zones.stream().filter(tmp -> tmp.getServerGroup() == serverGroup &&
                tmp.getZone() == zoneType.getValue()).findFirst().orElse(null);
    }

    /**
     * 获取所在战区
     *
     * @param server
     * @return
     */
    public DfdjZone getZoneByServer(CfgServerEntity server) {
        List<DfdjZone> zones = getZones();
        // 合服后保持本赛季的战区
        int sid = server.getId();
        Optional<DfdjZone> optional = zones.stream().filter(tmp -> tmp.getSids().contains(sid)).findFirst();
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
    public DfdjZone getCurOrLastZone(long uid) {
        CfgServerEntity server = gameUserService.getOriServer(uid);
        DfdjZone zone = getZoneByServer(server);
        if (zone == null) {
            zone = getLastZone(server);
        }
        return zone;
    }

    /**
     * 上个赛季的战区
     *
     * @param server
     * @return
     */
    public DfdjZone getLastZone(CfgServerEntity server) {
        List<DfdjZone> zones = gameDataService.getGameDatas(DfdjZone.class);
        DfdjZoneService.ZoneDate zoneDate = getLastZoneDate();
        zones = zones.stream().filter(tmp -> tmp.ifMatch(zoneDate.getMonthInt())).collect(Collectors.toList());
        if (ListUtil.isNotEmpty(zones)) {
            // 合服后区服获取上赛季赛季的战区已合服前为准
            int sid = server.getId();
            DfdjZone zone = zones.stream().filter(tmp -> tmp.getSids().contains(sid)).findFirst().orElse(null);
            if (zone == null) {
                int mergeId = server.getMergeSid();
                zone = zones.stream().filter(tmp -> tmp.getSids().contains(mergeId)).findFirst().orElse(null);
            }
            return zone;
        }
        return null;
    }

    public DfdjZone getLastZone(int serverGroup, int zoneType) {
        List<DfdjZone> zones = gameDataService.getGameDatas(DfdjZone.class);
        DfdjZoneService.ZoneDate zoneDate = getLastZoneDate();
        zones = zones.stream().filter(tmp -> tmp.ifMatch(zoneDate.getMonthInt())).collect(Collectors.toList());
        if (ListUtil.isNotEmpty(zones)) {
            DfdjZone zone = zones.stream().filter(tmp -> tmp.getServerGroup() == serverGroup && tmp.getZone() == zoneType).findFirst().orElse(null);
            return zone;
        }
        return null;
    }

    /**
     * 获得上个赛季的时间信息
     *
     * @return
     */
    private DfdjZoneService.ZoneDate getLastZoneDate() {
        DfdjZoneService.ZoneDate zoneDate = getZoneDate(-1);
        Date now = DateUtil.now();
        Date monthEnd = DateUtil.getMonthEnd(now, 0);
        monthEnd = DateUtil.getDateBegin(monthEnd);
        monthEnd = DateUtil.addHours(monthEnd, 21);
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
    public List<DfdjZone> getZones() {
        return getZones(DateUtil.now());
    }

    /**
     * 获取指定日期的神仙大会
     *
     * @param date
     * @return
     */
    public List<DfdjZone> getZones(Date date) {
        List<DfdjZone> zones = gameDataService.getGameDatas(DfdjZone.class);
        zones = zones.stream().filter(tmp -> tmp.getBeginDate().before(date) && tmp.getEndDate().after(date)).collect(Collectors.toList());
        return zones;
    }

    /**
     * 昨日是否是上赛季
     *
     * @return
     */
    public boolean isLastZoneAsYesterday(DfdjZone lastZone) {
        if (lastZone == null) {
            return false;
        }
        Date yesterday = dfdjDateService.getDfdjDateEnd(-1);
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
    public List<DfdjZone> newZones(int seasonIndex) {
        DfdjZoneService.ZoneDate zoneDate = getZoneDate(seasonIndex);
        log.info("生成新的战区：" + zoneDate.toString());
        List<CfgServerEntity> allServer = ServerTool.getAvailableServers();
        Map<Integer, List<CfgServerEntity>> groupServers = allServer.stream().collect(Collectors.groupingBy(CfgServerEntity::getGroupId));
        Set<Integer> groupSet = groupServers.keySet();

        List<DfdjZone> dfdjZones = new ArrayList<>();
        for (Integer serverGroup : groupSet) {
            List<DfdjZone> groupZones = buildZones(serverGroup, groupServers.get(serverGroup), zoneDate);
            dfdjZones.addAll(groupZones);
        }
        List<DfdjZone> preZones = gameDataService.getGameDatas(DfdjZone.class);
        preZones = preZones.stream().filter(tmp -> null != tmp.getSeason()).collect(Collectors.toList());
        List<DfdjZone> toAdds = new ArrayList<>();
        for (DfdjZone dfdjZone : dfdjZones) {
            boolean isAdded = preZones.stream().anyMatch(tmp -> tmp.getZone().intValue() == dfdjZone.getZone() && tmp.getSeason().intValue() == dfdjZone.getSeason());
            if (isAdded) {
                continue;
            }
            toAdds.add(dfdjZone);
        }
        gameDataService.addGameDatas(toAdds);
        return dfdjZones;
    }

    /**
     * 生成新的区服组战区
     *
     * @return
     */
    public List<DfdjZone> buildZones(int serverGroup, List<CfgServerEntity> servers, DfdjZoneService.ZoneDate zoneDate) {
        Date now = DateUtil.now();
        List<DfdjZone> dfdjZones = new ArrayList<>();
        int maxOpenDay = DateUtil.getDaysBetween(servers.get(0).getBeginTime(), now);
        List<CfgDfdjZoneEntity> zones = DfdjTool.getZones(maxOpenDay + 1);
        List<Integer> joinedSids = new ArrayList<>();
        for (int i = zones.size() - 1; i >= 0; i--) {
            CfgDfdjZoneEntity zone = zones.get(i);
            List<Integer> sids = servers.stream().filter(tmp -> !joinedSids.contains(tmp.getId()) && DateUtil.getDaysBetween(tmp.getBeginTime(), now) + 1 >= zone.getMinOpenDay()).map(CfgServerEntity::getId).collect(Collectors.toList());
            joinedSids.addAll(sids);
            DfdjZone dfdjZone = DfdjZone.instance(serverGroup, zone.getId(), sids, zoneDate);
            dfdjZones.add(dfdjZone);
        }
        return dfdjZones;
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
    public DfdjZoneService.ZoneDate getZoneDate(int seasonIndex) {
        CfgDfdj cfgDfdj = DfdjTool.getDfdj();
        Date now = DateUtil.now();
        // 赛季开始时间
        Date monthBegin = DateUtil.getMonthBegin(now, seasonIndex);
        Date beginDate = DateUtil.addHours(monthBegin, cfgDfdj.getSeasonBeginHour());

        // 赛季结束时间
        Date endDate = DateUtil.getMonthEnd(now, seasonIndex);
        endDate = DateUtil.getDateBegin(endDate);
        endDate = DateUtil.addHours(endDate, cfgDfdj.getSeasonEndHour());
        return new DfdjZoneService.ZoneDate(beginDate, endDate);
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
