package com.bbw.god.activity.processor;

import com.bbw.common.ListUtil;
import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.task.TaskStatusEnum;
import com.bbw.god.gameuser.task.biggodplan.BigGodPlanTaskService;
import com.bbw.god.gameuser.task.biggodplan.UserBigGodPlanTask;
import com.bbw.god.rd.RDSuccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 大仙计划
 *
 * @author: huanghb
 * @date: 2022/2/14 18:18
 */
@Service
public class BigGodPlanProcessor extends AbstractActivityProcessor {
	@Autowired
	private BigGodPlanTaskService bigGodPlanTaskService;

	public BigGodPlanProcessor() {
		this.activityTypeList = Arrays.asList(ActivityEnum.BIG_GOD_PLAN);
	}

	@Override
	public RDSuccess getActivities(long uid, int activityType) {
		return super.getActivities(uid, ActivityEnum.BIG_GOD_PLAN.getValue());
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
	 * 获得可领取奖励数量
	 * @param gu
	 * @param a
	 * @return
	 */
	/**
	 * 是否在活动期间
	 *
	 * @param sid
	 * @return
	 */
	public boolean isOpened(int sid) {
		ActivityEnum activityEnum = ActivityEnum.fromValue(ActivityEnum.COMBINED_SERVICE_DISCOUNT.getValue());
		IActivity a = this.activityService.getActivity(sid, activityEnum);
		return a != null;
	}

	@Override
	public int getAbleAwardedNum(GameUser gu, IActivity a) {
		//获得玩家任务信息
		List<UserBigGodPlanTask> userBigGodPlanTasks = bigGodPlanTaskService.getbigGodPlanTasks(gu.getId());
		//任务信息为空
		if (ListUtil.isEmpty(userBigGodPlanTasks)) {
			return 0;
		}
		//活动开启天数
		int activityOpenDays = bigGodPlanTaskService.getOpenDays(gu.getId());
		//已开放的任务
		userBigGodPlanTasks = userBigGodPlanTasks.stream().filter(tmp -> activityOpenDays >= tmp.getDays()).collect(Collectors.toList());
		return (int) userBigGodPlanTasks.stream().filter(tmp -> tmp.getStatus() == TaskStatusEnum.ACCOMPLISHED.getValue()).count();
	}
	
	@Override
	protected long getRemainTime(long uid, int sid, IActivity a) {
		if (null != a.gainEnd()) {
			return a.gainEnd().getTime() - System.currentTimeMillis();
		}
		return NO_TIME;
	}
}
