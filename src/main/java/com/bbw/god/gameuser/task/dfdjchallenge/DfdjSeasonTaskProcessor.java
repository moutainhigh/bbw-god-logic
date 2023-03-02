package com.bbw.god.gameuser.task.dfdjchallenge;

import com.bbw.common.ListUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.game.dfdj.config.DfdjRankType;
import com.bbw.god.game.dfdj.rank.DfdjRankService;
import com.bbw.god.game.dfdj.zone.DfdjZone;
import com.bbw.god.game.dfdj.zone.DfdjZoneService;
import com.bbw.god.gameuser.task.*;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 巅峰对决赛季挑战
 *
 * @author suhq
 * @date 2020-04-27 10:33
 **/
@Service
public class DfdjSeasonTaskProcessor extends AbstractTaskProcessor {

    @Autowired
    private UserDfdjSeasonTaskService dfdjSeasonTaskService;
    @Autowired
    private DfdjZoneService dfdjZoneService;
    @Autowired
    private DfdjRankService dfdjRankService;

    public DfdjSeasonTaskProcessor() {
        this.taskTypes = Arrays.asList(TaskTypeEnum.DFDJ_SEASON_TASK);
    }

    @Override
    public RDTaskList getTasks(long uid, Integer days) {
        RDTaskList rd = new RDTaskList();
        DfdjZone zone = dfdjZoneService.getCurOrLastZone(uid);
        List<UserDfdjSeasonTask> seasonTasks = dfdjSeasonTaskService.getSeasonTasks(uid, zone);
        if (ListUtil.isEmpty(seasonTasks)) {
            seasonTasks = dfdjSeasonTaskService.generateSeasonTasks(uid, zone);
        }
        List<RDTaskItem> rdTaskItems = new ArrayList<>();
        for (UserDfdjSeasonTask seasonTask : seasonTasks) {
            CfgTaskEntity taskEntity = TaskTool.getTaskEntity(TaskGroupEnum.DFDJ_SEASON_TASK, seasonTask.getBaseId());
            RDTaskItem rdTask = new RDTaskItem();
            rdTask.setId(seasonTask.getBaseId());
            rdTask.setProgress((int) seasonTask.getValue());
            rdTask.setTotalProgress(seasonTask.getNeedValue());
            rdTask.setAwards(taskEntity.getAwards());
            rdTask.setStatus(seasonTask.getStatus());
            rdTaskItems.add(rdTask);
        }
        rd.setItems(rdTaskItems);
        return rd;
    }

    @Override
    public RDCommon gainTaskAward(long uid, int id, String awardIndex) {
        DfdjZone zone = dfdjZoneService.getCurOrLastZone(uid);
        UserDfdjSeasonTask uSeasonTask = dfdjSeasonTaskService.getSeasonTask(uid, zone, id);
        if (uSeasonTask == null) {
            throw new ExceptionForClientTip("task.dfdj.task.not.exist");
        }
        if (!uSeasonTask.ifAccomplished()) {
            throw new ExceptionForClientTip("task.dfdj.not.accomplish");
        }
        if (uSeasonTask.getStatus() == TaskStatusEnum.AWARDED.getValue()) {
            throw new ExceptionForClientTip("task.dfdj.already.award");
        }
        RDGetDfdjTaskAward rd = new RDGetDfdjTaskAward();
        CfgTaskEntity task = TaskTool.getTaskEntity(TaskGroupEnum.DFDJ_SEASON_TASK, id);
        int addedScore = task.getAwards().get(0).getNum();
        dfdjRankService.incrementRankValue(zone, DfdjRankType.RANK, uid, addedScore);
        uSeasonTask.setStatus(TaskStatusEnum.AWARDED.getValue());
        gameUserService.updateItem(uSeasonTask);
        rd.setAddedScore(addedScore);
        return rd;
    }


}
