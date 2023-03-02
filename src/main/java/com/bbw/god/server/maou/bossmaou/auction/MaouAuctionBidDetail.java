package com.bbw.god.server.maou.bossmaou.auction;

import com.bbw.common.DateUtil;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author suchaobin
 * @description 魔王拍卖出价明细
 * @date 2020/7/23 14:20
 **/
@Data
public class MaouAuctionBidDetail implements Serializable {
    private static final long serialVersionUID = -3809137682613212955L;
    private Long uid;
    private Integer sid;
    private Date bidTime = DateUtil.now();
    // 出的总价
    private Integer price;
    private Long maouId;

    public static MaouAuctionBidDetail getInstance(long uid, int sid, int price, long maouId) {
        MaouAuctionBidDetail detail = new MaouAuctionBidDetail();
        detail.setUid(uid);
        detail.setSid(sid);
        detail.setPrice(price);
        detail.setMaouId(maouId);
        return detail;
    }
}
