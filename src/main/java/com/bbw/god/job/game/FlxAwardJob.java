package com.bbw.god.job.game;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bbw.common.DateUtil;
import com.bbw.god.server.flx.FlxService;

/**
 * 福临轩定时发放奖励,每天00:10执行
 * 
 * @author suhq
 * @date 2019年3月3日 下午6:43:32
 */
@Component("flxAwardJob")
public class FlxAwardJob extends GameJob {
	@Autowired
	private FlxService flxService;

	@Override
	public void job() {
		Date yesterday = DateUtil.addDays(DateUtil.now(), -1);
		flxService.sendFlxMailAward(DateUtil.toDateInt(yesterday));
	}

	@Override
	public String getJobDesc() {
		return "派发福临轩奖励";
	}

	//必须重载，否则定时任务引擎认不到方法
	@Override
	public void doJob(String sendMail) {
		super.doJob(sendMail);
	}

}
