package com.bbw.god.gameuser.task;

import com.bbw.common.ListUtil;
import com.bbw.common.SpringContextUtil;
import com.bbw.god.game.award.Award;
import com.bbw.god.gameuser.task.activitytask.UserActivityDailyTask;
import com.bbw.god.gameuser.task.biggodplan.UserBigGodPlanTask;
import com.bbw.god.gameuser.task.brocadegift.UserBrocadeGiftDailyTask;
import com.bbw.god.gameuser.task.businessgang.UserBusinessGangWeeklyTask;
import com.bbw.god.gameuser.task.businessgang.yingjie.UserBusinessGangYingJieTask;
import com.bbw.god.gameuser.task.godtraining.UserGodTrainingTask;
import com.bbw.god.gameuser.task.halloweenRestaurant.UserHalloweenRestaurantDailyTask;
import com.bbw.god.gameuser.task.sxdhchallenge.UserSxdhSeasonTask;
import com.bbw.god.gameuser.task.timelimit.TimeLimitTaskTool;
import com.bbw.god.gameuser.task.timelimit.UserDispatchTaskService;
import com.bbw.god.gameuser.task.timelimit.UserTimeLimitTask;
import com.bbw.god.rd.item.RDAchievableItem;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author suchaobin
 * @description 任务返回基类
 * @date 2020/10/13 10:14
 **/
@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
public class RDTaskItem extends RDAchievableItem implements Serializable {
    private static UserDispatchTaskService userDispatchTaskService = SpringContextUtil.getBean(UserDispatchTaskService.class);
    private static final long serialVersionUID = 1L;
    private Integer inFsHelper = null;// 是都追踪 0否 1是
    protected Integer fsTaskType = null;
    private Integer days = null;// 第几天，回归任务中用到
    private Long remainTime = null;// 倒计时
    private Integer costTime = null;// 任务时长
    protected Integer successRate = null;//派遣成功率
    protected List<Integer> dispatchCardIds = null;//派遣卡牌
    /** 任务是否可以执行 0可以 1不可以 */
    private Integer isExecutable = 0;
    /** 村庄疑云卡牌信息 */
    private RDTimeLimitFightCardInfo limitFightCardInfos;

    public static RDTaskItem getInstance(Integer id, Integer status, Integer progress, Integer totalProgress,
                                         List<Award> awards, Integer inFsHelper, Integer fsTaskType, Integer days) {
        RDTaskItem item = new RDTaskItem();
        item.setInFsHelper(inFsHelper);
        item.setId(id);
        item.setStatus(status);
        item.setProgress(progress);
        item.setFsTaskType(fsTaskType);
        item.setTotalProgress(totalProgress);
        item.setType(fsTaskType);
        item.setAwards(awards);
        item.setDays(days);
        return item;
    }

    public static RDTaskItem getInstance(UserGodTrainingTask task, List<Award> awards) {
        RDTaskItem item = new RDTaskItem();
        item.setInFsHelper(1);
        item.setId(task.getBaseId());
        item.setStatus(task.getStatus());
        item.setProgress((int) task.getValue());
        item.setFsTaskType(task.getTaskType());
        item.setTotalProgress(task.getNeedValue());
        item.setType(task.getTaskType());
        item.setAwards(awards);
        return item;
    }

    public static RDTaskItem getInstance(UserBigGodPlanTask task, List<Award> awards) {
        RDTaskItem item = new RDTaskItem();
        item.setInFsHelper(1);
        item.setId(task.getTaskId());
        item.setStatus(task.getStatus());
        item.setProgress((int) task.getValue());
        item.setFsTaskType(task.getTaskType());
        item.setTotalProgress(task.getNeedValue());
        item.setType(task.getTaskType());
        item.setAwards(awards);
        return item;
    }

    public static RDTaskItem getInstance(UserBrocadeGiftDailyTask task, List<Award> awards) {
        RDTaskItem item = new RDTaskItem();
        item.setInFsHelper(0);
        item.setId(task.getTaskId());
        item.setStatus(task.getStatus());
        item.setProgress((int) task.getProgress());
        item.setFsTaskType(task.getTaskType());
        item.setTotalProgress(task.getNeedProgress());
        item.setType(task.getTaskType());
        item.setAwards(awards);
        return item;
    }

    public static RDTaskItem getInstance(UserHalloweenRestaurantDailyTask task, List<Award> awards) {
        RDTaskItem item = new RDTaskItem();
        item.setInFsHelper(0);
        item.setId(task.getTaskId());
        item.setStatus(task.getStatus());
        item.setFsTaskType(task.getTaskType());
        CfgTaskEntity cfgTaskEntity = TaskTool.getDailyTask(task.getTaskId());
        item.setProgress((int) task.getProgress() - task.getNeedProgress() + cfgTaskEntity.getValue());
        item.setTotalProgress(cfgTaskEntity.getValue());
        item.setType(task.getTaskType());
        item.setAwards(awards);
        return item;
    }

