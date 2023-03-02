package com.bbw.god.gameuser.task.timelimit.qingming;

import com.bbw.common.DateUtil;
import com.bbw.common.ListUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.activity.ActivityService;
import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.activity.holiday.processor.HolidayCunZYiYunProcessor;
import com.bbw.god.cache.TimeLimitCacheUtil;
import com.bbw.god.fight.RDFightsInfo;
import com.bbw.god.game.combat.data.param.CCardParam;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.maou.GameMaouAttacker;
import com.bbw.god.game.maou.GameMaouAttackerService;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.res.ResChecker;
import com.bbw.god.gameuser.res.ResEventPublisher;
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
 * 清明节任务
 *
 * @author fzj
 * @date 2022/3/28 10:59
 */
@Service
public class QingMingTaskProcessor extends AbstractTaskProcessor {
    @Autowired
    private UserQingMingTaskService userQingMingTaskService;
    @Autowired
    private UserDispatchTaskService userDispatchTaskService;
    @Autowired
    private UserTimeLimitTaskService userTimeLimitTaskService;
    @Autowired
    private HolidayCunZYiYunProcessor holidayCunZYiYun;
    @Autowired
    private GameMaouAttackerService gameMaouAttackerService;
    @Autowired
    private ActivityService activityService;
    @Autowired
    private TimeLimitFightTaskService timeLimitFightTaskService;
    @Autowired
    private CunZYiYunFightService cunZYiYunFightService;

    public QingMingTaskProcessor() {
        this.taskTypes = Arrays.asList(TaskTypeEnum.QING_MING_TASK);
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
        if (!isJoined(uid)) {
            CfgTaskEntity taskEntity = TaskTool.getTaskEntity(TaskGroupEnum.QING_MING_TASK, 200001);
            UserTimeLimitTask userTimeLimitTask = userQingMingTaskService.makeUserTaskInstance(uid, taskEntity);
            gameUserService.addItem(uid, userTimeLimitTask);
            setJoinStatus(uid);
        }
        List<UserTimeLimitTask> uts = userQingMingTaskService.getAllTasks(uid);
        UserTimeLimitTask ut = uts.get(uts.size() - 1);
        CfgTaskEntity taskEntity = TaskTool.getTaskEntity(TaskGroupEnum.QING_MING_TASK, ut.getBaseId());
        Integer tasksAllNum = TaskTool.getTasksByTaskGroupEnum(TaskGroupEnum.QING_MING_TASK).size();
        boolean executable = holidayCunZYiYun.isExecutable(uid, taskEntity, tasksAllNum);
        if (taskEntity.getType() == TaskTypeEnum.TIME_LIMIT_DISPATCH_TASK.getValue()) {
            return userDispatchTaskService.doGetDispatchInfo(ut, executable);
        }
        RDTaskItem rdTask = RDTaskItem.getInstance(ut, taskEntity, TaskGroupEnum.QING_MING_TASK);
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
        if (!getFightTasks().contains(taskId)) {
            return;
        }
        long uid = ut.getGameUserId();
        TaskGroupEnum taskGroup = TaskGroupEnum.QING_MING_TASK;
        //生成npc卡组
        RDFightsInfo rdFightsInfo = cunZYiYunFightService.getOrCreatOpponentFightsInfo(taskId, taskGroup);
        //打包数据返回给客户端
        RDTimeLimitFightCardInfo rdCardInfo = new RDTimeLimitFightCardInfo();
        List<RDFightsInfo.RDFightCard> rdFightCards = RDTimeLimitFightCardInfo.setCardGroup(rdFightsInfo);
        rdCardInfo.setOpponentCards(rdFightCards);
        List<CCardParam> attackerCardGroups = cunZYiYunFightService.getOwnCardGroup(uid, taskGroup, taskId);
        rdCardInfo.setOwnCards(RDTimeLimitFightCardInfo.setCardGroup(attackerCardGroups));
        List<CCardParam> timeLimitCardLibrary = timeLimitFightTaskService.getOrCreateCardLibrary(uid, taskGroup, taskId);
        rdCardInfo.setOptionalCardsPool(RDTimeLimitFightCardInfo.setCardGroup(timeLimitCardLibrary));
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
        Integer tasksAllNum = TaskTool.getTasksByTaskGroupEnum(TaskGroupEnum.QING_MING_TASK).size();
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
        UserTimeLimitTask nextUt = userQingMingTaskService.makeUserTaskInstance(uid, nextTask);
        gameUserService.addItem(uid, nextUt);
        synchronizeAttackMaouTask(uid, nextUt);
        return rd;
    }

