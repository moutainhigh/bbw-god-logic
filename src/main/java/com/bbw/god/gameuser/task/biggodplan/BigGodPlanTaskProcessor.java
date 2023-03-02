package com.bbw.god.gameuser.task.biggodplan;

import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.activity.processor.BigGodPlanProcessor;
import com.bbw.god.gameuser.task.AbstractTaskProcessor;
import com.bbw.god.gameuser.task.RDTaskList;
import com.bbw.god.gameuser.task.TaskTypeEnum;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * 大仙计划
 *
 * @author: huanghb
 * @date: 2022/2/15 16:32
 */
@Component
public class BigGodPlanTaskProcessor extends AbstractTaskProcessor {
    @Autowired
    private BigGodPlanTaskService bigGodPlanTaskService;
    @Autowired
    private BigGodPlanProcessor bigGodPlanProcessor;

    public BigGodPlanTaskProcessor() {
        this.taskTypes = Arrays.asList(TaskTypeEnum.BIG_GOD_PLAN);
    }

    /**
     * 获得任务列表
     *
     * @param uid
     * @return
     */
    @Override
    public RDTaskList getTasks(long uid, Integer days) {
        if (!bigGodPlanProcessor.isOpened(gameUserService.getActiveSid(uid))) {
            throw new ExceptionForClientTip("activity.not.exist");
        }
        return bigGodPlanTaskService.getTasks(uid, days);
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
        if (!bigGodPlanProcessor.isOpened(gameUserService.getActiveSid(uid))) {
            throw new ExceptionForClientTip("activity.not.exist");
        }
        return bigGodPlanTaskService.gainTaskAward(uid, id);
    }
}
