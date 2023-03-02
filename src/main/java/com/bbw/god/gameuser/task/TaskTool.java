package com.bbw.god.gameuser.task;

import com.bbw.common.CloneUtil;
import com.bbw.common.ListUtil;
import com.bbw.exception.CoderException;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.gameuser.task.CfgTaskConfig.CfgBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TaskTool {

    /**
     * 获取任务配置
     *
     * @param groupId
     * @return
     */
    public static CfgTaskConfig getTaskConfig(int groupId) {
        return Cfg.I.get(groupId, CfgTaskConfig.class);
    }

    /**
     * 获取任务配置
     *
     * @param typeEnum
     * @return
     */
    public static CfgTaskConfig getTaskConfig(TaskGroupEnum typeEnum) {
        return Cfg.I.get(typeEnum.getValue(), CfgTaskConfig.class);
    }

    /**
     * 获得第一个任务
     *
     * @param typeEnum
     * @return
     */
    public static CfgTaskEntity getFirstTask(TaskGroupEnum typeEnum) {
        return getTaskConfig(typeEnum).getTasks().stream().findFirst().get();
    }

    /**
     * 根据类型获取任务列表
     *
     * @param type
     * @return
     */
    public static List<CfgTaskEntity> getTasksByTaskGroupEnum(TaskGroupEnum type) {
        CfgTaskConfig cfg = getTaskConfig(type.getValue());
        return ListUtil.copyList(cfg.getTasks(), CfgTaskEntity.class);
    }

    /**
     * 根据类型获取宝箱
     *
     * @param type
     * @return
     */
    public static List<CfgBox> getBoxsByTaskGroupEnum(TaskGroupEnum type) {
        CfgTaskConfig cfg = getTaskConfig(type.getValue());
        if (ListUtil.isEmpty(cfg.getBoxs())) {
            return new ArrayList<>();
        }
        return ListUtil.copyList(cfg.getBoxs(), CfgBox.class);
    }

    /***
     * 根据任务Id获取每日任务
     * @param taskId
     * @return
     */
    public static CfgTaskEntity getDailyTask(int taskId) {
        CfgTaskConfig cfg = getTaskConfig(taskId / 1000 * 1000);
        if (cfg == null) {
            throw CoderException.high("无效的任务" + taskId);
        }
        Optional<CfgTaskEntity> entity = cfg.getTasks().stream().filter(p -> p.getId() == taskId).findFirst();
        if (!entity.isPresent()) {
            throw CoderException.high("无效的任务" + taskId);
        }
        return CloneUtil.clone(entity.get());
    }

    /**
     * 获取某个系列组对应系列号的任务
     *
     * @param taskGroup
     * @param seqGroup
     * @param seq
     * @return
     */
    public static CfgTaskEntity getTask(TaskGroupEnum taskGroup, int seqGroup, int seq) {
        CfgTaskConfig taskConfig = getTaskConfig(taskGroup);
        for (CfgTaskEntity task : taskConfig.getTasks()) {
            if (task.getSeqGroup() == seqGroup && task.getSeq() == seq) {
                return task;
            }
        }
        return null;
    }

    /**
     * 根据Id获取每日任务宝箱
     *
     * @param boxId
     * @return
     */
    public static CfgBox getDailyTaskCfgBox(int boxId) {
        CfgTaskConfig cfg = getTaskConfig(boxId / 1000 * 1000);
        if (cfg == null) {
            throw CoderException.high("无效的活跃宝箱：" + boxId);
        }
        Optional<CfgBox> boxOp = cfg.getBoxs().stream().filter(p -> p.getId() == boxId).findFirst();
        if (!boxOp.isPresent()) {
            throw CoderException.high("无效的活跃宝箱：" + boxId);
        }
        return CloneUtil.clone(boxOp.get());
    }

    /**
     * 根据任务Id获取 每日宝箱的起始Id
     *
     * @param taskId
     * @return
     */
    public static Integer getDailyBoxIdBeginByTaskId(int taskId) {
        return taskId / 1000 * 1000 + 900;
    }

    public static CfgBox getNewbieTaskBox(int boxId) {
        List<CfgBox> boxs = getBoxsByTaskGroupEnum(TaskGroupEnum.TASK_NEWBIE);
        Optional<CfgBox> boxOp = boxs.stream().filter(p -> p.getId() == boxId).findFirst();
        if (!boxOp.isPresent()) {
            throw CoderException.high("无效的新手任务阶段宝箱：" + boxId);
        }
        return boxOp.get();
    }

    public static CfgTaskEntity getNewbieTask(int taskId) {
        return getTaskEntity(TaskGroupEnum.TASK_NEWBIE, taskId);
    }

    /**
     * 根据任务类型和id获取任务数据
     *
     * @param type
     * @param taskId
     * @return
     */
    public static CfgTaskEntity getTaskEntity(TaskGroupEnum type, int taskId) {
        List<CfgTaskEntity> tasks = getTasksByTaskGroupEnum(type);
        Optional<CfgTaskEntity> boxOp = tasks.stream().filter(p -> p.getId() == taskId).findFirst();
        if (!boxOp.isPresent()) {
            throw CoderException.high("无效任务：" + taskId);
        }
        return boxOp.get();
    }

    public static List<Award> getAwards(TaskGroupEnum type, int taskId) {
        CfgTaskConfig taskConfig = getTaskConfig(type);
        // 宝箱任务
        if (isBoxTask(taskId)) {
            CfgBox cfgBox = taskConfig.getBoxs().stream().filter(tmp -> tmp.getId() == taskId).findFirst().orElse(null);
            return cfgBox == null ? new ArrayList<>() : cfgBox.getAwards();
        }
        // 普通任务
        CfgTaskEntity cfgTaskEntity = taskConfig.getTasks().stream().filter(tmp -> tmp.getId() == taskId).findFirst().orElse(null);
        return cfgTaskEntity == null ? new ArrayList<>() : cfgTaskEntity.getAwards();
    }

    /**
     * 是否是宝箱任务
     *
     * @param taskId 任务id
     * @return
     */
    public static boolean isBoxTask(int taskId) {
        return taskId % 1000 > 900;
    }

    /**
     * 获得任务难度
     *
     * @param taskGroupEnum
     * @param taskId
     * @return
     */
    public static Integer getTaskDifficulty(TaskGroupEnum taskGroupEnum, int taskId){
        return getTaskEntity(taskGroupEnum, taskId).getDifficulty();
    }
}
