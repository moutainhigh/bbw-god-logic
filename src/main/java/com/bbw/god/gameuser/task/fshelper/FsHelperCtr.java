package com.bbw.god.gameuser.task.fshelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bbw.common.Rst;
import com.bbw.god.controller.AbstractController;
import com.bbw.god.game.CR;

/** 
* @author 作者 ：lwb
* @version 创建时间：2020年2月13日 上午11:36:25 
* 类说明 
*/
@RestController
public class FsHelperCtr extends AbstractController {
	@Autowired
	private FsHelperService fsHelperService;

	@RequestMapping(CR.FsHelper.ADD_TASK)
	public Rst addTask(Integer type, Integer taskId) {
		fsHelperService.addTask(getUserId(), type, taskId);
		return Rst.businessOK();
	}

	@RequestMapping(CR.FsHelper.DEL_TASK)
	public Rst delTask(Integer type, Integer taskId) {
		fsHelperService.delTask(getUserId(), type, taskId);
		return Rst.businessOK();
	}

	@RequestMapping(CR.FsHelper.LIST_TASK)
	public RDFsHelper listTasks() {
		return fsHelperService.getTaskList(getUserId());
	}

}
