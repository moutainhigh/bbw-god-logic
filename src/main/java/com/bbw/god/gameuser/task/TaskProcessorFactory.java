package com.bbw.god.gameuser.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 
 * @author suhq
 * @date 2018年12月6日 上午10:57:04
 */
@Service
public class TaskProcessorFactory {
	@Autowired
	@Lazy
	private List<AbstractTaskProcessor> taskProcessors;

	/**
	 * 根据任务类型获取任务服务实现对象
	 * @param taskType
	 * @return
	 */
	public AbstractTaskProcessor getTaskProcessor(TaskTypeEnum taskType) {
		return taskProcessors.stream().filter(mp -> mp.isMatch(taskType)).findFirst().orElse(null);
	}

}
