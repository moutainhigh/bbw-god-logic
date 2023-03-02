package com.bbw.god.server.guild.event;

import com.bbw.god.event.BaseEventParam;

import lombok.Data;

/**
 * @author 作者 ：lwb
 * @version 创建时间：2020年3月16日 下午4:55:38 类说明 增加行会经验
 */
@Data
public class EPAddGuildExp extends BaseEventParam {
	private long guildId;
	private int exp;

	public static EPAddGuildExp instance(BaseEventParam ep,int exp,long guildId) {
    	EPAddGuildExp ev = new EPAddGuildExp();
        ev.setValues(ep);
		ev.setGuildId(guildId);
		ev.setExp(exp);
        return ev;
    }
}