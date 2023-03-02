package com.bbw.god.gameuser.task.timelimit.cunz;

import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.game.award.AwardService;
import com.bbw.god.gameuser.task.*;
import com.bbw.god.gameuser.task.timelimit.TimeLimitTaskTool;
import com.bbw.god.gameuser.task.timelimit.UserDispatchTaskService;
import com.bbw.god.gameuser.task.timelimit.UserTimeLimitTask;
import com.bbw.god.gameuser.task.timelimit.UserTimeLimitTaskService;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * 村庄任务
 *
 * @author: suhq
 * @date: 2021/8/9 5:29 下午
 */
@Service
public class CunZTaskProcessor extends AbstractTaskProcessor {

    @Autowired
    private UserCunZTaskService userCunZTaskService;
    @Autowired
    private UserDispatchTaskService userDispatchTaskService;
    @Autowired
    private UserTimeLimitTaskService userTimeLimitTaskService;
    @Autowired
    private AwardService awardService;

    public CunZTaskProcessor() {
        this.taskTypes = Arrays.asList(TaskTypeEnum.TIME_LIMIT_NORMAL, TaskTypeEnum.TIME_LIMIT_DISPATCH_TASK, TaskTypeEnum.TIME_LIMIT_FIGHT_TASK);
    }

    @Override
    public RDTaskList getTasks(long uid, Integer days) {
        RDTaskList rd = new RDTaskList();
        List<RDTaskItem> rdTaskItems = new ArrayList<>();
        List<UserTimeLimitTask> uts = userCunZTaskService.getTasks(uid);
        for (UserTimeLimitTask ut : uts) {
            CfgTaskEntity taskEntity = TaskTool.getTaskEntity(TaskGroupEnum.CUN_ZHUANG_TASK, ut.getBaseId());
            RDTaskItem rdTaskItem = RDTaskItem.getInstance(ut, taskEntity, TaskGroupEnum.CUN_ZHUANG_TASK);
            rdTaskItems.add(rdTaskItem);
        }
        rd.setItems(rdTaskItems);
        return rd;
    }

    @Override
    public RDCommon gainTaskAward(long uid, int id, String awardIndex) {
        return new RDCommon();
    }

    /**
     * 领奖励
     *
     * @param ut
     * @return
     */
    public RDCommon gainTaskAward(UserTimeLimitTask ut) {
        long uid = ut.getGameUserId();
        //检查任务有效性
        TaskStatusEnum taskStatus = userTimeLimitTaskService.doBeforeDispatchAward(ut);
        RDCommon rd = userTimeLimitTaskService.doDispatchAward(ut, taskStatus);
        CfgTaskEntity taskEntity = TaskTool.getTaskEntity(TaskGroupEnum.fromValue(ut.getGroup()), ut.getBaseId());
        // 史诗自动生成下一轮
        if (taskStatus == TaskStatusEnum.ACCOMPLISHED && taskEntity.getDifficulty() == TaskDifficulty.SUPER_LEVEL.getValue()) {
            CfgTaskEntity nextTask = TaskTool.getTask(TaskGroupEnum.fromValue(ut.getGroup()), taskEntity.getSeqGroup(), taskEntity.getSeq() + 1);
            if (null != nextTask) {
                UserTimeLimitTask nextUt = userCunZTaskService.makeUserTaskInstance(uid, nextTask);
                gameUserService.addItem(uid, nextUt);
            }
        }
        return rd;
    }


}
