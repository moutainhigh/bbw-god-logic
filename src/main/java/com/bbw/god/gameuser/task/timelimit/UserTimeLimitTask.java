package com.bbw.god.gameuser.task.timelimit;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.bbw.common.ID;
import com.bbw.god.game.award.Award;
import com.bbw.god.gameuser.UserDataType;
import com.bbw.god.gameuser.task.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 时效任务
 *
 * @author: suhq
 * @date: 2021/8/6 5:31 下午
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class UserTimeLimitTask extends UserTask implements Serializable {
	private static final long serialVersionUID = 1L;
	private Integer group;
	/** 派遣的卡牌 */
	private List<Integer> dispatchCards;
	/** 额外技能条件 */
	private List<Integer> extraSkills;
	/**
	 * 额外奖励
	 * 关闭引用检测,重复引用对象时不会被$ref代替
	 */
	@JSONField(serialzeFeatures = SerializerFeature.DisableCircularReferenceDetect)
	private List<Award> extraAwards;
	/** 对应状态的结束时间 */
	private Date timeEnd;

	public static UserTimeLimitTask instance(long uid, TaskGroupEnum taskGroup, CfgTaskEntity task, List<Integer> extraSkills, List<Award> extraAwards) {
		UserTimeLimitTask ut = instance(uid, taskGroup, task);
		ut.setExtraSkills(extraSkills);
		ut.setExtraAwards(extraAwards);
		return ut;
	}

	public static UserTimeLimitTask instance(long uid, TaskGroupEnum taskGroup, CfgTaskEntity task) {
		UserTimeLimitTask ut = new UserTimeLimitTask();
		ut.setId(ID.INSTANCE.nextId());
		ut.setGameUserId(uid);
		ut.setGroup(taskGroup.getValue());
		ut.setBaseId(task.getId());
		ut.setNeedValue(task.getValue());
		TaskStatusEnum status = TimeLimitTaskTool.getInitStatus(task);
		Date timeEnd = TimeLimitTaskTool.getEndInitTime(taskGroup, task.getId());
		ut.setStatus(status.getValue());
		ut.setTimeEnd(timeEnd);
		return ut;
	}

	/**
	 * 是否正在派遣中任务
	 *
	 * @return
	 */
	public boolean isDoingDispatchTask() {
		if (TaskStatusEnum.DOING.getValue() != this.getStatus()) {
			return false;
		}
		TaskGroupEnum taskGroup = TaskGroupEnum.fromValue(group);
		CfgTaskEntity cfgTaskEntity = TaskTool.getTaskEntity(taskGroup, getBaseId());
		if (TaskTypeEnum.TIME_LIMIT_DISPATCH_TASK.getValue() != cfgTaskEntity.getType()) {
			return false;
		}
		return true;
	}

	/**
	 * 获取任务的状态
	 *
	 * @param now
	 * @return
	 */
	public TaskStatusEnum gainTaskStatus(Date now) {
		boolean isOutDate = TaskStatusEnum.WAITING.getValue() == getStatus() && getTimeEnd().before(now);
		if (isOutDate) {
			return TaskStatusEnum.TIME_OUT;
		}
		isOutDate = TaskStatusEnum.DOING.getValue() == getStatus() && getTimeEnd().before(now);
		if (isOutDate) {
			return TaskStatusEnum.TIME_OUT;
		}
		return TaskStatusEnum.fromValue(getStatus());
	}

	/**
	 * 派发卡牌
	 *
	 * @param dispatchCards
	 * @param dispatchDate
	 */
	public void addDispatchCards(List<Integer> dispatchCards, Date dispatchDate) {
		this.setDispatchCards(dispatchCards);
		this.setTimeEnd(dispatchDate);
		this.setStatus(TaskStatusEnum.DOING.getValue());
	}

	/**
	 * 进入派遣状态
	 *
	 * @param dispatchDate
	 */
	public void enterDoing(Date dispatchDate) {
		this.setTimeEnd(dispatchDate);
		this.setStatus(TaskStatusEnum.DOING.getValue());
	}

	/**
	 * 进入排队状态
	 *
	 * @param dispatchCards
	 */
	public void enterQueuing(List<Integer> dispatchCards) {
		this.setDispatchCards(dispatchCards);
		this.setStatus(TaskStatusEnum.QUEUING.getValue());
	}

	/**
	 * 重新开始任务
	 */
	public void reset() {
		TaskGroupEnum taskGroup = TaskGroupEnum.fromValue(group);
		CfgTaskEntity task = TaskTool.getTaskEntity(taskGroup, getBaseId());
		TaskStatusEnum status = TimeLimitTaskTool.getInitStatus(task);
		Date timeEnd = TimeLimitTaskTool.getEndInitTime(taskGroup, task.getId());
		this.setStatus(status.getValue());
		this.setTimeEnd(timeEnd);
		this.setDispatchCards(null);
		this.setValue(0);
		this.setAccomplishTime(0L);

	}


	@Override
	public UserDataType gainResType() {
		return UserDataType.USER_TIME_LIMIT_TASK;
	}
}
