package com.bbw.god.db.pool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bbw.common.SpringContextUtil;
import com.bbw.common.StrUtil;
import com.bbw.db.redis.RedisValueUtil;
import com.bbw.god.db.entity.CfgServerEntity;
import com.bbw.god.db.entity.InsServerDataEntity;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.game.data.redis.RedisKeyConst;
import com.bbw.god.server.ServerData;
import com.bbw.god.server.ServerDataType;
import com.bbw.god.server.redis.ServerRedisKey;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**<pre>
 * 区服数据保存
 * </pre>
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2018-11-13 23:15
 * 
 */
@Slf4j
@Service
public class ServerDataPool extends DataPool {
	//业务
	private static final String BUSINESS_KEY = BASE_KEY + RedisKeyConst.SPLIT + "serverdata";
	//保存的数据类型
	private static final String DATA_TYPE = "ServerData";

	@Autowired
	private RedisValueUtil<ServerData> serverDataRedis;

	/**
	 * 
	 * @param poolKey
	 * @return
	 */
	private ArrayList<InsServerDataEntity> getEntityList(Set<String> poolKey) {
		List<ServerData> datas = serverDataRedis.getBatch(poolKey);
		if (null == datas || datas.isEmpty()) {
			log.error("无法获取到区服数据。" + poolKey);
			return new ArrayList<>();
		}
		ArrayList<InsServerDataEntity> resList = new ArrayList<>(datas.size());
		//		for (int i = 0; i < datas.size(); i++) {
		//			ServerData data = null;
		//			try {
		//				data = datas.get(i);
		//			} catch (Exception e) {
		//				continue;
		//			}
		//			if (null == data) {
		//				continue;
		//			}
		//			InsServerDataEntity resEntity = InsServerDataEntity.fromServerData(data);
		//			resList.add(resEntity);
		//		}
		for (ServerData data : datas) {
			if (null == data) {
				continue;
			}
			InsServerDataEntity resEntity = InsServerDataEntity.fromServerData(data);
			resList.add(resEntity);
		}
		return resList;
	}

