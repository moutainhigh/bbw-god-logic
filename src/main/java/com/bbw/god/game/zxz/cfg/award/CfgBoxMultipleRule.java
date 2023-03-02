package com.bbw.god.game.zxz.cfg.award;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 宝箱奖励倍数的随机规则（每个道具单独计算倍数）
 * @author: hzf
 * @create: 2022-09-22 11:42
 **/
@Data
public class CfgBoxMultipleRule implements Serializable {
    private static final long serialVersionUID = 6283485026406890074L;
    /** 区域等级区间 */
    private List<Integer> regionLvInterval;
    private List<ZxzAwardMultiplePool> awardMultiplePool;

    @Data
    public static class ZxzAwardMultiplePool implements Serializable {
        private static final long serialVersionUID = 6283485026406890074L;
        /** 倍数 */
        private Integer multiple;
        /** 概率 */
        private Integer probability;
    }
}
