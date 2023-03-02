package com.bbw.god.job.game;

import com.bbw.god.activity.holiday.lottery.service.bocake.HolidayBoCakeJHNFService;
import com.bbw.god.db.entity.CfgServerEntity;
import com.bbw.god.game.config.server.ServerTool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 发送金虎纳福王中王奖励定时器
 *
 * @author: huanghb
 * @date: 2022/1/11 17:21
 */
@Component("holdaiyLotteryJHNFAwardsJob")
@Slf4j
public class HoldaiyLotteryJHNFAwardsJob extends GameJob {
	@Autowired
	private HolidayBoCakeJHNFService holidayBoCakeJHNFService;

	@Override
	public void job() {
		List<Integer> groupIds = ServerTool.getAvailableServers().stream()
				.map(CfgServerEntity::getGroupId).distinct().collect(Collectors.toList());
		for (Integer group : groupIds) {
			holidayBoCakeJHNFService.drawWangZhongWang(group);
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
