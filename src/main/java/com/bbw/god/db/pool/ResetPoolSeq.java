package com.bbw.god.db.pool;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bbw.god.clear.ServerClearService;

/**
 * 重置缓冲池序号
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-03-13 11:34
 */
@Service
public class ResetPoolSeq implements ServerClearService {
	@Autowired
	private UserDataPool uDataPool;
	@Autowired
	private ServerDataPool sDataPool;

	@Override
	public void clear(int sid) {
		//重置缓冲池增长序列值。1分钟保存1次，理论上可以用4000+年，可以不重置
		//uDataPool.resetPoolSeq();
		//sDataPool.resetPoolSeq();
	}

}
