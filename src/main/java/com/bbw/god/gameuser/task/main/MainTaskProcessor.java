package com.bbw.god.gameuser.task.main;

import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.game.config.city.CityTool;
import com.bbw.god.gameuser.task.*;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author suhq
 * @description: 新手进阶任务
 * @date 2019-11-20 10:22
 **/
@Service
public class MainTaskProcessor extends AbstractTaskProcessor {
    @Autowired
    private UserMainTaskService mainTaskService;

    private MainTaskProcessor() {
        this.taskTypes = Arrays.asList(TaskTypeEnum.MAIN_TASK);
    }

    @Override
    public RDTaskList getTasks(long uid, Integer days) {
        List<UserMainTask> umTasks = this.mainTaskService.getUserMainTasks(uid);
        List<RDTaskItem> rdMainTasks = umTasks.stream().map(this::toRdMainTask).collect(Collectors.toList());
        RDTaskList rd = new RDTaskList();
        rd.setItems(rdMainTasks);
        return rd;
    }

    @Override
    public RDCommon gainTaskAward(long uid, int id, String awardIndexStr) {
        UserMainTask umTask = this.mainTaskService.getUserMainTask(uid, id);
        int awardedIndex = umTask.getAwardedIndex() + 1;
        // 已达成
        if (awardedIndex > umTask.getEnableAwardIndex()) {
            throw new ExceptionForClientTip("task.not.accomplish");
        }
        // 经历发放
        RDMainTaskAwarded rd = new RDMainTaskAwarded();
        Award award = this.mainTaskService.getAward(id, awardedIndex);
        if (award == null) {
            throw new ExceptionForClientTip("task.not.exist");
        }
        this.awardService.fetchAward(uid, Arrays.asList(award), WayEnum.MAIN_TASK, "", rd);
        umTask.setAwardedIndex(awardedIndex);
        this.gameUserService.updateItem(umTask);
        if (umTask.isEnableAward()) {
            rd.setNextMainTask(toRdMainTask(umTask));
        }
        return rd;
    }

    private RDMainTask toRdMainTask(UserMainTask umt) {
        int curId = umt.getEnableAwardIndex();
        int awardedId = umt.getAwardedIndex();
        int max = 0;//任务进度上限
        if ((umt.getBaseId() == 1100 || umt.getBaseId() == 1200)) {
            max = CityTool.getCcCount();
        }
        if (umt.getBaseId() == 1300) {
            max = CardTool.getCardCount();
        }
        int status = 0;
        if (awardedId == max) {
            status = 2;
        } else if (curId > awardedId) {
            status = 1;
        }
        Award award = this.mainTaskService.getAward(umt.getBaseId(), awardedId);
        RDMainTask task = RDMainTask.instance(umt, umt.getBaseId(), status, max);
        task.addAward(award);
        return task;
    }
}
