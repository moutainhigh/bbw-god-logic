package com.bbw.god.gameuser.task.daily.service;

import com.bbw.exception.CoderException;
import com.bbw.god.event.common.CommonEventPublisher;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.statistic.behavior.building.BuildingAwardStatisticService;
import com.bbw.god.gameuser.statistic.behavior.fight.FightStatisticService;
import com.bbw.god.gameuser.statistic.behavior.login.LoginStatisticService;
import com.bbw.god.gameuser.statistic.behavior.miaoy.MiaoYStatisticService;
import com.bbw.god.gameuser.statistic.behavior.nvwm.NvwmStatisticService;
import com.bbw.god.gameuser.statistic.behavior.task.CocTaskStatisticService;
import com.bbw.god.gameuser.statistic.behavior.task.GuildTaskStatisticService;
import com.bbw.god.gameuser.statistic.resource.card.CardResStatisticService;
import com.bbw.god.gameuser.statistic.resource.copper.CopperResStatisticService;
import com.bbw.god.gameuser.statistic.resource.dice.DiceResStatisticService;
import com.bbw.god.gameuser.statistic.resource.ele.EleResStatisticService;
import com.bbw.god.gameuser.statistic.resource.gold.GoldResStatisticService;
import com.bbw.god.gameuser.statistic.resource.special.SpecialResStatisticService;
import com.bbw.god.gameuser.statistic.resource.treasure.TreasureResStatisticService;
import com.bbw.god.gameuser.task.CfgTaskEntity;
import com.bbw.god.gameuser.task.TaskTool;
import com.bbw.god.gameuser.task.TaskTypeEnum;
import com.bbw.god.gameuser.task.UserTaskInfo;
import com.bbw.god.notify.rednotice.ModuleEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author suchaobin
 * @description 基础每日任务service
 * @date 2020/11/24 11:22
 **/
@Service
public abstract class BaseDailyTaskService {
    @Autowired
    protected UserDailyTaskService userDailyTaskService;
    @Autowired
    protected GameUserService gameUserService;
    @Autowired
    protected LoginStatisticService loginStatisticService;
    @Autowired
    protected EleResStatisticService eleStatisticService;
    @Autowired
    protected DiceResStatisticService diceStatisticService;
    @Autowired
    protected CopperResStatisticService copperStatisticService;
    @Autowired
    protected GoldResStatisticService goldStatisticService;
    @Autowired
    protected FightStatisticService fightStatisticService;
    @Autowired
    protected BuildingAwardStatisticService buildingAwardStatisticService;
    @Autowired
    protected CardResStatisticService cardStatisticService;
    @Autowired
    protected TreasureResStatisticService treasureStatisticService;
    @Autowired
    protected CocTaskStatisticService cocTaskStatisticService;
    @Autowired
    protected GuildTaskStatisticService guildTaskStatisticService;
    @Autowired
    protected SpecialResStatisticService specialStatisticService;
    @Autowired
    protected MiaoYStatisticService miaoYStatisticService;
    @Autowired
    protected NvwmStatisticService nvwmStatisticService;


    /**
     * 获取当前service对应的任务id集合
     *
     * @return 当前service对应的任务id集合
     */
    public abstract List<Integer> getMyTaskIds();

    /**
     * 获取当前任务所需值
     *
     * @param level  玩家等级
     * @param taskId 任务id
     * @return 当前任务所需值
     */
    public int getMyNeedValue(int level, int taskId) {
        if (!getMyTaskIds().contains(taskId)) {
            throw new CoderException(String.format("当前service不支持id=%s的任务", taskId));
        }
        if (TaskTool.isBoxTask(taskId)) {
            return TaskTool.getDailyTaskCfgBox(taskId).getScore();
        }
        return TaskTool.getDailyTask(taskId).getValue();
    }

