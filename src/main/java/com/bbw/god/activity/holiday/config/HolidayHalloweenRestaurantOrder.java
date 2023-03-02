package com.bbw.god.activity.holiday.config;

import com.bbw.common.DateUtil;
import com.bbw.common.ID;
import com.bbw.common.PowerRandom;
import com.bbw.god.activity.holiday.processor.holidayhalloweenRestaurant.HolidayHalloweenRestaurantTool;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 万圣餐厅订单
 *
 * @author: huanghb
 * @date: 2022/10/11 15:16
 */
@Data
public class HolidayHalloweenRestaurantOrder implements Serializable {
    private static final long serialVersionUID = 1L;
    /** 餐品id */
    private Long orderId;
    /** 效果 */
    private List<Integer> foods;
    /** 价格 */
    private Integer price;
    /** 是否接受 0 未接受 1已接受 */
    private Integer accepted = 0;
    /** 顾客类型 */
    private Integer type = 0;
    /** 是否完成 */
    private Integer completed = 0;
    /** 订单生成时间 */
    private Date generationTime;

    public static HolidayHalloweenRestaurantOrder instance(CfgHolidayTreatOrTrick.Meal meal) {
        HolidayHalloweenRestaurantOrder order = new HolidayHalloweenRestaurantOrder();
        order.setOrderId(ID.INSTANCE.nextId());
        List<Integer> foods = new ArrayList<>();
        foods.add(meal.getFirstFood());
        foods.add(meal.getSecondFood());
        order.setFoods(foods);
        order.setPrice(meal.getPrice());
        int randomInt = PowerRandom.randomInt(2);
        order.setType(randomInt);
        order.setGenerationTime(DateUtil.now());
        return order;
    }

    /**
     * 接受订单
     */
    public void acceptOrder() {
        accepted = 1;
    }

    /** 完成订单 */
    public void completeOrder() {
        this.completed = 1;
    }

    /**
     * 是否有效的订单
     *
     * @return
     */
    public boolean ifValidOrder() {
        long hourBetween = DateUtil.getHourBetween(this.getGenerationTime(), DateUtil.now());
        //是否完成
        if (1 == this.completed) {
            return false;
        }
        //是否过期订单
        if (hourBetween >= HolidayHalloweenRestaurantTool.getOrderValidTime() && this.accepted == 0) {
            return false;
        }
        return true;
    }
}
