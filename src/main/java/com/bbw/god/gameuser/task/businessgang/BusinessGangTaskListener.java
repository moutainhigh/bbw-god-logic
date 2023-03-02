package com.bbw.god.gameuser.task.businessgang;

import com.bbw.god.fight.FightTypeEnum;
import com.bbw.god.game.combat.event.CombatFightWinEvent;
import com.bbw.god.game.combat.event.EPFightEnd;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.city.CityTool;
import com.bbw.god.game.config.special.SpecialTool;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.businessgang.BusinessGangCfgTool;
import com.bbw.god.gameuser.businessgang.cfg.CfgTaskRules;
import com.bbw.god.gameuser.special.event.EPSpecialDeduct;
import com.bbw.god.gameuser.special.event.SpecialDeductEvent;
import com.bbw.god.gameuser.task.CfgTaskEntity;
import com.bbw.god.gameuser.task.TaskDifficulty;
import com.bbw.god.gameuser.task.TaskGroupEnum;
import com.bbw.god.gameuser.task.TaskTool;
import com.bbw.god.gameuser.task.businessgang.event.BusinessGangTaskAchievedEvent;
import com.bbw.god.gameuser.task.businessgang.event.EPBusinessGangTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 商帮任务监听事件
 *
 * @author fzj
 * @date 2022/1/29 10:56
 */
@Component
@Slf4j
@Async
public class BusinessGangTaskListener {
    /** 特产买卖任务 */
    private final static List<Integer> SPECIALTY_WEEKLY_TASK = Arrays.asList(160001, 160002);
    /** 战斗任务 */
    private final static List<Integer> FIGHT_WEEKLY_TASK = Arrays.asList(160009, 160010);
    @Autowired
    UserSpecialtyShippingTaskService specialtyShippingTaskService;
    @Autowired
    GameUserService gameUserService;
    @Autowired
    UserWeeklyTaskService userWeeklyTaskService;

    /**
     * 完成特产派送任务监听
     *
     * @param event
     */
    @Order(1000)
    @EventListener
    public void finishShippingTask(SpecialDeductEvent event) {
        EPSpecialDeduct ep = event.getEP();
        if (ep.getWay() != WayEnum.TRADE) {
            return;
        }
        Long uid = ep.getGuId();
        Integer pos = ep.getPos();
        Integer cityId = CityTool.getCityId(pos);
        //城池等级
        Integer cityLv = CityTool.getCityById(cityId).getLevel();
        //获得城区
        Integer cityArea = CityTool.getCityById(cityId).getCountry();
        //获取当前正在进行的特产运送任务
        List<UserBusinessGangSpecialtyShippingTask> tasks = specialtyShippingTaskService.getTasks(uid);
        if (tasks.isEmpty()) {
            return;
        }
        //获得需要加进度的任务
        List<UserBusinessGangSpecialtyShippingTask> shippingTasks = tasks.stream().filter(t -> {
            Integer difficulty = TaskTool.getTaskEntity(TaskGroupEnum.BUSINESS_GANG_SPECIALTY_SHIPPING_TASK, t.getBaseId()).getDifficulty();
            if (difficulty == TaskDifficulty.MIDDLE_LEVEL.getValue()) {
                return cityLv.equals(t.getTargetLv()) && cityArea.equals(t.getTargetCityArea());
            }
            return cityId.equals(t.getTargetCity()) || cityArea.equals(t.getTargetCityArea());
        }).collect(Collectors.toList());
        if (shippingTasks.isEmpty()) {
            return;
        }
        List<EPSpecialDeduct.SpecialInfo> specialInfoList = ep.getSpecialInfoList();
        //获得任务规则
        for (UserBusinessGangSpecialtyShippingTask task : shippingTasks) {
            Integer difficulty = TaskTool.getTaskDifficulty(TaskGroupEnum.BUSINESS_GANG_SPECIALTY_SHIPPING_TASK, task.getBaseId());
            CfgTaskRules cfgTaskRules = BusinessGangCfgTool.getShippingTaskRules(difficulty);
            if (null == cfgTaskRules) {
                continue;
            }
            specialInfoList.forEach(s -> addProgress(task, cfgTaskRules, s));
        }
        gameUserService.updateItems(shippingTasks);
    }

    /**
     * 加进度
     *
     * @param task
     * @param cfgTaskRules
     * @param specialInfo
     */
    private void addProgress(UserBusinessGangSpecialtyShippingTask task, CfgTaskRules cfgTaskRules, EPSpecialDeduct.SpecialInfo specialInfo) {
        Map<String, Integer> targetSpecial = task.getTargetAndProgress();
        String specialName = SpecialTool.getSpecialById(specialInfo.getBaseSpecialIds()).getName();
        Integer progress = targetSpecial.get(specialName);
        if (null == progress) {
            return;
        }
        task.getTargetAndProgress().put(specialName, progress + 1);
        //更改任务状态
        Integer needProgress = cfgTaskRules.getShippingNum();
        boolean finish = targetSpecial.values().stream().noneMatch(t -> t < needProgress);
        if (!finish) {
            return;
        }
        task.addValue(1);
    }

