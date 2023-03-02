package com.bbw.god.gameuser.task.timelimit.dragonboatfestival;

import com.bbw.common.DateUtil;
import com.bbw.common.ListUtil;
import com.bbw.god.activity.holiday.processor.HolidayCunZYiYunProcessor;
import com.bbw.god.cache.TimeLimitCacheUtil;
import com.bbw.god.fight.RDFightsInfo;
import com.bbw.god.game.combat.data.param.CCardParam;
import com.bbw.god.gameuser.task.*;
import com.bbw.god.gameuser.task.timelimit.*;
import com.bbw.god.rd.RDCommon;
import com.bbw.god.rd.RDSuccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 端午节任务
 *
 * @author: huanghb
 * @date: 2022/5/23 16:03
 */
@Service
public class DragonBoatFestivalTaskProcessor extends AbstractTaskProcessor {
    @Autowired
    private UserDragonBoatFestivalTaskService userDragonBoatFestivalTaskService;
    @Autowired
    private UserDispatchTaskService userDispatchTaskService;
    @Autowired
    private UserTimeLimitTaskService userTimeLimitTaskService;
    @Autowired
    private HolidayCunZYiYunProcessor holidayCunZYiYun;
    @Autowired
    private TimeLimitFightTaskService timeLimitFightTaskService;
    @Autowired
    private CunZYiYunFightService cunZYiYunFightService;


    public DragonBoatFestivalTaskProcessor() {
        this.taskTypes = Arrays.asList(TaskTypeEnum.DRAGON_BOAT_FESTIVAL_TASK);
    }

    @Override
    public RDTaskList getTasks(long uid, Integer days) {
        return new RDTaskList();
    }

    @Override
    public RDCommon gainTaskAward(long uid, int id, String awardIndex) {
        return new RDCommon();
    }

    /**
     * 获取任务信息
     *
     * @param uid
     * @return
     */
    public RDSuccess getTask(long uid) {
        //是否参加过端午节村庄任务
        if (!isJoined(uid)) {
            //生成初始任务
            CfgTaskEntity taskEntity = TaskTool.getFirstTask(TaskGroupEnum.DRAGON_BOAT_FESTIVAL_TASK);
            UserTimeLimitTask userTimeLimitTask = userDragonBoatFestivalTaskService.makeUserTaskInstance(uid, taskEntity);
            gameUserService.addItem(uid, userTimeLimitTask);
            setCurrentTaskId(uid, taskEntity.getId());
        }
        //获取玩家限时任务信息
        List<UserTimeLimitTask> uts = userDragonBoatFestivalTaskService.getAllTasks(uid);
        //任务信息丢失修复
        if (ListUtil.isEmpty(uts)) {
            //兼容旧的-加入状态key值
            int currentTaskId = getCurrentTaskId(uid);
            int oldTaskStatus = 1;
            if (currentTaskId == oldTaskStatus) {
                currentTaskId = TaskTool.getFirstTask(TaskGroupEnum.DRAGON_BOAT_FESTIVAL_TASK).getId();
            }
            //根据当前任务id修复任务进度
            CfgTaskEntity taskEntity = TaskTool.getDailyTask(currentTaskId);
            UserTimeLimitTask userTimeLimitTask = userDragonBoatFestivalTaskService.makeUserTaskInstance(uid, taskEntity);
            gameUserService.addItem(uid, userTimeLimitTask);
            uts.add(userTimeLimitTask);
        }
        //获取玩家当前任务信息
        UserTimeLimitTask ut = uts.get(uts.size() - 1);
        CfgTaskEntity taskEntity = TaskTool.getTaskEntity(TaskGroupEnum.DRAGON_BOAT_FESTIVAL_TASK, ut.getBaseId());
        //获取任务总数量
        Integer tasksAllNum = TaskTool.getTasksByTaskGroupEnum(TaskGroupEnum.DRAGON_BOAT_FESTIVAL_TASK).size();
        //任务是否可以执行
        boolean executable = holidayCunZYiYun.isExecutable(uid, taskEntity, tasksAllNum);
        //是否是派遣任务
        if (taskEntity.getType() == TaskTypeEnum.TIME_LIMIT_DISPATCH_TASK.getValue()) {
            return userDispatchTaskService.doGetDispatchInfo(ut, executable);
        }
        //返回任务信息
        RDTaskItem rdTask = RDTaskItem.getInstance(ut, taskEntity, TaskGroupEnum.DRAGON_BOAT_FESTIVAL_TASK);
        String[] titleFormats = {String.valueOf(ut.getValue())};
        rdTask.setTitleFormats(titleFormats);
        //判断当前任务是否可以执行
        if (!executable) {
            rdTask.setIsExecutable(1);
        }
        //执行战斗任务相关操作
        executeFightTaskAction(ut, rdTask);
        RDTaskInfo rd = new RDTaskInfo();
        rd.setTaskItem(rdTask);
        return rd;
    }

