package com.bbw.god.gameuser.task.grow;

import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.gameuser.task.*;
import com.bbw.god.gameuser.task.CfgTaskConfig.CfgBox;
import com.bbw.god.gameuser.task.fshelper.FsTaskEnum;
import com.bbw.god.gameuser.task.fshelper.event.EpFsHelperChange;
import com.bbw.god.gameuser.task.fshelper.event.TaskEventPublisher;
import com.bbw.god.gameuser.task.grow.event.EPFinishNewbieTask;
import com.bbw.god.gameuser.task.grow.event.NewbieTaskEventPublisher;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * @author 作者 ：lwb
 * @version 创建时间：2020年2月11日 下午10:19:37 类说明 新手任务
 */
@Service
public class NewbieTaskProcessor extends AbstractTaskProcessor {

    @Autowired
    private NewbieTaskService taskService;

    public NewbieTaskProcessor() {
        this.taskTypes = Arrays.asList(TaskTypeEnum.NEWER_TASK);
    }

    @Override
    public RDTaskList getTasks(long uid, Integer days) {
        RDTaskList rd = new RDTaskList();
        List<RDTaskItem> tasks = taskService.toRdDailyTasks(uid);
        rd.setItems(tasks);
        return rd;
    }

    @Override
    public RDCommon gainTaskAward(long uid, int id, String awardIndex) {
        Optional<UserGrowTask> udTaskOp = taskService.getUserGrowTask(uid, id);
        if (!udTaskOp.isPresent()) {
            throw new ExceptionForClientTip("task.daily.already.updated");
        }
        UserGrowTask udTask = udTaskOp.get();
        if (udTask.getStatus() == TaskStatusEnum.DOING.getValue()) {
            throw new ExceptionForClientTip("task.not.accomplish");
        }
        if (udTask.getStatus() == TaskStatusEnum.AWARDED.getValue()) {
            throw new ExceptionForClientTip("task.already.award");
        }
        int boxIdBegin = taskService.getBoxbeginIndex();
        RDCommon rd = new RDCommon();
        int step = 0;
        String stepName;
        if (boxIdBegin < id) {
            // 宝箱
            CfgBox box = TaskTool.getNewbieTaskBox(id);
            step = box.getId();
            stepName = "宝箱" + step % 10;
            this.awardService.fetchAward(uid, box.getAwards(), WayEnum.GROW_TASK_BOX, "通过新手任务宝箱获得", rd);
            taskService.isPassGrowTask(gameUserService.getGameUser(uid));
        } else {
            // 任务
            CfgTaskEntity task = TaskTool.getNewbieTask(id);
            step = task.getSeq();
            stepName = task.getName();
            this.awardService.fetchAward(uid, task.getAwards(), WayEnum.GROW_TASK, "通过新手任务获得", rd);
            EpFsHelperChange dta = EpFsHelperChange.instanceUpdateTask(new BaseEventParam(uid), FsTaskEnum.NewBie, id);
            TaskEventPublisher.pubEpFsHelperChangeEvent(dta);
        }
        udTask.setStatus(TaskStatusEnum.AWARDED.getValue());
        this.gameUserService.updateItem(udTask);
        // 发布事件
        BaseEventParam bep = new BaseEventParam(uid);
        NewbieTaskEventPublisher.pubFinishNewbieTaskEvent(new EPFinishNewbieTask(step, stepName, bep));
        return rd;
    }


}
