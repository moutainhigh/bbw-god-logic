package com.bbw.god.server.guild.event;

import com.bbw.god.event.BaseEventParam;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.rd.RDCommon;
import com.bbw.god.server.guild.GuildInfo;
import lombok.Getter;
import lombok.Setter;

/**
 * @author suchaobin
 * @description 申请加入行会事件
 * @date 2019/12/19 14:47
 */
@Getter
@Setter
public class EPGuildJoin extends BaseEventParam {
    private GuildInfo guild;

    public static EPGuildJoin instance(Long guId, WayEnum way, RDCommon rd,GuildInfo guild) {
        EPGuildJoin ev = new EPGuildJoin();
        ev.setGuId(guId);
        ev.setWay(way);
        ev.setRd(rd);
        ev.setGuild(guild);
        return ev;
    }
}
