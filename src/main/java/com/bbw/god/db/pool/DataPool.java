package com.bbw.god.db.pool;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bbw.db.redis.RedisSetUtil;
import com.bbw.god.game.data.redis.RedisKeyConst;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * 数据池。
 * 目前使用两个池。
 * 每个数据池包含等待进行数据库【insert】、【update】、【delete】操作三个分类池。
 * 内部实现通过对数据池序列号对2取余决定当前使用的池。
 * 通过序列号自增切换数据池。
 * </pre>
 * 
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2018-11-13 23:15
 */
@Slf4j
@Service
public abstract class DataPool extends BasePool {
	private static Long idSeq = 1L;
	@Autowired
	protected RedisSetUtil<String> dataPoolKeySet;// Data缓存池
	// 玩家数据
	private static final String INSERT_KEY = RedisKeyConst.SPLIT + "insert" + RedisKeyConst.SPLIT;// 新增
	private static final String UPDATE_KEY = RedisKeyConst.SPLIT + "update" + RedisKeyConst.SPLIT;// 修改
	private static final String DELETE_KEY = RedisKeyConst.SPLIT + "delete" + RedisKeyConst.SPLIT;// 删除

	private static final int LONG_TIME = 1000 * 1;// 保存时间

	/** 保存新增数据 **/
	protected abstract DBResult<String> dbInsert(PoolKeys poolKeys);

	/** 保存更新数据 **/
	protected abstract DBResult<String> dbUpdate(PoolKeys poolKeys);

	/** 删除数据 **/
	protected abstract DBResult<String> dbDelete(String deletePoolKey);

