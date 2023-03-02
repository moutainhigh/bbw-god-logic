package com.bbw.god.gameuser.achievement.behavior.leader;

import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.achievement.AchievementServiceFactory;
import com.bbw.god.gameuser.achievement.BaseAchievementService;
import com.bbw.god.gameuser.achievement.UserAchievementInfo;
import com.bbw.god.gameuser.leadercard.event.EPLeaderCardAddLv;
import com.bbw.god.gameuser.leadercard.event.EPLeaderCardUpHv;
import com.bbw.god.gameuser.leadercard.event.LeaderCardAddLvEvent;
import com.bbw.god.gameuser.leadercard.event.LeaderCardUpHvEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;


/**
 * @author suchaobin
 * @description 法外分身行为成就监听器
 * @date 2021/4/13 14:04
 **/
@Component
@Async
@Slf4j
public class LeaderCardAchievementListener {
	@Autowired
	private AchievementServiceFactory achievementServiceFactory;
	@Autowired
	private GameUserService gameUserService;

	/**
	 * 法外分身升级
	 * @param event
	 */
	@Order(1000)
	@EventListener
	public void leaderCardAddLv(LeaderCardAddLvEvent event) {
		EPLeaderCardAddLv ep = event.getEP();
		Long uid = ep.getGuId();
		UserAchievementInfo info = gameUserService.getSingleItem(uid, UserAchievementInfo.class);
		if(ep.getLeaderCardLv() >= 10){
			//本固枝荣
			BaseAchievementService service15150 = achievementServiceFactory.getById(15150);
			service15150.achieve(uid, 10, info, ep.getRd());
		}
		if(ep.getLeaderCardLv() >= 20){
			//金丹换骨
			BaseAchievementService service15160 = achievementServiceFactory.getById(15160);
			service15160.achieve(uid, 20, info, ep.getRd());
		}
	}

	/**
	 * 法外分身升阶
	 * @param event
	 */
	@Order(1000)
	@EventListener
	public void leaderCardUpHv(LeaderCardUpHvEvent event) {
		EPLeaderCardUpHv ep = event.getEP();
		Long uid = ep.getGuId();
		UserAchievementInfo info = gameUserService.getSingleItem(uid, UserAchievementInfo.class);
		if(ep.getLeaderCardHv() >= 5){
			//循序渐进
			BaseAchievementService service15170 = achievementServiceFactory.getById(15170);
			service15170.achieve(uid, 5, info, ep.getRd());
		}
		if(ep.getLeaderCardHv() >= 10){
			//超凡脱俗
			BaseAchievementService service15180 = achievementServiceFactory.getById(15180);
			service15180.achieve(uid, 10, info, ep.getRd());
		}
	}
}
