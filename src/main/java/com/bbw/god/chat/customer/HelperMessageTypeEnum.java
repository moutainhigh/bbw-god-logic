package com.bbw.god.chat.customer;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 发送消息的人的身份
 *
 * @author suhq
 * @date 2019年3月2日 下午6:45:56
 */
@Getter
@AllArgsConstructor
public enum HelperMessageTypeEnum {
    Player("游戏用户", 10),
    GM("客服人员", 1000);
    private final String name;
    private final int value;

    public static HelperMessageTypeEnum fromValue(int value) {
        for (HelperMessageTypeEnum item : values()) {
            if (item.getValue() == value) {
                return item;
            }
        }
        return null;
    }
}
