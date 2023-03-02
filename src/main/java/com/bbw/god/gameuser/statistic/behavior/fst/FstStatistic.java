package com.bbw.god.gameuser.statistic.behavior.fst;

import com.bbw.god.gameuser.statistic.behavior.BehaviorStatistic;
import com.bbw.god.gameuser.statistic.behavior.BehaviorType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 封神台统计
 *
 * @author lzc
 * @description
 * @date 2021/4/15 11:28
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class FstStatistic extends BehaviorStatistic {
    /** 连续赛季处于保级区次数 */
    private int continuousGrading = 0;
    /** 守位成功次数 */
    private int guardWin = 0;
    /** 收集封神台卡牌数量 */
    private int card = 0;
    /** 跨服挑战胜利次数 */
    private Integer winNum = 0;

    public FstStatistic() {
        super(BehaviorType.FST);
    }

    public FstStatistic(Integer continuousGrading, Integer guardWin, Integer card, Integer winNum) {
        super(BehaviorType.FST);
        this.continuousGrading = continuousGrading;
        this.guardWin = guardWin;
        this.card = card;
        this.winNum = winNum;
    }
}
