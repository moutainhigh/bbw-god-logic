package com.bbw.god.gameuser.task.businessgang.yingjie;

import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.activity.holiday.processor.BusinessGangYingJieProcessor;
import com.bbw.god.gameuser.task.AbstractTaskProcessor;
import com.bbw.god.gameuser.task.RDTaskList;
import com.bbw.god.gameuser.task.TaskTypeEnum;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * 商帮英杰任务
 *
 * @author: huanghb
 * @date: 2022/7/25 11:00
 */
@Component
public class BusinessGangYingJieTaskProcessor extends AbstractTaskProcessor {
    @Autowired
    private BusinessGangYingJieTaskService businessGangYingJieTaskService;
    @Autowired
    private BusinessGangYingJieProcessor businessGangYingJieProcessor;

    public BusinessGangYingJieTaskProcessor() {
        this.taskTypes = Arrays.asList(TaskTypeEnum.BUSINESS_GANG_YINGJIE_TASK);
    }

    /**
     * 获得任务列表
     *
     * @param uid
     * @return
     */
    @Override
    public RDTaskList getTasks(long uid, Integer days) {
        if (!businessGangYingJieProcessor.isOpened(gameUserService.getActiveSid(uid))) {
            throw new ExceptionForClientTip("activity.not.exist");
        }
        return businessGangYingJieTaskService.getTasks(uid, days);
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
        if (!businessGangYingJieProcessor.isOpened(gameUserService.getActiveSid(uid))) {
            throw new ExceptionForClientTip("activity.not.exist");
        }
        return businessGangYingJieTaskService.gainTaskAward(uid, id);
    }
}
