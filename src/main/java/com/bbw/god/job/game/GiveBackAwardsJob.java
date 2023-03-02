package com.bbw.god.job.game;

import com.bbw.god.game.award.giveback.GiveBackPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 定时返还奖励
 *
 * @author: suhq
 * @date: 2022/5/26 4:10 下午
 */
@Component("giveBackAwardsJob")
public class GiveBackAwardsJob extends GameJob {
	@Autowired
	private GiveBackPool giveBackPool;

	@Override
	public void job() {
		giveBackPool.giveBack();
	}

	@Override
	public String getJobDesc() {
		return "定时返还奖励";
	}

	//必须重载，否则定时任务引擎认不到方法
	@Override
	public void doJob(String sendMail) {
		super.doJob(sendMail);
	}

}
