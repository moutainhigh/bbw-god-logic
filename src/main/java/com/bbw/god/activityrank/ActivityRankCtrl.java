package com.bbw.god.activityrank;

import com.bbw.god.controller.AbstractController;
import com.bbw.god.game.CR;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 冲榜相关接口入口
 * 
 * @author suhq
 * @date 2019年3月2日 下午4:19:19
 */
@RestController
public class ActivityRankCtrl extends AbstractController {
	@Autowired
	private ActivityRankLogic activityRankLogic;

	/**
	 * 获得冲榜列表
	 *
	 * @return
	 */
	@GetMapping(CR.Activity.GET_RANK_ACTIVITIES)
	public RDActivityRankList getRankActivities() {
		return activityRankLogic.getRankActivities(getUserId());
	}

	/**
	 * 获得冲榜奖励列表，page和limit不传的时候默认返回排名在[1,ActivityConfig.numRankersToShow]范围内的
	 *
	 * @see com.bbw.god.activity.config.ActivityConfig
	 * @param type 榜单类型
	 * @param page 排名当前页数
	 * @param limit 排名每页存放数
	 * @return
	 */
	@GetMapping(CR.Activity.GET_RANK_AWARDS)
	public RDActivityRankerAwardList getRankAwards(int type, Integer page, Integer limit, Boolean isToday) {
		if (page == null) {
			page = 1;
		}
		if (isToday == null) {
			isToday = true;
		}
		return activityRankLogic.getRankerAwards(getUserId(), type, page, limit, isToday);
	}

}
