package com.bbw.god.game.dfdj.fight;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author suchaobin
 * @description 巅峰对决战斗明细
 * @date 2021/1/22 10:08
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DfdjFightDetail implements Serializable {
    private static final long serialVersionUID = -6134170271582817532L;
    private Integer zoneType; // 战区
    private Integer level1; // 座位号1等级
    private Integer level2; // 座位号2等级
    private Integer addScore1 = 0; // 座位号1奖励积分
    private Integer addScore2 = 0;// 座位号2奖励积分
}
