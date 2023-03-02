package com.bbw.god.gameuser.task.daily.heroback;

import com.bbw.common.DateUtil;
import com.bbw.common.StrUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.activity.ActivityService;
import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.UserActivity;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.activity.config.ActivityTool;
import com.bbw.god.activity.processor.HeroBackSignProcessor;
import com.bbw.god.db.entity.CfgActivityEntity;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.task.*;
import com.bbw.god.gameuser.task.CfgTaskConfig.CfgBox;
import com.bbw.god.gameuser.task.daily.RDDailyTask;
import com.bbw.god.gameuser.task.daily.UserDailyTask;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 作者 ：lwb
 * @version 创建时间：2020年1月8日 下午3:45:19
 *          英雄回归任务说明：当用户触发英雄回归活动的条件时生成所有英雄回归任务，然后通过登录的天数去控制任务是否可完成
 */
@Service("dailyTaskForHeroBack")
public class HeroBackTaskInfoProcessor extends AbstractTaskProcessor{
	@Autowired
	private ActivityService activityService;
	@Autowired
	private GameUserService gameUserService;
	@Autowired
	private HeroBackSignProcessor heroBackSignProcessor;

	public HeroBackTaskInfoProcessor() {
		this.taskTypes = Arrays.asList(TaskTypeEnum.HERO_BACK_BOX_TASK, TaskTypeEnum.HERO_BACK_TASK);
	}

	/**
	 * 获取任务列表
	 */
	@Override
	public RDTaskList getTasks(long guId, Integer days) {
		RDTaskList rd = new RDTaskList();
		List<UserDailyTask> tasks = getShowTasks(guId);
		if (tasks.isEmpty()) {
			return rd;
		}
		toRdDailyTasks(tasks, rd);
		int day = heroBackSignProcessor.getSignDays(gameUserService.getGameUser(guId));
		rd.setUnlock(day);
		return rd;
	}

	/**
	 * 领取奖励
	 */
	@Override
	public RDCommon gainTaskAward(long uid, int id,String awardIndex) {
		UserDailyTask udTask = getTodayTask(uid, id);
		if (udTask == null) {
			throw new ExceptionForClientTip("task.daily.already.updated");
		}
		if (udTask.getStatus() == TaskStatusEnum.DOING.getValue()) {
			throw new ExceptionForClientTip("task.not.accomplish");
		}
		if (udTask.getStatus() == TaskStatusEnum.AWARDED.getValue()) {
			throw new ExceptionForClientTip("task.already.award");
		}
        int boxIdBegin=TaskTool.getDailyBoxIdBeginByTaskId(id);
        RDCommon rd = new RDCommon();
        if (boxIdBegin<id) {
			//宝箱
        	CfgBox box=TaskTool.getDailyTaskCfgBox(id);
			if (udTask.getAwardIndex() != null && udTask.getAwardIndex() > 0
					&& udTask.getAwardIndex() <= box.getAwards().size()) {
				// 有预选的
				this.awardService.fetchAward(uid, Arrays.asList(box.getAwards().get(udTask.getAwardIndex() - 1)),
						WayEnum.OPEN_HERO_TASK_BOX, "通过英雄回归巅峰值宝箱获得", rd);
			} else if (StrUtil.isNotNull(awardIndex)) {
				int award = Integer.parseInt(awardIndex);
				if (award <= 0 || award > box.getAwards().size()) {
					award = 1;
				}
				this.awardService.fetchAward(uid, Arrays.asList(box.getAwards().get(award - 1)),
						WayEnum.OPEN_HERO_TASK_BOX,
						"通过英雄回归巅峰值宝箱获得", rd);
				udTask.setAwardIndex(award);
			} else {
				this.awardService.fetchAward(uid, box.getAwards(), WayEnum.OPEN_HERO_TASK_BOX, "通过英雄回归巅峰值宝箱获得", rd);
			}
		}else {
			//任务
			 CfgTaskEntity task = TaskTool.getDailyTask(id);
			 this.awardService.fetchAward(uid, task.getAwards(), WayEnum.OPEN_HERO_TASK_BOX, "通过英雄回归巅峰值宝箱获得", rd);
		}
	    udTask.setStatus(TaskStatusEnum.AWARDED.getValue());
	    this.gameUserService.updateItem(udTask);
		return rd;
	}
	
	/**
	 * 获取今日回归任务
	 * @param guId
	 * @return
	 */
	public List<UserDailyTask> getShowTasks(long guId) {
		List<UserDailyTask> dailyTasks = gameUserService.getMultiItems(guId, UserDailyTask.class);
		List<UserDailyTask> todayTasks = dailyTasks.stream().filter(udt -> udt.heroBackValidTask())
					.collect(Collectors.toList());
		if (todayTasks.isEmpty() && needCreate(guId)) {
			return createTasks(guId);
		}
		return todayTasks;
	}

