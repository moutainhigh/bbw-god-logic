package com.bbw.god.gm;

import com.bbw.common.Rst;
import com.bbw.god.controller.AbstractController;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.server.ServerService;
import com.bbw.god.server.guild.GuildInfo;
import com.bbw.god.server.guild.UserGuild;
import com.bbw.god.server.guild.service.GuildInfoService;
import com.bbw.god.server.guild.service.GuildUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 作者 ：lwb
 * @version 创建时间：2020年3月19日 下午4:18:07
 * 类说明
 */
@RestController
@RequestMapping("/gm")
public class GMGuildCtrl extends AbstractController {
	@Autowired
	private GameUserService gameUserService;
	@Autowired
	private ServerService serverService;
	@Autowired
	private GuildInfoService guildInfoService;
	@Autowired
	private GuildUserService guildUserService;

	@RequestMapping("guild!resetViceBoss")
	public Rst resetRst(long uid) {
		UserGuild userGuild = gameUserService.getSingleItem(uid, UserGuild.class);
		if (userGuild == null || userGuild.getGuildId() == 0) {
			return Rst.businessFAIL("未加行会");
		}
		GuildInfo info = serverService.getServerData(gameUserService.getActiveSid(uid), userGuild.getGuildId(), GuildInfo.class);
		if (info == null) {
			return Rst.businessFAIL("未加行会");
		}
		info.setViceBossId(0L);
		serverService.updateServerData(info);
		return Rst.businessOK();
	}

	@RequestMapping("guild!kickMember")
	public Rst kickMember(long presidentId, long memberId) {
		int sid = gameUserService.getActiveSid(presidentId);
		guildInfoService.expulsion(presidentId, sid, memberId);
		UserGuild presidentGuild = guildUserService.getUserGuild(presidentId);
		GuildInfo guild = serverService.getServerData(sid, presidentGuild.getGuildId(), GuildInfo.class);
		UserGuild memberGuild = guildUserService.getUserGuild(memberId);
		Rst rst = Rst.businessOK();
		rst.put("guild", guild.getId());
		rst.put("presidentGuild", presidentGuild.getId());
		rst.put("memberGuild", memberGuild.getId());
		return rst;
	}
}
