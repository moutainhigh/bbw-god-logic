package com.bbw.god.gameuser.businessgang.digfortreasure;

import com.bbw.exception.CoderException;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 挖宝层数枚举;
 *
 * @author: huanghb
 * @date: 2022/1/18 14:47
 */
@Getter
@AllArgsConstructor
public enum DigTreasureFloorEnum {
    Ground("地面", 0, -1),

    FIRST_FLOOR("第一层", 1, 0),

    SECOND_FLOOR("第二层", 2, 1),

    THIRD_FLOOR("第三层", 3, 2);

    /*挖宝层数名称*/
    private final String name;
    /*挖宝层数*/
    private final Integer floor;
    /** 顺序 */
    private final Integer order;

    public static DigTreasureFloorEnum fromValue(int value) {
        for (DigTreasureFloorEnum resultLevel : values()) {
            if (resultLevel.getOrder() == value) {
                return resultLevel;
            }
        }
        throw CoderException.high(String.format("没有下标=%s的楼层", value));
    }

    public static DigTreasureFloorEnum fromFloor(int floor) {
        for (DigTreasureFloorEnum resultLevel : values()) {
            if (resultLevel.getFloor() == floor) {
                return resultLevel;
            }
        }
        throw CoderException.high(String.format("没有楼层=%s的楼层", floor));
    }
}
