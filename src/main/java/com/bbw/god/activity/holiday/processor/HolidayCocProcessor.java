package com.bbw.god.activity.holiday.processor;

import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.activity.processor.AbstractActivityProcessor;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * @author lsb
 * 5、物资告急
 * （1）活动时间：11月18日00:00:00—11月23日23:59:59
 * 活动期间，由于凛冬将至，多处城池急需储备特产，所有商会任务处于加急状态。且完成任意难度商会任务都将获得凛冬点
 * @date 2020-08-27 14:00
 **/
@Service
public class HolidayCocProcessor extends AbstractActivityProcessor {

    public HolidayCocProcessor() {
        this.activityTypeList = Arrays.asList(ActivityEnum.HOLIDAY_COC);
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
		return false;
	}

	/**
	 * 是否在活动期间
	 *
	 * @param sid
	 * @return
	 */
	public boolean opened(int sid) {
		ActivityEnum activityEnum = ActivityEnum.fromValue(ActivityEnum.HOLIDAY_COC.getValue());
		IActivity a = this.activityService.getActivity(sid, activityEnum);
		if (a == null) {
			return false;
		}
		return a.ifTimeValid();
	}
}
