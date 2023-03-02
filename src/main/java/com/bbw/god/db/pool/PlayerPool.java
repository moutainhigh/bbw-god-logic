package com.bbw.god.db.pool;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bbw.common.SpringContextUtil;
import com.bbw.db.redis.RedisSetUtil;
import com.bbw.god.db.entity.CfgServerEntity;
import com.bbw.god.db.entity.InsUserEntity;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.game.data.redis.RedisKeyConst;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.redis.GameUserRedisUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * GameUser对象缓冲池
 * </pre>
 * 
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2018-11-13 23:15
 */
@Slf4j
@Service
public class PlayerPool extends BasePool {
	@Autowired
	private RedisSetUtil<Long> userPoolKeySet;// 需要更新到数据库到 gameuser缓存池
	@Autowired
	private GameUserRedisUtil userRedis;
	// 业务
	private static final String BUSINESS_KEY = BASE_KEY + RedisKeyConst.SPLIT + "gameuser";
	// 保存的数据类型
	private static final String DATA_TYPE = "GameUser";
	// 用户key
	private static final String USER_UPDATE = RedisKeyConst.SPLIT + "update" + RedisKeyConst.SPLIT;

	/**
	 * 添加到数据池，等待【更新】到数据库
	 * 
	 * @param res
	 */
	public void addToUpdatePool(Long uid) {
		userPoolKeySet.add(getCurrentUpdatePoolKey(), uid);
	}

	/**
	 * 获取当前缓冲池
	 * 
	 * @return
	 */
	private String getCurrentUpdatePoolKey() {
		return this.getBaseKey() + USER_UPDATE + getCurrentPoolSeq();
	}

	/**
	 * 获取上一个缓冲池key
	 * 
	 * @return
	 */
	private String getLastUpdatePoolKey() {
		return this.getBaseKey() + USER_UPDATE + getLastPoolSeq();
	}

	@Override
	protected void save() {
		Long b = System.currentTimeMillis();
		String lastUpdatePoolKey = getLastUpdatePoolKey();
		// 保存GameUser对象
		DBResult<Long> playerUpdate = dbUpdatePlayer(lastUpdatePoolKey);
		Long e = System.currentTimeMillis();
		log.debug("保存成功[{}]条，失败[{}]条用户对象到数据库。耗时:{}" ,playerUpdate.getSuccessSize(),playerUpdate.getFailure().size(), (e - b));
		b = e;
		// 转移保存失败数据到当前数据池
		if (!playerUpdate.getFailure().isEmpty()) {
			Long[] array = new Long[playerUpdate.getFailure().size()];
			userPoolKeySet.add(this.getCurrentUpdatePoolKey(), playerUpdate.getFailure().toArray(array));
			log.error("用户对象gameuser更新失败：" + playerUpdate.getFailure().toString());
		}
		userPoolKeySet.delete(lastUpdatePoolKey);
		e = System.currentTimeMillis();
		log.debug("清除redis缓冲池保存成功的GameUser。耗时：{}", (e - b));
	}

	// TODO:可能存在性能问题
	private DBResult<Long> dbUpdatePlayer(String userUpdatePoolKey) {

		Set<Long> updateKeys = userPoolKeySet.members(userUpdatePoolKey);
		if (null == updateKeys || updateKeys.isEmpty()) {
			return new DBResult<Long>();
		}
		return dbUpdatePlayer(updateKeys);
	}

	// TODO:可能存在性能问题
	public DBResult<Long> dbUpdatePlayer(Set<Long> uids) {
		// 从redis获取用户对象
		ArrayList<InsUserEntity> userList = new ArrayList<>();
		for (Long uid : uids) {
			try {
				GameUser user = userRedis.fromRedis(uid, false);
				if (null != user) {
					user.getSetting();
					user.getStatus();
					user.getRoleInfo();
					userList.add(InsUserEntity.fromGameUser(user));
				}
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				continue;
			}
		}
		// 分拣到各个服务器
		Map<Integer, List<InsUserEntity>> serverEntity = userList.stream().collect(Collectors.groupingBy(InsUserEntity::getSid));
		DBResult<Long> result = new DBResult<>();
		// 保存
		for (Integer serverId : serverEntity.keySet()) {
			CfgServerEntity server = Cfg.I.get(serverId, CfgServerEntity.class);
			if (null == server) {
				continue;
			}
			PlayerDataDAO surop = SpringContextUtil.getBean(PlayerDataDAO.class, serverId);
			List<InsUserEntity> list = serverEntity.get(serverId);
			List<InsUserEntity> failList = surop.dbUpdateUserBatch(list);
			if (!failList.isEmpty()) {
				for (InsUserEntity dataEntity : failList) {
					result.failureAdd(dataEntity.getUid());
				}
				result.setSuccessSize(list.size() - failList.size());
			} else {
				result.setSuccessSize(list.size());
			}
		}
		return result;
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
