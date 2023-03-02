package com.bbw.god.activity.holiday.processor.holidayhalloweenRestaurant;

import com.bbw.common.DateUtil;
import com.bbw.god.activity.holiday.config.HolidayHalloweenRestaurantOrder;
import com.bbw.god.activity.rd.RDActivityList;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 万圣节餐厅订单
 *
 * @author: huanghb
 * @date: 2022/10/11 15:31
 */
@Data
public class RdHalloweenRestaurantOrderInfo extends RDActivityList implements Serializable {
    private static final long serialVersionUID = 1L;
    /** 盘子上食物信息 */
    private List<RdHalloweenRestaurantOrder> orderInfos;
    /** 离线收益 */
    private Integer offlineRevenue;

    @Data
    public static class RdHalloweenRestaurantOrder implements Serializable {
        private static final long serialVersionUID = 1536119545723607347L;
        /** 餐品id */
        private Long orderId;
        /** 效果 */
        private List<Integer> foods;
        /** 价格 */
        private Integer price;
        /** 是否接受 0 未接受 1已接受 */
        private Integer accepted = 0;
        /** 顾客类型 */
        private Integer type;
        /** 订单有效时间 */
        private long validTime;

        public static RdHalloweenRestaurantOrder instance(HolidayHalloweenRestaurantOrder order) {
            RdHalloweenRestaurantOrder rdOrder = new RdHalloweenRestaurantOrder();
            rdOrder.setOrderId(order.getOrderId());
            rdOrder.setFoods(order.getFoods());
            rdOrder.setPrice(order.getPrice());
            rdOrder.setAccepted(order.getAccepted());
            rdOrder.setType(order.getType());
            //获得订单有效时间
            long validTime = DateUtil.addHours(order.getGenerationTime(), HolidayHalloweenRestaurantTool.getOrderValidTime()).getTime() - System.currentTimeMillis();
            rdOrder.setValidTime(validTime);
            return rdOrder;
        }
    }

    public static RdHalloweenRestaurantOrderInfo instance(List<HolidayHalloweenRestaurantOrder> orders) {
        RdHalloweenRestaurantOrderInfo rdHalloweenRestaurantOrderInfo = new RdHalloweenRestaurantOrderInfo();
        List<RdHalloweenRestaurantOrder> orderList = orders.stream().map(RdHalloweenRestaurantOrder::instance).collect(Collectors.toList());
        rdHalloweenRestaurantOrderInfo.setOrderInfos(orderList);
        return rdHalloweenRestaurantOrderInfo;
    }
}
