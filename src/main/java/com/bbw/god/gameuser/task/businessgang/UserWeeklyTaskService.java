package com.bbw.god.gameuser.task.businessgang;

import com.bbw.common.DateUtil;
import com.bbw.common.PowerRandom;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardService;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.businessgang.BusinessGangCfgTool;
import com.bbw.god.gameuser.businessgang.BusinessGangService;
import com.bbw.god.gameuser.businessgang.cfg.CfgPrestigeEntity;
import com.bbw.god.gameuser.businessgang.user.UserBusinessGangInfo;
import com.bbw.god.gameuser.task.CfgTaskEntity;
import com.bbw.god.gameuser.task.TaskGroupEnum;
import com.bbw.god.gameuser.task.TaskStatusEnum;
import com.bbw.god.gameuser.task.TaskTool;
import com.bbw.god.gameuser.task.businessgang.event.BusinessGangTaskEventPublisher;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 商帮周常任务服务
 *
 * @author fzj
 * @date 2022/1/18 16:57
 */
@Service
public class UserWeeklyTaskService {
    @Autowired
    GameUserService gameUserService;
    @Autowired
    AwardService awardService;
    // 无时间
    protected static final long NO_TIME = -1;

    /**
     * 获取所有任务
     *
     * @param uid
     * @return
     */
    public List<UserBusinessGangWeeklyTask> getAllTasks(long uid) {
        return gameUserService.getMultiItems(uid, UserBusinessGangWeeklyTask.class);
    }

    /**
     * 获取正在进行的任务
     *
     * @param uid
     * @return
     */
    public List<UserBusinessGangWeeklyTask> getTasks(long uid) {
        return getAllTasks(uid).stream().filter(t -> t.getStatus() == TaskStatusEnum.DOING.getValue()).collect(Collectors.toList());
    }

    /**
     * 获得指定任务
     *
     * @param uid
     * @param taskId
     * @return
     */
    public UserBusinessGangWeeklyTask getTask(long uid, int taskId) {
        UserBusinessGangWeeklyTask task = getAllTasks(uid).stream().filter(t -> t.getBaseId() == taskId).findFirst().orElse(null);
        if (null == task) {
            throw new ExceptionForClientTip("task.not.exist");
        }
        return task;
    }

    /**
     * 获得指定任务
     *
     * @param uid
     * @param dataId
     * @return
     */
    public UserBusinessGangWeeklyTask getTask(long uid, long dataId) {
        UserBusinessGangWeeklyTask task = getAllTasks(uid).stream().filter(t -> t.getId() == dataId).findFirst().orElse(null);
        if (null == task) {
            throw new ExceptionForClientTip("task.not.exist");
        }
        return task;
    }

    /**
     * 删除任务实例
     *
     * @param uid
     * @param dataId
     */
    public void delAllTask(long uid, long dataId) {
        UserBusinessGangWeeklyTask task = getTask(uid, dataId);
        gameUserService.deleteItem(task);
    }

    /**
     * 删除所有任务实例
     *
     * @param uid
     */
    public void delAllTask(long uid) {
        List<UserBusinessGangWeeklyTask> allTasks = getAllTasks(uid);
        gameUserService.deleteItems(uid, allTasks);
    }

    /**
     * 获取剩余时间
     *
     * @param task
     * @return
     */
    public long getRemainTime(UserBusinessGangWeeklyTask task) {
        Date generateTime = task.getGenerateTime();
        Date weekEndDateTime = DateUtil.getWeekEndDateTime(generateTime);
        Date refreshTime = DateUtil.addHours(weekEndDateTime, 8);
        return DateUtil.millisecondsInterval(refreshTime, DateUtil.now());
    }

    /**
     * 是否可以刷新
     *
     * @param uid
     * @return
     */
    public boolean canRefresh(long uid) {
        UserBusinessGangWeeklyTask task = getAllTasks(uid).stream().findFirst().orElse(null);
        if (null == task) {
            return false;
        }
        Date generateTime = task.getGenerateTime();
        long currentRemainTime = DateUtil.millisecondsInterval(DateUtil.now(), generateTime);
        long remainTime = task.getRemainTime();
        if (currentRemainTime - remainTime >= 0) {
            return true;
        }
        return false;
    }

    /**
     * 发送奖励
     *
     * @param uid
     * @param rd
     */
    public void sendAwards(long uid, long dataId, int businessGang, RDCommon rd) {
        UserBusinessGangWeeklyTask task = getTask(uid, dataId);
        Integer status = task.getStatus();
        if (status != TaskStatusEnum.ACCOMPLISHED.getValue()) {
            throw new ExceptionForClientTip("task.not.accomplish");
        }
        //声望配置
        CfgPrestigeEntity prestigeEntity = BusinessGangCfgTool.getPrestigeEntity(businessGang);
        //获取奖励
        CfgTaskEntity taskEntity = TaskTool.getTaskEntity(TaskGroupEnum.BUSINESS_GANG_WEEKLY_TASK, task.getBaseId());
        List<Award> awards = taskEntity.getAwards();
        for (Award award : awards) {
            Integer awardId = award.getAwardId();
            if (0 != awardId) {
                continue;
            }
            award.setAwardId(prestigeEntity.getPrestigeId());
        }
        //发布完成商帮任务事件
        BusinessGangTaskEventPublisher.pubBusinessGangTaskAchievedEvent(uid, taskEntity.getId(), TaskGroupEnum.BUSINESS_GANG_WEEKLY_TASK);
        //发送奖励
        awardService.fetchAward(uid, awards, WayEnum.BUSINESS_GANG_WEEKLY_TASK, "", rd);
        //更改任务状态
        task.setStatus(TaskStatusEnum.AWARDED.getValue());
        gameUserService.updateItem(task);
    }

    /**
     * 构建任务实例
     *
     * @param uid
     */
    public void makeUserTaskInstance(long uid, Integer currentTaskId) {
        //随机一个任务
        List<CfgTaskEntity> cfgTaskEntities = TaskTool.getTasksByTaskGroupEnum(TaskGroupEnum.BUSINESS_GANG_WEEKLY_TASK);
        if (null != currentTaskId){
            cfgTaskEntities.removeIf(c -> currentTaskId.equals(c.getId()));
        }
        CfgTaskEntity taskEntity = PowerRandom.getRandomFromList(cfgTaskEntities);
        UserBusinessGangWeeklyTask weeklyTask = UserBusinessGangWeeklyTask.getInstance(uid, TaskGroupEnum.BUSINESS_GANG_WEEKLY_TASK, taskEntity);
        //设置剩余时间
        long remainTime = getRemainTime(weeklyTask);
        weeklyTask.setRemainTime(remainTime);
        gameUserService.addItem(uid, weeklyTask);
    }
}
