package com.bbw.god.gameuser.task.daily.service;

import com.bbw.common.DateUtil;
import com.bbw.common.PowerRandom;
import com.bbw.common.lock.SyncLockUtil;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.task.CfgTaskConfig.CfgBox;
import com.bbw.god.gameuser.task.CfgTaskEntity;
import com.bbw.god.gameuser.task.RDTaskItem;
import com.bbw.god.gameuser.task.TaskGroupEnum;
import com.bbw.god.gameuser.task.TaskTool;
import com.bbw.god.gameuser.task.daily.DailyTaskProcessor;
import com.bbw.god.gameuser.task.daily.UserDailyTask;
import com.bbw.god.gameuser.task.daily.UserDailyTaskInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserDailyTaskService {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private SyncLockUtil syncLockUtil;
    @Autowired
    private DailyTaskProcessor dailyTaskProcessor;

    // 可前往的任务ID
    private static List<Integer> gotoIds = Arrays.asList(21018, 24018, 21013, 25013, 25018, 25021, 25023, 22013, 22018,
            22020, 22021, 22022, 22023, 23013, 23018, 23020, 23021, 23022, 23023);

    /**
     * 生成每日任务
     *
     * @param guId
     * @return
     */
    public List<UserDailyTask> generateDailyTasks(long guId) {
        long generateTime = DateUtil.toDateTimeLong();
        GameUser gu = gameUserService.getGameUser(guId);
        // 获得每日随机任务
        List<CfgTaskEntity> dailyTasks = getInitDailyTaskList(TaskGroupEnum.fromLv(gu.getLevel()));
        // 生成玩家每日任务记录
        List<UserDailyTask> udTasks = new ArrayList<>();
        UserDailyTask udTask = null;
        for (CfgTaskEntity task : dailyTasks) {
            udTask = instanceUserDailyTask(gu, task, generateTime);
            udTasks.add(udTask);
            gameUserService.addItem(guId, udTask);
        }
        // 开箱子记录
        List<CfgBox> boxs = TaskTool.getBoxsByTaskGroupEnum(TaskGroupEnum.fromLv(gu.getLevel()));
        for (CfgBox box : boxs) {
            udTask = UserDailyTask.instance(gu.getId(), box.getId(), box.getScore(), generateTime);
            udTasks.add(udTask);
            gameUserService.addItem(guId, udTask);
        }
        log.info("{}生成{}批次的每日任务,本次任务级别：{}数量：{}", guId, generateTime, TaskGroupEnum.fromLv(gu.getLevel()).getValue(), udTasks.size());
        return udTasks;
    }

    private UserDailyTask instanceUserDailyTask(GameUser gu, CfgTaskEntity task, long generateTime) {
        int needValue = 9999999;
        if (task.getValue() != null) {
            needValue = task.getValue();
        } else {
            //以下需要特殊处理的任务，即每个玩家的任务指标不一样
            int guLevel = gu.getLevel();
            switch (task.getId()) {
                case 21016:
                case 22016:
                case 23016:
                case 24016:
                case 25016:
                    //目前所有累计铜钱收益都为：lv*2万
                    needValue = 20000 * guLevel;
                    break;
                default:
                    break;
            }
        }
        UserDailyTask udTask = UserDailyTask.instance(gu.getId(), task.getId(), needValue, generateTime);
        if (task.getId() == 21010 || task.getId() == 22010 || task.getId() == 23010 || task.getId() == 24010 || task.getId() == 25010) {
            //每日首次登录任务
            udTask.addValue(1);
        }
        return udTask;
    }

    /**
     * 获取今日的每日任务对象信息
     *
     * @param uid 玩家id
     * @return
     */
    public UserDailyTaskInfo getTodayDailyTaskInfo(GameUser user) {
        long uid = user.getId();
        // 未解锁
        if (!isUnlock(user)) {
            return null;
        }
        // 获取对象，如果对象不存在则生成
        UserDailyTaskInfo info = gameUserService.getSingleItem(uid, UserDailyTaskInfo.class);
        if (null == info) {
            info = (UserDailyTaskInfo) syncLockUtil.doSafe(String.valueOf(uid), tmp -> {
                UserDailyTaskInfo taskInfo = gameUserService.getSingleItem(uid, UserDailyTaskInfo.class);
                if (null == taskInfo) {
                    Integer level = user.getLevel();
                    List<Integer> unFinishIds = getTodayDailyTaskIds(level);
                    taskInfo = UserDailyTaskInfo.getInstance(uid, unFinishIds);
                    gameUserService.addItem(uid, taskInfo);
                }
                return taskInfo;
            });
        }
        // 判断生成时间是否是当天，不是当天的每日任务数据，重新初始化
        if (!info.isToday()) {
            Integer level = user.getLevel();
            List<Integer> unFinishIds = getTodayDailyTaskIds(level);
            info = UserDailyTaskInfo.getInstance(uid, info.getId(), unFinishIds);
            gameUserService.updateItem(info);
        }
        return info;
    }

    /**
     * 获取今日每日任务的id集合
     *
     * @param level 玩家等级
     * @return
     */
    private List<Integer> getTodayDailyTaskIds(int level) {
        List<Integer> taskIds = getInitDailyTaskList(TaskGroupEnum.fromLv(level)).stream().map(CfgTaskEntity::getId).collect(Collectors.toList());
        List<Integer> boxIds = TaskTool.getBoxsByTaskGroupEnum(TaskGroupEnum.fromLv(level)).stream().map(CfgBox::getId).collect(Collectors.toList());
        taskIds.addAll(boxIds);
        return taskIds;
    }

    /**
     * 每日任务是否已解锁 4级解锁
     *
     * @param gu
     * @return
     */
    public boolean isUnlock(GameUser gu) {
        return gu.getLevel() >= 4;
    }

    public List<UserDailyTask> getAllDailyTasks(long guId) {
        return gameUserService.getMultiItems(guId, UserDailyTask.class);
    }

    /**
     * 获取初始化后的每日任务列表
     *
     * @param type
     * @return
     */
    private List<CfgTaskEntity> getInitDailyTaskList(TaskGroupEnum type) {
        List<CfgTaskEntity> dailyTasks = TaskTool.getTasksByTaskGroupEnum(type);
        //先获取固定类最后3位数 小于100
        int id = type.getValue() + 100;
        List<CfgTaskEntity> res = dailyTasks.stream().filter(p -> p.getId() < id && p.getIsValid()).collect(Collectors.toList());
        for (int i = 1; i < 4; i++) {
            //在1~3类中各获取1个任务 最后3位数 大于100，每类间距100
            int begin = type.getValue() + 100 * i;
            int end = type.getValue() + 100 * (i + 1);
            List<CfgTaskEntity> levelTasks = new ArrayList<CfgTaskEntity>();
            for (CfgTaskEntity entity : dailyTasks) {
                if (begin < entity.getId() && entity.getId() < end && entity.getIsValid()) {
                    levelTasks.add(entity);
                }
            }
            res.add(PowerRandom.getRandomFromList(levelTasks));
        }
        return res;
    }

    /**
     * 获取可前往的未领取的任务
     *
     * @return
     */
    public List<RDTaskItem> getGotoTasks(long uid, int size) {
        List<RDTaskItem> rdTasks = new ArrayList<>();
        GameUser user = gameUserService.getGameUser(uid);
        UserDailyTaskInfo taskInfo = getTodayDailyTaskInfo(user);
        if (null == taskInfo) {
            return rdTasks;
        }
        List<Integer> awardedIds = taskInfo.getAwardedIds();
        List<Integer> accomplishIds = taskInfo.getAccomplishIds();
        List<Integer> unFinishIds = taskInfo.getUnFinishIds();
        for (Integer gotoId : gotoIds) {
            if (awardedIds.contains(gotoId)) {
                continue;
            }
            if (!accomplishIds.contains(gotoId) && !unFinishIds.contains(gotoId)) {
                continue;
            }
            RDTaskItem task = dailyTaskProcessor.getRdTask(user, gotoId, taskInfo);
            rdTasks.add(task);
            if (rdTasks.size() == size) {
                return rdTasks;
            }
        }
        return rdTasks;
    }
}
