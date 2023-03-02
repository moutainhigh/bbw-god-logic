package com.bbw.god.gameuser.leadercard.skil;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

/**
 *
 *
 *
 * 技能激活状态枚举  -1 未激活  1可激活 2已激活
 * @author liuwenbin
 *
 */

@Getter
@AllArgsConstructor
public enum SkillStateEnum implements Serializable {
    /**
     * 未激活
     */
    INACTIVATED(-1),
    /**
     * 可激活
     */
    CAN_BE_ACTIVATED(1),
    /**
     * 已激活
     */
    ACTIVATED(2);
    private int state;
}
