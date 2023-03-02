package com.bbw.god.game.data;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bbw.god.db.entity.InsGameDataEntity;
import com.bbw.god.db.service.InsGameDataService;
import com.bbw.god.game.data.redis.GameDataRedisUtil;

@Service
public class GameDataServiceImpl implements GameDataService {
	@Autowired
	private GameDataRedisUtil dataRedis;
	@Autowired
	private InsGameDataService dbDataService;

	@Override
	public <T extends GameData> void addGameDatas(List<T> dataList) {
		if (null == dataList || dataList.isEmpty()) {
			return;
		}
		dataRedis.toRedis(dataList);
		List<InsGameDataEntity> entityList = dataList.stream().map(data -> InsGameDataEntity.fromGameData(data))
				.collect(Collectors.toList());
		dbDataService.insertBatch(entityList);
	}

	@Override
	public void addGameData(GameData data) {
		dataRedis.toRedis(data);
		dbDataService.insert(InsGameDataEntity.fromGameData(data));
	}

	@Override
	public void updateGameData(GameData data) {
		dataRedis.toRedis(data);
		dbDataService.updateById(InsGameDataEntity.fromGameData(data));
	}

	/**
	 * 批量更新
	 *
	 * @param dataList
	 */
	@Override
	public <T extends GameData> void updateGameDatas(List<T> dataList) {
		dataRedis.toRedis(dataList);
		List<InsGameDataEntity> list = dataList.stream().map(InsGameDataEntity::fromGameData).collect(Collectors.toList());
		dbDataService.updateBatchById(list);
	}

	@Override
	public void deleteGameData(GameData data) {
		dataRedis.deleteGameData(data);
		dbDataService.deleteById(data.getId());
	}

	@Override
	public <T extends GameData> void deleteGameDatas(List<Long> dataIds, Class<T> clazz) {
		dataRedis.deleteGameDatas(dataIds, clazz);
		dbDataService.deleteBatchIds(dataIds);
	}

	@Override
	public <T extends GameData> T getGameData(long dataId, Class<T> clazz) {
		return dataRedis.fromRedis(dataId, clazz);
	}

	@Override
	public <T extends GameData> List<T> getGameDatas(Class<T> clazz) {
		List<T> objs = dataRedis.fromRedis(clazz);
		objs.sort(Comparator.comparing(GameData::getId));
		return objs;
	}

}
