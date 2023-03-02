package com.bbw.god.city.nvwm.nightmare.nuwamarket.marketenum;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 讨价商品状态
 *
 * @author fzj
 * @date 2022/5/26 14:48
 */
@Getter
@AllArgsConstructor
public enum BargainProductEnum {

    AGREE("已同意", 0),
    REFUSE("已拒绝", 1),
    EXPIRED("已过期", 2),
    REVOKE("已撤销", 3),
    UNDECIDED("摊主未决定", 4);
    private final String name;
    private final int value;

    public static BargainProductEnum fromValue(int value) {
        for (BargainProductEnum item : values()) {
            if (item.getValue() == value) {
                return item;
            }
        }
        return null;
    }
}
