package com.bbw.god.gameuser.task.timelimit;

import com.bbw.common.DateUtil;
import com.bbw.common.ListUtil;
import com.bbw.common.PowerRandom;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.fight.FighterInfo;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.game.config.card.CfgCardEntity;
import com.bbw.god.gameuser.task.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 时效任务工具类
 *
 * @author: suhq
 * @date: 2021/8/5 5:52 下午
 */
public class TimeLimitTaskTool {
    /**
     * 获取全局配置
     *
     * @return
     */
    public static CfgTimeLimitTaskRules getRules(TaskGroupEnum taskGroup) {
        return Cfg.I.get(taskGroup.getValue(), CfgTimeLimitTaskRules.class);
    }

    /**
     * 获取任务初始化的结束时间
     *
     * @param taskId
     * @return
     */
    public static Date getEndInitTime(TaskGroupEnum taskGroup, int taskId) {
        CfgTaskEntity task = TaskTool.getTaskEntity(taskGroup, taskId);
        CfgTimeLimitTaskRules rules = getRules(taskGroup);
        TaskTypeEnum taskType = TaskTypeEnum.fromValue(task.getType());
        Date timeEnd = DateUtil.now();
        switch (taskType) {
            case TIME_LIMIT_NORMAL:
                return DateUtil.addMinutes(timeEnd, rules.getNormalTaskWait());
            case TIME_LIMIT_FIGHT_TASK:
                return DateUtil.addMinutes(timeEnd, rules.getFightTaskWait());
            case TIME_LIMIT_DISPATCH_TASK:
                return DateUtil.addMinutes(timeEnd, rules.getDispatchTaskConfigs().get(task.getDifficulty()).getWaitTime());
        }
        return null;
    }

    /**
     * 获取派遣时间
     *
     * @param taskId
     * @return
     */
    public static int getDispatchTime(TaskGroupEnum taskGroup, int taskId) {
        CfgTaskEntity task = TaskTool.getTaskEntity(taskGroup, taskId);
        if (task.getType() != TaskTypeEnum.TIME_LIMIT_DISPATCH_TASK.getValue()) {
            throw new ExceptionForClientTip("task.not.dispatch");
        }
        CfgTimeLimitTaskRules rules = getRules(taskGroup);
        return rules.getDispatchTaskConfigs().get(task.getDifficulty()).getDispatchTime();
    }

    /**
     * 获取任务初始时间
     *
     * @param task
     * @return
     */
    public static TaskStatusEnum getInitStatus(CfgTaskEntity task) {
        TaskTypeEnum taskType = TaskTypeEnum.fromValue(task.getType());
        TaskStatusEnum status = TaskStatusEnum.WAITING;
        switch (taskType) {
            case TIME_LIMIT_NORMAL:
            case TIME_LIMIT_FIGHT_TASK:
                status = TaskStatusEnum.DOING;
                break;
            case TIME_LIMIT_DISPATCH_TASK:
                status = TaskStatusEnum.WAITING;
                break;
        }
        return status;
    }

    /**
     * 获取派发规则
     *
     * @param taskGroup
     * @param taskId
     * @return
     */
    public static CfgDispatchTaskRules getDispatchRule(TaskGroupEnum taskGroup, int taskId) {
        CfgTaskEntity task = TaskTool.getTaskEntity(taskGroup, taskId);
        CfgTimeLimitTaskRules rules = getRules(taskGroup);
        return rules.getDispatchTaskConfigs().get(task.getDifficulty());
    }

    /**
     * 获取额外技能派发要求
     *
     * @param taskGroup
     * @param task
     * @return
     */
    public static List<Integer> getExtraRandomSkills(TaskGroupEnum taskGroup, CfgTaskEntity task) {
        if (TaskTypeEnum.TIME_LIMIT_DISPATCH_TASK.getValue() != task.getType()) {
            return new ArrayList<>();
        }
        CfgDispatchTaskRules cfgDispatchTaskRules = getDispatchRule(taskGroup, task.getId());
        List<Integer> extraSkills = new ArrayList<>();
        if (ListUtil.isNotEmpty(cfgDispatchTaskRules.getSkillPool1())) {
            extraSkills.add(PowerRandom.getRandomFromList(cfgDispatchTaskRules.getSkillPool1(), extraSkills));
        }
        if (ListUtil.isNotEmpty(cfgDispatchTaskRules.getSkillPool2())) {
            extraSkills.add(PowerRandom.getRandomFromList(cfgDispatchTaskRules.getSkillPool2(), extraSkills));
        }
        if (ListUtil.isNotEmpty(cfgDispatchTaskRules.getSkillPool3())) {
            extraSkills.add(PowerRandom.getRandomFromList(cfgDispatchTaskRules.getSkillPool3(), extraSkills));
        }
        return extraSkills;
    }

    /**
     * 获取额外派发奖励
     *
     * @param taskGroup
     * @param task
     * @return
     */
    public static List<Award> getExtraRandomAward(TaskGroupEnum taskGroup, CfgTaskEntity task) {
        List<Award> extraAwards = new ArrayList<>();
        if (TaskTypeEnum.TIME_LIMIT_DISPATCH_TASK.getValue() != task.getType()) {
            return extraAwards;
        }
        CfgTimeLimitTaskRules rules = getRules(taskGroup);
        List<Award> awards = rules.getDispatchTaskExtraAward().get(task.getDifficulty()).getAwards();
        List<Integer> probs = awards.stream().map(Award::getProbability).collect(Collectors.toList());
        int index = PowerRandom.getIndexByProbs(probs, 100);
        extraAwards.add(awards.get(index));
        return extraAwards;
    }


    /**
     * 获得卡牌最大精力
     *
     * @param cardId
     * @param hie
     * @return
     */
    public static int getMaxCardVigor(int cardId, int hie) {
        CfgCardEntity card = CardTool.getCardById(cardId);
        return card.getStar() + hie / 2;
    }

    /**
     * 获得攻击者配置信息
     *
     * @param taskGroup
     * @param taskId
     */
    public static FighterInfo getAttackerCfgInfo(TaskGroupEnum taskGroup, int taskId) {
        CfgTimeLimitTaskRules rules = getRules(taskGroup);
        Map<Integer, FighterInfo> attackersInfo = rules.getAttackersInfo();
        if (null == attackersInfo) {
            throw new ExceptionForClientTip("activity.cunz.task.not.info");
        }
        return attackersInfo.get(taskId);
    }

}
