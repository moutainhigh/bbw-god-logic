package com.bbw.god.gameuser.task.activitytask.guesstask;

import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.activity.processor.WorldCupGuessTaskProcessor;
import com.bbw.god.gameuser.task.AbstractTaskProcessor;
import com.bbw.god.gameuser.task.RDTaskList;
import com.bbw.god.gameuser.task.TaskTypeEnum;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * 竞猜每日任务
 *
 * @author: huanghb
 * @date: 2022/11/14 9:46
 */
@Component
public class GuessDailyTaskProcessor extends AbstractTaskProcessor {
    @Autowired
    private WorldCupGuessTaskProcessor worldCupGuessTaskProcessor;
    @Autowired
    private GuessDailyTaskService guessDailyTaskService;

    public GuessDailyTaskProcessor() {
        this.taskTypes = Arrays.asList(TaskTypeEnum.GUESS_DAILY_TASK);
    }

    /**
     * 获得任务列表
     *
     * @param uid
     * @return
     */
    @Override
    public RDTaskList getTasks(long uid, Integer days) {
        if (!worldCupGuessTaskProcessor.isOpened(gameUserService.getActiveSid(uid))) {
            throw new ExceptionForClientTip("activity.not.exist");
        }
        return guessDailyTaskService.getTasks(uid, days);
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
        if (!worldCupGuessTaskProcessor.isOpened(gameUserService.getActiveSid(uid))) {
            throw new ExceptionForClientTip("activity.not.exist");
        }
        return guessDailyTaskService.gainTaskAward(uid, id);
    }
}
