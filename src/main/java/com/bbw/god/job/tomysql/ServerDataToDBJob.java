package com.bbw.god.job.tomysql;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bbw.god.db.pool.ServerDataPool;

/**
 * 定时持久化到数据库
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2018-11-04 16:22
 */
@Component("serverDataToDBJob")
public class ServerDataToDBJob extends DbJob {
	@Autowired
	private ServerDataPool pool;

	@Override
	public void job() {
		pool.saveToDB();
	}

	@Override
	public String getJobDesc() {
		return "ServerData保存";
	}

	@Override
	public void doJob(String sendMail) {
		super.doJob(sendMail);
	}

}
