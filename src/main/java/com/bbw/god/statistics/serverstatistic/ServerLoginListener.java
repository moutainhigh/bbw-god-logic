package com.bbw.god.statistics.serverstatistic;

import com.bbw.common.DateUtil;
import com.bbw.god.login.LoginPlayer;
import com.bbw.god.login.event.LoginEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @author suchaobin
 * @description TODO
 * @date 2020/4/2 8:59
 */
@Component
@Async
public class ServerLoginListener {
	@Autowired
	private GodServerStatisticService serverStatisticService;


	@Order(2)
	@EventListener
	public void login(LoginEvent event) {
		LoginPlayer player = event.getLoginPlayer();
		Long uid = player.getUid();
		int serverId = player.getServerId();
		serverStatisticService.loginStatistic(uid, serverId, DateUtil.toDateTimeLong());
	}
}
