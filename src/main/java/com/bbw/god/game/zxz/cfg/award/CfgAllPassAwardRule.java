package com.bbw.god.game.zxz.cfg.award;

import com.bbw.god.game.award.Award;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 全通奖励
 * @author: hzf
 * @create: 2022-09-22 11:57
 **/
@Data
public class CfgAllPassAwardRule implements Serializable {
    private static final long serialVersionUID = 6283485026406890074L;
    /** 难度类型 */
    private Integer difficulty;
    /** 区域等级 : 每添加区域[addRegionLv]等级 添加[addMultiple]倍数*/
    private Integer addRegionLv;
    /**添加的倍数 */
    private Integer addMultiple;
    /** 通过次数上限 */
    private Integer clearanceNumLimit;
    /** 全通奖励奖池 */
    private List<AllAwardPool> awardPool;

    @Data
    public static class AllAwardPool implements Serializable {
        private static final long serialVersionUID = 6283485026406890074L;
        List<Award> awards;
        /** 概率 */
        private Integer probability;
    }


}
