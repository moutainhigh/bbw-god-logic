package com.bbw.god.gameuser.task;

import com.bbw.god.activity.holiday.processor.holidayspecialcity.HolidayCelebrationInviteProcessor;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserTaskLogic {
    @Autowired
    private TaskProcessorFactory taskProcessorFactory;
    @Autowired
    private HolidayCelebrationInviteProcessor holidayCelebrationInviteProcessor;

    /**
     * 获得任务列表
     *
     * @param uid
     * @param type 参见 TaskTypeEnum
     * @return
     */
    public RDTaskList getTasks(long uid, int type, Integer days) {
        TaskTypeEnum taskType = TaskTypeEnum.fromValue(type);
        //双旦节活动任务
        if (taskType == TaskTypeEnum.CELEBRATION_INVITATION_TASK){
            return holidayCelebrationInviteProcessor.getTasks(uid);
        }
        AbstractTaskProcessor taskProcessor = this.taskProcessorFactory.getTaskProcessor(taskType);
        return taskProcessor.getTasks(uid, days);
    }

    /**
     * 获取任务奖励
     *
     * @param uid
     * @param type
     * @param id
     * @return
     */
    public RDCommon gainTaskAward(long uid, int type, int id, String awardIndex) {
        TaskTypeEnum taskType = TaskTypeEnum.fromValue(type);
        AbstractTaskProcessor taskProcessor = this.taskProcessorFactory.getTaskProcessor(taskType);
        return taskProcessor.gainTaskAward(uid, id, awardIndex);
    }

    /**
     * 批量领取奖励
     *
     * @param uid
     * @param type
     * @return
     */
    public RDCommon gainBatchTaskAward(long uid, int type) {
        TaskTypeEnum taskType = TaskTypeEnum.fromValue(type);
        AbstractTaskProcessor taskProcessor = this.taskProcessorFactory.getTaskProcessor(taskType);
        return taskProcessor.gainBatchTaskAward(uid, taskType);
    }

    /**
     * 设置任务奖励
     *
     * @param uid
     * @param type
     * @param id
     * @param awardIndex
     */
    public void setTaskAwardIndex(long uid, int type, int id, String awardIndex) {
        TaskTypeEnum taskType = TaskTypeEnum.fromValue(type);
        AbstractTaskProcessor taskProcessor = this.taskProcessorFactory.getTaskProcessor(taskType);
        taskProcessor.setTaskAwardIndex(uid, id, awardIndex);
    }
}
