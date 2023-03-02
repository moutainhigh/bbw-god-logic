package com.bbw.god.server;

import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bbw.common.DateUtil;
import com.bbw.god.db.entity.CfgServerEntity;
import com.bbw.god.game.config.Cfg;

@Service("serverService")
public class ServerServiceImpl implements ServerService {
	@Autowired
	private ServerDataService serverDataService;

	@Override
	public void addServerData(int serverId, ServerData data) {
		data.setSid(serverId);
		serverDataService.addServerData(data);
	}

	@Override
	public void addServerDatas(int sid, List<ServerData> datas) {
		if (datas==null || datas.isEmpty()) {
			return;
		}
		datas.stream().forEach(p->p.setSid(sid));
		serverDataService.addServerData(datas);
	}

	@Override
	public void updateServerData(ServerData data) {
		serverDataService.updateServerData(data);
	}

	@Override
	public <T extends ServerData> void updateServerData(List<T> dataList) {
		serverDataService.updateServerData(dataList);
	}

	@Override
	public void deleteServerData(ServerData data) {
		serverDataService.deleteServerData(data);
	}

	@Override
	public <T extends ServerData> void deleteServerDatas(int sid, List<Long> dataIds, Class<T> clazz, String... loopKey) {
		serverDataService.deleteServerDatas(sid, dataIds, clazz, loopKey);
	}

	@Override
	public <T extends ServerData> void deleteServerDatas(int sid, Class<T> objClass, String... loopKey) {
		serverDataService.deleteServerDatas(sid, objClass, loopKey);
	}

	@Override
	public <T extends ServerData> T getServerData(int sId, long dataId, Class<T> clazz) {
		return serverDataService.getServerData(sId, clazz, dataId);
	}

	@Override
	public <T extends ServerData> List<T> getServerDatas(int sid, Class<T> clazz) {
		List<T> objs = serverDataService.getServerDatas(sid, clazz);
		objs.sort(Comparator.comparing(ServerData::getId));
		return objs;
	}

	@Override
	public int getOpenWeek(int sId, Date date) {
		CfgServerEntity server = Cfg.I.get(sId, CfgServerEntity.class);
		long days = DateUtil.until(server.getBeginTime(), date);
		long weeks = days / 7 + 1;
		return Long.valueOf(weeks).intValue();
	}
	
}
