package com.bbw.god.gameuser.task.timelimit.cunz;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 村庄npc的枚举
 *
 * @author fzj
 * @date 2021/11/19 14:08
 */
@Getter
@AllArgsConstructor
public enum CunZNPCEnum {
    CUN_ZHANG("村长大叔", 10),
    XIAO_BA("村里的小巴", 20),
    XIAO_BU("村里的小布", 30),
    ER_MAO("村里的二毛", 40),
    XIAO_HONG("村里的小红", 50),
    LAO_ZHE("神神叨叨的老者", 60),
    ;
    private final String name;
    private final Integer type;

    public static CunZNPCEnum fromValue(int value) {
        for (CunZNPCEnum item : values()) {
            if (item.getType() == value) {
                return item;
            }
        }
        return null;
    }

}
