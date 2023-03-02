package com.bbw.god.activity.processor;

import com.bbw.common.ListUtil;
import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.activity.rd.RDActivityList;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.task.TaskStatusEnum;
import com.bbw.god.gameuser.task.activitytask.UserActivityDailyTask;
import com.bbw.god.gameuser.task.activitytask.guesstask.GuessDailyTaskProcessor;
import com.bbw.god.gameuser.task.activitytask.guesstask.GuessDailyTaskService;
import com.bbw.god.rd.RDSuccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * 世界杯竞猜活动
 *
 * @author: huanghb
 * @date: 2022/11/14 9:38
 */
@Service
public class WorldCupGuessTaskProcessor extends AbstractActivityProcessor {
	@Autowired
	private GuessDailyTaskProcessor guessDailyTaskProcessor;

	@Autowired
	private GuessDailyTaskService guessDailyTaskService;


	public WorldCupGuessTaskProcessor() {
		this.activityTypeList = Arrays.asList(ActivityEnum.WORLD_CUP_ACTIVITIE_GUESS_TASK);
	}

	@Override
	public RDSuccess getActivities(long uid, int activityType) {
		RDActivityList rd = (RDActivityList) super.getActivities(uid, ActivityEnum.WORLD_CUP_ACTIVITIE_GUESS_TASK.getValue());
		rd.setRdTaskList(guessDailyTaskProcessor.getTasks(uid, 0));
		return rd;
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
		ActivityEnum activityEnum = ActivityEnum.fromValue(ActivityEnum.WORLD_CUP_ACTIVITIE_GUESS_TASK.getValue());
		IActivity a = this.activityService.getActivity(sid, activityEnum);
		return a != null;
	}

	@Override
	public int getAbleAwardedNum(GameUser gu, IActivity a) {
		//获得玩家任务信息
		List<UserActivityDailyTask> tasks = guessDailyTaskService.getDailyTasks(gu.getId());
		//任务信息为空
		if (ListUtil.isEmpty(tasks)) {
			return 0;
		}
		//已开放的任务
		return (int) tasks.stream().filter(tmp -> tmp.getStatus() == TaskStatusEnum.ACCOMPLISHED.getValue()).count();
	}

	@Override
	protected long getRemainTime(long uid, int sid, IActivity a) {
		if (null != a.gainEnd()) {
			return a.gainEnd().getTime() - System.currentTimeMillis();
		}
		return NO_TIME;
	}
}
