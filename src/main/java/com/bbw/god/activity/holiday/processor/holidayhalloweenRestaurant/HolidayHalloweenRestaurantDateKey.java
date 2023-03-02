package com.bbw.god.activity.holiday.processor.holidayhalloweenRestaurant;

import org.springframework.stereotype.Service;

import static com.bbw.god.game.data.redis.RedisKeyConst.SPLIT;

/**
 * 万圣餐厅数据储存服务类
 *
 * @author: huanghb
 * @date: 2022/10/11 15:41
 */
@Service
public class HolidayHalloweenRestaurantDateKey {
    /**
     * 获得订单key
     *
     * @param uid
     * @return
     */
    public String getOrderKey(long uid) {
        return "usr" + SPLIT + uid + SPLIT + "halloweenRestaurant" + SPLIT + "order";

    }

    /**
     * 获得盘子上食物信息key
     *
     * @return
     */
    public String getPlateFoodInfoKey(long uid) {
        return "usr" + SPLIT + uid + SPLIT + "halloweenRestaurant" + SPLIT + "plateFoodInfo";
    }

    /**
     * 获得离线收益信息key
     *
     * @return
     */
    public String getOfflineRevenueInfoKey(long uid) {
        return "usr" + SPLIT + uid + SPLIT + "halloweenRestaurant" + SPLIT + "offlineRevenueInf";
    }

}
