package com.bbw.god.gameuser.task.timelimit;

import com.bbw.common.DateUtil;
import com.bbw.common.ListUtil;
import com.bbw.common.PowerRandom;
import com.bbw.god.game.award.RDAward;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.card.UserCard;
import com.bbw.god.gameuser.card.UserCardService;
import com.bbw.god.gameuser.task.*;
import com.bbw.god.gameuser.task.timelimit.event.TimeLimitTaskEventPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 派遣类任务公共接口
 *
 * @author: suhq
 * @date: 2021/8/9 11:10 上午
 */
@Service
public class UserDispatchTaskService {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private UserCardService userCardService;
    @Autowired
    private UserDispatchCardVigorService dispatchCardVigorService;
    @Autowired
    @Lazy
    private List<AbstractDispatchModeProcessor> dispatchModeProcessors;

    /**
     * 更新派发任务的结果
     *
     * @param ut
     */
    public void updateDispatchResult(UserTimeLimitTask ut) {
        TaskGroupEnum taskGroup = TaskGroupEnum.fromValue(ut.getGroup());
        CfgTaskEntity taskEntity = TaskTool.getTaskEntity(taskGroup, ut.getBaseId());
        if (null == taskEntity || taskEntity.getType() != TaskTypeEnum.TIME_LIMIT_DISPATCH_TASK.getValue()) {
            return;
        }
        int successRate = getSuccessRate(ut);
        int random = PowerRandom.getRandomBetween(1, 100);
        if (successRate >= 100 || random <= successRate) {
            ut.setStatus(TaskStatusEnum.ACCOMPLISHED.getValue());
            TimeLimitTaskEventPublisher.pubTimeLimitTaskAchievedEvent(ut.getGameUserId(), taskGroup, ut.getBaseId());
        } else {
            ut.setStatus(TaskStatusEnum.FAIL.getValue());
        }
        gameUserService.updateItem(ut);
    }

    /**
     * 批量更新派发任务的结果
     *
     * @param uts
     */
    public void updateDispatchResult(List<UserTimeLimitTask> uts) {
        for (UserTimeLimitTask task : uts) {
            TaskGroupEnum taskGroup = TaskGroupEnum.fromValue(task.getGroup());
            CfgTaskEntity taskEntity = TaskTool.getTaskEntity(taskGroup, task.getBaseId());
            if (null == taskEntity || taskEntity.getType() != TaskTypeEnum.TIME_LIMIT_DISPATCH_TASK.getValue()) {
                return;
            }
            int successRate = getSuccessRate(task);
            int random = PowerRandom.getRandomBetween(1, 100);
            if (successRate >= 100 || random <= successRate) {
                task.setStatus(TaskStatusEnum.ACCOMPLISHED.getValue());
                TimeLimitTaskEventPublisher.pubTimeLimitTaskAchievedEvent(task.getGameUserId(), taskGroup, task.getBaseId());
            } else {
                task.setStatus(TaskStatusEnum.FAIL.getValue());
            }
        }
        gameUserService.updateItems(uts);
    }

    /**
     * 获取成功率
     *
     * @param dispatchTask
     * @return
     */
    public int getSuccessRate(UserTimeLimitTask dispatchTask) {
        AbstractDispatchModeProcessor dispatchModeProcessor = getDispatchModeProcessor(dispatchTask);
        int successRate = dispatchModeProcessor.getSuccessRate(dispatchTask);
        return successRate;
    }

    /**
     * 获取派遣时间
     *
     * @param dispatchTask
     * @return
     */
    public Date getDispatchDate(UserTimeLimitTask dispatchTask) {
        AbstractDispatchModeProcessor dispatchModeProcessor = getDispatchModeProcessor(dispatchTask);
        Date dispatchDate = dispatchModeProcessor.getDispatchDate(dispatchTask);
        return dispatchDate;
    }

    /**
     * 获取派遣时间
     *
     * @param dispatchTask
     * @return
     */
    public int getDispatchTime(UserTimeLimitTask dispatchTask) {
        AbstractDispatchModeProcessor dispatchModeProcessor = getDispatchModeProcessor(dispatchTask);
        int dispatchTime = dispatchModeProcessor.getDispatchMinute(dispatchTask);
        return dispatchTime;
    }


