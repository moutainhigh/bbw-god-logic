package com.bbw.god.game.data;

import java.util.List;

/**
 * 全服数据
 * 
 * @author suhq
 * @date 2019年4月9日 下午2:21:42
 */
public interface GameDataService {

	/**
	 * 添加全服数据
	 * 
	 * @param data
	 */
	void addGameData(GameData data);

	/**
	 * 添加全服数据
	 * 
	 * @param dataList
	 */
	<T extends GameData> void addGameDatas(List<T> dataList);

	/**
	 * 更新全服数据
	 * 
	 * @param data
	 */
	void updateGameData(GameData data);

	/**
	 * 批量更新
	 *
	 * @param dataList
	 */
	<T extends GameData> void updateGameDatas(List<T> dataList);

	/**
	 * 删除拥有项
	 */
	void deleteGameData(GameData data);

	<T extends GameData> void deleteGameDatas(List<Long> dataIds, Class<T> clazz);

	<T extends GameData> T getGameData(long dataId, Class<T> clazz);

	/**
	 * 返回全服服的某一类的所有数据，如果没有符合的数据，返回一个empty的List。
	 * 
	 * @return
	 */
	<T extends GameData> List<T> getGameDatas(Class<T> clazz);

}
