package com.bbw.god.city.nvwm.nightmare.nuwamarket.marketenum;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 交易状态
 *
 * @author fzj
 * @date 2022/5/9 8:50
 */
@Getter
@AllArgsConstructor
public enum TradeStatusEnum {

    NO_TRADE("未交易", 0),
    ALREADY_TRADED("已交易", 1);

    private final String name;
    private final int value;

    public static TradeStatusEnum fromValue(int value) {
        for (TradeStatusEnum item : values()) {
            if (item.getValue() == value) {
                return item;
            }
        }
        return null;
    }
}