    /**
     * 执行战斗任务相关操作
     *
     * @param ut
     * @param rdTask
     */
    private void executeFightTaskAction(UserTimeLimitTask ut, RDTaskItem rdTask) {
        int taskId = ut.getBaseId();
        //战斗任务id不存在
        if (!getFightTasks().contains(taskId)) {
            return;
        }
        long uid = ut.getGameUserId();
        TaskGroupEnum taskGroup = TaskGroupEnum.DRAGON_BOAT_FESTIVAL_TASK;
        //生成npc卡组
        RDFightsInfo rdFightsInfo = cunZYiYunFightService.getOrCreatOpponentFightsInfo(taskId, taskGroup);
        //打包数据返回给客户端
        RDTimeLimitFightCardInfo rdCardInfo = new RDTimeLimitFightCardInfo();
        //npc卡组信息
        List<RDFightsInfo.RDFightCard> rdFightCards = RDTimeLimitFightCardInfo.setCardGroup(rdFightsInfo);
        rdCardInfo.setOpponentCards(rdFightCards);
        //获取玩家战斗卡组
        List<CCardParam> attackerCardGroups = cunZYiYunFightService.getOwnCardGroup(uid, taskGroup, taskId);
        rdCardInfo.setOwnCards(RDTimeLimitFightCardInfo.setCardGroup(attackerCardGroups));
        //获取卡牌库
        List<CCardParam> timeLimitCardLibrary = timeLimitFightTaskService.getOrCreateCardLibrary(uid, taskGroup, taskId);
        rdCardInfo.setOptionalCardsPool(RDTimeLimitFightCardInfo.setCardGroup(timeLimitCardLibrary));
        //设置限时战斗双方卡牌信息
        rdTask.setLimitFightCardInfos(rdCardInfo);
    }

    /**
     * 领奖励
     *
     * @param ut
     * @return
     */
    public RDCommon gainTaskAward(UserTimeLimitTask ut) {
        long uid = ut.getGameUserId();
        CfgTaskEntity taskEntity = TaskTool.getTaskEntity(TaskGroupEnum.fromValue(ut.getGroup()), ut.getBaseId());
        Integer tasksAllNum = TaskTool.getTasksByTaskGroupEnum(TaskGroupEnum.DRAGON_BOAT_FESTIVAL_TASK).size();
        //检查是否可以生成下一个任务
        if (!holidayCunZYiYun.isExecutable(uid, taskEntity, tasksAllNum)) {
            return new RDCommon();
        }
        TaskStatusEnum taskStatus = userTimeLimitTaskService.doBeforeDispatchAward(ut);
        RDCommon rd = userTimeLimitTaskService.doDispatchAward(ut, taskStatus);
        // 生成下一个任务
        int taskSeq = taskEntity.getSeq() + 1;
        CfgTaskEntity nextTask = TaskTool.getTask(TaskGroupEnum.fromValue(ut.getGroup()), taskEntity.getSeqGroup(), taskSeq);
        if (null == nextTask) {
            return rd;
        }
        //刷新玩家牌库
        refreshCardLibrary(ut.getGameUserId(), nextTask.getId());
        UserTimeLimitTask nextUt = userDragonBoatFestivalTaskService.makeUserTaskInstance(uid, nextTask);
        gameUserService.addItem(uid, nextUt);
        //缓存当前任务id
        setCurrentTaskId(uid, nextTask.getId());
        return rd;
    }

    /**
     * 刷新牌库
     *
     * @param taskId
     */
    public void refreshCardLibrary(long uid, int taskId) {
        if (!getFightTasks().contains(taskId)) {
            return;
        }
        //获取随机牌库
        List<CCardParam> cardLibrary = timeLimitFightTaskService.getRandomCardLibrary(TaskGroupEnum.DRAGON_BOAT_FESTIVAL_TASK, taskId);
        //存入缓存
        timeLimitFightTaskService.saveCardLibrary(uid, cardLibrary);
        //清空玩家编组
        List<Integer> cardGroup = new ArrayList<>();
        timeLimitFightTaskService.saveUserTimeLimitFightCards(uid, cardGroup);
    }

    /**
     * 判断是否参加端午节村庄任务
     *
     * @param uid
     * @return
     */
    private boolean isJoined(long uid) {
        Integer hasJoinNewYearAndChristmasTask = TimeLimitCacheUtil.getFromCache(uid, "hasJoinDragonBoatFestivalTask", Integer.class);
        return null != hasJoinNewYearAndChristmasTask;
    }

    /**
     * 保存端午节任务村庄当前任务id
     *
     * @param uid
     */
    private void setCurrentTaskId(long uid, int taskId) {
        TimeLimitCacheUtil.cacheBothLocalAndRedis(uid, "hasJoinDragonBoatFestivalTask", taskId, DateUtil.SECOND_ONE_DAY * 12);
    }

    /**
     * 获得端午节任务村庄当前任务id
     *
     * @param uid
     */
    private int getCurrentTaskId(long uid) {
        return TimeLimitCacheUtil.getFromCache(uid, "hasJoinDragonBoatFestivalTask", Integer.class);
    }

    /**
     * 获取战斗任务id集合
     *
     * @return
     */
    private List<Integer> getFightTasks() {
        return TaskTool.getTaskConfig(TaskGroupEnum.DRAGON_BOAT_FESTIVAL_TASK).getTasks().stream()
                .filter(t -> t.getType() == TaskTypeEnum.TIME_LIMIT_FIGHT_TASK.getValue()).map(CfgTaskEntity::getId).collect(Collectors.toList());
    }
}
