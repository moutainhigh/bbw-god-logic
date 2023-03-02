package com.bbw.god.game.config;

import lombok.Data;

import java.io.Serializable;

/**
 * 战斗buff加成
 *
 * @author: suhq
 * @date: 2021/9/10 10:34 上午
 */
@Data
public class CfgBuff implements Serializable {
    private static final long serialVersionUID = -3749514129067783335L;
    private int min;
    private int max;
    private double add;
}
