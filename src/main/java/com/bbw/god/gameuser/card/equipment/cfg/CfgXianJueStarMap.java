package com.bbw.god.gameuser.card.equipment.cfg;

import com.bbw.god.game.award.Award;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 装备加成
 *
 * @author: huanghb
 * @date: 2022/9/17 9:23
 */
@Data
public class CfgXianJueStarMap implements Serializable {
    private static final long serialVersionUID = 1L;
    /** 品质 */
    private Integer quality;
    /** 星级 */
    private Integer star;
    /** 成功概率 [0,100] */
    private Integer successRate;
    /** 加成率 0.01即给与可怕0.01%的加成 */
    private Double additionRatio;
    /** 升级星图需要消耗的道具 */
    private List<Award> needs;
}
