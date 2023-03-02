package com.bbw.god.game.online;

import com.alibaba.fastjson.JSON;
import com.bbw.common.*;
import com.bbw.god.db.entity.CfgServerEntity;
import com.bbw.god.game.config.server.ServerTool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * tapdb https://www.tapdb.com/docs/zh_CN/sdk/iOS.html#aea457feb3d22f612ac7505de9b800e5
 *
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-05-08 21:52
 */
@Slf4j
@Service
public class TapdbReporter {
	private static final String ONLINE_REPORT_URI = "https://se.tapdb.net/tapdb/online";
	@Autowired
	private GameOnlineService gameOnlineService;

	/**
	 * 在线数据统计接口
	 */
	public void reportOnline() {
		Date fiveMinutesAgo = DateUtil.addMinutes(DateUtil.now(), -5);
		long timestamp = fiveMinutesAgo.getTime() / 1000;
		Set<Long> sids = gameOnlineService.getLastSidList();
		if (SetUtil.isEmpty(sids)) {
			log.error(JSONUtil.toJson(fiveMinutesAgo) + "没有任何区服有人在线！");
			return;
		}
		List<Long> sidList = sids.stream().collect(Collectors.toList());
		List<List> sidParts = ListUtil.partition(sidList, 100);
		for (List sidPart : sidParts) {
			try {
				doReport(sidPart, timestamp);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}
	}

	private void doReport(List<Long> sids, long timestamp) {
		GameOnline gameOnline = new GameOnline();
		for (Long sid : sids) {
			int count = gameOnlineService.getLastUidCount(sid.intValue());
			if (0 == count) {
				continue;
			}
			CfgServerEntity server = ServerTool.getServer(sid.intValue());
			GameOnline.ServerOnline serverOnline = new GameOnline.ServerOnline();
			serverOnline.setServer(server.getName());
			serverOnline.setOnline(count);
			serverOnline.setTimestamp(timestamp);
			//
			gameOnline.getOnlines().add(serverOnline);
		}
		String json = JSON.toJSONString(gameOnline);
		HttpClientUtil.doPostJson(ONLINE_REPORT_URI, json);
		log.info("\n报送tapdb在线用户\n批次区服：" + sids + "\n上报数据：\n" + json);
	}


}
