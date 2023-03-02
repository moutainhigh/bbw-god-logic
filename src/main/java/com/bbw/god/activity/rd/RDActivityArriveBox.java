package com.bbw.god.activity.rd;

import com.bbw.god.city.RDCityInfo;
import lombok.Data;

import java.io.Serializable;

/**
 * 到达特定位置触发宝箱（eg:不给糖就捣乱活动）
 *
 * @author fzj
 * @date 2021/10/21 11:29
 */
@Data
public class RDActivityArriveBox extends RDCityInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    /** 剩余宝箱次数 */
    private Integer remainTimes;
    /** 剩余免费开宝箱次数 */
    private Integer freeTimes;
    /** 开宝箱需要消耗的元宝数 */
    private Integer openBoxNeedGolds;

    public static RDActivityArriveBox getInstance(int totalTimes, int freeTimes, int openBoxNeedGold) {
        RDActivityArriveBox rd = new RDActivityArriveBox();
        rd.setRemainTimes(totalTimes);
        rd.setFreeTimes(freeTimes);
        rd.setOpenBoxNeedGolds(openBoxNeedGold);
        return rd;
    }
}
