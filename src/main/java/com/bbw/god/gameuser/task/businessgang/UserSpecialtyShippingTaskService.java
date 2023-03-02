package com.bbw.god.gameuser.task.businessgang;

import com.bbw.cache.UserCacheService;
import com.bbw.common.ListUtil;
import com.bbw.common.PowerRandom;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardService;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.city.CfgCityEntity;
import com.bbw.god.game.config.city.CityTool;
import com.bbw.god.game.config.special.CfgSpecialEntity;
import com.bbw.god.game.config.special.SpecialTool;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.businessgang.BusinessGangCfgTool;
import com.bbw.god.gameuser.businessgang.BusinessGangService;
import com.bbw.god.gameuser.businessgang.Enum.BusinessGangEnum;
import com.bbw.god.gameuser.businessgang.UserBusinessGangService;
import com.bbw.god.gameuser.businessgang.cfg.CfgBusinessGangShippingTaskRules;
import com.bbw.god.gameuser.businessgang.cfg.CfgPrestigeEntity;
import com.bbw.god.gameuser.businessgang.cfg.CfgSpecialsRules;
import com.bbw.god.gameuser.businessgang.cfg.CfgTaskRules;
import com.bbw.god.gameuser.businessgang.user.UserBusinessGangInfo;
import com.bbw.god.gameuser.businessgang.user.UserBusinessGangTaskInfo;
import com.bbw.god.gameuser.task.*;
import com.bbw.god.gameuser.task.businessgang.event.BusinessGangTaskEventPublisher;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 特产运送任务服务类
 *
 * @author fzj
 * @date 2022/1/18 15:59
 */
@Service
public class UserSpecialtyShippingTaskService {
    @Autowired
    GameUserService gameUserService;
    @Autowired
    private UserCacheService userCacheService;
    @Autowired
    UserBusinessGangService userBusinessGangService;
    @Autowired
    private AwardService awardService;
    @Autowired
    private BusinessGangService businessGangService;

    /**
     * 获取所有任务
     *
     * @param uid
     * @return
     */
    public List<UserBusinessGangSpecialtyShippingTask> getAllTasks(long uid) {
        return userCacheService.getUserDatas(uid,UserBusinessGangSpecialtyShippingTask.class);
//        return gameUserService.getMultiItems(uid, UserBusinessGangSpecialtyShippingTask.class);
    }

    /**
     * 获取真正进行任务
     *
     * @param uid
     * @return
     */
    public List<UserBusinessGangSpecialtyShippingTask> getTasks(long uid) {
        return getAllTasks(uid).stream().filter(t -> t.getStatus() == TaskStatusEnum.DOING.getValue()).collect(Collectors.toList());
    }

    /**
     * 获得指定任务
     *
     * @param uid
     * @param taskId
     * @return
     */
    public UserBusinessGangSpecialtyShippingTask getTask(long uid, int taskId) {
        UserBusinessGangSpecialtyShippingTask task = getAllTasks(uid).stream().filter(t -> t.getBaseId() == taskId).findFirst().orElse(null);
        if (null == task) {
            throw new ExceptionForClientTip("task.not.exist");
        }
        return task;
    }

    /**
     * 获得指定任务
     *
     * @param uid
     * @param dataId
     * @return
     */
    public UserBusinessGangSpecialtyShippingTask getTask(long uid, long dataId) {
        UserBusinessGangSpecialtyShippingTask task = getAllTasks(uid).stream().filter(t -> t.getId() == dataId).findFirst().orElse(null);
        if (null == task) {
            throw new ExceptionForClientTip("task.not.exist");
        }
        return task;
    }

    /**
     * 删除任务实例
     *
     * @param uid
     * @param dataId
     */
    public void delTask(long uid, long dataId) {
        UserBusinessGangSpecialtyShippingTask task = getTask(uid, dataId);
        userCacheService.delUserData(task);
//        gameUserService.deleteItem(task);
    }

    /**
     * 发送奖励
     *
     * @param uid
     * @param dataId
     * @param rd
     */
    public void sendAwards(long uid, long dataId, int businessGang, UserBusinessGangTaskInfo gangTask, RDCommon rd) {
        UserBusinessGangSpecialtyShippingTask task = getTask(uid, dataId);
        Integer status = task.getStatus();
        if (status != TaskStatusEnum.ACCOMPLISHED.getValue()) {
            throw new ExceptionForClientTip("task.not.accomplish");
        }
        Integer awardableNum = gangTask.getAwardableNum();
        if (awardableNum <= 0) {
            throw new ExceptionForClientTip("businessGang.not.awardable.num");
        }
        //声望配置
        CfgPrestigeEntity prestigeEntity = BusinessGangCfgTool.getPrestigeEntity(businessGang);
        //获取奖励
        CfgTaskEntity taskEntity = TaskTool.getTaskEntity(TaskGroupEnum.BUSINESS_GANG_SPECIALTY_SHIPPING_TASK, task.getBaseId());
        List<Award> awards = taskEntity.getAwards();
        for (Award award : awards) {
            Integer awardId = award.getAwardId();
            if (0 == awardId) {
                award.setAwardId(prestigeEntity.getPrestigeId());
                continue;
            }
            //加急奖励翻倍
            if (task.isUrgent()) {
                award.setNum(award.getNum() * 2);
            }
        }
        awardService.fetchAward(uid, awards, WayEnum.BUSINESS_GANG_SHIPPING_TASK, "", rd);
        //发布完成商帮任务事件
        BusinessGangTaskEventPublisher.pubBusinessGangTaskAchievedEvent(uid, taskEntity.getId(), TaskGroupEnum.BUSINESS_GANG_SPECIALTY_SHIPPING_TASK);
        //删除任务实例
        delTask(uid, task.getId());
        //刷新任务
        businessGangService.generateTask(uid);
    }

