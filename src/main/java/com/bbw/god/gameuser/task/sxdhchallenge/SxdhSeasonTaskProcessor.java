package com.bbw.god.gameuser.task.sxdhchallenge;

import com.bbw.common.ListUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.game.sxdh.SxdhRankService;
import com.bbw.god.game.sxdh.SxdhZone;
import com.bbw.god.game.sxdh.SxdhZoneService;
import com.bbw.god.game.sxdh.config.SxdhRankType;
import com.bbw.god.gameuser.task.*;
import com.bbw.god.rd.RDCommon;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 神仙大会赛季挑战
 *
 * @author suhq
 * @date 2020-04-27 10:33
 **/
@Slf4j
@Service
public class SxdhSeasonTaskProcessor extends AbstractTaskProcessor {

    @Autowired
    private UserSxdhSeasonTaskService sxdhSeasonTaskService;
    @Autowired
    private SxdhZoneService sxdhZoneService;
    @Autowired
    private SxdhRankService sxdhRankService;

    public SxdhSeasonTaskProcessor() {
        this.taskTypes = Arrays.asList(TaskTypeEnum.SXDH_SEASON_TASK);
    }

    @Override
    public RDTaskList getTasks(long uid, Integer days) {
        RDTaskList rd = new RDTaskList();
        SxdhZone sxdhZone = sxdhZoneService.getCurOrLastZone(uid);
        if (null == sxdhZone) {
            log.error("{}获取神仙大会任务失败，sxdhZone为null", uid);
            return rd;
        }
        List<UserSxdhSeasonTask> seasonTasks = sxdhSeasonTaskService.getSeasonTasks(uid, sxdhZone);
        if (ListUtil.isEmpty(seasonTasks)) {
            seasonTasks = sxdhSeasonTaskService.generateSeasonTasks(uid, sxdhZone);
        }
        List<RDTaskItem> rdTasks = new ArrayList<>();
        for (UserSxdhSeasonTask seasonTask : seasonTasks) {
            CfgTaskEntity taskEntity = TaskTool.getTaskEntity(TaskGroupEnum.SXDH_SEASON_TASK, seasonTask.getBaseId());
            RDTaskItem rdTask = RDTaskItem.getInstance(seasonTask, taskEntity);
            rdTasks.add(rdTask);
        }
        rd.setItems(rdTasks);
        return rd;
    }

    @Override
    public RDCommon gainTaskAward(long uid, int id, String awardIndex) {
        SxdhZone sxdhZone = sxdhZoneService.getCurOrLastZone(uid);
        UserSxdhSeasonTask uSeasonTask = sxdhSeasonTaskService.getSeasonTask(uid, sxdhZone, id);
        if (uSeasonTask == null) {
            throw new ExceptionForClientTip("task.sxdh.task.not.exist");
        }
        if (!uSeasonTask.ifAccomplished()) {
            throw new ExceptionForClientTip("task.sxdh.not.accomplish");
        }
        if (uSeasonTask.getStatus() == TaskStatusEnum.AWARDED.getValue()) {
            throw new ExceptionForClientTip("task.sxdh.already.award");
        }
        RDGetSxdhTaskAward rd = new RDGetSxdhTaskAward();
        CfgTaskEntity task = TaskTool.getTaskEntity(TaskGroupEnum.SXDH_SEASON_TASK, id);
        int addedScore = task.getAwards().get(0).getNum();
        sxdhRankService.incrementRankValue(sxdhZone, SxdhRankType.RANK, uid, addedScore);
        uSeasonTask.setStatus(TaskStatusEnum.AWARDED.getValue());
        gameUserService.updateItem(uSeasonTask);
        rd.setAddedScore(addedScore);
        return rd;
    }


}
