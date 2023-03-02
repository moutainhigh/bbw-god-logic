package com.bbw.god.activity.holiday.lottery.rd;

import com.bbw.god.rd.RDCommon;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author suchaobin
 * @description 抽奖奖励信息集合
 * @date 2020/9/17 16:17
 **/
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class RDHolidayLotteryAwards extends RDCommon {
    private static final long serialVersionUID = 4515035871187998916L;
    private List<RDHolidayLotteryAward> awards;
    private List<Integer> result;// 五气朝元本次排序结果

    public RDHolidayLotteryAwards(List<RDHolidayLotteryAward> awards) {
        this.awards = awards;
    }

    public RDHolidayLotteryAwards(List<RDHolidayLotteryAward> awards, List<Integer> result) {
        this.awards = awards;
        this.result = result;
    }
}
