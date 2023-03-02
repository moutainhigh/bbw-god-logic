package com.bbw.god.login.repairdata;

import com.bbw.god.activity.ActivityService;
import com.bbw.god.gameuser.GameUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author suchaobin
 * @description 检测加入英雄回归活动
 * @date 2020/7/7 15:01
 **/
@Service
public class RepairHeroBackService implements BaseRepairDataService {
	@Autowired
	private ActivityService activityService;

	@Override
	public void repair(GameUser gu, Date lastLoginDate) {
		this.activityService.joinHeroBackActivity(gu, lastLoginDate);
	}
}
