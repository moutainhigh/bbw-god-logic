package com.bbw.god.gameuser.leadercard.equipment;

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
public class CfgEquipmentStrengthen implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer minLevel;
    private Integer maxLevel;
    private Integer needCopper;
    /** [0,100] */
    private Integer successRate;
    private List<Addition> additions;

    /**
     * 等级匹配
     *
     * @param level
     * @return
     */
    public boolean isMatch(int level) {
        return level >= minLevel && level <= maxLevel;
    }
}
