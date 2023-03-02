package com.bbw.god.activity.processor;

import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.activity.ActivityLogic;
import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.rd.RDSuccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author suchaobin
 * @description 多日累充处理器
 * @date 2020/6/29 11:48
 **/
@Service
public class MultiAccRechargeProcessor extends AbstractActivityProcessor {
	@Autowired
	private ActivityLogic activityLogic;

	public MultiAccRechargeProcessor() {
		this.activityTypeList = Arrays.asList(ActivityEnum.MULTI_DAY_ACC_R, ActivityEnum.MULTI_DAY_ACC_R2,
				ActivityEnum.MULTI_DAY_ACC_R3);
	}

	@Override
	public RDSuccess getActivities(long uid, int activityType) {
		Integer curActivityType = getCurActivityType(uid, activityType);
		return super.getActivities(uid, curActivityType);
	}

	/**
	 * 获取当前开启的活动
	 *
	 * @param uid
	 * @param activityType
	 * @return
	 */
	private Integer getCurActivityType(long uid, int activityType) {
		int sid = this.gameUserService.getActiveSid(uid);
		ActivityEnum activityEnum = ActivityEnum.fromValue(activityType);
		IActivity a = this.activityService.getActivity(sid, activityEnum);
		if (a != null) {
			return activityType;
		}
		List<ActivityEnum> activityEnums = this.activityTypeList.stream().filter(at -> at.getValue() != activityType)
				.collect(Collectors.toList());
		for (ActivityEnum ac : activityEnums) {
			a = this.activityService.getActivity(sid, ac);
			if (a != null) {
				return ac.getValue();
			}
		}
		throw new ExceptionForClientTip("activity.not.exist");
	}

	@Override
	protected long getRemainTime(long uid, int sid, IActivity a) {
		ActivityEnum activityEnum = activityLogic.getCurMultiAccR();
		IActivity a1 = this.activityService.getActivity(sid, activityEnum);
		if (null != a1.gainEnd()) {
			return a1.gainEnd().getTime() - System.currentTimeMillis();
		}
		return NO_TIME;
	}
}
