package com.bbw.god.activity.holiday.processor.holidayhalloweenRestaurant;

import com.bbw.common.DateUtil;
import com.bbw.common.ListUtil;
import com.bbw.common.MapUtil;
import com.bbw.db.redis.RedisHashUtil;
import com.bbw.god.activity.holiday.config.HolidayHalloweenRestaurantOrder;
import com.bbw.god.game.config.treasure.TreasureEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 万圣餐厅数据储存服务类
 *
 * @author: huanghb
 * @date: 2022/10/11 15:41
 */
@Slf4j
@Service
public class HolidayHalloweenRestaurantDateService {
    @Autowired
    private HolidayHalloweenRestaurantDateKey holidayHalloweenRestaurantDateKey;
    /** 订单信息 */
    @Autowired
    private RedisHashUtil<Long, HolidayHalloweenRestaurantOrder> orderRedisUtil;
    /** 盘子食物信息 */
    @Autowired
    private RedisHashUtil<Integer, Integer> plateFoodInfosRedisUtil;
    /** 离线收益信息 */
    @Autowired
    private RedisHashUtil<Integer, String> offlineRevenueInfoRedisUtil;
    /** 缓存过期时间 */
    private final static Integer CACHE_TIME_OUT = 7;

    /**
     * 获得订单信息
     *
     * @param uid
     * @return
     */
    public List<HolidayHalloweenRestaurantOrder> getOrders(long uid) {
        String orderKey = holidayHalloweenRestaurantDateKey.getOrderKey(uid);
        Map<Long, HolidayHalloweenRestaurantOrder> longHolidayTreatOrTrickOrderMap = orderRedisUtil.get(orderKey);
        if (MapUtil.isEmpty(longHolidayTreatOrTrickOrderMap)) {
            return new ArrayList<>();
        }
        //过滤掉过期订单
        List<HolidayHalloweenRestaurantOrder> orders = longHolidayTreatOrTrickOrderMap
                .values().stream().filter(tmp -> tmp.ifValidOrder()).collect(Collectors.toList());
        return orders;
    }

    /**
     * 获得订单信息
     *
     * @param uid
     * @return
     */
    public HolidayHalloweenRestaurantOrder getOrder(long uid, long orderId) {
        String orderKey = holidayHalloweenRestaurantDateKey.getOrderKey(uid);
        Map<Long, HolidayHalloweenRestaurantOrder> longHolidayTreatOrTrickOrderMap = orderRedisUtil.get(orderKey);
        if (MapUtil.isEmpty(longHolidayTreatOrTrickOrderMap)) {
            return null;
        }
        HolidayHalloweenRestaurantOrder order = longHolidayTreatOrTrickOrderMap.get(orderId);
        if (null == order) {
            return null;
        }
        if (!order.ifValidOrder()) {
            return null;
        }
        return order;
    }

    /**
     * 获得接受订单数量
     *
     * @param uid
     * @return
     */
    public Integer getNumberOfOrdersAccepted(long uid) {
        List<HolidayHalloweenRestaurantOrder> orders = getOrders(uid);
        if (ListUtil.isEmpty(orders)) {
            return 0;
        }
        return (int) orders.stream().filter(tmp -> 0 != tmp.getAccepted()).count();
    }

    /**
     * 更新订单信息
     *
     * @param uid
     */
    public void updateOrder(long uid, HolidayHalloweenRestaurantOrder order) {
        String key = holidayHalloweenRestaurantDateKey.getOrderKey(uid);
        orderRedisUtil.putField(key, order.getOrderId(), order, DateUtil.SECOND_ONE_DAY * CACHE_TIME_OUT);
        return;
    }

