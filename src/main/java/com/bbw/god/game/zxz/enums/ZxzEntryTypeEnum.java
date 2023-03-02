package com.bbw.god.game.zxz.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 诛仙阵词条类型枚举
 * @author: hzf
 * @create: 2022-09-19 14:22
 **/
@AllArgsConstructor
@Getter
public enum ZxzEntryTypeEnum {
    ENTRY_TYPE_10(10,"普通词条"),
    ENTRY_TYPE_20(20,"支援词条"),
    ENTRY_TYPE_30(30,"特殊词条"),
    ENTRY_TYPE_40(40,"诅咒词条"),
    ;

    private int entryType;
    private String describe;


    public static ZxzEntryTypeEnum fromEntryType(int entryType) {
        for (ZxzEntryTypeEnum way:values()) {
            if (way.getEntryType() == entryType) {
                return way;
            }
        }
        return null;
    }

}
