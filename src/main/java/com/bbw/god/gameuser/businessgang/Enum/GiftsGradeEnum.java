package com.bbw.god.gameuser.businessgang.Enum;

import com.bbw.exception.CoderException;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 礼物级别枚举
 *
 * @author fzj
 * @date 2022/1/29 9:34
 */
@Getter
@AllArgsConstructor
public enum GiftsGradeEnum {
    ADVANCED("高级", 1),
    LOW("低级", 0);

    private final String name;
    private final int grade;

    public static GiftsGradeEnum fromValue(int type) {
        for (GiftsGradeEnum item : values()) {
            if (item.getGrade() == type) {
                return item;
            }
        }
        throw CoderException.high("无效的礼物-" + type);
    }
}
