package com.bbw.god.gameuser.task;

import com.bbw.common.ListUtil;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.task.grow.NewbieTaskService;
import com.bbw.god.gameuser.task.grow.UserGrowTask;
import com.bbw.god.gameuser.task.main.UserMainTask;
import com.bbw.god.gameuser.task.main.UserMainTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserTaskService {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private NewbieTaskService newbieTaskService;
    @Autowired
    private UserMainTaskService userMainTaskService;

    /**
     * 创建角色初始化任务
     *
     * @param gu
     */
    public void initTasks(GameUser gu) {
        if (gu.getStatus().isGrowTaskCompleted()) {
            return;
        }
        long guId = gu.getId();
        // 初始化新手进阶任务
        List<UserGrowTask> ugts = this.newbieTaskService.getAllNewbieTasks(guId);
        if (ListUtil.isEmpty(ugts)) {
            this.newbieTaskService.updateTask(guId);
        }

        // 初始化主线任务
        List<UserMainTask> umts = this.userMainTaskService.getUserMainTasks(guId);
        if (ListUtil.isEmpty(umts)) {
            List<CfgTaskEntity> mainTasks = TaskTool.getTasksByTaskGroupEnum(TaskGroupEnum.TASK_MAIN);
            mainTasks.forEach(tmp -> {
                UserMainTask umt = UserMainTask.fromTask(guId, tmp);
                this.userMainTaskService.addUserMainTask(guId, umt);
            });
        }

    }
}
