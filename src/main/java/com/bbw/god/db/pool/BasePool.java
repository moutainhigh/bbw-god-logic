package com.bbw.god.db.pool;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bbw.db.redis.RedisValueUtil;
import com.bbw.god.game.data.redis.RedisKeyConst;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**<pre>
 * 数据池。
 * 目前使用两个池。
 * 每个数据池包含等待进行数据库【insert】、【update】、【delete】操作三个分类池。
 * 内部实现通过对数据池序列号对2取余决定当前使用的池。
 * 通过序列号自增切换数据池。
 * </pre>
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2018-11-13 23:15
 */
@Slf4j
@Service
public abstract class BasePool {
	//缓冲池数量
	private static final int POOL_SIZE = 2;
	//基础键值
	protected static final String BASE_KEY = RedisKeyConst.RUNTIME_KEY + RedisKeyConst.SPLIT + "dbpool";
	//数据池增长序列，用来切换数据池
	private static final String SEQ_KEY = RedisKeyConst.SPLIT + "seq";
	//上一个缓冲池序列
	private static final String LAST_SEQ_KEY = RedisKeyConst.SPLIT + "seqlast";
	//保存动作状态。1保存中。0保存完毕。
	private static final String SAVING_KEY = RedisKeyConst.SPLIT + "saving";
	@Autowired
	private RedisValueUtil<Integer> valueRedis;//缓冲池开关

	/**返回基础键值 */
	protected abstract String getBaseKey();

	/**获取数据类型 */
	protected abstract String getDataType();

	/**保存数据 */
	protected abstract void save();

	/**
	 * 将数据池的对象持久化到数据库
	 */
	public void saveToDB() {
		try {
			Integer saving = Optional.ofNullable(valueRedis.get(getSavingKey())).orElse(0);
			//数据保存中
			if (saving > 0) {
				return;
			}
			//切换到新的数据池
			this.switchPool();
			//标志为保存中
			valueRedis.increment(getSavingKey(), 1);
			Long begin = System.currentTimeMillis();
			//保存数据
			save();
			Long end = System.currentTimeMillis();
			if (end - begin > 1000) {
				log.info("保存[" + getDataType() + "]数据。耗时：" + (end - begin));
			}
			if (end - begin > 5 * 1000) {
				log.error("保存[" + getDataType() + "]数据。耗时：" + (end - begin));
			}
		} finally {
			valueRedis.set(getSavingKey(), 0);
		}
	}

	/**保存动作状态的KEY */
	private String getSavingKey() {
		return getBaseKey() + SAVING_KEY;
	}

	/**当前序列的KEY */
	private String getSeqKey() {
		return getBaseKey() + SEQ_KEY;
	}

	/**当前序列的值 */
	protected int getCurrentPoolSeq() {
		Integer value = valueRedis.get(getSeqKey());
		if (null == value) {
			value = 1;
			valueRedis.set(getSeqKey(), value);
		}
		int rtn = (int) (value % POOL_SIZE) + 1;
		return rtn;
	}

	/**上一个缓冲池序列的值 */
	protected int getLastPoolSeq() {
		Integer value = valueRedis.get(this.getBaseKey() + LAST_SEQ_KEY);
		if (null == value) {
			value = 1;
			valueRedis.set(getSeqKey(), value);
		}
		int rtn = (int) (value % POOL_SIZE) + 1;
		return rtn;
	}

	/**
	 * 重置缓冲池增长序列值。1分钟保存1次，理论上可以用4000+年，可以不重置
	 */
	public void resetPoolSeq() {
		int value = this.getCurrentPoolSeq();
		//保存当前缓冲池
		valueRedis.set(this.getBaseKey() + LAST_SEQ_KEY, value);
		value++;
		valueRedis.set(getSeqKey(), value);
	}

	/**
	 * 切换数据池
	 */
	private void switchPool() {
		//保存当前缓冲池
		valueRedis.set(this.getBaseKey() + LAST_SEQ_KEY, valueRedis.get(getSeqKey()));
		//自增长1
		valueRedis.increment(getSeqKey(), 1);
	}

	@Getter
	public class DBResult<T> {
		@Setter
		private int successSize = 0;
		private ArrayList<T> failure = new ArrayList<>();

		public void failureAdd(T e) {
			failure.add(e);
		}

		public void failureAdd(Collection<T> coll) {
			failure.addAll(coll);
		}
	}

}
