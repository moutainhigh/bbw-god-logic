package com.bbw.god.job.server;

import com.bbw.god.db.entity.CfgServerEntity;
import com.bbw.god.mall.snatchtreasure.SnatchTreasureService;
import com.bbw.god.server.ServerUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * @author suchaobin
 * @description 夺宝每周重置定时器
 * @date 2020/7/6 9:41
 **/
@Deprecated
//@Component("snatchTreasurePerWeekResetJob")
public class SnatchTreasurePerWeekResetJob extends ServerJob {
	@Autowired
	private SnatchTreasureService snatchTreasureService;
	@Autowired
	private ServerUserService serverUserService;

	@Override
	public String getJobDesc() {
		return "夺宝每周重置定时器";
	}

	@Override
	public void job(CfgServerEntity server) {
		// 获取7天内登陆的玩家集合（活动每7天重置一次）
		Set<Long> uids = serverUserService.getUidsInDays(server.getMergeSid(), 7);
		for (Long uid : uids) {
//			snatchTreasureService.resetUserSnatchTreasure(uid);
		}
	}

	//必须重载，否则定时任务引擎认不到方法
	@Override
	public void doJob(String sendMail) {
		super.doJob(sendMail);
	}
}