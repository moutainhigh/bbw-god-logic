package com.bbw.god.gameuser.task.grow;

import com.bbw.god.gameuser.task.CfgTaskConfig.CfgBox;
import com.bbw.god.gameuser.task.CfgTaskEntity;
import com.bbw.god.gameuser.task.RDTaskItem;
import com.bbw.god.gameuser.task.TaskStatusEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 新手任务返回
 *
 * @author lwb
 */
@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDNewbieTask extends RDTaskItem implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private static RDNewbieTask instance(UserGrowTask udt) {
    	RDNewbieTask rDailyTask = new RDNewbieTask();
   	 	rDailyTask.setId(udt.getBaseId());
        rDailyTask.setStatus(udt.getStatus());
        rDailyTask.setProgress((int) udt.getValue());
        rDailyTask.setTotalProgress(udt.getNeedValue());
        return rDailyTask;
   }
    
    public static RDNewbieTask fromUserBoxTask(UserGrowTask udt,CfgBox box) {
		RDNewbieTask rDailyTask = instance(udt);
		rDailyTask.setAwards(box.getAwards());
        return rDailyTask;
    }
    
	public static RDNewbieTask fromCfgBoxTask(CfgBox box) {
		RDNewbieTask rDailyTask = new RDNewbieTask();
		rDailyTask.setId(box.getId());
		rDailyTask.setStatus(TaskStatusEnum.AWARDED.getValue());
		rDailyTask.setProgress(box.getScore());
		rDailyTask.setTotalProgress(box.getScore());
		rDailyTask.setAwards(box.getAwards());
		return rDailyTask;
	}

    public static RDNewbieTask fromUserTask(UserGrowTask udt,CfgTaskEntity taskEntity) {
    	RDNewbieTask rDailyTask =instance(udt);
        rDailyTask.setAwards(taskEntity.getAwards());
        return rDailyTask;
    }

}