    /**
     * 获取当前任务奖励
     *
     * @param taskId 任务id
     * @return 当前任务奖励
     */
    public List<Award> getMyAwards(int taskId) {
        if (!getMyTaskIds().contains(taskId)) {
            throw new CoderException(String.format("当前service不支持id=%s的任务", taskId));
        }
        if (TaskTool.isBoxTask(taskId)) {
            return TaskTool.getDailyTaskCfgBox(taskId).getAwards();
        }
        return TaskTool.getDailyTask(taskId).getAwards();
    }

    /**
     * 获取当前任务进度(用于判断任务是否完成)
     *
     * @param uid    玩家id
     * @param level  玩家等级
     * @param taskId 任务id
     * @param info   任务对象信息
     * @return 当前任务进度
     */
    public int getMyProgress(long uid, int level, int taskId, UserTaskInfo info) {
        if (isAccomplished(taskId, info)) {
            return getMyNeedValue(level, taskId);
        }
        return doGetProgress(uid, level, taskId, info);
    }

    /**
     * 获取当前任务进度(用于判断任务是否完成)
     *
     * @param uid    玩家id
     * @param level  玩家等级
     * @param taskId 任务id
     * @param info   任务对象信息
     * @return 当前任务进度
     */
    public abstract int doGetProgress(long uid, int level, int taskId, UserTaskInfo info);

    /**
     * 获取每日任务进度
     *
     * @param level  玩家等级
     * @param taskId 任务id
     * @param info   任务对象信息
     * @return 当前任务进度
     */
    public int getDailyTaskProgress(int level, int taskId, UserTaskInfo info) {
        if (isAccomplished(taskId, info)) {
            return getMyNeedValue(level, taskId);
        }
        List<Integer> awardedIds = info.getAwardedIds();
        int sum = 0;
        for (Integer awardedId : awardedIds) {
            // 箱子不计算分数
            if (TaskTool.isBoxTask(awardedId)) {
                continue;
            }
            CfgTaskEntity dailyTask = TaskTool.getDailyTask(awardedId);
            sum += dailyTask.getAwards().stream().filter(tmp ->
                    tmp.getItem() == AwardEnum.HY.getValue()).mapToInt(Award::getNum).sum();
        }
        return sum;
    }

    /**
     * 获取当前任务进度(用于展示给客户端)
     *
     * @param uid    玩家id
     * @param level  玩家等级
     * @param taskId 任务id
     * @param info   任务对象信息
     * @return 当前任务进度
     */
    public int getMyProgressForShow(long uid, int level, int taskId, UserTaskInfo info) {
        return getMyProgress(uid, level, taskId, info);
    }

    /**
     * 判断当前任务是否完成（已领取的也算）
     *
     * @param taskId 任务id
     * @param info   任务信息
     * @return 当前任务是否完成（已领取的也算）
     */
    public boolean isAccomplished(int taskId, UserTaskInfo info) {
        if (null == info) {
            return false;
        }
        return info.ifAccomplished(taskId) || info.ifAwarded(taskId);
    }

    /**
     * 完成任务
     *
     * @param uid        玩家id
     * @param level      玩家等级
     * @param taskId     任务id
     * @param totalValue 玩家目前任务进度
     */
    public void finish(GameUser gu, int taskId, long totalValue, UserTaskInfo info) {
        if (null == info) {
            return;
        }
        // 如果不是今日数据，重新生成对象
        if (!info.isToday()) {
            info = userDailyTaskService.getTodayDailyTaskInfo(gu);
        }
        // 不在未完成任务列表中
        if (!info.getUnFinishIds().contains(taskId)) {
            return;
        }
        // 已完成
        if (isAccomplished(taskId, info)) {
            return;
        }
        // 达成任务条件
        int myNeedValue = getMyNeedValue(gu.getLevel(), taskId);
        if (totalValue >= myNeedValue) {
            info.accomplish(taskId);
            gameUserService.updateItem(info);
            CommonEventPublisher.pubAccomplishEvent(gu.getId(), ModuleEnum.TASK, TaskTypeEnum.DAILY_TASK.getValue(), taskId);
        }
    }
}
