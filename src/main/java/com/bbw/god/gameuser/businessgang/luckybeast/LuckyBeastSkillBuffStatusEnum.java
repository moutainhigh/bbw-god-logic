package com.bbw.god.gameuser.businessgang.luckybeast;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 招财兽技能buff状态枚举;
 *
 * @author: huanghb
 * @date: 2022/1/18 14:47
 */
@Getter
@AllArgsConstructor
public enum LuckyBeastSkillBuffStatusEnum {
    NO_SKILL_Buff("没有技能加成", 0),
    SKILL_Buff("有技能加成", 1),
    ;

    private final String name;
    private final Integer value;

    public static LuckyBeastSkillBuffStatusEnum fromValue(int value) {
        for (LuckyBeastSkillBuffStatusEnum resultLevel : values()) {
            if (resultLevel.getValue() == value) {
                return resultLevel;
            }
        }
        return null;
    }
}
