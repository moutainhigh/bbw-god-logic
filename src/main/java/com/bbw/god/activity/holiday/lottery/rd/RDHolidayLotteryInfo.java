package com.bbw.god.activity.holiday.lottery.rd;

import com.bbw.god.activity.holiday.lottery.UserHolidayLottery10;
import com.bbw.god.rd.RDCommon;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author suchaobin
 * @description 进入活动抽奖
 * @date 2020/8/27 10:42
 **/
@Data
@EqualsAndHashCode(callSuper = true)
public class RDHolidayLotteryInfo extends RDCommon {
    private static final long serialVersionUID = 9082801535711203223L;
    private List<RDHolidayLotteryAward> awarded;// 已经领取的奖励及下标
    private List<RDHolidayLotteryAward> awards;// 所有奖励
    private Integer nextDrawCost;// 下次抽奖要花费的法宝数量
    private Integer refreshTimes;// 已刷新次数
    private Integer nextRefreshCost;// 下次刷新要花费的元宝数量
    private List<Integer> lastResult;// 上次丢的点数
    private List<ZhuangYuanNOInfo> zhuangYuanNOList;// 状元奖券编号集合
    private List<RDHolidayLotteryAwards> myRecords;// 我的奖品记录
    private List<String> gameRecords;// 全服奖品记录
    private Integer totalPoolGold;//总奖池元宝数量
    private List<Integer> drawResults;//抽奖结果

    public static RDHolidayLotteryInfo getInstance(int nextDrawCost, int refreshTimes, int nextRefreshCost,
                                                   UserHolidayLottery10 userHolidayLottery) {
        RDHolidayLotteryInfo rd = new RDHolidayLotteryInfo();
        rd.setAwards(userHolidayLottery.getAwards());
        rd.setAwarded(userHolidayLottery.gainAwardedLotteryAwards());
        rd.setNextDrawCost(nextDrawCost);
        rd.setRefreshTimes(refreshTimes);
        rd.setNextRefreshCost(nextRefreshCost);
        return rd;
    }

    public static RDHolidayLotteryInfo getInstance(List<Integer> lastResult, List<ZhuangYuanNOInfo> zhuangYuanNOList,
                                                   List<RDHolidayLotteryAwards> myRecords, List<String> gameRecords) {
        RDHolidayLotteryInfo rd = new RDHolidayLotteryInfo();
        rd.setLastResult(lastResult);
        rd.setZhuangYuanNOList(zhuangYuanNOList);
        rd.setMyRecords(myRecords);
        rd.setGameRecords(gameRecords);
        return rd;
    }

    public static RDHolidayLotteryInfo getInstance(List<RDHolidayLotteryAwards> myRecords) {
        RDHolidayLotteryInfo rd = new RDHolidayLotteryInfo();
        rd.setMyRecords(myRecords);
        return rd;
    }
}
