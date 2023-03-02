package com.bbw.god.gameuser.task.timelimit;

import com.bbw.common.DateUtil;
import com.bbw.common.ListUtil;
import com.bbw.common.PowerRandom;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.award.AwardService;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.privilege.PrivilegeService;
import com.bbw.god.gameuser.task.*;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 派遣类任务公共接口
 *
 * @author: suhq
 * @date: 2021/8/9 11:10 上午
 */
@Service
public class UserTimeLimitTaskService {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private UserDispatchTaskService userDispatchTaskService;
    @Autowired
    private AwardService awardService;
    @Autowired
    private PrivilegeService privilegeService;

    /**
     * 获取所有任务
     *
     * @param uid
     * @return
     */
    public List<UserTimeLimitTask> getAllTasks(long uid, TaskGroupEnum taskGroup) {
        List<UserTimeLimitTask> uts = gameUserService.getMultiItems(uid, UserTimeLimitTask.class);
        uts = uts.stream().filter(tmp -> tmp.getGroup() == taskGroup.getValue()).collect(Collectors.toList());
        return uts;
    }

    /**
     * 获得最大进行中任务数
     *
     * @param uid
     * @param taskGroup
     * @return
     */
    public int getMaxDoingTaskNum(long uid, TaskGroupEnum taskGroup) {
        //获得任务规则
        CfgTimeLimitTaskRules cfgTimeLimitTaskRules = TimeLimitTaskTool.getRules(taskGroup);
        //获得默认最大任务数量
        Integer maxTaskNum = cfgTimeLimitTaskRules.getMaxTaskNum();
        //检查是否有地灵印
        if (privilegeService.isOwnDiLing(uid)) {
            maxTaskNum += cfgTimeLimitTaskRules.getExtraTimesForDiLY();
        }
        //检查是否有天灵印
        if (privilegeService.isOwnTianLing(uid)) {
            maxTaskNum += cfgTimeLimitTaskRules.getExtraTimesForTianLY();
        }
        return maxTaskNum;
    }

    /**
     * 获取正在进行任务数量
     *
     * @param tasks
     * @return
     */
    public int getDoingTaskNum(List<UserTimeLimitTask> tasks) {
        if (ListUtil.isEmpty(tasks)) {
            return 0;
        }
        //检查可执行任务是否达到上限
        long taskNum = tasks.stream().filter(t -> t.getStatus() == TaskStatusEnum.DOING.getValue()).count();
        return (int) taskNum;
    }

    /**
     * 获取任务列表，不包含过期和已领取的活动
     *
     * @param uid
     * @param taskGroup
     * @return
     */
    public List<UserTimeLimitTask> getTasks(long uid, TaskGroupEnum taskGroup) {
        //获取限时任务信息，空则直接返回
        List<UserTimeLimitTask> uts = getAllTasks(uid, taskGroup).stream().filter(tmp -> tmp.getStatus() != TaskStatusEnum.AWARDED.getValue()).collect(Collectors.toList());
        //派礼小鹿任务去除上一次活动任务-》针对两次活动时间间隔过短
        if (ListUtil.isNotEmpty(uts) && TaskGroupEnum.PAI_LI_FAWN_51 == taskGroup) {
            List<Award> awards = new ArrayList<>(TimeLimitTaskTool.getRules(taskGroup).getDispatchTaskExtraAward().values()).get(0).getAwards();
            uts = uts.stream().filter(tmp -> ListUtil.isNotEmpty(tmp.getExtraAwards()) && awards.contains(tmp.getExtraAwards().get(0))).collect(Collectors.toList());
        }
        if (ListUtil.isEmpty(uts)) {
            return new ArrayList<>();
        }
        Date now = DateUtil.now();
        List<UserTimeLimitTask> timeOutTasks = new ArrayList<>();
        List<Integer> overflowSeconds = new ArrayList<>();
        //处理任务状态
        handleTaskStatus(uts, now, timeOutTasks, overflowSeconds);
        //是否有队列中的任务
        List<UserTimeLimitTask> queuingTask = uts.stream().filter(tmp -> tmp.getStatus() == TaskStatusEnum.QUEUING.getValue()).collect(Collectors.toList());
        if (ListUtil.isNotEmpty(queuingTask)) {
            //获得进行中任务
            List<UserTimeLimitTask> doingTask = uts.stream().filter(tmp -> tmp.getStatus() == TaskStatusEnum.DOING.getValue()).collect(Collectors.toList());
            //获得最大进行中任务
            int maxTaskNum = getMaxDoingTaskNum(uid, taskGroup);
            //需要新增进行中任务数量
            int needAddDoingTaskNum = maxTaskNum - doingTask.size() + overflowSeconds.size();
            //处理进行中任务
            handleQueuingTask(needAddDoingTaskNum, now, timeOutTasks, overflowSeconds, queuingTask);
        }
        //过期处理结算
        updateStatusForTimeout(timeOutTasks);
        return uts;
    }

