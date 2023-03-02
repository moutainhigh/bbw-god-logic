package com.bbw.god.activity.rd;

import com.bbw.god.rd.RDCommon;
import com.bbw.god.rd.RDSuccess;
import lombok.Data;

import java.util.List;

/**
 * 萌虎集市返回信息
 *
 * @author fzj
 * @date 2022/3/7 13:51
 */
@Data
public class RDCuteTigerMarketInfo extends RDCommon {
    /** 剩余刷新时间 */
    private long remainRefreshTime;
    /** 以及触发的事件 */
    private List<SpecialEvents> hasTriggerEvents;
    /** 糕点售价 */
    private List<PastriesSellPrice> pastriesSellPrices;

    @Data
    public static class PastriesSellPrice {
        /** 糕点Id */
        private int pastryId;
        /** 价格 */
        private long price;
    }

    @Data
    public static class SpecialEvents {
        /** 事件Id */
        private int id;
        /** 触发玩家 */
        private String triggerNickname;
    }
}
