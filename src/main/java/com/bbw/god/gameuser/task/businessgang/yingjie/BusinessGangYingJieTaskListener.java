package com.bbw.god.gameuser.task.businessgang.yingjie;

import com.bbw.common.ListUtil;
import com.bbw.god.activity.holiday.processor.BusinessGangYingJieProcessor;
import com.bbw.god.event.common.CommonEventPublisher;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.special.event.EPSpecialDeduct;
import com.bbw.god.gameuser.special.event.SpecialDeductEvent;
import com.bbw.god.gameuser.task.*;
import com.bbw.god.gameuser.task.businessgang.event.BusinessGangTaskAchievedEvent;
import com.bbw.god.gameuser.task.businessgang.event.EPBusinessGangTask;
import com.bbw.god.notify.rednotice.ModuleEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 锦礼每天任务监听器
 *
 * @author: huanghb
 * @date: 2022/2/15 16:39
 */
@Slf4j
@Component
@Async
public class BusinessGangYingJieTaskListener {
    @Autowired
    private BusinessGangYingJieTaskDataService businessGangYingJieTaskDataService;
    @Autowired
    private BusinessGangYingJieProcessor businessGangYingJieProcessor;
    @Autowired
    private GameUserService gameUserService;
    /** 初级 */
    private static final Integer FIRST_LEVEL = 200000;
    /** 中级 */
    private static final Integer MIDDLE_LEVEL = 250000;
    /** 高级 */
    private static final Integer HIGH_LEVEL = 300000;
    /** 史诗级 */
    private static final Integer SUPER_LEVEL = 500000;
    /** 商帮英杰任务 */
    private static final List<Integer> BUSINESS_GANG_YINGJIE_TASK = TaskTool.getTaskConfig(TaskGroupEnum.BUSINESS_GANG_YINGJIE_TASK)
            .getTasks().stream().map(CfgTaskEntity::getId).collect(Collectors.toList());

    /**
     * 完成商帮任务统计
     *
     * @param event
     */
    @Order(2)
    @EventListener
    public void finishGangTask(BusinessGangTaskAchievedEvent event) {
        EPBusinessGangTask businessGangTask = event.getEP();
        TaskGroupEnum taskGroupEnum = businessGangTask.getTaskGroup();
        if (taskGroupEnum == TaskGroupEnum.BUSINESS_GANG_WEEKLY_TASK) {
            return;
        }
        Long uid = businessGangTask.getGuId();
        CfgTaskEntity taskEntity = TaskTool.getTaskEntity(taskGroupEnum, businessGangTask.getTaskId());
        Integer difficulty = taskEntity.getDifficulty();
        if (difficulty == TaskDifficulty.FIRST_LEVEL.getValue()) {
            addProgress(uid, BUSINESS_GANG_YINGJIE_TASK, FIRST_LEVEL);
        }
        if (difficulty == TaskDifficulty.MIDDLE_LEVEL.getValue()) {
            addProgress(uid, BUSINESS_GANG_YINGJIE_TASK, MIDDLE_LEVEL);
        }
        if (difficulty == TaskDifficulty.HIGH_LEVEL.getValue()) {
            addProgress(uid, BUSINESS_GANG_YINGJIE_TASK, HIGH_LEVEL);
        }
        if (difficulty == TaskDifficulty.SUPER_LEVEL.getValue()) {
            addProgress(uid, BUSINESS_GANG_YINGJIE_TASK, SUPER_LEVEL);
        }
    }

    @Order(2)
    @EventListener
    public void sellSpecial(SpecialDeductEvent event) {
        EPSpecialDeduct ep = event.getEP();
        Long uid = ep.getGuId();
        List<EPSpecialDeduct.SpecialInfo> specialInfoList = ep.getSpecialInfoList();
        if (ListUtil.isEmpty(specialInfoList)) {
            return;
        }
        int totalSellingPrice = specialInfoList.stream().filter(tmp -> null != tmp.getSellPrice()).mapToInt(EPSpecialDeduct.SpecialInfo::getSellPrice).sum();
        int totalBuyPrice = specialInfoList.stream().filter(tmp -> null != tmp.getBuyPrice()).mapToInt(EPSpecialDeduct.SpecialInfo::getBuyPrice).sum();
        int profit = totalSellingPrice - totalBuyPrice;
        if (profit <= 0) {
            return;
        }
        addProgress(uid, BUSINESS_GANG_YINGJIE_TASK, profit);
    }

    /**
     * 增加任务进度
     *
     * @param uid
     * @param taskId
     * @param value
     */
    private void addProgress(long uid, int taskId, int value) {
        if (!businessGangYingJieProcessor.isOpened(gameUserService.getActiveSid(uid))) {
            return;
        }
        UserBusinessGangYingJieTask userBusinessGangYingJieTask = businessGangYingJieTaskDataService.getBusinessGangYingJieTaskFromCache(uid, taskId);
        if (null == userBusinessGangYingJieTask) {
            return;
        }
        Integer status = userBusinessGangYingJieTask.getStatus();
        //是否任务已完成
        boolean isAccomplishedTask = status == TaskStatusEnum.ACCOMPLISHED.getValue() || status == TaskStatusEnum.AWARDED.getValue();
        if (isAccomplishedTask) {
            return;
        }
        userBusinessGangYingJieTask.addProgress(value);
        businessGangYingJieTaskDataService.updateBusinessGangYingJieTaskToCache(uid, userBusinessGangYingJieTask);
        redNotice(uid, taskId, userBusinessGangYingJieTask);
    }


    /**
     * 增加多个任务进度
     *
     * @param uid
     * @param taskIds
     * @param value
     */
    private void addProgress(long uid, List<Integer> taskIds, int value) {
        for (Integer taskId : taskIds) {
            addProgress(uid, taskId, value);
        }
    }

    /**
     * 发送红点
     *
     * @param uid
     * @param taskId
     * @param userBusinessGangYingJieTask
     */
    private void redNotice(long uid, int taskId, UserBusinessGangYingJieTask userBusinessGangYingJieTask) {
        if (userBusinessGangYingJieTask.getStatus() == TaskStatusEnum.ACCOMPLISHED.getValue()) {
            CommonEventPublisher.pubAccomplishEvent(uid, ModuleEnum.TASK, TaskTypeEnum.BUSINESS_GANG_YINGJIE_TASK.getValue(), taskId);
        }
    }
}
