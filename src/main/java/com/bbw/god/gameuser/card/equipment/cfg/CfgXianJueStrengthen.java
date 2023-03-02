package com.bbw.god.gameuser.card.equipment.cfg;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 卡牌仙诀加成
 *
 * @author: huanghb
 * @date: 2022/9/17 8:58
 */
@Data
public class CfgXianJueStrengthen implements Serializable {
    private static final long serialVersionUID = 1L;
    /** 最小等级 */
    private Integer minLevel;
    /** 最大等级 */
    private Integer maxLevel;
    /** 需要铜钱 */
    private Integer needCopper;
    /** 成功概率[0,100] */
    private Integer successRate;
    /** 加成数值 */
    private List<CardEquipmentAddition> additions;

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
