package com.bbw.god.activity.processor;

import com.bbw.common.DateUtil;
import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.UserActivity;
import com.bbw.god.db.entity.CfgActivityEntity;
import com.bbw.god.detail.LogUtil;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardStatus;
import com.bbw.god.gameuser.GameUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/** 
* @author 作者 ：lwb
* @version 创建时间：2020年1月9日 下午3:16:25 
* 类说明 
*/
public abstract class HeroBackLogic extends AbstractActivityProcessor{
	@Override
	public AwardStatus getUAStatus(GameUser gu, IActivity a, UserActivity ua, CfgActivityEntity ca) {
        if (ua != null) {
			long jionSeconds = DateUtil.getSecondsBetween(ua.getDate(), new Date());
			long second10Days = 60 * 60 * 24 * 10;// 10天的秒数
			if (jionSeconds > second10Days) {
				// 有效期只有10天
				gameUserService.deleteItem(ua);
				LogUtil.logDeletedUserData("活动时间已过", ua);
				return AwardStatus.LOCK;
			}
			return AwardStatus.fromValue(ua.getStatus());
		}
        return AwardStatus.LOCK;
	}

	@Override
	protected long getRemainTime(long uid, int sid, IActivity a) {
		List<UserActivity> uas = this.activityService.getUserActivities(uid, a.gainId(), activityTypeList.get(0));
		if (uas!=null && !uas.isEmpty()) {
			UserActivity uActivity = uas.get(0);
			Date end = DateUtil.addSimpleDays(uActivity.getDate(), 10);
			return DateUtil.millisecondsInterval(end, new Date());
		}
		return 0;
	}

	@Override
	public List<Award> getAwardsToShow(GameUser gu, UserActivity ua, CfgActivityEntity ca) {
		if (ua==null) {
			return new ArrayList<Award>();
		}
		List<Award> awards = this.awardService.parseAwardJson(ca.getAwards(),Award.class);
		if (awards == null || awards.isEmpty()) {
			return new ArrayList<Award>();
		}
		List<Award> subAwards = new ArrayList<Award>();
		if (ua.getAwardIndex() != null && ua.getAwardIndex() > 0 && ua.getAwardIndex() <= awards.size()) {
			subAwards.add(awards.get(ua.getAwardIndex() - 1));
		} else {
			subAwards = awards;
		}
		return subAwards;
	}
}
