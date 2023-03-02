package com.bbw.god.gameuser.leadercard.equipment;

import com.bbw.god.game.award.Award;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 装备加成
 *
 * @author suhq
 * @date 2021-03-26 13:47
 **/
@Data
public class CfgEquipmentStarMap implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer quality;
    private Integer star;
    /** [0,100] */
    private Integer successRate;
    private Double additionRatio;
    private List<Award> needs;
}
