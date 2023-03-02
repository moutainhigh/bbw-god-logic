package com.bbw.god.gameuser.task.brocadegift;

import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.activity.holiday.processor.holidaybrocadegift.HolidayBrocadeGiftProcessor;
import com.bbw.god.gameuser.task.AbstractTaskProcessor;
import com.bbw.god.gameuser.task.RDTaskList;
import com.bbw.god.gameuser.task.TaskTypeEnum;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * 锦礼每天任务
 *
 * @author: huanghb
 * @date: 2022/2/15 16:32
 */
@Component
public class BrocadeGiftDailyTaskProcessor extends AbstractTaskProcessor {
    @Autowired
    private BrocadeGiftDailyTaskService brocadeGiftDailyTaskService;
    @Autowired
    private HolidayBrocadeGiftProcessor holidayBrocadeGiftProcessor;

    public BrocadeGiftDailyTaskProcessor() {
        this.taskTypes = Arrays.asList(TaskTypeEnum.ANNUAL_GIFT_DAILY_TASK);
    }

    /**
     * 获得任务列表
     *
     * @param uid
     * @return
     */
    @Override
    public RDTaskList getTasks(long uid, Integer days) {
        if (!holidayBrocadeGiftProcessor.isOpened(gameUserService.getActiveSid(uid))) {
            throw new ExceptionForClientTip("activity.not.exist");
        }
        return brocadeGiftDailyTaskService.getTasks(uid, days);
    }

    /**
     * 获取任务奖励
     *
     * @param uid
     * @param id
     * @param awardIndex
     * @return
     */
    @Override
    public RDCommon gainTaskAward(long uid, int id, String awardIndex) {
        if (!holidayBrocadeGiftProcessor.isOpened(gameUserService.getActiveSid(uid))) {
            throw new ExceptionForClientTip("activity.not.exist");
        }
        return brocadeGiftDailyTaskService.gainTaskAward(uid, id);
    }
}