    /**
     * 获取盘子食物信息
     *
     * @param uid
     */
    public Map<Integer, Integer> getPlateFoodInfos(long uid) {
        String key = holidayHalloweenRestaurantDateKey.getPlateFoodInfoKey(uid);
        Map<Integer, Integer> plateFoodInfos = plateFoodInfosRedisUtil.get(key);
        //盘子信息初始化
        if (MapUtil.isNotEmpty(plateFoodInfos)) {
            return plateFoodInfos.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldVal, newVal) -> newVal, LinkedHashMap::new));
        }
        List<Integer> plateInits = HolidayHalloweenRestaurantTool.getPlateInits();
        plateFoodInfos = new LinkedHashMap<>(9);
        for (int i = 0; i < 9; i++) {
            plateFoodInfos.put(i, plateInits.get(i));
        }
        plateFoodInfosRedisUtil.putAllField(key, plateFoodInfos);
        plateFoodInfosRedisUtil.expire(key, CACHE_TIME_OUT, TimeUnit.DAYS);
        return plateFoodInfos;
    }


    /**
     * 更新盘子食物信息
     *
     * @param uid
     * @param plateFoodInfos 盘子位置=》盘子食物信息
     */
    public Map<Integer, Integer> updatePlateFoodInfos(long uid, Map<Integer, Integer> plateFoodInfos) {
        String key = holidayHalloweenRestaurantDateKey.getPlateFoodInfoKey(uid);
        plateFoodInfosRedisUtil.putAllField(key, plateFoodInfos);
        plateFoodInfosRedisUtil.expire(key, CACHE_TIME_OUT, TimeUnit.DAYS);
        return plateFoodInfos;
    }

    /**
     * 卖出食物
     *
     * @param uid
     * @param foodPos
     */
    public void sellFood(long uid, Integer foodPos) {
        String key = holidayHalloweenRestaurantDateKey.getPlateFoodInfoKey(uid);
        plateFoodInfosRedisUtil.putField(key, foodPos, 0, DateUtil.SECOND_ONE_DAY * CACHE_TIME_OUT);
        return;
    }

    /**
     * 购买食物
     *
     * @param uid
     * @param foodPos
     */
    public void buyFood(long uid, Integer mallId, Integer foodPos) {
        String key = holidayHalloweenRestaurantDateKey.getPlateFoodInfoKey(uid);
        plateFoodInfosRedisUtil.putField(key, foodPos, mallId, DateUtil.SECOND_ONE_DAY * CACHE_TIME_OUT);
        return;
    }

    /**
     * 获得离线收益信息
     *
     * @param uid
     * @return
     */
    private Map<Integer, String> getOfflineRevenueInfo(long uid) {
        String key = holidayHalloweenRestaurantDateKey.getOfflineRevenueInfoKey(uid);
        Map<Integer, String> map = offlineRevenueInfoRedisUtil.get(key);
        if (MapUtil.isEmpty(map)) {
            return new HashMap<>();
        }
        return map;
    }

    /**
     * 获得离线收益
     *
     * @param uid
     * @return
     */
    public Map<Integer, String> getOfflineRevenue(long uid) {
        Map<Integer, String> offlineRevenueInfo = getOfflineRevenueInfo(uid);
        if (MapUtil.isEmpty(offlineRevenueInfo)) {
            return new HashMap<>();
        }
        return offlineRevenueInfo;
    }


    /**
     * 更新离线收益信息
     *
     * @param uid
     * @param offlineRevenue 食物mallId=》时间
     */
    public void updateOfflineRevenueInfo(long uid, Map<Integer, String> offlineRevenue) {
        for(Map.Entry<Integer,String> entry:offlineRevenue.entrySet()){
            if(null==entry.getValue()){
                continue;
            }
            entry.setValue(DateUtil.toDateTimeString(DateUtil.now()));
        }
        String key = holidayHalloweenRestaurantDateKey.getOfflineRevenueInfoKey(uid);
        offlineRevenueInfoRedisUtil.putAllField(key, offlineRevenue);
        offlineRevenueInfoRedisUtil.expire(key, CACHE_TIME_OUT, TimeUnit.DAYS);
    }

    /**
     * 更新离线收益信息
     *
     * @param uid
     * @param foodMallId 食物mallId
     * @param platePos   盘子位置
     */
    public void updateOfflineRevenueInfo(long uid, Integer foodMallId, Integer platePos) {
        int foodId = HolidayHalloweenRestaurantTool.getFoodIdByMallId(foodMallId);
        if (TreasureEnum.CANDY_PUMPKIN_BUCKET.getValue() != foodId) {
            return;
        }
        String key = holidayHalloweenRestaurantDateKey.getOfflineRevenueInfoKey(uid);
        offlineRevenueInfoRedisUtil.putField(key, platePos, DateUtil.toDateTimeString(DateUtil.now()));
    }

    /**
     * 移除离线收益信息
     *
     * @param uid
     * @param foodPos 食物位置
     */
    public void removeOfflineRevenueInfo(long uid, Integer foodPos) {
        String key = holidayHalloweenRestaurantDateKey.getOfflineRevenueInfoKey(uid);
        Boolean hasField = offlineRevenueInfoRedisUtil.hasField(key, foodPos);
        if (!hasField) {
            return;
        }
        offlineRevenueInfoRedisUtil.removeField(key, foodPos);
        return;
    }

    /**
     * 移除离线收益信息
     *
     * @param uid
     * @param foodPoss 食物位置
     */
    public void removeOfflineRevenueInfo(long uid, List<Integer> foodPoss) {
        for (Integer foodPos : foodPoss) {
            removeOfflineRevenueInfo(uid, foodPos);
        }
    }

    /**
     * 获得指定位置离线收益
     *
     * @param uid
     * @param foodPos
     * @return
     */
    public Integer getOfflineRevenue(long uid, Integer foodPos) {
        Map<Integer, String> offlineRevenueInfo = getOfflineRevenueInfo(uid);
        if (MapUtil.isEmpty(offlineRevenueInfo)) {
            return 0;
        }
        boolean containsKey = offlineRevenueInfo.containsKey(foodPos);
        if (!containsKey) {
            return 0;
        }

        Date date = DateUtil.fromDateTimeString(offlineRevenueInfo.get(foodPos));
        if (null == date) {
            return 0;
        }
        int hourBetween = (int) DateUtil.getHourBetween(date, DateUtil.now());
        return hourBetween > 0 ? hourBetween * HolidayHalloweenRestaurantTool.getPlateOutPutNumPerHour() : 0;
    }

    /**
     * 获得离线收益
     *
     * @param uid
     * @param foodPoss
     * @return
     */
    public Integer getOfflineRevenues(long uid, List<Integer> foodPoss) {
        int offlineRevenue = 0;
        if (ListUtil.isEmpty(foodPoss)) {
            return offlineRevenue;
        }
        for (Integer foodPos : foodPoss) {
            offlineRevenue += getOfflineRevenue(uid, foodPos);

        }
        return offlineRevenue;
    }
}
