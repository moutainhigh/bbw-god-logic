package com.bbw.god.activity.holiday.processor.HolidayGroceryShop.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 杂货小铺领取状态
 * @author hzf
 * @create: 2022-12-09 09:53
 */
@Getter
@AllArgsConstructor
public enum GroceryShopReceiveStatusEnum {
    UNCLAIMED("未领取", 0),
    RECEIVED("已领取", 1),

    ;

    private String explain;
    private int value;

    public static GroceryShopReceiveStatusEnum fromValue(int value) {
        for (GroceryShopReceiveStatusEnum item : values()) {
            if (item.getValue() == value) {
                return item;
            }
        }
        return null;
    }
}
