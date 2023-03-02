package com.bbw.god.job.game;

import com.bbw.common.DateUtil;
import com.bbw.god.city.mixd.nightmare.pos.CengZhuProcessor;
import com.bbw.god.db.entity.CfgServerEntity;
import com.bbw.god.game.config.server.ServerTool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 重置迷仙洞层主信息
 *
 * @author: huanghb
 * @date: 2022/1/11 17:21
 */
@Slf4j
@Component("reSetMIXDCengZhuInfoJob")
public class ReSetMIXDCengZhuInfoJob extends GameJob {
	@Autowired
	private CengZhuProcessor cengZhuProcessor;

	/**
	 * 获取任务描述
	 *
	 * @return
	 */
	@Override
	public String getJobDesc() {
		return "重置迷仙洞层主信息";
	}

	@Override
	public void job() {
		Date beginDate = DateUtil.fromDateTimeString("2022-06-29 00:00:00");
		Date endDate = DateUtil.fromDateTimeString("2022-06-29 23:59:59");

		Date now = DateUtil.now();
		if (DateUtil.millisecondsInterval(now, beginDate) < 0 || DateUtil.millisecondsInterval(now, endDate) > 0) {
			log.info("定时器不在执行时间之内！");
			return;
		}
		List<Integer> groupIds = ServerTool.getAvailableServers().stream()
				.map(CfgServerEntity::getGroupId).distinct().collect(Collectors.toList());
		for (Integer group : groupIds) {
			cengZhuProcessor.ResetLevelOwnerInfo(group);
		}
	}

	// 必须重载，否则定时任务引擎认不到方法
	@Override
	public void doJob(String sendMail) {
		super.doJob(sendMail);
	}
}
