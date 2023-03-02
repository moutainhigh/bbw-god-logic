package com.bbw.god.city.nvwm.nightmare.nuwamarket.marketenum;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 摊位状态枚举
 *
 * @author fzj
 * @date 2022/5/9 11:22
 */
@Getter
@AllArgsConstructor
public enum BoothStatusEnum {

    OPEN_BOOTH("出摊", 0),
    CLOSE_BOOTH("收摊", 1);
    private final String name;
    private final int value;

    public static BoothStatusEnum fromValue(int value) {
        for (BoothStatusEnum item : values()) {
            if (item.getValue() == value) {
                return item;
            }
        }
        return null;
    }
}
