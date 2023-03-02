package com.bbw.god.gameuser.nightmarenvwam.godsaltar;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 神格仓库枚举
 *
 * @author fzj
 * @date 2022/5/10 15:30
 */
@Getter
@AllArgsConstructor
public enum GodsAltarEnum {

    GOD_HEAD_CARD("神格牌", 10),
    GOD_TREASURE("神将羁绊道具", 20),
    SKILL_PAGE("技能衍生残页", 30);

    private final String name;
    private final int value;

    public static GodsAltarEnum fromValue(int value) {
        for (GodsAltarEnum item : values()) {
            if (item.getValue() == value) {
                return item;
            }
        }
        return null;
    }
}
