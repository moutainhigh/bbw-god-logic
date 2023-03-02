package com.bbw.god.game.online;

import com.bbw.god.login.LoginPlayer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 处理在线状态
 *
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-05-08 22:40
 */
@Slf4j
@Async
@Component
public class GameOnlineAsyncHandler {
	@Autowired
	private GameOnlineService service;

	public void addToOnline(LoginPlayer player) {
		try {
			service.addToOnline(player.getServerId(), player.getUid());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
}
