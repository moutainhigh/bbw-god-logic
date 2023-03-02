package com.bbw.god.chat.customer;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 房间状态枚举
 *
 * @author: huanghb
 * @date: 2021/12/9 22:14
 */
@Getter
@AllArgsConstructor
public enum HelperMessageRoomStastusEnum {
    NOT_Hide("未隐藏", 0),
    Hide("隐藏", 1);

    private final String name;
    private final int value;

    public static HelperMessageRoomStastusEnum fromValue(int value) {
        for (HelperMessageRoomStastusEnum item : values()) {
            if (item.getValue() == value) {
                return item;
            }
        }
        return null;
    }
}
