package com.bbw.god.game.combat.runes;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author lwb
 * @date 2020/9/17 10:13
 */
@Getter
@AllArgsConstructor
public enum  RunesStageEnum {
    INIT(10,"初始化");
    private int type;
    private String memo;
}
