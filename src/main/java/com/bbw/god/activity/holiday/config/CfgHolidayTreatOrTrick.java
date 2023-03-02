package com.bbw.god.activity.holiday.config;

import com.bbw.common.PowerRandom;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.game.config.CfgEntityInterface;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 不给糖就捣乱2
 *
 * @author: huanghb
 * @date: 2022/10/11 15:16
 */
@Data
public class CfgHolidayTreatOrTrick implements CfgEntityInterface, Serializable {
    private static final long serialVersionUID = 1L;
    private String key;
    /** 村庄触发概率 */
    private int cunZTriggerProb;
    /** 村庄宝箱产出 */
    private List<Award> boxOutPut;
    /** 村庄宝箱触发概率 */
    private Integer boxTriggerProb;
    /** 宝箱领取需要法宝 */
    private Integer boxReceiveNeedTreasure;
    /** 宝箱领取需要法宝数量 */
    private Integer boxReceiveNeedTreasureNum;
    /** 订单触发概率 */
    private Integer orderTriggerProb;
    /** 餐品 */
    private List<Meal> meals;
    ;

    public static CfgHolidayTreatOrTrick getCfg() {
        return Cfg.I.getUniqueConfig(CfgHolidayTreatOrTrick.class);
    }

    /**
     * 获得订单
     *
     * @return
     */
    public static HolidayHalloweenRestaurantOrder getOrder() {
        List<Meal> meals = getCfg().getMeals();
        Meal meal = randomFoodByProb(meals);
        return HolidayHalloweenRestaurantOrder.instance(meal);
    }

    /**
     * 根据概率随机获得奖励
     *
     * @param
     * @return
     */
    public static Award randomAwardByProb(List<Award> outPut) {
        List<Integer> probs = outPut.stream().map(Award::getProbability).collect(Collectors.toList());
        Integer awardIndex = PowerRandom.hitProbabilityIndex(probs);
        return outPut.get(awardIndex);
    }

    /**
     * 根据概率随机获得订单
     *
     * @param
     * @return
     */
    public static Meal randomFoodByProb(List<Meal> meals) {
        List<Integer> probs = meals.stream().map(Meal::getProb).collect(Collectors.toList());
        Integer awardIndex = PowerRandom.hitProbabilityIndex(probs);
        return meals.get(awardIndex);
    }

    @Data
    public static class Meal {
        /** 第一个食物 */
        private Integer firstFood;
        /** 第二个食物 */
        private Integer secondFood;
        /** 概率 */
        private Integer prob;
        /** 价格 */
        private Integer price;
    }

    @Override
    public Serializable getId() {
        return key;
    }

    @Override
    public int getSortId() {
        return 0;
    }
}
