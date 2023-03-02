package com.bbw.god.activity.processor;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;

import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.UserActivity;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.gameuser.GameUser;

/** 
* @author 作者 ：lwb
* @version 创建时间：2020年1月7日 上午10:32:28 
* 类说明 
*/
@Service
public class HeroBackSignProcessor extends HeroBackLogic{
	public HeroBackSignProcessor() {
		this.activityTypeList = Arrays.asList(ActivityEnum.HERO_BACK_SIGIN);
	}
	
	public int getSignDays(GameUser gu) {
		IActivity a = this.activityService.getActivity(gu.getServerId(), ActivityEnum.HERO_BACK_SIGIN);
		if (a == null) {
			return 0;
		}
		List<UserActivity> uas = this.activityService.getUserActivities(gu.getId(), a.gainId(),
				ActivityEnum.HERO_BACK_SIGIN);
		int totalProgress = this.getTotalProgress(uas);
		return totalProgress;
	}
}
