package com.bbw.god.activity.holiday.lottery.rd;

import com.bbw.god.game.award.Award;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author suchaobin
 * @description 抽奖奖励信息
 * @date 2020/9/6 16:31
 **/
@Data
@EqualsAndHashCode(callSuper = true)
public class RDHolidayLotteryAward extends Award {
    private static final long serialVersionUID = 558598265341034841L;
    private Integer index = null;// 奖品所在位置
    private Boolean isAwarded = null;// 是否已领取
    private Integer level = null;// 奖品等级

    public static RDHolidayLotteryAward getInstance(Award award) {
        RDHolidayLotteryAward lotteryAward = new RDHolidayLotteryAward();
        lotteryAward.setAwardId(award.getAwardId());
        lotteryAward.setItem(award.getItem());
        lotteryAward.setNum(award.getNum());
        return lotteryAward;
    }

    public static RDHolidayLotteryAward getInstance(Award award, int level) {
        RDHolidayLotteryAward lotteryAward = new RDHolidayLotteryAward();
        lotteryAward.setAwardId(award.getAwardId());
        lotteryAward.setItem(award.getItem());
        lotteryAward.setNum(award.getNum());
        lotteryAward.setLevel(level);
        return lotteryAward;
    }

    public static RDHolidayLotteryAward getInstance(Award award, int index, boolean isAwarded) {
        RDHolidayLotteryAward lotteryAward = new RDHolidayLotteryAward();
        lotteryAward.setAwardId(award.getAwardId());
        lotteryAward.setItem(award.getItem());
        lotteryAward.setNum(award.getNum());
        lotteryAward.setIndex(index);
        lotteryAward.setIsAwarded(isAwarded);
        return lotteryAward;
    }

    public static RDHolidayLotteryAward getInstance(Award award, boolean isAwarded) {
        RDHolidayLotteryAward lotteryAward = new RDHolidayLotteryAward();
        lotteryAward.setAwardId(award.getAwardId());
        lotteryAward.setItem(award.getItem());
        lotteryAward.setNum(award.getNum());
        lotteryAward.setIsAwarded(isAwarded);
        return lotteryAward;
    }
}
