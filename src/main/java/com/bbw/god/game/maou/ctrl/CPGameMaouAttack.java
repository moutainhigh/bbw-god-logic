package com.bbw.god.game.maou.ctrl;

import lombok.Data;

import java.io.Serializable;

/**
 * 攻打魔王参数
 *
 * @author: suhq
 * @date: 2022/1/7 2:11 下午
 */
@Data
public class CPGameMaouAttack implements Serializable {
    private static final long serialVersionUID = 8854421974249780448L;
    /** 法宝ID */
    private Integer treasureId;
}
