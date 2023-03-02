package com.bbw.god.job.game;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.bbw.god.PrepareDataService;

/**
 * 提前生成数据任务
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-01-16 09:47
 */
@Component("prepareDataJob")
public class PrepareDataJob extends GameJob {
	@Autowired
	@Lazy
	private List<PrepareDataService> prepareDataServices;
	@Value("${game-data-result-days:10}")
	private int prepareDays;// 提前生成多少天的结果数据

	@Override
	public void job() {
		for (PrepareDataService service : prepareDataServices) {
			service.prepareDatas(prepareDays);
		}
	}

	@Override
	public String getJobDesc() {
		return "提前生成数据";
	}

	@Override
	public void doJob(String sendMail) {
		super.doJob(sendMail);
	}
}