	@Override
	protected DBResult<String> dbInsert(PoolKeys poolKeys) {
		//实际需要 插入 的数据是【插入】减去【删除】
		Set<String> insertKeys = dataPoolKeySet.difference(poolKeys.getInsertPoolKey(), poolKeys.getDeletePoolKey());
		if (null == insertKeys || insertKeys.isEmpty()) {
			return new DBResult<String>();
		}
		ArrayList<InsServerDataEntity> insertList = getEntityList(insertKeys);
		//HashMap<String, ArrayList<InsServerDataEntity>> serverEntity = sortByServer(insertList, insert_size);
		Map<Integer, List<InsServerDataEntity>> serverDatas = insertList.stream().collect(Collectors.groupingBy(InsServerDataEntity::getSid));

		DBResult<String> result = new DBResult<>();
		//保存
		for (Integer serverId : serverDatas.keySet()) {
			CfgServerEntity server = Cfg.I.get(serverId, CfgServerEntity.class);
			if (null == server) {
				continue;
			}
			ServerDataDAO sdd = SpringContextUtil.getBean(ServerDataDAO.class, serverId);
			try {
				List<InsServerDataEntity> failList = sdd.dbInsertServerDataBatch(serverDatas.get(serverId));
				if (failList.isEmpty()) {
					result.setSuccessSize(result.getSuccessSize() + serverDatas.size());
				} else {
					for (InsServerDataEntity dataEntity : failList) {
						result.failureAdd(ServerRedisKey.getServerDataKey(dataEntity));
					}
				}
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}
		return result;
	}

	@Override
	protected DBResult<String> dbUpdate(PoolKeys poolKeys) {
		//实际需要 更新 的数据是【更新】减去【删除】减去【插入】
		ArrayList<String> difference = new ArrayList<String>();
		difference.add(poolKeys.getInsertPoolKey());
		difference.add(poolKeys.getDeletePoolKey());
		Set<String> updateKeys = dataPoolKeySet.difference(poolKeys.getUpdatePoolKey(), difference);
		if (null == updateKeys || updateKeys.isEmpty()) {
			return new DBResult<String>();
		}
		return dbUpdate(updateKeys);
	}

	@Override
	protected DBResult<String> dbDelete(String deletePoolKey) {
		Set<String> deleteKeys = dataPoolKeySet.members(deletePoolKey);
		if (null == deleteKeys || deleteKeys.isEmpty()) {
			return new DBResult<String>();
		}
		//分拣到各个区服
		HashMap<Integer, ArrayList<Long>> serverSplit = new HashMap<>();
		HashMap<Integer, ArrayList<String>> serverSplitDeleteKeys = new HashMap<>();
		for (String deleteKey : deleteKeys) {
			ServerIdEntity idEntity = this.getIdEntity(deleteKey);
			if (!serverSplit.containsKey(idEntity.getServerId())) {
				ArrayList<Long> resList = new ArrayList<>();
				serverSplit.put(idEntity.getServerId(), resList);
				//
				ArrayList<String> resListDeleteKeys = new ArrayList<>();
				serverSplitDeleteKeys.put(idEntity.getServerId(), resListDeleteKeys);
			}
			ArrayList<Long> resList = serverSplit.get(idEntity.getServerId());
			resList.add(idEntity.getDataId());
		}
		DBResult<String> result = new DBResult<>();
		//按照区服删除
		for (Integer serverKey : serverSplit.keySet()) {
			CfgServerEntity server = Cfg.I.get(serverKey, CfgServerEntity.class);
			if (null == server) {
				continue;
			}
			ArrayList<Long> resKeyList = serverSplit.get(serverKey);
			ServerDataDAO sdd = SpringContextUtil.getBean(ServerDataDAO.class, serverKey);
			if (!sdd.dbDeleteServerDataBatch(resKeyList)) {
				result.failureAdd(serverSplitDeleteKeys.get(serverKey));
			} else {
				result.setSuccessSize(serverSplit.size());
			}
		}
		return result;
	}


	public DBResult<String> dbUpdate(Set<String> updateKeys) {
		ArrayList<InsServerDataEntity> updateList = getEntityList(updateKeys);
		Map<Integer, List<InsServerDataEntity>> serverDatas = updateList.stream().collect(Collectors.groupingBy(InsServerDataEntity::getSid));

		DBResult<String> result = new DBResult<>();
		//保存
		for (Integer serverId : serverDatas.keySet()) {
			CfgServerEntity server = Cfg.I.get(serverId, CfgServerEntity.class);
			if (null == server) {
				continue;
			}
			ServerDataDAO sdd = SpringContextUtil.getBean(ServerDataDAO.class, serverId);
			List<InsServerDataEntity> failList = sdd.dbUpdateServerDataBatch(serverDatas.get(serverId));
			if (failList.isEmpty()) {
				result.setSuccessSize(result.getSuccessSize() + serverDatas.size());
			} else {
				for (InsServerDataEntity dataEntity : failList) {
					result.failureAdd(ServerRedisKey.getServerDataKey(dataEntity));
				}
			}
		}
		return result;
	}

	public DBResult<String> dbSaveOrUpdate(Set<String> keys) {
		ArrayList<InsServerDataEntity> updateList = getEntityList(keys);
		Map<Integer, List<InsServerDataEntity>> serverDatas = updateList.stream().collect(Collectors.groupingBy(InsServerDataEntity::getSid));

		DBResult<String> result = new DBResult<>();
		//保存
		for (Integer serverId : serverDatas.keySet()) {
			CfgServerEntity server = Cfg.I.get(serverId, CfgServerEntity.class);
			if (null == server) {
				continue;
			}
			ServerDataDAO sdd = SpringContextUtil.getBean(ServerDataDAO.class, serverId);
			List<InsServerDataEntity> failList = sdd.dbInsertServerDataBatch(serverDatas.get(serverId));
			if (failList.isEmpty()) {
				result.setSuccessSize(result.getSuccessSize() + serverDatas.size());
			} else {
				for (InsServerDataEntity dataEntity : failList) {
					result.failureAdd(ServerRedisKey.getServerDataKey(dataEntity));
				}
			}
		}
		return result;
	}

	/**
	 * <pre>
	 * 根据rediskey获取信息。
	 * 解析以下类型的key:
	 * 类型1格式如  前缀:区服ID:数据类型
	 * 类型2格式如  前缀:区服ID:数据类型:资源ID
	 * </pre>
	 * 
	 * @param redisKey
	 * @return
	 */
	private ServerIdEntity getIdEntity(String redisKey) {
		ServerIdEntity id = new ServerIdEntity();
		String[] keyParts = redisKey.split(ServerRedisKey.SPLIT);
		id.setServerId(Integer.parseInt(keyParts[1]));
		id.setResType(ServerDataType.fromRedisKey(keyParts[2]));
		// 如果
		if (keyParts.length > 3) {
			// 如果第三部分是数字，判定为资源ID
			if (StrUtil.isDigit(keyParts[3])) {
				id.setDataId(Long.valueOf(keyParts[3]));
			}
		}
		return id;
	}

	/**
	 * 区服ID对象
	 * @author lsj@bamboowind.cn
	 * @version 1.0.0
	 * @date 2019-01-14 22:41
	 */
	@Data
	private class ServerIdEntity {
		private ServerDataType resType;//资源类型
		private Long dataId;//资源ID
		private Integer serverId;//服务器ID
	}

	@Override
	protected String getBaseKey() {
		return BUSINESS_KEY;
	}

	@Override
	protected String getDataType() {
		return DATA_TYPE;
	}
}
