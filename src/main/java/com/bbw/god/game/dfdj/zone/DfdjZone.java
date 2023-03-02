package com.bbw.god.game.dfdj.zone;

import com.bbw.common.ID;
import com.bbw.god.game.data.GameData;
import com.bbw.god.game.data.GameDataType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Date;
import java.util.List;

/**
 * @author suchaobin
 * @description 巅峰对决战区
 * @date 2021/1/5 14:25
 **/
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class DfdjZone extends GameData {
    private Integer serverGroup;// 区服组
    private Integer zone;// 战区
    private List<Integer> sids;// 战区区服
    private Integer season;//赛季
    private Date beginDate;// 战区开始时间
    private Date endDate;// 战区结束时间

    public static DfdjZone instance(int serverGroup, int zoneId, List<Integer> sids, DfdjZoneService.ZoneDate zoneDate) {
        DfdjZone zone = new DfdjZone();
        zone.setId(ID.INSTANCE.nextId());
        zone.setServerGroup(serverGroup);
        zone.setZone(zoneId);
        zone.setSids(sids);
        zone.setBeginDate(zoneDate.getBeginDate());
        zone.setSeason(zoneDate.getMonthInt());
        zone.setEndDate(zoneDate.getEndDate());
        return zone;
    }

    public boolean ifMatch(int season) {
        return this.season != null && this.season == season;
    }

    @Override
    public GameDataType gainDataType() {
        return GameDataType.DFDJ_ZONE;
    }

}
