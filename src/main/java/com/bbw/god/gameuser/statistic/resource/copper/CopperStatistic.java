package com.bbw.god.gameuser.statistic.resource.copper;

import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.gameuser.statistic.resource.ResourceStatistic;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

/**
 * @author suchaobin
 * @description 铜钱统计
 * @date 2020/4/20 11:49
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class CopperStatistic extends ResourceStatistic {
	private Long todayNum = 0L;
	private Long totalNum = 0L;
	private Long todayProfit = 0L;
	private Long totalProfit = 0L;
	private Map<WayEnum, Long> todayMap = new HashMap<>(16);
	private Map<WayEnum, Long> totalMap = new HashMap<>(16);

	public CopperStatistic(Integer date, int type, Long todayNum, Long totalNum, Long todayProfit, Long totalProfit,
						   Map<WayEnum, Long> todayMap, Map<WayEnum, Long> totalMap) {
		super(date, AwardEnum.TQ, type);
		this.todayNum = todayNum;
		this.totalNum = totalNum;
		this.todayProfit = todayProfit;
		this.totalProfit = totalProfit;
		this.todayMap = todayMap;
		this.totalMap = totalMap;
    }

    public CopperStatistic(int type, Long todayNum, Long totalNum, Long todayProfit, Long totalProfit) {
        super(AwardEnum.TQ, type);
        this.todayNum = todayNum;
        this.totalNum = totalNum;
        this.todayProfit = todayProfit;
        this.totalProfit = totalProfit;
    }

    public void addCopper(long addCopper, long profit, WayEnum way) {
        this.todayNum += addCopper;
        this.totalNum += addCopper;
        addProfit(profit);
        Long todayWay = this.todayMap.get(way) == null ? 0L : this.todayMap.get(way);
        Long totalWay = this.totalMap.get(way) == null ? 0L : this.totalMap.get(way);
        this.todayMap.put(way, todayWay + addCopper);
        this.totalMap.put(way, totalWay + addCopper);
    }

    public void addProfit(long profit) {
        this.todayProfit += profit;
        this.totalProfit += profit;
    }
}