    /**
     * 同步攻打任务血量任务进度
     *
     * @param uid
     */
    private void synchronizeAttackMaouTask(long uid, UserTimeLimitTask ut) {
        if (ut.getBaseId() != 200022 && ut.getBaseId() != 200027) {
            return;
        }
        int sid = gameUserService.getActiveSid(uid);
        IActivity gameActivity = activityService.getGameActivity(sid, ActivityEnum.GUAN_ZU);
        GameMaouAttacker userMaouAttacker = gameMaouAttackerService.getOrCreateAttacker(uid, gameActivity);
        ut.addValue(userMaouAttacker.getAttackBlood());
        gameUserService.updateItem(ut);
    }

    /**
     * 使用元宝刷新卡牌库
     *
     * @param uid
     * @param taskDataId
     */
    public RDTimeLimitFightCardInfo useColdRefreshCardLibrary(long uid, long taskDataId) {
        UserTimeLimitTask task = userTimeLimitTaskService.getTask(uid, taskDataId);
        if (null == task || task.getStatus() != TaskStatusEnum.DOING.getValue()) {
            throw new ExceptionForClientTip("task.not.exist");
        }
        int taskId = task.getBaseId();
        TaskGroupEnum taskGroup = TaskGroupEnum.fromValue(task.getGroup());
        GameUser gu = gameUserService.getGameUser(uid);
        //检查元宝
        int needCold = 50;
        ResChecker.checkGold(gu, needCold);
        //刷新卡牌库
        refreshCardLibrary(uid, taskId);
        //扣除元宝
        RDTimeLimitFightCardInfo rd = new RDTimeLimitFightCardInfo();
        ResEventPublisher.pubGoldDeductEvent(uid, needCold, WayEnum.CUNZ_YIYUN, rd);
        //返回客户端
        List<CCardParam> timeLimitCardLibrary = timeLimitFightTaskService.getOrCreateCardLibrary(uid, taskGroup, task.getBaseId());
        rd.setOptionalCardsPool(RDTimeLimitFightCardInfo.setCardGroup(timeLimitCardLibrary));
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
        List<CCardParam> cardLibrary = timeLimitFightTaskService.getRandomCardLibrary(TaskGroupEnum.QING_MING_TASK, taskId);
        //存入缓存
        timeLimitFightTaskService.saveCardLibrary(uid, cardLibrary);
        //清空玩家编组
        List<Integer> cardGroup = new ArrayList<>();
        timeLimitFightTaskService.saveUserTimeLimitFightCards(uid, cardGroup);
    }

    /**
     * 编辑玩家限时卡组
     *
     * @param uid
     * @param cards
     */
    public RDSuccess editUserTimeLimitFightCards(long uid, String cards) {
        List<Integer> cardsList = ListUtil.parseStrToInts(cards);
        if (cardsList.isEmpty()) {
            throw new ExceptionForClientTip("mxd.cant.save.empty.cardgroup");
        }
        if (cardsList.size() > 20) {
            throw new ExceptionForClientTip("card.grouping.outOfLimit");
        }
        //存缓存
        timeLimitFightTaskService.saveUserTimeLimitFightCards(uid, cardsList);
        return new RDSuccess();
    }

    /**
     * 判断是否参加清明村庄任务
     *
     * @param uid
     * @return
     */
    private boolean isJoined(long uid) {
        Integer hasJoinNewYearAndChristmasTask = TimeLimitCacheUtil.getFromCache(uid, "hasJoinQingMingTask", Integer.class);
        return null != hasJoinNewYearAndChristmasTask && hasJoinNewYearAndChristmasTask == 1;
    }

    /**
     * 保存清明任务村庄状态
     *
     * @param uid
     */
    private void setJoinStatus(long uid) {
        TimeLimitCacheUtil.cacheBothLocalAndRedis(uid, "hasJoinQingMingTask", 1, DateUtil.SECOND_ONE_DAY * 12);
    }

    /**
     * 获取战斗任务id集合
     *
     * @return
     */
    private List<Integer> getFightTasks() {
        return TaskTool.getTaskConfig(TaskGroupEnum.QING_MING_TASK).getTasks().stream()
                .filter(t -> t.getType() == TaskTypeEnum.TIME_LIMIT_FIGHT_TASK.getValue()).map(CfgTaskEntity::getId).collect(Collectors.toList());
    }
}
