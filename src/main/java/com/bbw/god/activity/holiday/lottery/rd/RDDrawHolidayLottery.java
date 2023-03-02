package com.bbw.god.activity.holiday.lottery.rd;

import com.bbw.god.rd.RDCommon;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author suchaobin
 * @description 本次抽奖抽到的物品
 * @date 2020/9/6 16:27
 **/
@Data
@EqualsAndHashCode(callSuper = true)
public class RDDrawHolidayLottery extends RDCommon {
    private static final long serialVersionUID = 3390524477810936343L;
    // 抽到的奖励
    private RDHolidayLotteryAward award;
    // 本次博饼的结果
    private List<Integer> resultList;
    // 如果本次抽到的是状元，则返回状元奖券的编号
    private String zhuangYuanNO;
    // 本次中奖的等级
    private Integer resultLevel;

    public static RDDrawHolidayLottery getInstance(String zhuangYuanNo, int resultLevel, List<Integer> resultList) {
        RDDrawHolidayLottery instance = new RDDrawHolidayLottery();
        instance.setZhuangYuanNO(zhuangYuanNo);
        instance.setResultList(resultList);
        instance.setResultLevel(resultLevel);
        return instance;
    }
}