    public static RDTaskItem getInstance(UserActivityDailyTask task, List<Award> awards) {
        RDTaskItem item = new RDTaskItem();
        item.setInFsHelper(0);
        item.setId(task.getTaskId());
        item.setStatus(task.getStatus());
        int progress = task.getStatus() == TaskStatusEnum.AWARDED.getValue() ? task.getNeedProgress() : (int) task.getProgress();
        CfgTaskEntity cfgTaskEntity = TaskTool.getDailyTask(task.getTaskId());

        int ableAccomplishProgress = (cfgTaskEntity.getTimesLimit() - task.getTimesOfAccomplish()) * task.getNeedProgress();
        progress = progress > ableAccomplishProgress ? ableAccomplishProgress : progress;
        item.setProgress(progress);
        item.setFsTaskType(task.getTaskType());
        item.setTotalProgress(task.getNeedProgress());
        item.setType(task.getTaskType());
        item.setAwards(awards);
        return item;
    }

    public static RDTaskItem getInstance(UserBusinessGangYingJieTask task, List<Award> awards) {
        RDTaskItem item = new RDTaskItem();
        item.setInFsHelper(0);
        item.setId(task.getTaskId());
        item.setStatus(task.getStatus());
        item.setProgress((int) task.getProgress());
        item.setFsTaskType(task.getTaskType());
        item.setTotalProgress(task.getNeedProgress());
        item.setType(task.getTaskType());
        item.setAwards(awards);
        return item;
    }

    public static RDTaskItem getInstance(UserTimeLimitTask ut, CfgTaskEntity taskEntity, TaskGroupEnum taskGroupEnum) {
        RDTaskItem rdTaskItem = new RDTaskItem();
        rdTaskItem.setDataId(ut.getId());
        rdTaskItem.setId(ut.getBaseId());
        rdTaskItem.setProgress((int) ut.getValue());
        rdTaskItem.setTotalProgress(ut.getNeedValue());
        List<Award> awards = new ArrayList<>();
        awards.addAll(taskEntity.getAwards());
        //处理额外奖励
        handleExtraAwards(ut, awards);
        rdTaskItem.setAwards(awards);
        rdTaskItem.setStatus(ut.getStatus());
        rdTaskItem.setRemainTime(ut.getTimeEnd().getTime() - System.currentTimeMillis());
        if (taskEntity.getType() == TaskTypeEnum.TIME_LIMIT_DISPATCH_TASK.getValue()) {
            Integer dispatchTime = TimeLimitTaskTool.getDispatchRule(taskGroupEnum, ut.getBaseId()).getDispatchTime();
            rdTaskItem.setCostTime(dispatchTime * 60 * 1000);
            rdTaskItem.setDispatchCardIds(ut.getDispatchCards());
            rdTaskItem.setSuccessRate(userDispatchTaskService.getSuccessRate(ut));
        }
        String[] titleFormats = {String.valueOf(ut.getValue())};
        rdTaskItem.setTitleFormats(titleFormats);
        return rdTaskItem;
    }

    /**
     * 处理额外奖励
     *
     * @param ut
     * @param awards
     */
    private static void handleExtraAwards(UserTimeLimitTask ut, List<Award> awards) {
        if (null == ut) {
            return;
        }
        if (ListUtil.isEmpty(ut.getExtraAwards())) {
            return;
        }
        //派遣小鹿添加额外奖励显示
        if (TaskGroupEnum.PAI_LI_FAWN_51.getValue() == ut.getGroup()) {
            awards.addAll(ut.getExtraAwards());
        }
    }

    public static RDTaskItem getInstance(UserSxdhSeasonTask seasonTask, CfgTaskEntity taskEntity) {
        RDTaskItem rdTask = new RDTaskItem();
        rdTask.setId(seasonTask.getBaseId());
        rdTask.setProgress((int) seasonTask.getValue());
        rdTask.setTotalProgress(seasonTask.getNeedValue());
        rdTask.setAwards(taskEntity.getAwards());
        rdTask.setStatus(seasonTask.getStatus());
        return rdTask;
    }

    public static RDTaskItem getInstance(UserGodTrainingTask trainingTask){
        RDTaskItem rdTask = new RDTaskItem();
        rdTask.setId(trainingTask.getBaseId());
        rdTask.setStatus(trainingTask.getStatus());
        rdTask.setProgress((int) trainingTask.getValue());
        rdTask.setTotalProgress(trainingTask.getNeedValue());
        return rdTask;
    }

    public static RDTaskItem getInstance(UserBusinessGangWeeklyTask weeklyTask, CfgTaskEntity taskEntity){
        RDTaskItem rdTask = new RDTaskItem();
        rdTask.setId(weeklyTask.getBaseId());
        rdTask.setDataId(weeklyTask.getId());
        rdTask.setProgress((int) weeklyTask.getValue());
        rdTask.setTotalProgress(weeklyTask.getNeedValue());
        rdTask.setAwards(taskEntity.getAwards());
        rdTask.setMemo(taskEntity.getName());
        rdTask.setStatus(weeklyTask.getStatus());
        return rdTask;
    }
}
