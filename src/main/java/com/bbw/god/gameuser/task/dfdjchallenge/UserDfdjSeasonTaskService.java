package com.bbw.god.gameuser.task.dfdjchallenge;

import com.bbw.common.DateUtil;
import com.bbw.god.game.dfdj.zone.DfdjZone;
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
 * 巅峰对决赛季挑战
 *
 * @author suhq
 * @date 2020-04-27 09:52
 **/
@Slf4j
@Service
public class UserDfdjSeasonTaskService {
    @Autowired
    private GameUserService gameUserService;

    /**
     * 生成每日任务
     *
     * @param guId
     * @return
     */
    public List<UserDfdjSeasonTask> generateSeasonTasks(long guId, DfdjZone zone) {
        // 获得每日随机任务
        List<CfgTaskEntity> tasks = TaskTool.getTaskConfig(TaskGroupEnum.DFDJ_SEASON_TASK.getValue()).getTasks();
        // 生成玩家每日任务记录
        List<UserDfdjSeasonTask> uTasks = new ArrayList<>();
        UserDfdjSeasonTask uTask = null;
        for (CfgTaskEntity task : tasks) {
            uTask = UserDfdjSeasonTask.fromTask(guId, task, zone.getBeginDate());
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
    public UserDfdjSeasonTask getSeasonTask(long guId, DfdjZone zone, int taskId) {
        List<UserDfdjSeasonTask> uTasks = getSeasonTasks(guId, zone);
        UserDfdjSeasonTask uTask = uTasks.stream().filter(dt -> dt.getBaseId() == taskId).findFirst().orElse(null);
        return uTask;
    }

    /**
     * 获得赛季挑战
     *
     * @param guId
     * @return
     */
    public List<UserDfdjSeasonTask> getSeasonTasks(long guId, DfdjZone zone) {
        List<UserDfdjSeasonTask> allSeasonTasks = getAllSeasonTasks(guId);
        List<UserDfdjSeasonTask> seasonTasks = allSeasonTasks.stream().filter(tmp -> DateUtil.getDaysBetween(tmp.getGenerateTime(), zone.getBeginDate()) == 0)
                .collect(Collectors.toList());
        return seasonTasks;
    }

    private List<UserDfdjSeasonTask> getAllSeasonTasks(long guId) {
        return gameUserService.getMultiItems(guId, UserDfdjSeasonTask.class);
    }
}
