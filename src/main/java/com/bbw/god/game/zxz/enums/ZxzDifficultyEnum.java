package com.bbw.god.game.zxz.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;


/**
 * 诛仙阵的难度枚举
 * @author: hzf
 * @create: 2022-09-17 14:30
 */
@AllArgsConstructor
@Getter
public enum ZxzDifficultyEnum {
    DIFFICULTY_10(10,"简单"),
    DIFFICULTY_20(20,"普通"),
    DIFFICULTY_30(30,"困难"),
    DIFFICULTY_40(40,"噩梦"),
    DIFFICULTY_50(50,"地狱"),

    ;

    private int difficulty;
    private String describe;


    public static ZxzDifficultyEnum fromZxzDifficulty(int difficulty) {
        for (ZxzDifficultyEnum way:values()) {
            if (way.getDifficulty() == difficulty) {
                return way;
            }
        }
        return null;
    }

}