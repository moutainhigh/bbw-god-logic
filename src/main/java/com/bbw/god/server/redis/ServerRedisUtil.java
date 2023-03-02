package com.bbw.god.server.redis;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.bbw.common.SpringContextUtil;
import com.bbw.db.redis.RedisSetUtil;
import com.bbw.db.redis.RedisValueUtil;
import com.bbw.god.db.entity.InsServerDataEntity;
import com.bbw.god.db.pool.ServerDataDAO;
import com.bbw.god.server.ServerData;
import com.bbw.god.server.ServerDataType;

/**
 * 区服Server对象对redis操作
 * 
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-01-14 21:29
 */
@Component
public class ServerRedisUtil {
	private static final String DB_LOAD_KEY = "load";
	@Autowired
	private RedisValueUtil<ServerData> serverDataRedis;
	@Autowired
	private RedisSetUtil<String> serverDataTypeSetRedis;
	@Autowired
	private ServerDataBatchRedis batchRedis;
	@Autowired
	private RedisValueUtil<String> statusRedis;// 区服数据是否从mysql载入的状态标志

	// 从数据库载入
	private <T extends ServerData> void dbLoadToRedis(int sid, Class<T> clazz) {
		ServerDataDAO sdd = SpringContextUtil.getBean(ServerDataDAO.class, sid);
		ServerDataType dataType = ServerDataType.fromClass(clazz);
		List<InsServerDataEntity> entityList = sdd.dbSelectServerDataByType(dataType.getRedisKey());
		List<ServerData> dataList = new ArrayList<>();
		for (InsServerDataEntity entity : entityList) {
			T serverData = JSON.parseObject(entity.getDataJson(), clazz);
			serverData.setSid(entity.getSid());
			dataList.add(serverData);
		}
		batchRedis.batchSetServerDataFromDb(dataList);
		setLoadStatus(sid, dataType);
	}

	public <T extends ServerData> Set<String> deleteFromRedis(int sid, Class<T> clazz, String... loopKey) {
		// 删除对象
		ServerDataType dataType = ServerDataType.fromClass(clazz);
		String dataTypeKey = ServerRedisKey.getDataTypeKey(sid, dataType, loopKey);
		Set<String> keys = serverDataTypeSetRedis.members(dataTypeKey);
		serverDataRedis.delete(keys);
		serverDataTypeSetRedis.delete(dataTypeKey);
		return keys;
	}

	/**
	 * 批量删除数据
	 * 
	 * @param sid
	 * @param dataIds
	 * @param clazz
	 */
	public <T extends ServerData> Set<String> deleteFromRedis(int sid, List<Long> dataIds, Class<T> clazz, String... loopKey) {
		if (null == dataIds || dataIds.isEmpty()) {
			return new HashSet<>();
		}
		// 删除对象
		ServerDataType dataType = ServerDataType.fromClass(clazz);
		String dataTypeKey = ServerRedisKey.getDataTypeKey(sid, dataType, loopKey);
		Set<String> keys = new HashSet<>();
		for (Long dataId : dataIds) {
			keys.add(ServerRedisKey.getServerDataKey(sid, dataType, dataId));
		}
		serverDataRedis.delete(keys);
		serverDataTypeSetRedis.remove(dataTypeKey, keys);
		return keys;
	}

	public void deleteFromRedis(ServerData data) {
		String typeKey = ServerRedisKey.getDataTypeKey(data);
		String key = ServerRedisKey.getServerDataKey(data);
		serverDataRedis.delete(key);
		serverDataTypeSetRedis.remove(typeKey, key);
	}

	/**
	 * 判断key是否存在
	 * 
	 * @param key 键
	 * @return true 存在 false不存在
	 */
	public boolean exists(String key) {
		return serverDataRedis.exists(key);
	}

	/**
	 * 从redis中获取对象
	 * 
	 * @param sid
	 * @param clazz
	 * @param dataId
	 * @return
	 */
	@Nullable
	public <T extends ServerData> T fromRedis(int sid, Class<T> clazz, Long dataId) {
		ServerDataType dataType = ServerDataType.fromClass(clazz);
		if (!hasLoadFromDb(sid, dataType)) {
			dbLoadToRedis(sid, dataType.getEntityClass());
		}
		String key = ServerRedisKey.getServerDataKey(sid, dataType, dataId);
		ServerData data = serverDataRedis.get(key);
		if (null != data) {
			return clazz.cast(data);
		}
		return null;
	}

	/**
	 * 获取区服某一类型数据。如果没有符合的数据，返回一个empty的List。
	 * 
	 * @param sid
	 * @param clazz
	 * @param loopKey
	 * @return
	 */
	@NonNull
	public <T extends ServerData> List<T> fromRedis(int sid, Class<T> clazz, String... loopKey) {
		ServerDataType dataType = ServerDataType.fromClass(clazz);
		if (!hasLoadFromDb(sid, dataType)) {
			dbLoadToRedis(sid, dataType.getEntityClass());
		}
		String typeKey = ServerRedisKey.getDataTypeKey(sid, dataType, loopKey);
		Set<String> keys = serverDataTypeSetRedis.members(typeKey);
		List<ServerData> datas = serverDataRedis.getBatch(keys);
		ArrayList<T> result = new ArrayList<T>();
		if (datas != null && !datas.isEmpty()) {
			datas.forEach(data -> result.add(clazz.cast(data)));
		}
		return result;
	}

	/**
	 * 从redis中获取对象
	 * 
	 * @param sid: 区服ID
	 * @param dataType:
	 * @param dataId
	 * @return
	 */
	@Nullable
	public ServerData fromRedis(int sid, ServerDataType dataType, Long dataId) {
		if (!hasLoadFromDb(sid, dataType)) {
			dbLoadToRedis(sid, dataType.getEntityClass());
		}

		String key = ServerRedisKey.getServerDataKey(sid, dataType, dataId);
		return serverDataRedis.get(key);
	}

	// 已经从mysql载入过
	private boolean hasLoadFromDb(int sid, ServerDataType dataType) {
		String loadKey = DB_LOAD_KEY + ServerRedisKey.SPLIT + dataType.getRedisKey();
		String dataKey = ServerRedisKey.getRunTimeVarKey(sid, loadKey);
		return statusRedis.exists(dataKey);
	}

	private void setLoadStatus(int sid, ServerDataType dataType) {
		String loadKey = DB_LOAD_KEY + ServerRedisKey.SPLIT + dataType.getRedisKey();
		String dataKey = ServerRedisKey.getRunTimeVarKey(sid, loadKey);
		statusRedis.set(dataKey, "1");
	}

	/**
	 * 移除区服加载状态
	 * 
	 * @param sid
	 */
	public void deleteLoadStatus(int sid) {
		String loadKey = DB_LOAD_KEY + ServerRedisKey.SPLIT + "*";
		String dataKey = ServerRedisKey.getRunTimeVarKey(sid, loadKey);
		statusRedis.deleteBlear(dataKey);
	}

	/**
	 * 批量添加或者更新
	 * 
	 * @param dataList
	 */
	public <T extends ServerData> void toRedis(List<T> dataList) {
		batchRedis.batchSetServerData(dataList);
	}

	/**
	 * 将区服数据保存到redis
	 * 
	 * @param data
	 */
	public void toRedis(ServerData data) {
		String typeKey = ServerRedisKey.getDataTypeKey(data);
		String key = ServerRedisKey.getServerDataKey(data);
		serverDataRedis.set(key, data);
		serverDataTypeSetRedis.add(typeKey, key);
	}
}
