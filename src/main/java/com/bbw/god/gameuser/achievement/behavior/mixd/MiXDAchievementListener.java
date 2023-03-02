package com.bbw.god.gameuser.achievement.behavior.mixd;

import com.bbw.god.city.mixd.nightmare.NightmareMiXianPosEnum;
import com.bbw.god.fight.FightTypeEnum;
import com.bbw.god.game.combat.event.CombatFightWinEvent;
import com.bbw.god.game.combat.event.EPFightEnd;
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
 * @description 梦魇迷仙洞行为成就监听器
 * @date 2021/4/13 14:04
 **/
@Component
@Async
@Slf4j
public class MiXDAchievementListener {
	@Autowired
	private AchievementServiceFactory achievementServiceFactory;
	@Autowired
	private GameUserService gameUserService;

	/**
	 * 梦魇迷仙洞战斗事件
	 * @param event
	 */
	@Order(1000)
	@EventListener
	public void fightWin(CombatFightWinEvent event) {
		try {
			EPFightEnd ep = (EPFightEnd) event.getSource();
			Long uid = ep.getGuId();
			if(ep.getFightType() == FightTypeEnum.MXD && ep.getFightSubmit().getOpponentId()< 0){
				if(ep.getFightSubmit().getOpponentName().equals(NightmareMiXianPosEnum.XUN_SHI_JIANG_HUAN.getMemo())){
					BaseAchievementService service15330 = achievementServiceFactory.getById(15330);
					UserAchievementInfo info = gameUserService.getSingleItem(uid, UserAchievementInfo.class);
					service15330.achieve(uid, 1, info, ep.getRd());
				}else if(ep.getFightSubmit().getOpponentName().equals(NightmareMiXianPosEnum.XUN_SHI_ZHU_LONG.getMemo())){
					BaseAchievementService service15330 = achievementServiceFactory.getById(15340);
					UserAchievementInfo info = gameUserService.getSingleItem(uid, UserAchievementInfo.class);
					service15330.achieve(uid, 1, info, ep.getRd());
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
}
