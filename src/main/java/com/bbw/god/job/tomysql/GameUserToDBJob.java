package com.bbw.god.job.tomysql;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bbw.god.db.pool.PlayerPool;

/**
 * 定时持久化到数据库
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2018-11-04 16:22
 */
@Component("gameUserToDBJob")
public class GameUserToDBJob extends DbJob {
	@Autowired
	private PlayerPool pool;

	@Override
	public void job() {
		pool.saveToDB();
	}

	@Override
	public String getJobDesc() {
		return "GameUser数据保存";
	}

	@Override
	public void doJob(String sendMail) {
		super.doJob(sendMail);
	}

}
