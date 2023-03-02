package com.bbw.god.activity.holiday.processor.holidaychinesezodiaccollision;

import com.bbw.exception.CoderException;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 翻牌状态枚举
 *
 * @author: huanghb
 * @date: 2023/2/9 17:02
 */
@Getter
@AllArgsConstructor
public enum FlipStatusEnum {
    NOT_OPEN("未开", 0),
    OPEN("打开", 1),
    COLLISION_SUCESS("碰牌成功", 2),
    ;
    /** 翻牌状态名称 */
    private final String name;
    /** 值 */
    private final Integer status;

    public static FlipStatusEnum fromValue(int status) {
        for (FlipStatusEnum item : values()) {
            if (item.status == status) {
                return item;
            }
        }
        throw CoderException.high("无效的翻牌状态-" + status);
    }

    /**
     * 获得翻牌卡下一个状态
     *
     * @param status
     * @return
     */
    public static FlipStatusEnum getNextFlipStatus(int status) {
        FlipStatusEnum flipStatusEnum = FlipStatusEnum.fromValue(status);
        switch (flipStatusEnum) {
            case NOT_OPEN:
                return OPEN;
            case OPEN:
                return COLLISION_SUCESS;
            default:
                throw CoderException.high("无效的翻牌状态-" + status);
        }
    }

    /**
     * 获得翻牌卡上一个状态
     *
     * @param status
     * @return
     */
    public static FlipStatusEnum getLastFlipStatus(int status) {
        FlipStatusEnum flipStatusEnum = FlipStatusEnum.fromValue(status);
        switch (flipStatusEnum) {
            case OPEN:
                return NOT_OPEN;
            default:
                throw CoderException.high("无效的翻牌状态-" + status);
        }
    }
}
