package com.bbw.god.activity.holiday.processor.holidayJiuFZJ;

import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.activity.config.ActivityTool;
import com.bbw.god.activity.processor.AbstractActivityProcessor;
import com.bbw.god.db.entity.CfgActivityEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * 酒逢知己活动
 *
 * @author: huanghb
 * @date: 2023/2/10 14:58
 */
@Service
public class HolidayJiuFZJProcessor extends AbstractActivityProcessor {
	@Autowired
	JiuFZJRedisLockService jiuFZJRedisLockService;

	public HolidayJiuFZJProcessor() {
		this.activityTypeList = Arrays.asList(ActivityEnum.WINE_MEETS_A_BOSOM_FRIEND);
	}

	@Override
	protected long getRemainTime(long uid, int sid, IActivity a) {
		if (a.gainEnd() != null) {
			return a.gainEnd().getTime() - System.currentTimeMillis();
		}
		return NO_TIME;
	}

	/**
	 * 是否在ui中展示
	 *
	 * @return
	 */
	@Override
	public boolean isShowInUi(long uid) {
		return true;
	}

	/**
	 * 是否在活动期间
	 *
	 * @param sid
	 * @return
	 */
	public boolean opened(int sid) {
		ActivityEnum activityEnum = ActivityEnum.fromValue(ActivityEnum.WINE_MEETS_A_BOSOM_FRIEND.getValue());
		IActivity a = this.activityService.getActivity(sid, activityEnum);
		if (a == null) {
			return false;
		}
		return a.ifTimeValid();
	}


	/**
	 * 添加酒逢知己活动进度
	 *
	 * @param recipientId        收件人id
	 * @param carefreeBrewingNum 逍遥酿数量
	 * @param sid
	 */
	public void addActivityProgress(long recipientId, int sid, int carefreeBrewingNum) {
		//活动是否开启
		if (!opened(sid)) {
			return;
		}
		ActivityEnum activityEnum = ActivityEnum.WINE_MEETS_A_BOSOM_FRIEND;
		//活动实例
		IActivity a = this.activityService.getActivity(sid, activityEnum);
		//获取指定活动类型获取所有的活动
		List<CfgActivityEntity> cas = ActivityTool.getActivitiesByType(activityEnum);
		//添加就封至九活动进度
		jiuFZJRedisLockService.addActivityProgress(recipientId, carefreeBrewingNum, activityEnum, a, cas);
	}
}
