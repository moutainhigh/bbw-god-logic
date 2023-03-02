package com.bbw.god.gameuser.task.daily;

import com.bbw.god.gameuser.task.CfgTaskConfig.CfgBox;
import com.bbw.god.gameuser.task.CfgTaskEntity;
import com.bbw.god.gameuser.task.RDTaskItem;
import com.bbw.god.gameuser.task.TaskTool;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Arrays;

/**
 * @author suhq
 * @description: 每日任务返回数据
 * @date 2019-11-20 10:06
 **/
@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDDailyTask extends RDTaskItem implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private static RDDailyTask instance(UserDailyTask udt) {
        RDDailyTask rDailyTask = new RDDailyTask();
        rDailyTask.setId(udt.getBaseId());
        rDailyTask.setStatus(udt.getStatus());
        rDailyTask.setProgress((int) udt.getValue());
        rDailyTask.setTotalProgress(udt.getNeedValue());
        return rDailyTask;
    }
    public static RDDailyTask fromUserDailyBoxTask(UserDailyTask udt) {
        RDDailyTask rDailyTask = instance(udt);
        CfgBox box=TaskTool.getDailyTaskCfgBox(udt.getBaseId());
		if (udt.getAwardIndex() != null
				&& udt.getAwardIndex() > 0 && udt.getAwardIndex() <= box.getAwards().size()) {
			rDailyTask.setAwards(Arrays.asList(box.getAwards().get(udt.getAwardIndex() - 1)));
		} else {
			rDailyTask.setAwards(box.getAwards());
		}
        return rDailyTask;
    }
    
    public static RDDailyTask fromUserDailyTask(UserDailyTask udt,CfgTaskEntity cfgtask) {
    	RDDailyTask rDailyTask =instance(udt);
        if (cfgtask.getValue()==null) {
            int val = udt.getNeedValue();
            if ((cfgtask.getId() == 21016 || cfgtask.getId() == 22016 || cfgtask.getId() == 23016 || cfgtask.getId() == 24016 || cfgtask.getId() == 25016)) {
                //描述中数值过大的以万为单位
                val = val / 10000;
            }
            String[] strArr = {String.valueOf(val)};
            rDailyTask.setTitleFormats(strArr);
        }
        rDailyTask.setAwards(cfgtask.getAwards());
        return rDailyTask;
    }
}
