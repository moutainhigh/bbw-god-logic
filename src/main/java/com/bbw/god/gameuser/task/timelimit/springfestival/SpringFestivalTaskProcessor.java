package com.bbw.god.gameuser.task.timelimit.springfestival;

import com.bbw.common.DateUtil;
import com.bbw.common.ListUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.activity.holiday.processor.HolidayCunZYiYunProcessor;
import com.bbw.god.cache.TimeLimitCacheUtil;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.fight.RDFightsInfo;
import com.bbw.god.game.combat.data.param.CCardParam;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.special.CfgSpecialEntity;
import com.bbw.god.game.config.special.SpecialEnum;
import com.bbw.god.game.config.special.SpecialTool;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.res.ResChecker;
import com.bbw.god.gameuser.res.ResEventPublisher;
import com.bbw.god.gameuser.special.UserSpecial;
import com.bbw.god.gameuser.special.UserSpecialService;
import com.bbw.god.gameuser.special.event.EPSpecialDeduct;
import com.bbw.god.gameuser.special.event.SpecialEventPublisher;
import com.bbw.god.gameuser.task.*;
import com.bbw.god.gameuser.task.timelimit.*;
import com.bbw.god.gameuser.task.timelimit.event.TimeLimitTaskEventPublisher;
import com.bbw.god.gameuser.treasure.TreasureChecker;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.rd.RDCommon;
import com.bbw.god.rd.RDSuccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 春节任务
 *
 * @author fzj
 * @date 2022/1/5 11:43
 */
@Service
public class SpringFestivalTaskProcessor extends AbstractTaskProcessor {

    @Autowired
    private HolidayCunZYiYunProcessor holidayCunZYiYun;
    @Autowired
    private UserDispatchTaskService userDispatchTaskService;
    @Autowired
    private UserSpringFestivalTaskService userSpringFestivalTaskService;
    @Autowired
    private TimeLimitFightTaskService timeLimitFightTaskService;
    @Autowired
    private UserTimeLimitTaskService userTimeLimitTaskService;
    @Autowired
    private CunZYiYunFightService cunZYiYunFightService;
    @Autowired
    private UserSpecialService userSpecialService;

    public SpringFestivalTaskProcessor() {
        this.taskTypes = Arrays.asList(TaskTypeEnum.SPRING_FESTIVAL_TASK);
    }

    @Override
    public RDTaskList getTasks(long uid, Integer days) {
        return new RDTaskList();
    }

    @Override
    public RDCommon gainTaskAward(long uid, int id, String awardIndex) {
        return new RDCommon();
    }

    /** 活动需要特产 */
    private static final Map<Integer, SpecialEnum> ACTIVITY_SPECIALTY = new HashMap<Integer, SpecialEnum>() {
        private static final long serialVersionUID = -8516266763323893175L;

        {
            put(140004, SpecialEnum.SL);
            put(140007, SpecialEnum.GD);
            put(140011, SpecialEnum.SJ);
            put(140016, SpecialEnum.LJ);
            put(140024, SpecialEnum.SXJ);
        }
    };
    /** 活动需要收集产品 */
    private final static List<Integer> ACTIVITY_TREASURES = Arrays.asList(
            TreasureEnum.CHARCOAL.getValue(),
            TreasureEnum.SULFUR.getValue(),
            TreasureEnum.SALTPETER.getValue());

