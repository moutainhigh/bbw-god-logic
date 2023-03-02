package com.bbw.god.job.game;

import com.bbw.common.DateUtil;
import com.bbw.common.JSONUtil;
import com.bbw.god.game.chanjie.ChanjieStatusService;
import com.bbw.god.game.chanjie.service.ChanjieService;
import com.bbw.god.game.chanjie.service.ChanjieSundayFightService;
import com.bbw.god.game.config.server.ServerTool;
import com.bbw.mc.mail.MailAction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * 阐截斗法统计定时器
 * 
 * @author lwb
 * @date 2019年6月26日
 * @version 1.0
 */
@Slf4j
@Component("chanjieJob")
public class ChanjieAwardJob {
	@Autowired
	private ChanjieService chanjieService;
	@Autowired
	private ChanjieSundayFightService chanjieSundayFightService;
	@Autowired
	private ChanjieStatusService statusService;
	@Autowired
	private MailAction mailAction;
	// 每日统计
	public void dailyAwardJob() {
		log.info("开始更新全服阐截斗法玩家的每日积分结算！");
		if (!statusService.isOpen()) {
			log.info("阐截斗法未开放,不进行本次阐截斗法玩家的每日积分结算！");
			return;
		}
		List<Integer> groups = ServerTool.getServerGroups();
		String content=DateUtil.toDateTimeString(new Date())+":执行阐截斗法结算定时器，平台为：";
		for (Integer gid : groups) {
			log.debug(content+gid);
			chanjieService.settleAccounts(gid);
		}
		content+=JSONUtil.toJson(groups);
		mailAction.notifyCoder("阐截斗法每日结算定时器执行通知", content);
	}

	// 乱斗封神 缩圈淘汰
	// 活动开启20分钟后，自动淘汰排名90名以上的玩家；40分钟后，自动淘汰排名45名以上的玩家。
	public void eliminateJob(String minutes) {
		log.info("开始对周日乱斗封神玩家进行淘汰，当前淘汰阶段是" + minutes + "分钟！");
		if (!statusService.isOpen()) {
			log.info("阐截斗法未开放！，不进行本次缩圈定时器");
			return;
		}
		Integer min = Integer.parseInt(minutes);
		int num=90;
		if (min==40) {
			num=45;
		}
		List<Integer> groups = ServerTool.getServerGroups();
		for (Integer gid : groups) {
			chanjieSundayFightService.eliminate(num, gid);
		}
	}
	
	public void sunDayGameOver() {
		log.info("开始对周日乱斗封神胜利结算！");
		if (!statusService.isOpen()) {
			log.info("阐截斗法未开放！，不进行本次胜利结算");
			return;
		}
		List<Integer> groups = ServerTool.getServerGroups();
		String content=DateUtil.toDateTimeString(new Date())+":执行阐截斗法结算定时器，平台为：";
		for (Integer gid : groups) {
			content+=gid+"、";
			chanjieSundayFightService.stopGame(gid);
		}
		mailAction.notifyCoder("阐截斗法周日结算定时器执行通知", content);
	}
}