	/**
	 * 获取今日回归任务
	 * 
	 * @param guId
	 * @return
	 */
	public UserDailyTask getTodayTask(long guId, int taskId) {
		List<UserDailyTask> tasks = getShowTasks(guId);
		if (null != tasks) {
			for (UserDailyTask yTask : tasks) {
				if (yTask.getBaseId().intValue() == taskId) {
					return yTask;
				}
			}
		}
		return null;
	}
	/**
	 * 创建回归任务
	 * @param guId
	 * @return
	 */
	private List<UserDailyTask> createTasks(long guId) {
		GameUser gu = gameUserService.getGameUser(guId);
		List<CfgTaskEntity> dailyTasks = TaskTool.getTasksByTaskGroupEnum(TaskGroupEnum.HERO_BACK);
		List<UserDailyTask> udTasks = new ArrayList<>();
		UserDailyTask udTask = null;
		long generateTime = DateUtil.toDateTimeLong();
		for (CfgTaskEntity task : dailyTasks) {
			udTask =UserDailyTask.instance(gu.getId(), task.getId(), task.getValue(), generateTime);
			udTask.setTaskType(TaskTypeEnum.HERO_BACK_TASK.getValue());
			if (udTask.getBaseId() == 60011 || udTask.getBaseId() == 60111 || udTask.getBaseId() == 60211
					|| udTask.getBaseId() == 60311) {
				udTask.addValue(1);
			}
			udTasks.add(udTask);
			gameUserService.addItem(guId, udTask);
		}
		// 开箱子记录
		List<CfgBox> boxs = TaskTool.getBoxsByTaskGroupEnum(TaskGroupEnum.HERO_BACK);
	    for (CfgBox box:boxs) {
	    	udTask = UserDailyTask.instance(gu.getId(), box.getId(), box.getScore(), generateTime);
			udTask.setTaskType(TaskTypeEnum.HERO_BACK_BOX_TASK.getValue());
		    udTasks.add(udTask);
		    gameUserService.addItem(guId, udTask);
	    }
	    return udTasks;
	}

	/**
	 * 重新初始化任务
	 * 
	 * @param guId
	 */
	public void initTasks(long guId) {
		List<UserDailyTask> dailyTasks = gameUserService.getMultiItems(guId, UserDailyTask.class);
		List<UserDailyTask> todayTasks = dailyTasks.stream().filter(udt -> udt.heroBackValidTask())
				.collect(Collectors.toList());
		if (!todayTasks.isEmpty()) {
			gameUserService.deleteItems(guId, todayTasks);
		}
		createTasks(guId);
	}
	private void toRdDailyTasks(List<UserDailyTask> udTasks, RDTaskList rd) {
    	int boxIdBegin=TaskTool.getDailyBoxIdBeginByTaskId(udTasks.get(0).getBaseId());
		udTasks = udTasks.stream().sorted(Comparator.comparing(UserDailyTask::getBaseId)).collect(Collectors.toList());

    	List<RDTaskItem> rdTasks=new ArrayList<>();
    	List<List<RDTaskItem>> rdHeroBackTasks = new ArrayList<>();
		int base = 600;
    	for (UserDailyTask task:udTasks) {
			if (task.getBaseId() / 100 != base) {
				rdHeroBackTasks.add(rdTasks);
				rdTasks = new ArrayList<>();
				base = task.getBaseId() / 100;
			}
    		 if (boxIdBegin<task.getBaseId()) {
                 rdTasks.add(RDDailyTask.fromUserDailyBoxTask(task)); 
             } else {
				 CfgTaskEntity cfgtask=TaskTool.getDailyTask(task.getBaseId());
              	rdTasks.add(RDDailyTask.fromUserDailyTask(task,cfgtask));
             }
		}
		if (rdTasks.isEmpty()) {
			return;
		}
		rdHeroBackTasks.add(rdTasks);
		for (int i = 0; i < rdHeroBackTasks.size(); i++) {
			for (RDTaskItem rdTaskItem : rdHeroBackTasks.get(i)){
				rdTaskItem.setDays(i + 1);
			}
			rd.addTasks(rdHeroBackTasks.get(i));
		}
    }

	private boolean needCreate(long uid) {
		IActivity ia = activityService.getActivity(gameUserService.getActiveSid(uid), ActivityEnum.HERO_BACK_TASK);
		if (ia == null) {
			return false;
		}
		List<CfgActivityEntity> cas = ActivityTool.getActivitiesByType(ActivityEnum.fromValue(ia.gainType()));
		for (CfgActivityEntity ca : cas) {
			UserActivity ua = activityService.getUserActivity(uid, ia.gainId(), ca.getId());
			if (ua != null) {
				return true;
			}
		}
		return false;
	}

	public int awardNum(long guId) {
		// 获取可领取任务奖励数量
		List<UserDailyTask> tasks = getShowTasks(guId);
		int num = 0;
		int day = heroBackSignProcessor.getSignDays(gameUserService.getGameUser(guId));
		int unlockId = day + 600;
		if (tasks != null && !tasks.isEmpty()) {
			for (UserDailyTask task : tasks) {
				if ((task.getBaseId() / 100 < unlockId || task.getBaseId() / 100 == 609)
						&& task.getStatus() == TaskStatusEnum.ACCOMPLISHED.getValue()) {
					num++;
				}
			}
		}
		return num;
	}
}
