package com.bbw.god.gameuser.task.timelimit.newyearandchrist;

import com.bbw.common.DateUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.activity.ActivityService;
import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.activity.holiday.processor.HolidayCunZYiYunProcessor;
import com.bbw.god.cache.TimeLimitCacheUtil;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.game.maou.GameMaouAttacker;
import com.bbw.god.game.maou.GameMaouAttackerService;
import com.bbw.god.gameuser.task.*;
import com.bbw.god.gameuser.task.timelimit.UserDispatchTaskService;
import com.bbw.god.gameuser.task.timelimit.UserTimeLimitTask;
import com.bbw.god.gameuser.task.timelimit.UserTimeLimitTaskService;
import com.bbw.god.gameuser.treasure.TreasureChecker;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.rd.RDCommon;
import com.bbw.god.rd.RDSuccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * 双旦节任务
 *
 * @author fzj
 * @date 2021/11/19 8:55
 */
@Service
public class NewYearAndChristTaskProcessor extends AbstractTaskProcessor {
    @Autowired
    private UserNewYearAndChristTaskService userNewYearAndChristTaskService;
    @Autowired
    private UserDispatchTaskService userDispatchTaskService;
    @Autowired
    private UserTimeLimitTaskService userTimeLimitTaskService;
    @Autowired
    private HolidayCunZYiYunProcessor holidayCunZYiYun;
    @Autowired
    private GameMaouAttackerService gameMaouAttackerService;
    @Autowired
    private ActivityService activityService;


    public NewYearAndChristTaskProcessor() {
        this.taskTypes = Arrays.asList(TaskTypeEnum.NEW_YEAR_AND_CHRISTMAS_TASK);
    }

    @Override
    public RDTaskList getTasks(long uid, Integer days) {
        return new RDTaskList();
    }

    @Override
    public RDCommon gainTaskAward(long uid, int id, String awardIndex) {
        return new RDCommon();
    }

    /**
     * 获取任务信息
     *
     * @param uid
     * @return
     */
    public RDSuccess getTask(long uid) {
        if (!isJoined(uid)) {
            CfgTaskEntity taskEntity = TaskTool.getTaskEntity(TaskGroupEnum.NEW_YEAR_AND_CHRISTMAS_TASK, 130001);
            UserTimeLimitTask userTimeLimitTask = userNewYearAndChristTaskService.makeUserTaskInstance(uid, taskEntity);
            gameUserService.addItem(uid, userTimeLimitTask);
            setJoinStatus(uid);
        }
        List<UserTimeLimitTask> uts = userNewYearAndChristTaskService.getAllTasks(uid);
        UserTimeLimitTask ut = uts.get(uts.size() - 1);
        CfgTaskEntity taskEntity = TaskTool.getTaskEntity(TaskGroupEnum.NEW_YEAR_AND_CHRISTMAS_TASK, ut.getBaseId());
        Integer tasksAllNum = TaskTool.getTasksByTaskGroupEnum(TaskGroupEnum.NEW_YEAR_AND_CHRISTMAS_TASK).size();
        boolean executable = holidayCunZYiYun.isExecutable(uid, taskEntity, tasksAllNum);
        if (taskEntity.getType() == TaskTypeEnum.TIME_LIMIT_DISPATCH_TASK.getValue()) {
            return userDispatchTaskService.doGetDispatchInfo(ut, executable);
        }
        RDTaskItem rdTask = RDTaskItem.getInstance(ut, taskEntity, TaskGroupEnum.NEW_YEAR_AND_CHRISTMAS_TASK);
        String[] titleFormats = {String.valueOf(ut.getValue())};
        rdTask.setTitleFormats(titleFormats);
        //判断当前任务是否可以执行
        if (!executable) {
            rdTask.setIsExecutable(1);
        }
        RDTaskInfo rd = new RDTaskInfo();
        rd.setTaskItem(rdTask);
        return rd;
    }

    /**
     * 领奖励
     *
     * @param ut
     * @return
     */
    public RDCommon gainTaskAward(UserTimeLimitTask ut) {
        long uid = ut.getGameUserId();
        CfgTaskEntity taskEntity = TaskTool.getTaskEntity(TaskGroupEnum.fromValue(ut.getGroup()), ut.getBaseId());
        Integer tasksAllNum = TaskTool.getTasksByTaskGroupEnum(TaskGroupEnum.NEW_YEAR_AND_CHRISTMAS_TASK).size();
        //检查是否可以生成下一个任务
        if (!holidayCunZYiYun.isExecutable(uid, taskEntity, tasksAllNum)) {
            return new RDCommon();
        }
        TaskStatusEnum taskStatus = userTimeLimitTaskService.doBeforeDispatchAward(ut);
        RDCommon rd = userTimeLimitTaskService.doDispatchAward(ut, taskStatus);
        // 生成下一个任务
        int taskSeq = taskEntity.getSeq() + 1;
        CfgTaskEntity nextTask = TaskTool.getTask(TaskGroupEnum.fromValue(ut.getGroup()), taskEntity.getSeqGroup(), taskSeq);
        if (null == nextTask) {
            return rd;
        }
        UserTimeLimitTask nextUt = userNewYearAndChristTaskService.makeUserTaskInstance(uid, nextTask);
        gameUserService.addItem(uid, nextUt);
        synchronizeAttackMaouTask(uid, nextUt);
        return rd;
    }

    /**
     * 同步攻打魔王血量任务进度
     *
     * @param uid
     */
    private void synchronizeAttackMaouTask(long uid, UserTimeLimitTask ut) {
        if (ut.getBaseId() != 130041) {
            return;
        }
        int sid = gameUserService.getActiveSid(uid);
        IActivity gameActivity = activityService.getGameActivity(sid, ActivityEnum.RESIST_DEVIL);
        GameMaouAttacker userMaouAttacker = gameMaouAttackerService.getOrCreateAttacker(uid, gameActivity);
        ut.addValue(userMaouAttacker.getAttackBlood());
        gameUserService.updateItem(ut);
    }

    /**
     * 消耗指定道具
     *
     * @param taskDataId
     * @return
     */
    public RDCommon consumeTreasures(long uid, long taskDataId) {
        UserTimeLimitTask task = userTimeLimitTaskService.getTask(uid, taskDataId);
        if (null == task || task.getStatus() != TaskStatusEnum.DOING.getValue()) {
            throw new ExceptionForClientTip("task.not.exist");
        }
        int num = task.getNeedValue();
        TreasureChecker.checkIsEnough(TreasureEnum.CUNZ_COIN.getValue(), num, uid);
        RDCommon rd = new RDCommon();
        TreasureEventPublisher.pubTDeductEvent(uid, TreasureEnum.CUNZ_COIN.getValue(), num, WayEnum.CUNZ_YIYUN, rd);
        return rd;
    }

    /**
     * 判断是否参加双旦村庄任务
     *
     * @param uid
     * @return
     */
    private boolean isJoined(long uid) {
        Integer hasJoinNewYearAndChristmasTask = TimeLimitCacheUtil.getFromCache(uid, "hasJoinNewYearAndChristmasTask", Integer.class);
        return null != hasJoinNewYearAndChristmasTask && hasJoinNewYearAndChristmasTask == 1;
    }

    /**
     * 保存双旦任务村庄状态
     *
     * @param uid
     */
    private void setJoinStatus(long uid) {
        TimeLimitCacheUtil.cacheBothLocalAndRedis(uid, "hasJoinNewYearAndChristmasTask", 1, DateUtil.SECOND_ONE_DAY * 12);
    }
}
