package com.bbw.god.city.nvwm.nightmare.nuwamarket.marketenum;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 交易类型枚举
 *
 * @author fzj
 * @date 2022/5/24 16:01
 */
@Getter
@AllArgsConstructor
public enum TradeTypeEnum {

    BUY("买入", 10),
    SELL("卖出", 20);

    private final String name;
    private final int value;

    public static TradeTypeEnum fromValue(int value) {
        for (TradeTypeEnum item : values()) {
            if (item.getValue() == value) {
                return item;
            }
        }
        return null;
    }

    public static TradeTypeEnum getAnotherValue(int value) {
        for (TradeTypeEnum item : values()) {
            if (item.getValue() != value) {
                return item;
            }
        }
        return null;
    }
}
