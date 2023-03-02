package com.bbw.god.activity.holiday.processor.holidaymagicwitch;

import com.bbw.god.activity.holiday.lottery.rd.RDHolidayLotteryAwards;
import com.bbw.god.rd.RDCommon;
import lombok.Data;

import java.util.List;

/**
 * 返回魔法女巫炼制信息
 *
 * @author: huanghb
 * @date: 2022/12/14 14:40
 */
@Data
public class RDHolidayMagicWitch extends RDCommon {
    private static final long serialVersionUID = 9082801535711203223L;
    /** 我的炼制记录 */
    private List<RDHolidayLotteryAwards> myRecords;

    /**
     * 构造实例
     *
     * @param myRecords
     * @return
     */
    public static RDHolidayMagicWitch getInstance(List<RDHolidayLotteryAwards> myRecords) {
        RDHolidayMagicWitch rd = new RDHolidayMagicWitch();
        rd.setMyRecords(myRecords);
        return rd;
    }
}
