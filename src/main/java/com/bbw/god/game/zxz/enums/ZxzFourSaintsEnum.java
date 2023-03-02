package com.bbw.god.game.zxz.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 四圣挑战类型枚举
 * @author: hzf
 * @create: 2022-12-26 12:05
 **/
@AllArgsConstructor
@Getter
public enum ZxzFourSaintsEnum {
    CHALLENGE_TYPE_10(10,"白虎挑战"),
    CHALLENGE_TYPE_20(20,"青龙挑战"),
    CHALLENGE_TYPE_30(30,"玄武挑战"),
    CHALLENGE_TYPE_40(40,"朱雀挑战"),
    CHALLENGE_TYPE_50(50,"麒麟挑战"),

    ;

    private int challengeType;
    private String describe;


    public static ZxzFourSaintsEnum fromZxzChallengeType(int difficulty) {
        for (ZxzFourSaintsEnum way:values()) {
            if (way.getChallengeType() == difficulty) {
                return way;
            }
        }
        return null;
    }

}
