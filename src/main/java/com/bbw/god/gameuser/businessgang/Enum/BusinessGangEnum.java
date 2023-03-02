package com.bbw.god.gameuser.businessgang.Enum;

import com.bbw.exception.CoderException;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 商帮枚举
 *
 * @author fzj
 * @date 2022/1/18 14:02
 */
@Getter
@AllArgsConstructor
public enum BusinessGangEnum {
    ZHENG_CAI("正财", 1000),
    ZHAO_BAO("招宝", 2000),
    ZHAO_CAI("招财", 3000),
    ;

    private final String name;
    private final int type;

    public static BusinessGangEnum fromValue(int type) {
        for (BusinessGangEnum item : values()) {
            if (item.getType() == type) {
                return item;
            }
        }
        throw CoderException.high("无效的商帮类型-" + type);
    }
}