    /**
     * 构建任务实例
     *
     * @param uid
     * @param difficulty
     */
    public void makeUserTaskInstance(long uid, int difficulty) {
        //根据难度随机一个任务
        List<CfgTaskEntity> cfgTaskEntities = TaskTool.getTasksByTaskGroupEnum(TaskGroupEnum.BUSINESS_GANG_SPECIALTY_SHIPPING_TASK)
                .stream().filter(t -> t.getDifficulty() == difficulty).collect(Collectors.toList());
        CfgTaskEntity taskEntity = PowerRandom.getRandomFromList(cfgTaskEntities);
        //根据规则构建任务
        CfgBusinessGangShippingTaskRules shippingTaskRules = BusinessGangCfgTool.getShippingTaskRules();
        //是否加急
        Integer urgentPro = shippingTaskRules.getTaskUrgent().get(difficulty);
        //获得正财商帮的声望
        UserBusinessGangInfo userBusinessGang = userBusinessGangService.getOrCreateUserBusinessGang(uid);
        Integer prestige = userBusinessGang.getPrestige(BusinessGangEnum.ZHENG_CAI.getType());
        Integer needPrestige = BusinessGangCfgTool.getShippingTaskRules().getUrgentDoubleNeedPrestige();
        if (prestige >= needPrestige) {
            urgentPro *= 2;
        }
        boolean isUrgent = PowerRandom.hitProbability(urgentPro);
        UserBusinessGangSpecialtyShippingTask shippingTask = UserBusinessGangSpecialtyShippingTask.getInstance(uid, TaskGroupEnum.BUSINESS_GANG_SPECIALTY_SHIPPING_TASK, taskEntity, isUrgent);
        //获取任务信息
        CfgTaskRules cfgTaskRules = shippingTaskRules.getTaskRules().stream().filter(t -> t.getDifficulty() == difficulty).findFirst().orElse(null);
        //获得特产等级
        List<CfgTaskRules.SpecialtyGrading> specialtyGrading = cfgTaskRules.getSpecialtyGrading();
        List<Integer> gradingPro = specialtyGrading.stream().map(CfgTaskRules.SpecialtyGrading::getProbability).collect(Collectors.toList());
        int index = PowerRandom.getIndexByProbs(gradingPro, 100);
        Integer grade = specialtyGrading.get(index).getSpecialtyGrade();
        //获得特产
        List<CfgSpecialsRules> specials = shippingTaskRules.getSpecialsRules().stream().filter(s -> s.getGrade().equals(grade)).collect(Collectors.toList());
        List<CfgCityEntity> cities = CityTool.getCities();
        //获得特产数量
        Integer specialtyNum = cfgTaskRules.getSpecialtyNum();
        Map<String, Integer> targetAndProgress = new HashMap<>();
        //给特产随机运送城池
        for (int i = 0; i < specialtyNum; i++) {
            Integer specialId = PowerRandom.getRandomFromList(specials).getSpecialId();
            String special = SpecialTool.getSpecialById(specialId).getName();
            targetAndProgress.put(special, 0);
            specials.removeIf(s -> s.getSpecialId().equals(specialId));
            CfgSpecialEntity specialEntity = SpecialTool.getSpecialById(specialId);
            String sellingCities = specialEntity.getSellingCities();
            List<Integer> sellingCitiesId = ListUtil.parseStrToInts(sellingCities);
            cities.removeIf(c -> sellingCitiesId.contains(c.getId()));
            cities.removeIf(c -> !cfgTaskRules.getCityLvRange().contains(c.getLevel()));
            Integer country = specialEntity.getCountry();
            cities.removeIf(c -> c.getCountry() == country);
        }
        shippingTask.setTargetAndProgress(targetAndProgress);
        //随机一个城池
        CfgCityEntity cityEntity = PowerRandom.getRandomFromList(cities);
        TaskDifficulty taskDifficulty = TaskDifficulty.fromValue(difficulty);
        switch (taskDifficulty) {
            case FIRST_LEVEL:
                shippingTask.setTargetCityArea(cityEntity.getCountry());
                break;
            case MIDDLE_LEVEL:
                shippingTask.setTargetCityArea(cityEntity.getCountry());
                shippingTask.setTargetLv(cityEntity.getLevel());
                break;
            case HIGH_LEVEL:
            case SUPER_LEVEL:
                shippingTask.setTargetCity(cityEntity.getId());
                break;
            default:
        }
//        gameUserService.addItem(uid, shippingTask);
        userCacheService.addUserData(shippingTask);
    }


}