    /**
     * 处理任务状态
     *
     * @param uts
     * @param now
     * @param timeOutTasks
     * @param overflowSeconds
     */
    private void handleTaskStatus(List<UserTimeLimitTask> uts, Date now, List<UserTimeLimitTask> timeOutTasks, List<Integer> overflowSeconds) {
        for (UserTimeLimitTask ut : uts) {
            TaskStatusEnum taskStatus = ut.gainTaskStatus(now);
            if (taskStatus != TaskStatusEnum.TIME_OUT) {
                continue;
            }
            timeOutTasks.add(ut);
            if (TaskStatusEnum.DOING.getValue() != ut.getStatus()) {
                continue;
            }
            int secondsBetween = (int) DateUtil.getSecondsBetween(ut.getTimeEnd(), now);
            overflowSeconds.add(secondsBetween);
        }
    }

    /**
     * 处理进行中任务
     *
     * @param needAddDoingTaskNum
     * @param now
     * @param timeOutTasks
     * @param overflowSeconds
     * @param queuingTask
     */
    private void handleQueuingTask(int needAddDoingTaskNum, Date now, List<UserTimeLimitTask> timeOutTasks, List<Integer> overflowSeconds, List<UserTimeLimitTask> queuingTask) {
        List<UserTimeLimitTask> autoTimeOutTask = new ArrayList<>();
        //进行中任务数量未到达最大值，唤醒队列中任务
        for (int i = 0; i < needAddDoingTaskNum; i++) {
            //获得队列中任务下标
            int queuingTaskIndex = i + autoTimeOutTask.size();
            if (queuingTaskIndex >= queuingTask.size()) {
                continue;
            }

            UserTimeLimitTask userTimeLimitTask = queuingTask.get(queuingTaskIndex);
            //获得派遣秒数
            int dispatchMinute = userDispatchTaskService.getDispatchTime(userTimeLimitTask);
            int dispatchSeconds = dispatchMinute * 60;
            if (ListUtil.isEmpty(overflowSeconds)) {
                userTimeLimitTask.addDispatchCards(userTimeLimitTask.getDispatchCards(), DateUtil.addSeconds(now, dispatchSeconds));
                continue;
            }
            //溢出时间按时间从大到小排序-
            int overflowMinuteIndex = queuingTaskIndex % overflowSeconds.size();
            if (0 == overflowMinuteIndex) {
                overflowSeconds.stream().sorted(Comparator.comparing(Integer::intValue).reversed());
            }
            Integer overflowMinute = overflowSeconds.get(overflowMinuteIndex);
            //是否有足够的溢出时间自动完成队列中任务

            if (overflowMinute >= dispatchSeconds) {
                userTimeLimitTask.addDispatchCards(userTimeLimitTask.getDispatchCards(), now);
                autoTimeOutTask.add(userTimeLimitTask);
                overflowSeconds.set(overflowMinuteIndex, overflowMinute - dispatchSeconds);
                i--;
                continue;
            }
            int emdTime = dispatchSeconds;
            //溢出时间不足以完成任务缩短任务完成时间
            if (0 < overflowMinute) {
                emdTime -= overflowMinute;
                overflowSeconds.set(overflowMinuteIndex, 0);
            }
            //队列中任务进入派遣状态
            userTimeLimitTask.addDispatchCards(userTimeLimitTask.getDispatchCards(), DateUtil.addSeconds(now, emdTime));
        }
        timeOutTasks.addAll(autoTimeOutTask);
    }

