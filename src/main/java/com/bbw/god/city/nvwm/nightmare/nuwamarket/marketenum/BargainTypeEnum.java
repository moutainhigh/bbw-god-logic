package com.bbw.god.city.nvwm.nightmare.nuwamarket.marketenum;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 讨价还价类型\
 *
 * @author fzj
 * @date 2022/6/8 16:28
 */
@Getter
@AllArgsConstructor
public enum BargainTypeEnum {
    /** 消费者去摊位进行价格发起 */
    BARGAIN("讨价", 10),
    /** 摊主对于要价的决定 */
    COUNTER_OFFER("还价", 20);
    private final String name;
    private final int value;

    public static BargainTypeEnum fromValue(int value) {
        for (BargainTypeEnum item : values()) {
            if (item.getValue() == value) {
                return item;
            }
        }
        return null;
    }
}
