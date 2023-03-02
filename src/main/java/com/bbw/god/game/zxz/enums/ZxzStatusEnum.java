package com.bbw.god.game.zxz.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 诛仙阵状态枚举
 * @author: hzf
 * @create: 2022-09-17 14:30
 **/
@AllArgsConstructor
@Getter
public enum ZxzStatusEnum {

    NOT_OPEN(0,"还未开启"),
    ABLE_ATTACK(1,"可攻打"),
    PASSED(2,"已通关"),

   ;

    private int status;
    private String describe;


    public static ZxzStatusEnum fromZxzStatus(int status) {
        for (ZxzStatusEnum way:values()) {
            if (way.getStatus() == status) {
                return way;
            }
        }
        return null;
    }
}