    /**
     * 获取单个任务
     * @param uid
     * @param taskDataId
     * @return
     */
    public UserTimeLimitTask getTask(long uid, long taskDataId) {
        Optional<UserTimeLimitTask> userTimeLimitTask = gameUserService.getUserData(uid, taskDataId, UserTimeLimitTask.class);
        return userTimeLimitTask.orElse(null);
    }
    /**
     * 任务是否可进行
     *
     * @param uid
     * @param taskId
     * @return
     */
    public void checkIsAble(long uid, long taskId) {
        //检查任务有效性
        Optional<UserTimeLimitTask> utOp = gameUserService.getUserData(uid, taskId, UserTimeLimitTask.class);
        if (!utOp.isPresent()) {
            throw ExceptionForClientTip.fromi18nKey("task.not.exist");
        }
        UserTimeLimitTask ut = utOp.get();
        TaskStatusEnum status = TaskStatusEnum.fromValue(ut.getStatus());
        if (TaskStatusEnum.TIME_OUT == status) {
            throw ExceptionForClientTip.fromi18nKey("task.time.out");
        } else if (status == TaskStatusEnum.ACCOMPLISHED || status == TaskStatusEnum.AWARDED) {
            throw ExceptionForClientTip.fromi18nKey("task.already.accomplished");
        } else if (status == TaskStatusEnum.FAIL) {
            throw ExceptionForClientTip.fromi18nKey("task.is.over");
        }
    }

    /**
     * 任务过期后的状态处理
     *
     * @param timeoutTask
     */
    public void updateStatusForTimeout(UserTimeLimitTask timeoutTask) {
        CfgTaskEntity taskEntity = TaskTool.getTaskEntity(TaskGroupEnum.fromValue(timeoutTask.getGroup()), timeoutTask.getBaseId());
        if (taskEntity.getType() == TaskTypeEnum.TIME_LIMIT_DISPATCH_TASK.getValue()
                && TaskStatusEnum.DOING.getValue() == timeoutTask.getStatus()) {
            userDispatchTaskService.updateDispatchResult(timeoutTask);
        } else {
            timeoutTask.setStatus(TaskStatusEnum.FAIL.getValue());
            gameUserService.updateItem(timeoutTask);
        }
    }

    /**
     * 任务过期后的状态批量处理
     *
     * @param timeoutTasks
     */
    public void updateStatusForTimeout(List<UserTimeLimitTask> timeoutTasks) {
        List<UserTimeLimitTask> dispatchTasks = new ArrayList<>();
        List<UserTimeLimitTask> noDispatchTasks = new ArrayList<>();
        //区分派遣任务和非派遣任务
        for (UserTimeLimitTask task : timeoutTasks) {
            CfgTaskEntity taskEntity = TaskTool.getTaskEntity(TaskGroupEnum.fromValue(task.getGroup()), task.getBaseId());
            if (taskEntity.getType() == TaskTypeEnum.TIME_LIMIT_DISPATCH_TASK.getValue() && TaskStatusEnum.DOING.getValue() == task.getStatus()) {
                dispatchTasks.add(task);
                continue;
            }
            //非派遣任务状态设置
            task.setStatus(TaskStatusEnum.FAIL.getValue());
            noDispatchTasks.add(task);
        }
        //更新派遣结果
        if (ListUtil.isNotEmpty(dispatchTasks)) {
            userDispatchTaskService.updateDispatchResult(dispatchTasks);
        }
        //更新非派遣任务
        if (ListUtil.isNotEmpty(noDispatchTasks)) {
            gameUserService.updateItems(noDispatchTasks);
        }
    }

