package com.bbw.god.game.chanjie;

import com.bbw.common.DateUtil;
import com.bbw.god.controller.AbstractController;
import com.bbw.god.game.CR;
import com.bbw.god.game.chanjie.service.ChanjieFightService;
import com.bbw.god.game.chanjie.service.ChanjieService;
import com.bbw.god.game.chanjie.service.ChanjieSundayFightService;
import com.bbw.god.game.chanjie.service.ChanjieUserService;
import com.bbw.god.game.config.server.ServerTool;
import com.bbw.god.server.ServerDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 阐截斗法入口
 * 
 * @author lwb
 * @date 2019年6月14日
 * @version 1.0
 */
@RestController
public class ChanjieCtrl extends AbstractController {
	@Autowired
	private ChanjieService chanjieService;
	@Autowired
	private ChanjieUserService chanjieUserService;
	@Autowired
	private ChanjieSundayFightService chanjieSundayFightService;

	// 检查是否加入教派

	@RequestMapping(CR.Chanjie.CHECK_JOIN)
	public ChanjieRd checkJoin() {
		int gid = ServerTool.getServerGroup(getServerId());
		return chanjieUserService.checkJoinStatus(getUserId(), gid);
	}

	// 加入教派
	@RequestMapping(CR.Chanjie.JOIN_RELIGIOUS)
	public ChanjieRd join(Integer rid) {
		int gid = ServerTool.getServerGroup(getServerId());
		return chanjieUserService.joinReligiou(getUserId() , ChanjieType.getType(rid), gid);
	}

	// 获取主页信息
	@RequestMapping(CR.Chanjie.MAIN_INFO)
	public ChanjieRd mainInfo() {
		int gid = ServerTool.getServerGroup(getServerId());
		if (DateUtil.isWeekDay(7)) {
			return chanjieSundayFightService.getMainInfo(getUserId(), gid);
		}
		return chanjieService.getChanjieInfo(getUserId(), gid);
	}

	// 获取排行榜
	@RequestMapping(CR.Chanjie.RANKING_LIST)
	public ChanjieRd getRankingList(@RequestParam(defaultValue = "10") int pageSize,
			@RequestParam(defaultValue = "1") int current) {
		int gid = ServerTool.getServerGroup(getServerId());
		if (DateUtil.isWeekDay(7)) {
			return chanjieSundayFightService.getRankingList(getUserId(), gid, current, pageSize);
		}
		return chanjieService.getRanking(getUserId(), gid, current, pageSize);
	}

	// 获取荣誉榜
	@RequestMapping(CR.Chanjie.HONOR_LIST)
	public ChanjieRd getHonorList(Integer rid, @RequestParam(defaultValue = "1") int current,
			@RequestParam(defaultValue = "10") int pageSize) {
		int gid = ServerTool.getServerGroup(getServerId());
		return chanjieService.getHonorRanking(rid, gid, current, pageSize);
	}

	// 获取教派奇人
	@RequestMapping(CR.Chanjie.SPECAIL_LIST)
	public ChanjieRd getSpecailHonorList() {
		int gid = ServerTool.getServerGroup(getServerId());
		return chanjieService.getSpecailHonorList(getUserId(), gid);
	}

	// 点赞
	@RequestMapping(CR.Chanjie.THUMBS_UP)
	public ChanjieRd thumbsUp(Integer id) {
		int gid = ServerTool.getServerGroup(getServerId());
		return chanjieService.addLike(getUserId(), ChanjieType.getType(id), gid);
	}

	// 购买血量
	@RequestMapping(CR.Chanjie.BUY_BLOOD)
	public ChanjieRd buyBlood() {
		return chanjieUserService.buyBoold(getUserId());
	}

	// 封神实时战况
	@RequestMapping(CR.Chanjie.WAR_SITUATION)
	public ChanjieRd warSituation() {
		int gid = ServerTool.getServerGroup(getServerId());
		return chanjieSundayFightService.warSituation(getUserId(), gid);
	}

}
