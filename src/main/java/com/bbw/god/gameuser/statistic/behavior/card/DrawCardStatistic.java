package com.bbw.god.gameuser.statistic.behavior.card;

import com.bbw.god.gameuser.statistic.behavior.BehaviorStatistic;
import com.bbw.god.gameuser.statistic.behavior.BehaviorType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author suchaobin
 * @description 抽卡统计
 * @date 2020/4/16 9:38
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class DrawCardStatistic extends BehaviorStatistic {
    private Integer gold = 0;
    private Integer wood = 0;
    private Integer water = 0;
    private Integer fire = 0;
    private Integer earth = 0;
    private Integer wanwu = 0;
    private Integer jux = 0;

    public DrawCardStatistic(Integer today, Integer total, Integer gold, Integer wood, Integer water, Integer fire,
                             Integer earth, Integer wanwu, Integer jux, Integer date) {
        super(today, total, date, BehaviorType.CARD_DRAW);
        setGold(gold);
        setWood(wood);
        setWater(water);
        setFire(fire);
        setEarth(earth);
        setWanwu(wanwu);
        setJux(jux);
    }

    public DrawCardStatistic(Integer today, Integer total, Integer date) {
        super(today, total, date, BehaviorType.CARD_DRAW);
    }

    public DrawCardStatistic() {
        super(BehaviorType.CARD_DRAW);
    }
}