    /**
     * 奖励下发
     *
     * @param ut
     * @return
     */
    public TaskStatusEnum doBeforeDispatchAward(UserTimeLimitTask ut) {
        //检查任务状态
        TaskStatusEnum taskStatus = ut.gainTaskStatus(DateUtil.now());
        if (TaskStatusEnum.DOING == taskStatus || TaskStatusEnum.WAITING == taskStatus) {
            throw ExceptionForClientTip.fromi18nKey("task.not.accomplish");
        } else if (TaskStatusEnum.AWARDED == taskStatus) {
            throw ExceptionForClientTip.fromi18nKey("task.already.award");
        }
        if (TaskStatusEnum.TIME_OUT == taskStatus) {
            updateStatusForTimeout(ut);
            taskStatus = ut.gainTaskStatus(DateUtil.now());
        }
        return taskStatus;
    }

    /**
     * 奖励下发
     *
     * @param ut
     * @param taskStatus 下发前的状态
     * @return
     */
    public RDCommon doDispatchAward(UserTimeLimitTask ut, TaskStatusEnum taskStatus) {
        if (TaskStatusEnum.AWARDED.getValue() == ut.getStatus()) {
            throw new ExceptionForClientTip("task.already.award");
        }
        RDCommon rd = new RDCommon();
        // 发放奖励
        List<Award> awards = new ArrayList<>();
        CfgTaskEntity taskEntity = TaskTool.getTaskEntity(TaskGroupEnum.fromValue(ut.getGroup()), ut.getBaseId());
        //派遣任务失败，10000铜钱安慰奖励
        if (TaskStatusEnum.FAIL == taskStatus && taskEntity.getType() == TaskTypeEnum.TIME_LIMIT_DISPATCH_TASK.getValue()) {
            awards.add(new Award(AwardEnum.TQ, 10000));
        } else {
            awards.addAll(taskEntity.getAwards());
            //处理额外奖励
            handleExtraAwards(ut, awards);
        }
        awardService.fetchAward(ut.getGameUserId(), awards, WayEnum.DISPATCH_TASK, "", rd);
        //更新任务状态
        ut.setStatus(TaskStatusEnum.AWARDED.getValue());
        gameUserService.updateItem(ut);
        return rd;
    }

    /**
     * 处理额外奖励
     *
     * @param ut
     * @param awards
     */
    public void handleExtraAwards(UserTimeLimitTask ut, List<Award> awards) {
        if (null == ut) {
            return;
        }
        if (ListUtil.isEmpty(ut.getExtraAwards())) {
            return;
        }
        //是否派遣模式二
        AbstractDispatchModeProcessor dispatchModeProcessorByTaskGroupEnum = userDispatchTaskService.getDispatchModeProcessorByTaskGroupEnum(TaskGroupEnum.fromValue(ut.getGroup()));
        boolean match = dispatchModeProcessorByTaskGroupEnum.isMatch(DispatchModeEnum.DISPATCH_TIME_MODE);
        if (match) {
            awards.addAll(ut.getExtraAwards());
            return;
        }
        //默认派遣模式
        int successRate = userDispatchTaskService.getSuccessRate(ut);
        if (successRate > 100 && PowerRandom.getRandomBetween(1, 100) <= (successRate - 100)) {
            awards.addAll(ut.getExtraAwards());
        }

    }

    /**
     * 重新开始活动
     *
     * @param uid
     * @param taskId
     * @return
     */
    public void reset(long uid, long taskId) {
        //检查任务有效性
        Optional<UserTimeLimitTask> utOp = gameUserService.getUserData(uid, taskId, UserTimeLimitTask.class);
        if (!utOp.isPresent()) {
            throw ExceptionForClientTip.fromi18nKey("task.not.exist");
        }
        UserTimeLimitTask ut = utOp.get();
        TaskStatusEnum status = TaskStatusEnum.fromValue(ut.getStatus());
        if (TaskStatusEnum.FAIL != status) {
            return;
        }
        ut.reset();
        gameUserService.updateItem(ut);
    }

}
