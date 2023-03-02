package com.bbw.god.gameuser.leadercard.equipment;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 加成类别
 *
 * @author suhq
 * @version 创建时间：2018年9月21日 上午9:04:07
 */
@Getter
@AllArgsConstructor
public enum AdditionType {
    ATTACK("攻击", 10),
    DEFENCE("防御", 20),
    BLOOD("血量", 30),
    ;

    private String name;
    private int value;

    public static AdditionType fromValue(int value) {
        for (AdditionType item : values()) {
            if (item.getValue() == value) {
                return item;
            }
        }
        return null;
    }
}