    /**
     * 获取任务信息
     *
     * @param uid
     * @return
     */
    public RDSuccess getTask(long uid) {
        if (!isJoined(uid)) {
            CfgTaskEntity taskEntity = TaskTool.getTaskEntity(TaskGroupEnum.SPRING_FESTIVAL_TASK, 140001);
            UserTimeLimitTask userTimeLimitTask = userSpringFestivalTaskService.makeUserTaskInstance(uid, taskEntity);
            gameUserService.addItem(uid, userTimeLimitTask);
            setJoinStatus(uid);
        }
        List<UserTimeLimitTask> uts = userSpringFestivalTaskService.getAllTasks(uid);
        UserTimeLimitTask ut = uts.get(uts.size() - 1);
        CfgTaskEntity taskEntity = TaskTool.getTaskEntity(TaskGroupEnum.SPRING_FESTIVAL_TASK, ut.getBaseId());
        Integer tasksAllNum = TaskTool.getTasksByTaskGroupEnum(TaskGroupEnum.SPRING_FESTIVAL_TASK).size();
        boolean executable = holidayCunZYiYun.isExecutable(uid, taskEntity, tasksAllNum);
        if (taskEntity.getType() == TaskTypeEnum.TIME_LIMIT_DISPATCH_TASK.getValue()) {
            return userDispatchTaskService.doGetDispatchInfo(ut, executable);
        }
        RDTaskItem rdTask = RDTaskItem.getInstance(ut, taskEntity, TaskGroupEnum.SPRING_FESTIVAL_TASK);
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
        TaskGroupEnum taskGroup = TaskGroupEnum.SPRING_FESTIVAL_TASK;
        //生成npc卡组
        RDFightsInfo rdFightsInfo = cunZYiYunFightService.getOrCreatOpponentFightsInfo(taskId, taskGroup);
        //打包数据返回给客户端
        RDTimeLimitFightCardInfo rdCardInfo = new RDTimeLimitFightCardInfo();
        rdCardInfo.setOpponentCards(RDTimeLimitFightCardInfo.setCardGroup(rdFightsInfo));
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
        Integer tasksAllNum = TaskTool.getTasksByTaskGroupEnum(TaskGroupEnum.SPRING_FESTIVAL_TASK).size();
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
        UserTimeLimitTask nextUt = userSpringFestivalTaskService.makeUserTaskInstance(uid, nextTask);
        gameUserService.addItem(uid, nextUt);
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
        List<CCardParam> cardLibrary = timeLimitFightTaskService.getRandomCardLibrary(TaskGroupEnum.SPRING_FESTIVAL_TASK, taskId);
        //存入缓存
        timeLimitFightTaskService.saveCardLibrary(uid, cardLibrary);
        //清空玩家编组
        List<Integer> cardGroup = new ArrayList<>();
        timeLimitFightTaskService.saveUserTimeLimitFightCards(uid, cardGroup);
    }

