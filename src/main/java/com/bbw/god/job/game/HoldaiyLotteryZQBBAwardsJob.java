package com.bbw.god.job.game;

import com.bbw.god.activity.holiday.lottery.service.bocake.HolidayBoCakeZQBBService;
import com.bbw.god.db.entity.CfgServerEntity;
import com.bbw.god.game.config.server.ServerTool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author suchaobin
 * @description 发送中秋博饼王中王奖励定时器
 * @date 2020/9/18 17:06
 **/
@Component("holdaiyLotteryZQBBAwardsJob")
@Slf4j
public class HoldaiyLotteryZQBBAwardsJob extends GameJob {
	@Autowired
	private HolidayBoCakeZQBBService holidayBoCakeLotteryZQBBService;


	@Override
	public void job() {
		List<Integer> groupIds = ServerTool.getAvailableServers().stream()
				.map(CfgServerEntity::getGroupId).distinct().collect(Collectors.toList());
		for (Integer group : groupIds) {
			holidayBoCakeLotteryZQBBService.drawWangZhongWang(group);
		}
	}

	@Override
	public String getJobDesc() {
		return "王中王定时开奖";
	}

	// 必须重载，否则定时任务引擎认不到方法
	@Override
	public void doJob(String sendMail) {
		super.doJob(sendMail);
	}
}
