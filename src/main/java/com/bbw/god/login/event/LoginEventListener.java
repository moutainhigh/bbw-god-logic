package com.bbw.god.login.event;

import com.bbw.common.HttpClientUtil;
import com.bbw.god.exchange.exchangecode.ExchangeCodeLogic;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.game.config.CfgGame;
import com.bbw.god.login.LoginPlayer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;

/**
 * 登录事件监听器
 *
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-04-03 18:04
 */
@Slf4j
@Async
@Component
public class LoginEventListener {
	@Autowired
	private ExchangeCodeLogic exchangeCodeLogic;

	@EventListener
	public void login(LoginEvent event) {
		LoginPlayer player = event.getLoginPlayer();
		try {
			exchangeCodeLogic.dispatchWechatWeeklyPackNewCodeServer(player.getAccount(), player.getServerId(), player.getUid());

			// 报告登录明细
			CfgGame gameConfig = Cfg.I.getUniqueConfig(CfgGame.class);
			String baseUrl = gameConfig.getUacBaseUrl();
			String check_url = baseUrl + "account!reportLoginDetail?playerAccount=%s&guId=%s&clientIp=%s&serverid=%s&channelId=%s&device=%s&oaid=%s";
			String url = String.format(check_url, URLEncoder.encode(player.getAccount(), "utf-8"), player.getUid(), player.getClientIp(), player.getServerId(), player.getChannelId(), URLEncoder.encode(player.getDeviceId(), "utf-8"), URLEncoder.encode(player.getOaid(), "utf-8"));
			log.info(url);
			HttpClientUtil.doGet(url);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
	}

}
