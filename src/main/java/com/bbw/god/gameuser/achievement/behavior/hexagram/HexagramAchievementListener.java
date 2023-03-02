package com.bbw.god.gameuser.achievement.behavior.hexagram;

import com.bbw.god.city.miaoy.hexagram.HexagramAchievementEnum;
import com.bbw.god.city.miaoy.hexagram.event.EPHexagram;
import com.bbw.god.city.miaoy.hexagram.event.HexagramEvent;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.achievement.AchievementServiceFactory;
import com.bbw.god.gameuser.achievement.BaseAchievementService;
import com.bbw.god.gameuser.achievement.UserAchievementInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @author suchaobin
 * @description 文王庙64卦行为成就监听器
 * @date 2021/4/13 14:04
 **/
@Component
@Async
@Slf4j
public class HexagramAchievementListener {
	@Autowired
	private AchievementServiceFactory achievementServiceFactory;
	@Autowired
	private GameUserService gameUserService;

	/**
	 * 64卦事件
	 * @param event
	 */
	@Order(1000)
	@EventListener
	public void hexagram(HexagramEvent event) {
		EPHexagram ep = event.getEP();
		Long uid = ep.getGuId();
		if(ep.isNewHexagram()){
			HexagramAchievementEnum hexagram = HexagramAchievementEnum.fromValue(ep.getHexagramId());
			if(hexagram != null){
				int achievementId = hexagram.getAchievementId();
				BaseAchievementService service = achievementServiceFactory.getById(achievementId);
				UserAchievementInfo info = gameUserService.getSingleItem(uid, UserAchievementInfo.class);
				service.achieve(uid, 1, info, ep.getRd());
			}
		}
	}
}
