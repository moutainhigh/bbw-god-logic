package com.bbw.god.game.data;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bbw.god.db.entity.InsGameDayDataEntity;
import com.bbw.god.db.service.InsGameDayDataService;
import com.bbw.god.game.data.redis.GameDayDataRedisUtil;

/**
 * 全服数据服务
 * 
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-03-14 15:13
 */
@Service("gameDayDataService")
public class GameDayDataServiceImpl implements GameDayDataService {
	@Autowired
	private GameDayDataRedisUtil dataRedis;
	@Autowired
	private InsGameDayDataService dbDataService;

	@Override
	public <T extends GameDayData> void addGameDatas(List<T> dataList) {
		if (null == dataList || dataList.isEmpty()) {
			return;
		}
		dataRedis.toRedis(dataList);
		List<InsGameDayDataEntity> entityList = dataList.stream().map(data -> InsGameDayDataEntity.fromGameDayData(data)).collect(Collectors.toList());
		dbDataService.insertBatch(entityList);
	}

	@Override
	public void addGameData(GameDayData data) {
		dataRedis.toRedis(data);
		dbDataService.insert(InsGameDayDataEntity.fromGameDayData(data));
	}

	@Override
	public void updateGameData(GameDayData data) {
		dataRedis.toRedis(data);
		dbDataService.updateById(InsGameDayDataEntity.fromGameDayData(data));
	}

	@Override
	public void deleteGameData(GameDayData data) {
		dataRedis.deleteGameData(data);
		dbDataService.deleteById(data.getId());
	}

	@Override
	public <T extends GameDayData> void deleteGameDatas(List<Long> dataIds, Class<T> clazz) {
		dataRedis.deleteGameDatas(dataIds, clazz);
		dbDataService.deleteBatchIds(dataIds);
	}

	@Override
	public <T extends GameDayData> T getGameData(long dataId, Class<T> clazz) {
		return dataRedis.fromRedis(dataId, clazz);
	}

	@Override
	public <T extends GameDayData> List<T> getGameData(Class<T> clazz) {
		return dataRedis.fromRedis(clazz);
	}

}
