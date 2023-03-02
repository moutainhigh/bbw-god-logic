package com.bbw.god.gameuser.task.activitytask.guesstask;

import com.bbw.god.activity.processor.WorldCupGuessTaskProcessor;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.task.TaskGroupEnum;
import com.bbw.god.gameuser.task.TaskTypeEnum;
import com.bbw.god.gameuser.task.activitytask.ActivityDailyTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 竞猜每日任务service
 *
 * @author: huanghb
 * @date: 2022/11/14 9:48
 */
@Service
public class GuessDailyTaskService extends ActivityDailyTaskService {
    @Autowired
    private WorldCupGuessTaskProcessor worldCupGuessTaskProcessor;
    @Autowired
    private GameUserService gameUserService;

    /**
     * 获得任务类别
     *
     * @return
     */
    @Override
    protected Integer getTaskType() {
        return TaskTypeEnum.GUESS_DAILY_TASK.getValue();
    }

    /**
     * 获得任务组
     *
     * @return
     */
    @Override
    protected Integer getTaskGroup() {
        return TaskGroupEnum.GUESS_DAILY_TASK.getValue();
    }

    /**
     * 获得奖励获得方式
     *
     * @return
     */
    @Override
    protected WayEnum getWayEnum() {
        return WayEnum.WORLD_CUP_ACTIVITIE_GUESS_TASK;
    }

    /**
     * 是否开启活动
     *
     * @return
     */
    @Override
    protected boolean isOpenActivity(long uid) {
        return worldCupGuessTaskProcessor.isOpened(gameUserService.getActiveSid(uid));
    }
}