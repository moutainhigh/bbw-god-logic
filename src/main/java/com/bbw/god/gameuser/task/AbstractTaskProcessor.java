package com.bbw.god.gameuser.task;

import com.bbw.god.game.award.AwardService;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 任务处理类
 */
public abstract class AbstractTaskProcessor {
	@Autowired
	protected GameUserService gameUserService;
	@Autowired
	protected AwardService awardService;

	protected List<TaskTypeEnum> taskTypes;

	/**
	 * 获得任务列表
	 * 
	 * @param uid
	 * @return
	 */
	public abstract RDTaskList getTasks(long uid, Integer days);

	/**
	 * 获取任务奖励
	 *
	 * @param uid
	 * @param id
	 * @return
	 */
	public abstract RDCommon gainTaskAward(long uid, int id, String awardIndex);

	/**
	 * 已将领取
	 *
	 * @param uid
	 * @param type
	 * @return
	 */
	public RDCommon gainBatchTaskAward(long uid, TaskTypeEnum type) {
		return null;
	}

	/**
	 * 是否匹配特定的任务
	 *
	 * @param taskType
	 * @return
	 */
	public boolean isMatch(TaskTypeEnum taskType) {
		return this.taskTypes.contains(taskType);
	}

	public void setTaskAwardIndex(long uid, int id,String awardIndex) {

	}

}
