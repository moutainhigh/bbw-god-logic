package com.bbw.god.gameuser.task.main;

import com.bbw.cache.UserCacheService;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.task.CfgTaskEntity;
import com.bbw.god.gameuser.task.TaskGroupEnum;
import com.bbw.god.gameuser.task.TaskTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserMainTaskService {
    @Autowired
    private UserCacheService userCacheService;

    /**
     * 获取玩家单个主线任务记录，游戏业务逻辑必须调用该方法！！！<br/>
     * 不能直接使用GameUserService
     *
     * @param uid
     * @param taskId
     * @return
     */
    public UserMainTask getUserMainTask(long uid, int taskId) {
        UserMainTask userMainTask = this.userCacheService.getCfgItem(uid, taskId, UserMainTask.class);
        if (userMainTask == null) {
            CfgTaskEntity task = TaskTool.getTaskEntity(TaskGroupEnum.TASK_MAIN, taskId);
            userMainTask = UserMainTask.fromTask(uid, task);
            addUserMainTask(uid, userMainTask);
        }
        return userMainTask;
    }

    /**
     * 获取玩家所有主线任务记录，游戏业务逻辑必须调用该方法！！！<br/>
     * 不能直接使用GameUserService
     * @param uid
     * @return
     */
    public List<UserMainTask> getUserMainTasks(long uid) {
        List<UserMainTask> umTasks = this.userCacheService.getUserDatas(uid, UserMainTask.class);
        return umTasks;
    }

    /**
     * 添加玩家主线任务记录，游戏业务逻辑必须调用该方法！！！<br/>
     * 不能直接使用GameUserService
     * @param uid
     * @param userMainTask
     */
    public void addUserMainTask(long uid, UserMainTask userMainTask) {
        this.userCacheService.addUserData(userMainTask);
    }

    /**
     * 获得主线任务的奖励
     *
     * @param mainTaskId
     * @param awardIndex
     * @return
     */
    public Award getAward(int mainTaskId, int awardIndex) {
        switch (mainTaskId) {
            case 1100:
                int addedCopper = 0;
                if (awardIndex < 26) {
                    addedCopper = 10000;
                } else if (awardIndex < 46) {
                    addedCopper = 20000;
                } else if (awardIndex < 66) {
                    addedCopper = 30000;
                } else if (awardIndex < 81) {
                    addedCopper = 40000;
                } else {
                    addedCopper = 50000;
                }
                return new Award(AwardEnum.TQ, addedCopper);
            case 1200:
                return new Award(TreasureEnum.LBJQ.getValue(), AwardEnum.FB, 1);
            case 1300:
                int addedEle = 0;
                if (awardIndex < 11) {
                    addedEle = 1;
                } else if (awardIndex < 31) {
                    addedEle = 2;
                } else if (awardIndex < 51) {
                    addedEle = 3;
                } else if (awardIndex < 101) {
                    addedEle = 4;
                } else {
                    addedEle = 6;
                }
                return new Award(AwardEnum.YS, addedEle);
            default:
                return null;
        }
    }
}