    /**
     * 完成周常任务监听
     *
     * @param event
     */
    @Order(1000)
    @EventListener
    public void finishWeeklyTask(CombatFightWinEvent event) {
        EPFightEnd ep = event.getEP();
        FightTypeEnum fightType = ep.getFightType();
        Long uid = ep.getGuId();
        if (fightType != FightTypeEnum.YG && fightType != FightTypeEnum.TRAINING) {
            return;
        }
        //获取正在进行的周常任务
        List<UserBusinessGangWeeklyTask> tasks = userWeeklyTaskService.getTasks(uid);
        if (tasks.isEmpty()) {
            return;
        }
        List<UserBusinessGangWeeklyTask> weeklyTasks = tasks.stream().filter(t -> FIGHT_WEEKLY_TASK.contains(t.getBaseId())).collect(Collectors.toList());
        if (weeklyTasks.isEmpty()) {
            return;
        }
        for (UserBusinessGangWeeklyTask task : weeklyTasks) {
            addWeeklyTaskProgress(task, ep);
        }
        gameUserService.updateItems(tasks);
    }

    /**
     * 周常任务加进度
     *
     * @param task
     * @param ep
     */
    private void addWeeklyTaskProgress(UserBusinessGangWeeklyTask task, EPFightEnd ep) {
        FightTypeEnum fightType = ep.getFightType();
        if (fightType == FightTypeEnum.TRAINING && task.getBaseId() == 160009) {
            task.addValue(1);
            return;
        }
        if (fightType == FightTypeEnum.YG && task.getBaseId() == 160010) {
            task.addValue(1);
        }
    }

    /**
     * 完成周常任务监听
     *
     * @param event
     */
    @Order(1000)
    @EventListener
    public void finishWeeklyTask(SpecialDeductEvent event) {
        EPSpecialDeduct ep = event.getEP();
        Long uid = ep.getGuId();
        //获取正在进行的周常任务
        List<UserBusinessGangWeeklyTask> tasks = userWeeklyTaskService.getTasks(uid);
        if (tasks.isEmpty()) {
            return;
        }
        List<UserBusinessGangWeeklyTask> weeklyTasks = tasks.stream().filter(t -> SPECIALTY_WEEKLY_TASK.contains(t.getBaseId())).collect(Collectors.toList());
        if (weeklyTasks.isEmpty()) {
            return;
        }
        weeklyTasks.forEach(w -> addWeeklyTaskProgress(w, ep));
        gameUserService.updateItems(weeklyTasks);
    }

    /**
     * 周常任务加进度
     *
     * @param task
     * @param ep
     */
    private void addWeeklyTaskProgress(UserBusinessGangWeeklyTask task, EPSpecialDeduct ep) {
        Integer taskId = task.getBaseId();
        List<EPSpecialDeduct.SpecialInfo> specialInfoList = ep.getSpecialInfoList();
        if (specialInfoList.isEmpty()) {
            return;
        }
        if (taskId == 160001) {
            //计算利润
            long profit = specialInfoList.stream().mapToLong(s -> {
                if (s.getSellPrice() == null || s.getBuyPrice() == null) {
                    return 0;
                }
                return Math.max(s.getSellPrice() - s.getBuyPrice(), 0);
            }).sum();
            task.addValue(profit);
            return;
        }
        int specialNum = specialInfoList.size();
        task.addValue(specialNum);
    }

    /**
     * 完成周常任务监听
     *
     * @param event
     */
    @Order(1000)
    @EventListener
    public void finishWeeklyTask(BusinessGangTaskAchievedEvent event) {
        EPBusinessGangTask ep = event.getEP();
        Long uid = ep.getGuId();
        //获取正在进行的周常任务
        List<UserBusinessGangWeeklyTask> tasks = userWeeklyTaskService.getTasks(uid);
        if (tasks.isEmpty()) {
            return;
        }
        tasks.forEach(t -> addWeeklyTaskProgress(t, ep));
        gameUserService.updateItems(tasks);
    }

    /**
     * 增加周常任务的进度
     *
     * @param task
     * @param ep
     */
    private void addWeeklyTaskProgress(UserBusinessGangWeeklyTask task, EPBusinessGangTask ep) {
        Integer baseId = task.getBaseId();
        //完成任务的类型
        TaskGroupEnum taskGroup = ep.getTaskGroup();
        //完成任务的等级
        int taskId = ep.getTaskId();
        CfgTaskEntity taskEntity = TaskTool.getTaskEntity(taskGroup, taskId);
        Integer difficulty = taskEntity.getDifficulty();
        if (baseId == 160003 && taskGroup == TaskGroupEnum.BUSINESS_GANG_SPECIALTY_SHIPPING_TASK) {
            task.addValue(1);
            return;
        }
        if (baseId == 160004 && taskGroup == TaskGroupEnum.BUSINESS_GANG_DISPATCH_TASK) {
            task.addValue(1);
            return;
        }
        if (baseId == 160005 && difficulty == TaskDifficulty.SUPER_LEVEL.getValue()) {
            task.addValue(1);
            return;
        }
        if (baseId == 160006 && difficulty == TaskDifficulty.HIGH_LEVEL.getValue()) {
            task.addValue(1);
        }
    }
}
