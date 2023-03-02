package com.bbw.god.activityrank.server.gulevel;

import com.bbw.god.gameuser.GameUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.bbw.god.activityrank.ActivityRankEnum;
import com.bbw.god.activityrank.ActivityRankService;
import com.bbw.god.gameuser.level.EPGuLevelUp;
import com.bbw.god.gameuser.level.GuLevelUpEvent;

/**
 * 活动监听器
 * 
 * @author suhq
 * @date 2019年3月4日 下午4:15:07
 */
@Component
public class GuLevelRankListener {
	private ActivityRankEnum rankType = ActivityRankEnum.GU_LEVEL_RANK;
	@Autowired
	private ActivityRankService activityRankService;
	@Autowired
	private GameUserService gameUserService;

	/**
	 * 执行优先级低于升级处理
	 * 
	 * @param event
	 */
	@EventListener
	@Order(1000)
	public void levelUp(GuLevelUpEvent event) {
		EPGuLevelUp ep = event.getEP();
		long guId = ep.getGuId();
		int sId = gameUserService.getActiveSid(guId);
		int guLevel = ep.getNewLevel();
		// 等级榜
		activityRankService.setRankValue(guId, sId, guLevel, rankType);
	}
}
