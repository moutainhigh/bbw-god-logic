package com.bbw.god.gameuser.task.fshelper.event;

import com.bbw.god.event.BaseEventParam;
import com.bbw.god.gameuser.task.fshelper.FsTaskEnum;

import lombok.Getter;
import lombok.Setter;

/**
 * @author 作者 ：lwb
 * @version 创建时间：2019年11月25日 上午11:13:05 类说明 封神助手变化
 */
@Getter
@Setter
public class EpFsHelperChange extends BaseEventParam{
	private FsTaskEnum type;
	private Integer taskId;
	private boolean updateProgress = false;

	public static EpFsHelperChange instanceDelTask(BaseEventParam bep, FsTaskEnum type, int taskId) {
		EpFsHelperChange dta = instance(bep, type, taskId);
		dta.setUpdateProgress(false);
		return dta;
	}

	public static EpFsHelperChange instanceUpdateTask(BaseEventParam bep, FsTaskEnum type, int taskId) {
		EpFsHelperChange dta = instance(bep, type, taskId);
		dta.setUpdateProgress(true);
		return dta;
	}
	
	private static EpFsHelperChange instance(BaseEventParam bep, FsTaskEnum type, int taskId) {
		EpFsHelperChange dta=new EpFsHelperChange();
		dta.setValues(bep);
		dta.setType(type);
		dta.setTaskId(taskId);
		return dta;
	}
}
