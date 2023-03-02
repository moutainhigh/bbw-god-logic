package com.bbw.god.gameuser.task.godtraining;

import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.gameuser.task.AbstractTaskProcessor;
import com.bbw.god.gameuser.task.RDTaskList;
import com.bbw.god.gameuser.task.TaskTypeEnum;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * @author suchaobin
 * @description 上仙试炼处理器
 * @date 2021/1/19 20:43
 **/
@Component
public class GodTrainingTaskProcessor extends AbstractTaskProcessor {
    @Autowired
    private GodTrainingTaskService godTrainingTaskService;

    public GodTrainingTaskProcessor() {
        this.taskTypes = Arrays.asList(TaskTypeEnum.GOD_TRAINING_TASK);
    }

    /**
     * 获得任务列表
     *
     * @param uid
     * @return
     */
    @Override
    public RDTaskList getTasks(long uid, Integer days) {
        return godTrainingTaskService.getTasks(uid, days);
    }

    /**
     * 获取任务奖励
     *
     * @param uid
     * @param id
     * @param awardIndex
     * @return
     */
    @Override
    public RDCommon gainTaskAward(long uid, int id, String awardIndex) {
        return godTrainingTaskService.gainTaskAward(uid, id, awardIndex);
    }

    @Override
    public void setTaskAwardIndex(long uid, int id, String awardIndex) {
        UserGodTrainingTask task = godTrainingTaskService.getUserTrainingTask(uid, id);
        if (null == task) {
            throw new ExceptionForClientTip("task.not.exist");
        }
        task.setAwardIndex(Integer.parseInt(awardIndex));
        gameUserService.updateItem(task);
    }
}