    /**
     * 消耗指定道具
     *
     * @param taskDataId
     * @return
     */
    public RDCommon consumeTreasures(long uid, long taskDataId) {
        //获得任务信息
        UserTimeLimitTask task = userTimeLimitTaskService.getTask(uid, taskDataId);
        if (null == task || task.getStatus() != TaskStatusEnum.DOING.getValue()) {
            throw new ExceptionForClientTip("task.not.exist");
        }
        RDCommon rd = new RDCommon();
        //获得任务id
        Integer taskId = task.getBaseId();
        //是否是特此任务
        if (ACTIVITY_SPECIALTY.containsKey(taskId)) {
            //获得玩家指定特产信息
            SpecialEnum specialEnum = ACTIVITY_SPECIALTY.get(taskId);
            List<UserSpecial> userSpecials = userSpecialService.getOwnUnLockSpecialsByBaseId(uid, specialEnum.getValue());
            //没有特产抛出异常
            if (userSpecials.size() <= 0) {
                throw new ExceptionForClientTip("special.not.enough", specialEnum.getName());
            }
            //构造特产扣除事件参数
            GameUser gu = this.gameUserService.getGameUser(uid);
            BaseEventParam bep = new BaseEventParam(gu.getId(), WayEnum.CUNZ_YIYUN, rd);
            //获得需要特产数量
            int progress = (int) (task.getNeedValue() - task.getValue());
            int needNum = userSpecials.size() > progress ? progress : userSpecials.size();
            //需要扣除的特产信息
            List<UserSpecial> needHandInSpecials = userSpecials.subList(0, needNum);
            List<EPSpecialDeduct.SpecialInfo> infos = new ArrayList<>();
            for (UserSpecial userSpecial : needHandInSpecials) {
                CfgSpecialEntity special = SpecialTool.getSpecialById(userSpecial.getBaseId());
                EPSpecialDeduct.SpecialInfo info = EPSpecialDeduct.SpecialInfo.getInstance(userSpecial.getId(), userSpecial
                        .getBaseId(), special.getBuyPrice(userSpecial.getDiscount()));
                infos.add(info);
            }
            //发布事件
            EPSpecialDeduct epSpecialDeduct = EPSpecialDeduct.instance(bep, gu.getLocation().getPosition(), infos);
            SpecialEventPublisher.pubSpecialDeductEvent(epSpecialDeduct);
            achieveTask(uid, Collections.singletonList(taskId), infos.size());
            return rd;
        }
        //法宝任务
        if (task.getBaseId() == 140020) {
            for (Integer treasureId : ACTIVITY_TREASURES) {
                TreasureChecker.checkIsEnough(treasureId, 1, uid);
                TreasureEventPublisher.pubTDeductEvent(uid, treasureId, 1, WayEnum.CUNZ_YIYUN, rd);
            }
        } else {
            //默认灵石任务
            int num = task.getNeedValue();
            TreasureChecker.checkIsEnough(TreasureEnum.LING_SHI.getValue(), num, uid);
            TreasureEventPublisher.pubTDeductEvent(uid, TreasureEnum.LING_SHI.getValue(), num, WayEnum.CUNZ_YIYUN, rd);
        }
        return rd;
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
        ResEventPublisher.pubGoldDeductEvent(uid, needCold, WayEnum.LEADER_CARD_ACTIVATION, rd);
        //返回客户端
        List<CCardParam> timeLimitCardLibrary = timeLimitFightTaskService.getOrCreateCardLibrary(uid, taskGroup, task.getBaseId());
        rd.setOptionalCardsPool(RDTimeLimitFightCardInfo.setCardGroup(timeLimitCardLibrary));
        return rd;
    }

    /**
     * 判断是否参加新年村庄任务
     *
     * @param uid
     * @return
     */
    private boolean isJoined(long uid) {
        Integer hasJoinNewYearAndChristmasTask = TimeLimitCacheUtil.getFromCache(uid, "hasJoinSpringFestivalTask", Integer.class);
        return null != hasJoinNewYearAndChristmasTask && hasJoinNewYearAndChristmasTask == 1;
    }

    /**
     * 保存新年任务村庄状态
     *
     * @param uid
     */
    private void setJoinStatus(long uid) {
        TimeLimitCacheUtil.cacheBothLocalAndRedis(uid, "hasJoinSpringFestivalTask", 1, DateUtil.SECOND_ONE_DAY * 15);
    }

    /**
     * 获取战斗任务id集合
     *
     * @return
     */
    private List<Integer> getFightTasks() {
        return TaskTool.getTaskConfig(TaskGroupEnum.SPRING_FESTIVAL_TASK).getTasks().stream()
                .filter(t -> t.getType() == TaskTypeEnum.TIME_LIMIT_FIGHT_TASK.getValue()).map(CfgTaskEntity::getId).collect(Collectors.toList());
    }

    private void achieveTask(long uid, List<Integer> taskIds, long addedNum) {
        List<UserTimeLimitTask> uts = userSpringFestivalTaskService.getTasks(uid);
        uts = uts.stream().filter(tmp -> taskIds.contains(tmp.getBaseId()) &&
                tmp.getStatus() == TaskStatusEnum.DOING.getValue()).collect(Collectors.toList());
        if (ListUtil.isEmpty(uts)) {
            return;
        }
        for (UserTimeLimitTask ut : uts) {
            ut.addValue(addedNum);
            if (ut.getStatus() != TaskStatusEnum.ACCOMPLISHED.getValue()) {
                continue;
            }
            TimeLimitTaskEventPublisher.pubTimeLimitTaskAchievedEvent(uid, TaskGroupEnum.SPRING_FESTIVAL_TASK, ut.getBaseId());
        }
        gameUserService.updateItems(uts);
    }
}
