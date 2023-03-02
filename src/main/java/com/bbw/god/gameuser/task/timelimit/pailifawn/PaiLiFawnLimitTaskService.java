package com.bbw.god.gameuser.task.timelimit.pailifawn;

import com.bbw.common.CloneUtil;
import com.bbw.common.PowerRandom;
import com.bbw.god.game.award.Award;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.task.CfgTaskEntity;
import com.bbw.god.gameuser.task.TaskGroupEnum;
import com.bbw.god.gameuser.task.TaskTool;
import com.bbw.god.gameuser.task.timelimit.TimeLimitTaskTool;
import com.bbw.god.gameuser.task.timelimit.UserTimeLimitTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 派礼小鹿派遣任务
 *
 * @author: huanghb
 * @date: 2022/12/9 17:17
 */
@Service
public class PaiLiFawnLimitTaskService {
    @Autowired
    private GameUserService gameUserService;


    /**
     * 构建用户任务实例
     *
     * @param uid
     * @return
     */
    public List<UserTimeLimitTask> makeUserTaskInstance(long uid, int taskNum) {
        TaskGroupEnum taskGroupEnum = TaskGroupEnum.PAI_LI_FAWN_51;
        //获得任务配置信息
        List<CfgTaskEntity> cfgTaskEntities = TaskTool.getTasksByTaskGroupEnum(taskGroupEnum);
        //获取任务刷新概率
        List<Integer> probs = cfgTaskEntities.stream().map(CfgTaskEntity::getGenerateProb).collect(Collectors.toList());
        List<UserTimeLimitTask> userTimeLimitTasks = new ArrayList<>();
        for (int i = 0; i < taskNum; i++) {
            //随机任务
            int taskIndex = PowerRandom.hitProbabilityIndex(probs);
            CfgTaskEntity cfgTaskEntity = cfgTaskEntities.get(taskIndex);
            //随机任务技能
            List<Integer> extraRandomSkills = TimeLimitTaskTool.getExtraRandomSkills(taskGroupEnum, cfgTaskEntity);
            //随机额外奖励
            List<Award> extraRandomAward = new ArrayList<>();
            for (int j = 0; j < cfgTaskEntity.getDifficulty(); j += 10) {
                extraRandomAward.addAll(CloneUtil.cloneList(TimeLimitTaskTool.getExtraRandomAward(taskGroupEnum, cfgTaskEntity)));
            }
            //初始化
            UserTimeLimitTask instance = UserTimeLimitTask.instance(uid, taskGroupEnum, cfgTaskEntity, extraRandomSkills, extraRandomAward);
            userTimeLimitTasks.add(instance);

        }
        gameUserService.addItems(userTimeLimitTasks);
        return userTimeLimitTasks;
    }
}
