package com.bbw.god.job.tomysql;

import com.bbw.god.db.pool.StatisticPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author suchaobin
 * @description 定时保存玩家统计数据按到数据库
 * @date 2020/4/27 15:31
 */
@Component("userStatisticToDBJob")
public class UserStatisticToDBJob extends DbJob {
	@Autowired
	private StatisticPool pool;

	/**
	 * 获取任务描述
	 *
	 * @return
	 */
	@Override
	public String getJobDesc() {
		return "保存UserStatistic数据";
	}

	/**
	 * 具体的任务
	 */
	@Override
	public void job() {
		pool.saveToDB();
	}
}
