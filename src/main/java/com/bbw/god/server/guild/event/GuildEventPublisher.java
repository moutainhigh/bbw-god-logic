package com.bbw.god.server.guild.event;

import com.bbw.common.SpringContextUtil;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.game.config.CfgGuild;

/**
 * @author lwb
 * @version 1.0
 * @date 2019年6月24日
 */
public class GuildEventPublisher {

	public static void pubGuildTaskFinished(EPGuildTaskFinished task) {
		SpringContextUtil.publishEvent(new GuildTaskFinishedEvent(task));
	}

	public static void pubGuildJoinEvent(EPGuildJoin epGuildJoin) {
		SpringContextUtil.publishEvent(new GuildJoinEvent(epGuildJoin));
	}

	public static void pubGuildEightDiagramsTaskEvent(EPGuildEightDiagramsTask epGuildEightDiagramsTask) {
		SpringContextUtil.publishEvent(new GuildEightDiagramsTaskEvent(epGuildEightDiagramsTask));
	}

	public static void pubGuildCreateEvent(EPGuildCreate ep) {
		SpringContextUtil.publishEvent(new GuildCreateEvent(ep));
	}

	public static void pubGuildOpenBoxEvent(CfgGuild.BoxReward box, BaseEventParam bep) {
		SpringContextUtil.publishEvent(new GuildOpenBoxEvent(new EPGuildOpenBox(box, bep)));
	}

	public static void pubAddGuildExp(EPAddGuildExp ep) {
		SpringContextUtil.publishEvent(new GuildAddExpEvent(ep));
	}
}
