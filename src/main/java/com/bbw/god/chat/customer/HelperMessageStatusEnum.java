package com.bbw.god.chat.customer;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 消息的状态（是否被读取）
 *
 * @author suhq
 * @date 2019年3月2日 下午6:45:56
 */
@Getter
@AllArgsConstructor
public enum HelperMessageStatusEnum {

    NOT_READ("未读", 0),
    READ("已读", 1);

    private final String name;
    private final int value;

    public static HelperMessageStatusEnum fromValue(int value) {
        for (HelperMessageStatusEnum item : values()) {
            if (item.getValue() == value) {
                return item;
            }
        }
        return null;
    }
}
