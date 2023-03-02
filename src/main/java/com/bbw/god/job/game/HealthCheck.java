package com.bbw.god.job.game;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.bbw.common.DateUtil;
import com.bbw.god.game.health.CheckConfig;

/**
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-03-31 22:37
 */
@Component("healthCheck")
public class HealthCheck extends GameJob {
	@Autowired
	private CheckConfig checkConfig;
	@Value("${health-check-days:7}")
	private int checkDays;//检查未来天数的数据完整性情况

	@Override
	public void job() {
		String title = "";
		boolean success = true;
		Date now = DateUtil.now();
		for (int i = 0; i < checkDays; i++) {
			Date date = DateUtil.addDays(now, i + 1);
			boolean b = checkConfig.check(date);
			if (!b) {
				title += "[" + DateUtil.toDateInt(date) + "]";
			}
			success = success && b;
		}
		if (title.length() > 0) {
			title += "的数据异常，请及时排查确认！";
		} else {
			title = "未来" + checkDays + "天的配置数据正常！如果有配置新服，请注意初始化区服数据！";
		}
		notify.notifyCoder(title, title);
	}

	@Override
	public String getJobDesc() {
		return "健康检查";
	}

	@Override
	public void doJob(String sendMail) {
		super.doJob(sendMail);
	}

}
