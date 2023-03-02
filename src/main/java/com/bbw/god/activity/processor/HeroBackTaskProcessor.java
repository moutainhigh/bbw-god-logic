package com.bbw.god.activity.processor;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.db.entity.CfgActivityEntity;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.task.TaskStatusEnum;
import com.bbw.god.gameuser.task.daily.UserDailyTask;
import com.bbw.god.gameuser.task.daily.heroback.HeroBackTaskInfoProcessor;

/** 
* @author 作者 ：lwb
* @version 创建时间：2020年1月7日 上午10:32:28 
* 类说明 
*/
@Service
public class HeroBackTaskProcessor  extends HeroBackLogic{
	@Autowired
	private HeroBackTaskInfoProcessor taskProcessor;
	public HeroBackTaskProcessor() {
		this.activityTypeList = Arrays.asList(ActivityEnum.HERO_BACK_TASK);
	}

	@Override
	public int getAbleAwardedNum(GameUser gu, IActivity a) {
		return taskProcessor.awardNum(gu.getId());
	}

	@Override
	public boolean setAwardItem(Long uid, int sId, CfgActivityEntity ca, int awardIndex) {
		UserDailyTask uTask = taskProcessor.getTodayTask(uid, 60905);
		if (uTask == null || uTask.getStatus().equals(TaskStatusEnum.AWARDED.getValue())) {
			return false;
		}
		uTask.setAwardIndex(awardIndex);
		gameUserService.updateItem(uTask);
		return true;
	}
	
	
}
