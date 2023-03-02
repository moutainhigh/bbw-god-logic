package com.bbw.god.gameuser.task.timelimit.businessGang;

import com.bbw.common.PowerRandom;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardService;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.businessgang.BusinessGangCfgTool;
import com.bbw.god.gameuser.businessgang.BusinessGangService;
import com.bbw.god.gameuser.businessgang.UserBusinessGangService;
import com.bbw.god.gameuser.businessgang.cfg.CfgPrestigeEntity;
import com.bbw.god.gameuser.businessgang.user.UserBusinessGangTaskInfo;
import com.bbw.god.gameuser.task.*;
import com.bbw.god.gameuser.task.businessgang.event.BusinessGangTaskEventPublisher;
import com.bbw.god.gameuser.task.timelimit.TimeLimitTaskTool;
import com.bbw.god.gameuser.task.timelimit.UserTimeLimitTask;
import com.bbw.god.gameuser.task.timelimit.UserTimeLimitTaskService;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 商帮派遣任务服务类
 *
 * @author fzj
 * @date 2022/1/19 9:29
 */
@Service
public class UserBusinessGangLimitTaskService {
    @Autowired
    private UserTimeLimitTaskService userTimeLimitTaskService;
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private AwardService awardService;
    @Autowired
    private BusinessGangService businessGangService;
    @Autowired
    private UserBusinessGangService userBusinessGangService;

    /**
     * 获取任务列表
     *
     * @param uid
     * @return
     */
    public List<UserTimeLimitTask> getAllTasks(long uid) {
        return userTimeLimitTaskService.getAllTasks(uid, TaskGroupEnum.BUSINESS_GANG_DISPATCH_TASK);
    }

    /**
     * 获取指定任务
     *
     * @param uid
     * @param taskId
     * @return
     */
    public UserTimeLimitTask getTask(long uid, int taskId) {
        return getAllTasks(uid).stream().filter(t -> t.getBaseId() == taskId).findFirst().orElse(null);
    }

    /**
     * 获取指定任务
     *
     * @param uid
     * @param dataId
     * @return
     */
    public UserTimeLimitTask getTask(long uid, long dataId) {
        UserTimeLimitTask task = getAllTasks(uid).stream().filter(t -> t.getId() == dataId).findFirst().orElse(null);
        if (null == task) {
            throw new ExceptionForClientTip("task.not.exist");
        }
        return task;
    }

    /**
     * 获取任务列表,不包含过期和已领取的活动
     *
     * @param uid
     * @return
     */
    public List<UserTimeLimitTask> getTasks(long uid) {
        return userTimeLimitTaskService.getTasks(uid, TaskGroupEnum.BUSINESS_GANG_DISPATCH_TASK);
    }

    /**
     * 删除任务实例
     *
     * @param uid
     * @param dataId
     */
    public void delTask(long uid, long dataId) {
        UserTimeLimitTask task = getTask(uid, dataId);
        gameUserService.deleteItem(task);
    }

    /**
     * 发送奖励
     *
     * @param uid
     * @param rd
     */
    public void sendAwards(long uid, long dataId, int businessGang, UserBusinessGangTaskInfo gangTask, RDCommon rd) {
        UserTimeLimitTask task = getTask(uid, dataId);
        Integer status = task.getStatus();
        if (status != TaskStatusEnum.ACCOMPLISHED.getValue()){
            throw new ExceptionForClientTip("task.not.exist");
        }
        Integer awardableNum = gangTask.getAwardableNum();
        if (awardableNum <= 0){
            throw new ExceptionForClientTip("businessGang.not.awardable.num ");
        }
        //声望配置
        CfgPrestigeEntity prestigeEntity = BusinessGangCfgTool.getPrestigeEntity(businessGang);
        //获取奖励
        CfgTaskEntity taskEntity = TaskTool.getTaskEntity(TaskGroupEnum.BUSINESS_GANG_DISPATCH_TASK, task.getBaseId());
        List<Award> awards = taskEntity.getAwards();
        for (Award award : awards) {
            Integer awardId = award.getAwardId();
            if (0 != awardId) {
                continue;
            }
            award.setAwardId(prestigeEntity.getPrestigeId());
        }
        awardService.fetchAward(uid, awards, WayEnum.BUSINESS_GANG_DISPATCH_TASK, "", rd);
        //发布完成商帮任务事件
        BusinessGangTaskEventPublisher.pubBusinessGangTaskAchievedEvent(uid, taskEntity.getId(), TaskGroupEnum.BUSINESS_GANG_DISPATCH_TASK);
        //删除任务实例
        delTask(uid, task.getId());
        //刷新的任务
        businessGangService.generateTask(uid);
    }

    /**
     * 构建用户任务实例
     *
     * @param uid
     * @return
     */
    public void makeUserTaskInstance(long uid, int difficulty) {
        List<CfgTaskEntity> cfgTaskEntities = TaskTool.getTasksByTaskGroupEnum(TaskGroupEnum.BUSINESS_GANG_DISPATCH_TASK)
                .stream().filter(t -> t.getDifficulty() == difficulty).collect(Collectors.toList());
        CfgTaskEntity taskEntity = PowerRandom.getRandomFromList(cfgTaskEntities);
        List<Integer> extraRandomSkills = TimeLimitTaskTool.getExtraRandomSkills(TaskGroupEnum.BUSINESS_GANG_DISPATCH_TASK, taskEntity);
        UserTimeLimitTask instance = UserTimeLimitTask.instance(uid, TaskGroupEnum.BUSINESS_GANG_DISPATCH_TASK, taskEntity, extraRandomSkills, null);
        gameUserService.addItem(uid, instance);
    }
}
