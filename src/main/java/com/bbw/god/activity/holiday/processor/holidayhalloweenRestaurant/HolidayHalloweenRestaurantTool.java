package com.bbw.god.activity.holiday.processor.holidayhalloweenRestaurant;

import com.bbw.exception.CoderException;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.activity.holiday.config.CfgHalloweenRestaurant;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.game.config.treasure.TreasureEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 万圣餐厅工具类
 *
 * @author: huanghb
 * @date: 2022/10/11 15:41
 */
@Slf4j
@Service
public class HolidayHalloweenRestaurantTool {
    /**
     * 获得万圣餐厅配置类
     *
     * @return
     */
    public static CfgHalloweenRestaurant getCfg() {
        return Cfg.I.getUniqueConfig(CfgHalloweenRestaurant.class);
    }

    /**
     * 获得万圣餐厅食物商品id
     *
     * @return
     */
    public static Map<Integer, Integer> getFoodMallIds() {
        return getCfg().getFoodMallIds();
    }

    /**
     * 根据商品id获得食物id
     *
     * @param foodMallId
     * @return
     */
    public static Integer getFoodIdByMallId(int foodMallId) {
        for (Map.Entry<Integer, Integer> entry : getFoodMallIds().entrySet()) {
            if (entry.getValue() != foodMallId) {
                continue;
            }
            return entry.getKey();
        }
        return 0;
    }

    /**
     * 根据食物id获得商品id
     *
     * @param foodId
     * @return
     */
    public static Integer getMallIdByFoodId(int foodId) {
        for (Map.Entry<Integer, Integer> entry : getFoodMallIds().entrySet()) {
            if (entry.getKey() != foodId) {
                continue;
            }
            return entry.getValue();
        }
        return 0;
    }

    /**
     * 获得食物合成需要数量
     *
     * @return
     */
    public static Integer getFoodCompoundNeedNum() {
        return getCfg().getFoodCompoundNeedNum();
    }

    /**
     * 获得食物合成信息
     *
     * @return
     */
    public static Map<Integer, Integer> getFoodCompounds() {
        return getCfg().getFoodCompounds();
    }

    /**
     * 获得下一个食物id
     *
     * @param foodId
     * @return
     */
    public static Integer getNextFoodId(int foodId) {
        if(TreasureEnum.CANDY_PUMPKIN_BUCKET.getValue()==foodId){
            throw new ExceptionForClientTip("food.is.top");
        }

        Integer nextfoodId=getFoodCompounds().get(foodId);
        if(null==nextfoodId){
            throw CoderException.normal(String.format("没有食物id=【%s】的合成规则", foodId));
        }
        return nextfoodId;
    }

    /**
     * 获得盘子产出法宝
     *
     * @return
     */
    public static Integer getPlateOutPutTreasure() {
        return getCfg().getPlateOutPutTreasure();
    }

    /*
     * 获得解锁盘子需要法宝数量信息
     *
     * @return
     */
    public static Map<Integer, Integer> getUnlockPlateNeedTreasureNums() {
        return getCfg().getUnlockPlateNeedTreasureNums();
    }

    /**
     * 获得解锁盘子需要法宝数量
     *
     * @param unlockPlatePos
     * @return
     */
    public static Integer getUnlockPlateNeedTreasureNum(Integer unlockPlatePos) {
        return getUnlockPlateNeedTreasureNums().get(unlockPlatePos);
    }

    /**
     * 获得解锁盘子需要法宝
     *
     * @return
     */
    public static Integer getUnlockPlateNeedTreasure() {
        return getCfg().getUnlockPlateNeedTreasure();
    }

    /**
     * 获得接受订单上仙
     *
     * @return
     */
    public static Integer getAcceptOrderLimit() {
        return getCfg().getAcceptOrderLimit();
    }

    /**
     * 获得盘子初始化信息
     *
     * @return
     */
    public static List<Integer> getPlateInits() {
        return getCfg().getPlateInits();
    }

    /**
     * 获得盘子每小时产出数量
     *
     * @return
     */
    public static Integer getPlateOutPutNumPerHour() {
        return getCfg().getPlateOutPutNumPerHour();
    }

    /**
     * 获得订单有效时间
     *
     * @return
     */
    public static Integer getOrderValidTime() {
        return getCfg().getOrderValidTime();
    }
}
