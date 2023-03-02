package com.bbw.god.game.zxz.cfg.award;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 奖励掉落数量规则
 * @author: hzf
 * @create: 2022-09-22 11:17
 **/
@Data
public class CfgQuantityRule implements Serializable {
    private static final long serialVersionUID = 6283485026406890074L;
    /** 野怪种类 */
    private Integer defenderKind;
    /** 区域等级 */
    private Integer addRegionLv;
    /** 添加的概率*/
    private Integer addProbability;

    private List<QuantityRuleAwardNumPool> awardNumPool;

    @Data
    public static class QuantityRuleAwardNumPool implements Serializable {
        private static final long serialVersionUID = 6283485026406890074L;
        /** 数量 */
        private Integer num;
        /** 概率 */
        private Integer probability;
        /** 排序 */
        private Integer order;

    }
}
