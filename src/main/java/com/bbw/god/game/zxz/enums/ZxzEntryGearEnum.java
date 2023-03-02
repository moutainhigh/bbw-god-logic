package com.bbw.god.game.zxz.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 诛仙阵词条档位枚举
 * @author: hzf
 * @create: 2022-09-19 14:26
 **/
@AllArgsConstructor
@Getter
public enum ZxzEntryGearEnum {
    ENTRY_GEAR_0(0,"没有档位"),
    ENTRY_GEAR_1(1,"1档"),
    ENTRY_GEAR_2(2,"2档"),
    ENTRY_GEAR_3(3,"3档"),
    ENTRY_GEAR_4(4,"4档"),
    ENTRY_GEAR_5(5,"5档"),

            ;

    private int entryGear;
    private String describe;


    public static ZxzEntryGearEnum fromEntryGear(int entryGear) {
        for (ZxzEntryGearEnum way:values()) {
            if (way.getEntryGear() == entryGear) {
                return way;
            }
        }
        return null;
    }

}

