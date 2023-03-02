package com.bbw.god.gameuser.card.equipment.Enum;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 结果状态枚举
 *
 * @author: huanghb
 * @date: 2022/9/26 10:05
 */
@Getter
@AllArgsConstructor
public enum ResultStatusEnum {
    SUCCESS("成功", 0),
    FAIL("失败", 1),
    BIG_FAILURE("大失败", -1);

    private final String name;
    private final int value;
}
