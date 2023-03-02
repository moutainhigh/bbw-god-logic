package com.bbw.god.server;

import java.util.Date;
import java.util.List;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

/**
 * 区服服务
 * 
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2018-11-20 14:06
 */
public interface ServerService {

	/**
	 * 添加区服数据
	 * 
	 * @param serverId
	 * @param data
	 */
	void addServerData(int serverId, ServerData data);
	
	/**
	 * 添加区服数据
	 * 
	 * @param serverId
	 * @param data
	 */
	void addServerDatas(int serverId, List<ServerData> datas);

	/**
	 * 更新区服数据
	 * 
	 * @param data
	 */
	void updateServerData(ServerData data);

	public <T extends ServerData> void updateServerData(List<T> dataList);

	/**
	 * 删除拥有项
	 */
	void deleteServerData(ServerData data);

	<T extends ServerData> void deleteServerDatas(int sid, List<Long> ids, Class<T> objClass, String... loopKey);

	/**
	 * 删除某个区服的某一类的所有数据（一般用于初始回操作）
	 * 
	 * @param sid
	 * @param objClass
	 */
	<T extends ServerData> void deleteServerDatas(int sid, Class<T> objClass, String... loopKey);

	@Nullable
	<T extends ServerData> T getServerData(int sId, long serverDataId, Class<T> objClass);

	/**
	 * 返回区服的某一类的所有数据，如果没有数据返回一个空的列表。
	 * 
	 * @param sId
	 * @param clazz
	 * @return
	 */
	@NonNull
	<T extends ServerData> List<T> getServerDatas(int sId, Class<T> clazz);

	/**
	 * 获取开服周数
	 * 
	 * @param sId
	 * @param date
	 * @return
	 */
	int getOpenWeek(int sId, Date date);
}
