package com.bbw.god.activity.holiday.processor.holidayspecialcity;

import com.bbw.common.PowerRandom;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.activity.holiday.processor.HolidayBuildingAltarProcessor;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.task.*;
import com.bbw.god.gameuser.task.timelimit.TimeLimitTaskTool;
import com.bbw.god.gameuser.task.timelimit.UserDispatchTaskService;
import com.bbw.god.gameuser.task.timelimit.UserTimeLimitTask;
import com.bbw.god.gameuser.task.timelimit.UserTimeLimitTaskService;
import com.bbw.god.gameuser.treasure.TreasureChecker;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.rd.RDAdvance;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 庆典邀约
 *
 * @author fzj
 * @date 2021/12/15 10:53
 */
@Service
public class HolidayCelebrationInviteProcessor extends AbstractSpecialCityProcessor {
    @Autowired
    UserDispatchTaskService userDispatchTaskService;
    @Autowired
    UserTimeLimitTaskService userTimeLimitTaskService;

    public HolidayCelebrationInviteProcessor() {
        this.activityTypeList = Arrays.asList(ActivityEnum.CELEBRATION_INVITATION);
    }

    private static final List<Integer> RANDOM_LIST = Arrays.asList(50, 30, 20);
    @Override
    public boolean isShowInUi(long uid) {
        return true;
    }


    /**
     * 检查是否触发活动事件
     *
     * @param uid
     * @param rd
     */
    @Override
    public void cunZTriggerEvent(long uid, RDAdvance rd) {
        if (!isOpened(gameUserService.getActiveSid(uid))) {
            return;
        }
        // 如果未拥有庆典邀请卡不会触发活动事件
        boolean hasCelebrationInviteCard = TreasureChecker.hasTreasure(uid, TreasureEnum.CELEBRATION_INVITATION_CARD.getValue());
        if (!hasCelebrationInviteCard) {
            return;
        }
        //执行活动事件
        doEventTrigger(uid, rd);
    }

    /**
     * 执行活动事件
     *
     * @param uid
     * @param rd
     */
    public void doEventTrigger(long uid, RDAdvance rd) {
        //消耗庆典邀请卡
        TreasureEventPublisher.pubTDeductEvent(uid, TreasureEnum.CELEBRATION_INVITATION_CARD.getValue(), 1, WayEnum.CELEBRATION_INVITE, rd);
        //百分之30概率直接发放奖励
        if (PowerRandom.hitProbability(30)) {
            for (Integer awardId : HolidayBuildingAltarProcessor.ACTIVITY_AWARDS) {
                TreasureEventPublisher.pubTAddEvent(uid, awardId, 1, WayEnum.CELEBRATION_INVITE, rd);
            }
            rd.setActivityEvenType(0);
            return;
        }
        //触发村庄任务
        doSpecialCunZTask(uid);
        rd.setActivityEvenType(1);
    }

    /**
     * 添加村庄任务
     *
     * @param uid
     * @return
     */
    private void doSpecialCunZTask(long uid) {
        //根据概率获取村庄任务
        CfgTaskEntity taskEntity = getSpecialCunZTask();
        UserTimeLimitTask userTimeLimitTask = makeUserTaskInstance(uid, taskEntity);
        gameUserService.addItem(uid, userTimeLimitTask);
    }

    /**
     * 获取特殊村庄任务
     *
     * @return
     */
    private CfgTaskEntity getSpecialCunZTask() {
        //获取所有庆典邀约的村庄任务
        List<CfgTaskEntity> taskGroup = TaskTool.getTasksByTaskGroupEnum(TaskGroupEnum.CELEBRATION_INVITATION_TASK);
        //根据概率获取任务
        int index = PowerRandom.getIndexByProbs(RANDOM_LIST, 100);
        return taskGroup.get(index);
    }

    /**
     * 创建任务实例
     *
     * @param uid
     * @param taskEntity
     * @return
     */
    private UserTimeLimitTask makeUserTaskInstance(long uid, CfgTaskEntity taskEntity) {
        List<Integer> extraRandomSkills = TimeLimitTaskTool.getExtraRandomSkills(TaskGroupEnum.CELEBRATION_INVITATION_TASK, taskEntity);
        return UserTimeLimitTask.instance(uid, TaskGroupEnum.CELEBRATION_INVITATION_TASK, taskEntity, extraRandomSkills, null);
    }

    /**
     * 获取任务列表
     *
     * @param uid
     * @return
     */
    public RDTaskList getTasks(long uid) {
        if (!isOpened(gameUserService.getActiveSid(uid))) {
            throw new ExceptionForClientTip("activity.not.exist");
        }
        RDTaskList rd = new RDTaskList();
        List<RDTaskItem> rdTaskItems = new ArrayList<>();
        List<UserTimeLimitTask> uts = userTimeLimitTaskService.getTasks(uid, TaskGroupEnum.CELEBRATION_INVITATION_TASK);
        for (UserTimeLimitTask ut : uts) {
            CfgTaskEntity taskEntity = TaskTool.getTaskEntity(TaskGroupEnum.CELEBRATION_INVITATION_TASK, ut.getBaseId());
            rdTaskItems.add(RDTaskItem.getInstance(ut, taskEntity, TaskGroupEnum.CELEBRATION_INVITATION_TASK));
        }
        rd.setItems(rdTaskItems);
        return rd;
    }

    /**
     * 领奖励
     *
     * @param ut
     * @return
     */
    public RDCommon gainTaskAward(UserTimeLimitTask ut) {
        TaskStatusEnum taskStatus = userTimeLimitTaskService.doBeforeDispatchAward(ut);
        return userTimeLimitTaskService.doDispatchAward(ut, taskStatus);
    }
}
