package com.bbw.god.gameuser.task.timelimit;

import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.task.*;
import com.bbw.god.rd.RDSuccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 派遣类任务业务类
 *
 * @author: suhq
 * @date: 2021/8/9 11:10 上午
 */
@Service
public class UserTimeLimitTaskLogic {
    @Autowired
    private GameUserService gameUserService;

    /**
     * 丢弃任务
     *
     * @param uid
     * @param taskId
     */
    public RDSuccess abandom(long uid, long taskId) {
        //检查任务有效性
        Optional<UserTimeLimitTask> utOp = gameUserService.getUserData(uid, taskId, UserTimeLimitTask.class);
        if (!utOp.isPresent()) {
            throw ExceptionForClientTip.fromi18nKey("task.not.exist");
        }
        UserTimeLimitTask ut = utOp.get();
        TaskStatusEnum status = TaskStatusEnum.fromValue(ut.getStatus());
        if (status == TaskStatusEnum.ACCOMPLISHED || status == TaskStatusEnum.AWARDED) {
            throw ExceptionForClientTip.fromi18nKey("task.already.accomplished");
        }
        CfgTaskEntity task = TaskTool.getTaskEntity(TaskGroupEnum.fromValue(ut.getGroup()), ut.getBaseId());
        if (task.getType() == TaskTypeEnum.TIME_LIMIT_DISPATCH_TASK.getValue() && status == TaskStatusEnum.DOING) {
            throw ExceptionForClientTip.fromi18nKey("task.already.dispatch");
        }
        gameUserService.deleteItem(ut);
        return new RDSuccess();
    }
}
