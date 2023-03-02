package com.bbw.god.gameuser.task.fshelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.bbw.god.gameuser.task.fshelper.event.EpFsHelperChangeEvent;
import com.bbw.mc.m2c.M2cService;

/** 
* @author 作者 ：lwb
* @version 创建时间：2020年2月13日 下午4:24:12 
* 类说明 
*/
@Component
public class FsHelperListener {
	@Autowired
	private FsHelperService fsHelperService;
	@Autowired
	private M2cService m2cService;
	@Async
	@EventListener
	public void taskProgressAddEvent(EpFsHelperChangeEvent event) {
		long guId = event.getEP().getGuId();
		FsTaskEnum type = event.getEP().getType();
		int taskId = event.getEP().getTaskId();
		if (!event.getEP().isUpdateProgress()) {
			if (type.equals(FsTaskEnum.Daily)){
				fsHelperService.delTask(guId, type.getVal(), taskId);
			}
		} else {
			// 通知进度
			m2cService.sendFsHeplerMsg(guId);
		}

	}

}