	@Override
	protected void save() {
		log.error("--数据" + this.getDataType() + "保存" + ++idSeq + "开始-------------------------------------------------");

		PoolKeys lastPoolKeys = getLastPoolKeys();
		// 保存数据
		try {
			Long begin = System.currentTimeMillis();
			DBResult<String> insert = dbInsert(lastPoolKeys);
			Long end = System.currentTimeMillis();
			if (end - begin > LONG_TIME) {
				String msg = "添加成功[" + this.getDataType() + "][" + insert.getSuccessSize() + "]条，失败[" + insert.getFailure().size() + "]用户数据到数据库。耗时：" + (end - begin);
				log.error(msg);
			} else {
				String msg = "添加成功[" + this.getDataType() + "][" + insert.getSuccessSize() + "]条，失败[" + insert.getFailure().size() + "]用户数据到数据库。耗时：" + (end - begin);
				log.info(msg);
			}
			begin = end;
			// 转移保存失败数据到当前数据池
			moveFailure(insert.getFailure(), this.getInsertPoolKey());
			dataPoolKeySet.delete(lastPoolKeys.getInsertPoolKey());
			end = System.currentTimeMillis();
			log.info("转移并清除旧缓冲池的[" + lastPoolKeys.getInsertPoolKey() + "][" + this.getDataType() + "]数据。耗时：" + (end - begin));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		// 更新数据
		try {
			Long begin = System.currentTimeMillis();
			DBResult<String> update = dbUpdate(lastPoolKeys);
			Long end = System.currentTimeMillis();
			if (end - begin > LONG_TIME) {
				String msg = "更新成功[" + this.getDataType() + "][" + update.getSuccessSize() + "]条，失败[" + update.getFailure().size() + "]用户数据到数据库。耗时：" + (end - begin);
				log.error(msg);
			} else {
				String msg = "更新成功[" + this.getDataType() + "][" + update.getSuccessSize() + "]条，失败[" + update.getFailure().size() + "]用户数据到数据库。耗时：" + (end - begin);
				log.info(msg);
			}
			begin = end;
			// 转移保存失败数据到当前数据池
			moveFailure(update.getFailure(), this.getUpdatePoolKey());
			dataPoolKeySet.delete(lastPoolKeys.getUpdatePoolKey());
			end = System.currentTimeMillis();
			log.info("转移并清除旧缓冲池的[" + lastPoolKeys.getUpdatePoolKey() + "][" + this.getDataType() + "]数据。耗时：" + (end - begin));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		// 删除数据
		try {
			Long begin = System.currentTimeMillis();
			DBResult<String> delete = dbDelete(lastPoolKeys.getDeletePoolKey());
			Long end = System.currentTimeMillis();
			if (end - begin > LONG_TIME) {
				String msg = "删除成功[" + this.getDataType() + "][" + delete.getSuccessSize() + "]条，失败[" + delete.getFailure().size() + "]用户数据到数据库。耗时：" + (end - begin);
				log.error(msg);
			} else {
				String msg = "删除成功[" + this.getDataType() + "][" + delete.getSuccessSize() + "]条，失败[" + delete.getFailure().size() + "]用户数据到数据库。耗时：" + (end - begin);
				log.info(msg);
			}
			begin = end;
			// 转移保存失败数据到当前数据池
			moveFailure(delete.getFailure(), this.getDeletePoolKey());
			dataPoolKeySet.delete(lastPoolKeys.getDeletePoolKey());
			end = System.currentTimeMillis();
			log.info("转移并清除旧缓冲池的[" + lastPoolKeys.getDeletePoolKey() + "][" + this.getDataType() + "]数据。耗时：" + (end - begin));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		log.info("--数据" + this.getDataType() + "保存" + idSeq + "结束-------------------------------------------------");

	}

	/**
	 * 添加到数据池，等待【插入】到数据库
	 * 
	 * @param data
	 */
	public void toInsertPool(String... insertObjKey) {
		dataPoolKeySet.add(this.getInsertPoolKey(), insertObjKey);
	}

	/**
	 * 添加到数据池，等待【更新】到数据库
	 * 
	 * @param res
	 */
	public void toUpdatePool(String... updateObjKey) {
		// 玩家ID.资源类型.资源ID
		dataPoolKeySet.add(this.getUpdatePoolKey(), updateObjKey);
	}

	/**
	 * 添加到数据池，等待【删除】数据库数据
	 * 
	 * @param res
	 */
	public void toDeletePool(String... deleteObjKey) {
		// 玩家ID.资源类型.资源ID
		dataPoolKeySet.add(this.getDeletePoolKey(), deleteObjKey);
	}

	/**
	 * 获取当前等待【插入】数据池的key
	 * 
	 * @return
	 */
	private String getInsertPoolKey() {
		return this.getBaseKey() + INSERT_KEY + getCurrentPoolSeq();
	}

	/**
	 * 获取当前等待【更新】数据池的key
	 * 
	 * @return
	 */
	private String getUpdatePoolKey() {
		return this.getBaseKey() + UPDATE_KEY + getCurrentPoolSeq();
	}

	/**
	 * 获取当前等待【删除】数据池的key
	 * 
	 * @return
	 */
	private String getDeletePoolKey() {
		return this.getBaseKey() + DELETE_KEY + getCurrentPoolSeq();
	}

	protected PoolKeys getLastPoolKeys() {
		PoolKeys pk = new PoolKeys();
		String lastInsert = this.getBaseKey() + INSERT_KEY + this.getLastPoolSeq();
		String lastUpdate = this.getBaseKey() + UPDATE_KEY + this.getLastPoolSeq();
		String lastDelete = this.getBaseKey() + DELETE_KEY + this.getLastPoolSeq();
		pk.setInsertPoolKey(lastInsert);
		pk.setUpdatePoolKey(lastUpdate);
		pk.setDeletePoolKey(lastDelete);
		return pk;
	}

	protected void moveFailure(List<String> list, String key) {
		if (!list.isEmpty()) {
			String[] array = new String[list.size()];
			dataPoolKeySet.add(key, list.toArray(array));
			log.error(list.toString());
		}
	}

	@Getter
	@Setter
	public class PoolKeys {
		private String insertPoolKey;// 获取当前等待【插入】数据池的key
		private String updatePoolKey;// 获取当前等待【更新】数据池的key
		private String deletePoolKey;// 获取当前等待【删除】数据池的key

		public String[] getAllKeys() {
			return new String[] { insertPoolKey, updatePoolKey, deletePoolKey };
		}
	}
}
