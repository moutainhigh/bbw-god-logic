package com.bbw.god.server.guild.event;

import com.bbw.god.event.BaseEventParam;
import com.bbw.god.game.config.CfgGuild;
import lombok.Data;

/**
 * @author suchaobin
 * @description 行会宝箱开启事件参数
 * @date 2020/2/24 14:35
 */
@Data
public class EPGuildOpenBox extends BaseEventParam {
	private CfgGuild.BoxReward box;

	public EPGuildOpenBox(CfgGuild.BoxReward box, BaseEventParam bep) {
		this.box = box;
		setValues(bep);
	}
}
