package com.bbw.god.game.data;

import java.util.List;

/**
 * 全服数据
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-03-14 15:13
 */
public interface GameDayDataService {

	/**
	 * 添加全服数据
	 * 
	 * @param data
	 */
	void addGameData(GameDayData data);

	/**
	 * 添加全服数据
	 * 
	 * @param dataList
	 */
	<T extends GameDayData> void addGameDatas(List<T> dataList);

	/**
	 * 更新全服数据
	 * 
	 * @param data
	 */
	void updateGameData(GameDayData data);

	/**
	 * 删除拥有项
	 */
	void deleteGameData(GameDayData data);

	<T extends GameDayData> void deleteGameDatas(List<Long> dataIds, Class<T> clazz);

	<T extends GameDayData> T getGameData(long dataId, Class<T> clazz);

	/**
	 * 返回全服服的某一类的所有数据，如果没有符合的数据，返回一个empty的List。
	 * 
	 * @return
	 */
	<T extends GameDayData> List<T> getGameData(Class<T> clazz);

}