    /**
     * 执行派遣信息获取
     *
     * @param ut
     * @param executable 任务是否可以执行（给客户端的标识）
     * @return
     */
    public RDDispatchInfo doGetDispatchInfo(UserTimeLimitTask ut, boolean executable) {
        long uid = ut.getGameUserId();
        //检查任务状态
        TaskStatusEnum taskStatus = ut.gainTaskStatus(DateUtil.now());
        if (TaskStatusEnum.TIME_OUT == taskStatus) {
            if (TaskStatusEnum.DOING.getValue() == ut.getStatus()) {
                updateDispatchResult(ut);
                taskStatus = ut.gainTaskStatus(DateUtil.now());
            } else {
                taskStatus = TaskStatusEnum.FAIL;
                ut.setStatus(taskStatus.getValue());
                gameUserService.updateItem(ut);
            }
        }
        TaskGroupEnum taskGroup = TaskGroupEnum.fromValue(ut.getGroup());
        CfgTaskEntity taskEntity = TaskTool.getTaskEntity(taskGroup, ut.getBaseId());
        CfgDispatchTaskRules dispatchRule = TimeLimitTaskTool.getDispatchRule(taskGroup, ut.getBaseId());
        RDDispatchInfo rd = new RDDispatchInfo();
        rd.setId(ut.getBaseId());
        rd.setDataId(ut.getId());
        rd.setNeedDice(dispatchRule.getNeedDice());
        rd.setNeedCardVigor(dispatchRule.getNeedCardVigor());
        rd.setNeedStar(dispatchRule.getNeedStar());
        rd.setSkills(ut.getExtraSkills());
        rd.setAwards(RDAward.getInstances(taskEntity.getAwards()));
        rd.setExtAwards(RDAward.getInstances(ut.getExtraAwards()));
        rd.setStatus(ut.getStatus());
        Integer dispatchTime = TimeLimitTaskTool.getDispatchRule(taskGroup, ut.getBaseId()).getDispatchTime();
        rd.setCostTime(dispatchTime * 60 * 1000);
        rd.setRemainTime(ut.getTimeEnd().getTime() - System.currentTimeMillis());
        if (ListUtil.isNotEmpty(ut.getDispatchCards())) {
            List<UserCard> userCards = userCardService.getUserCards(uid, ut.getDispatchCards());
            UserCardVigor userCardVigor = gameUserService.getSingleItem(uid, UserCardVigor.class);
            for (int dispatchCard : ut.getDispatchCards()) {
                UserCard userCard = userCards.stream().filter(tmp -> tmp.getBaseId() == dispatchCard).findFirst().orElse(null);
                if (null == userCard) {
                    continue;
                }
                int cardVigor = dispatchCardVigorService.getCardVigor(taskGroup, userCard, userCardVigor);
                int maxCardVigor = TimeLimitTaskTool.getMaxCardVigor(userCard.getBaseId(), userCard.getHierarchy());
                rd.addCardVigor(dispatchCard, cardVigor, maxCardVigor);
            }
        }
        int successRate = getSuccessRate(ut);
        rd.setSuccessRate(successRate);
        if (successRate > 100) {
            rd.setExtAwardRate(successRate - 100);
        }
        if (!executable) {
            rd.setIsExecutable(1);
        }
        return rd;
    }


    /**
     * 获取正在排队中任务数量
     *
     * @param tasks
     * @return
     */
    public int getQueuingTaskNum(List<UserTimeLimitTask> tasks) {
        if (ListUtil.isEmpty(tasks)) {
            return 0;
        }
        //检查可执行任务是否达到上限
        long taskNum = tasks.stream().filter(t -> t.getStatus() == TaskStatusEnum.QUEUING.getValue()).count();
        return (int) taskNum;
    }

    /**
     * 获取派遣模式服务
     *
     * @param dispatchTask
     * @return
     */
    private AbstractDispatchModeProcessor getDispatchModeProcessor(UserTimeLimitTask dispatchTask) {
        TaskGroupEnum taskGroup = TaskGroupEnum.fromValue(dispatchTask.getGroup());
        return getDispatchModeProcessorByTaskGroupEnum(taskGroup);
    }

    /**
     * 获取派遣模式服务
     *
     * @param taskGroup
     * @return
     */
    public AbstractDispatchModeProcessor getDispatchModeProcessorByTaskGroupEnum(TaskGroupEnum taskGroup) {
        CfgTimeLimitTaskRules rules = TimeLimitTaskTool.getRules(taskGroup);
        DispatchModeEnum dispatchMode = DispatchModeEnum.fromValue(rules.getDispatchMode());
        return dispatchModeProcessors.stream().filter(mp -> mp.isMatch(dispatchMode)).findFirst().orElse(null);
    }
}
