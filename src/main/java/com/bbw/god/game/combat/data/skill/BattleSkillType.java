package com.bbw.god.game.combat.data.skill;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public enum  BattleSkillType implements Serializable {
    DEFAULT(0,"默认"),
    DEFENSE(3000,"法术防御"),
    DEFENSE_NORMAL_BUFF(43000,"物理BUFF防御")
    ;
    private int val;
    private String memo;
}
