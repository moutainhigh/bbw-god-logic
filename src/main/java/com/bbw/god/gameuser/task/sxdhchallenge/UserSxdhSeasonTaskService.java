package com.bbw.god.gameuser.task.sxdhchallenge;

import com.bbw.common.DateUtil;
import com.bbw.god.game.sxdh.SxdhZone;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.task.CfgTaskEntity;
import com.bbw.god.gameuser.task.TaskGroupEnum;
import com.bbw.god.gameuser.task.TaskTool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 神仙大会赛季挑战
 *
 * @author suhq
 * @date 2020-04-27 09:52
 **/
@Slf4j
@Service
public class UserSxdhSeasonTaskService {
    @Autowired
    private GameUserService gameUserService;

    /**
     * 生成每日任务
     *
     * @param guId
     * @return
     */
    public List<UserSxdhSeasonTask> generateSeasonTasks(long guId, SxdhZone sxdhZone) {
        // 获得每日随机任务
        List<CfgTaskEntity> tasks = TaskTool.getTaskConfig(TaskGroupEnum.SXDH_SEASON_TASK.getValue()).getTasks();
        // 生成玩家每日任务记录
        List<UserSxdhSeasonTask> uTasks = new ArrayList<>();
        UserSxdhSeasonTask uTask = null;
        for (CfgTaskEntity task : tasks) {
            uTask = UserSxdhSeasonTask.fromTask(guId, task, sxdhZone.getBeginDate());
            uTasks.add(uTask);
            gameUserService.addItem(guId, uTask);
        }
        return uTasks;
    }

    /**
     * 获得用户每日任务
     *
     * @param guId
     * @param taskId
     * @return
     */
    public UserSxdhSeasonTask getSeasonTask(long guId, SxdhZone sxdhZone, int taskId) {
        List<UserSxdhSeasonTask> uTasks = getSeasonTasks(guId, sxdhZone);
        UserSxdhSeasonTask uTask = uTasks.stream().filter(dt -> dt.getBaseId() == taskId).findFirst().orElse(null);
        return uTask;
    }

    /**
     * 获得赛季挑战
     *
     * @param guId
     * @return
     */
    public List<UserSxdhSeasonTask> getSeasonTasks(long guId, SxdhZone sxdhZone) {
        List<UserSxdhSeasonTask> allSeasonTasks = getAllSeasonTasks(guId);
        List<UserSxdhSeasonTask> seasonTasks = allSeasonTasks.stream().filter(tmp -> DateUtil.getDaysBetween(tmp.getGenerateTime(), sxdhZone.getBeginDate()) == 0)
                .collect(Collectors.toList());
        return seasonTasks;
    }

    private List<UserSxdhSeasonTask> getAllSeasonTasks(long guId) {
        return gameUserService.getMultiItems(guId, UserSxdhSeasonTask.class);
    }
}
