package com.bbw.god.login.repairdata;

import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.task.TaskTool;
import com.bbw.god.gameuser.task.grow.NewbieTaskService;
import com.bbw.god.gameuser.task.grow.UserGrowTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

import static com.bbw.god.login.repairdata.RepairDataConst.REPAIR_GROW_TASK_TIME;
import static com.bbw.god.login.repairdata.RepairDataConst.RESET_NEWER_TASK_TIME;

/**
 * 修复新手任务
 *
 * @author suchaobin
 * @date 2020/12/18 15:44
 **/
@Service
public class RepairNewerTaskService implements BaseRepairDataService {
    @Autowired
    private NewbieTaskService newbieTaskService;
    @Autowired
    private GameUserService gameUserService;

    /**
     * 修复新手任务数据
     *
     * @param gu            玩家对象
     * @param lastLoginDate 上次登录时间
     */
    @Override
    public void repair(GameUser gu, Date lastLoginDate) {
        long uid = gu.getId();
        boolean passGrowTask = newbieTaskService.isPassGrowTask(gu);
        //已通过不做任何处理
        if (passGrowTask) {
            return;
        }
        // 重置新手任务
        if (lastLoginDate.before(RESET_NEWER_TASK_TIME)) {
            newbieTaskService.updateTask(uid);
            List<UserGrowTask> tasks = newbieTaskService.getAllNewbieTasks(uid);
            tasks.forEach(tmp -> tmp.addValue(tmp.getNeedValue()));
            gameUserService.updateItems(tasks);
        }
        // 修复新手任务
        if (lastLoginDate.before(REPAIR_GROW_TASK_TIME)) {
            updateBoxNeedValue(uid, 10902);
            updateBoxNeedValue(uid, 10903);
            newbieTaskService.updateTask(uid);
        }
    }

    /**
     * 跟新新手任务宝箱所需要的值
     *
     * @param uid
     * @param boxId
     */
    private void updateBoxNeedValue(long uid, int boxId) {
        newbieTaskService.getUserGrowTask(uid, boxId).ifPresent(tmp -> {
            tmp.setNeedValue(TaskTool.getNewbieTaskBox(boxId).getScore());
            tmp.updateProgress((int) tmp.getValue());
            gameUserService.updateItem(tmp);
        });
    }

}