package com.bbw.god.game.sxdh;

import com.bbw.common.ID;
import com.bbw.god.game.data.GameData;
import com.bbw.god.game.data.GameDataType;
import lombok.Data;
import lombok.ToString;

import java.util.Date;
import java.util.List;

/**
 * 神仙大会战区
 *
 * @author suhq
 * @date 2019-06-18 11:18:21
 */
@Data
@ToString(callSuper = true)
public class SxdhZone extends GameData {
    private Integer serverGroup;// 区服组
    private Integer zone;// 战区
    private List<Integer> sids;// 战区区服
    private Integer season;//赛季
    private Date beginDate;// 战区开始时间
    private Date endDate;// 战区结束时间

    public static SxdhZone instance(int serverGroup, int zoneId, List<Integer> sids, SxdhZoneService.ZoneDate zoneDate) {
        SxdhZone sxdhZone = new SxdhZone();
        sxdhZone.setId(ID.INSTANCE.nextId());
        sxdhZone.setServerGroup(serverGroup);
        sxdhZone.setZone(zoneId);
        sxdhZone.setSids(sids);
        sxdhZone.setBeginDate(zoneDate.getBeginDate());
        sxdhZone.setSeason(zoneDate.getMonthInt());
        sxdhZone.setEndDate(zoneDate.getEndDate());
        return sxdhZone;
    }

    public boolean ifMatch(int season) {
        return this.season != null && this.season == season;
    }

    @Override
    public GameDataType gainDataType() {
        return GameDataType.SXDH_ZONE;
    }

}
