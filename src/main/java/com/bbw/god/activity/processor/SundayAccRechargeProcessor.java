package com.bbw.god.activity.processor;

import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.rd.RDSuccess;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * @author suchaobin
 * @description 多日累充处理器
 * @date 2020/6/29 11:48
 **/
@Service
public class SundayAccRechargeProcessor extends AbstractActivityProcessor {

	public SundayAccRechargeProcessor() {
		this.activityTypeList = Arrays.asList(ActivityEnum.SUNDAY_ACC);
	}

	@Override
	public RDSuccess getActivities(long uid, int activityType) {
		return super.getActivities(uid, ActivityEnum.SUNDAY_ACC.getValue());
	}

	@Override
	protected long getRemainTime(long uid, int sid, IActivity a) {
		IActivity a1 = this.activityService.getActivity(sid, ActivityEnum.SUNDAY_ACC);
		if (null != a1.gainEnd()) {
			return a1.gainEnd().getTime() - System.currentTimeMillis();
		}
		return NO_TIME;
	}
}
