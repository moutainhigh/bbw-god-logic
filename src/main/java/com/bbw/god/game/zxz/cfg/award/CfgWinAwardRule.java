package com.bbw.god.game.zxz.cfg.award;

import com.bbw.god.game.award.Award;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 战斗奖励掉落规则
 * @author: hzf
 * @create: 2022-09-22 11:24
 **/
@Data
public class CfgWinAwardRule implements Serializable {
    private static final long serialVersionUID = 6283485026406890074L;
    /** 难度类型 */
    private Integer difficulty;
    /** 关卡种类 */
    private Integer defenderKind;
    /**区域等级 ：每添加区域[addRegionLv]等级 添加[addProbability]概率*/
    private Integer addRegionLv;
    /** 添加的概率  */
    private Integer addProbability;
    /** 通过次数上限 */
    private Integer clearanceNumLimit;
    /**奖池 */
    private List<ZxzAwardPool> awardPool;

    @Data
    public static class ZxzAwardPool implements Serializable {
        private static final long serialVersionUID = 6283485026406890074L;
        /**奖励 */
        List<Award> awards;
        /** 掉落概率 */
        private Integer probability;
        /** 排序 */
        private Integer order;
    }
}
