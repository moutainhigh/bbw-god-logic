package com.bbw.god.job.game;

import com.bbw.god.db.entity.CfgServerEntity;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.game.config.server.ServerTool;
import com.bbw.god.mall.lottery.event.LotteryEventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author suchaobin
 * @description 发送奖券奖励定时器
 * @date 2020/7/2 10:43
 **/
@Component("lotteryAwardsJob")
@Slf4j
public class LotteryAwardsJob extends GameJob {

	@Override
	public void job() {
		List<Integer> groupIds = ServerTool.getAvailableServers().stream()
				.map(CfgServerEntity::getGroupId).distinct().collect(Collectors.toList());
		for (Integer group : groupIds) {
			LotteryEventPublisher.pubLotteryAwardSendEvent(group, new BaseEventParam());
		}
	}

	@Override
	public String getJobDesc() {
		return "奖券定时开奖";
	}

	// 必须重载，否则定时任务引擎认不到方法
	@Override
	public void doJob(String sendMail) {
		super.doJob(sendMail);
	}
}
