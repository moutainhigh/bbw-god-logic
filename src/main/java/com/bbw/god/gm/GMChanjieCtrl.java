package com.bbw.god.gm;

import com.bbw.common.Rst;
import com.bbw.god.controller.AbstractController;
import com.bbw.god.game.chanjie.service.ChanjieService;
import com.bbw.god.game.config.server.ServerTool;
import com.bbw.god.gameuser.achievement.UserAchievementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author 作者 ：lwb
 * @version 创建时间：2019年12月16日 上午10:37:16 类说明 阐截斗法工具类
 */
@RestController
@RequestMapping("/gm/chanjie")
public class GMChanjieCtrl extends AbstractController {
	@Autowired
	private UserAchievementService userAchievementService;
	@Autowired
	private ChanjieService chanjieService;
	
	@RequestMapping("/sendAward")
	public Rst send() {
		List<Integer> groups = ServerTool.getServerGroups();
		for (Integer gid : groups) {
			chanjieService.sendSeasonAwardXF(gid);
		}
		return Rst.businessOK();
	}

	@RequestMapping("/settle")
	public Rst settle(int gid){
		chanjieService.settleAccounts(gid);
		return Rst.businessOK();
	}
}
