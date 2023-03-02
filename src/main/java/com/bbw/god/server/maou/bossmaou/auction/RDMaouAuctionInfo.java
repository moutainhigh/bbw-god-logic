package com.bbw.god.server.maou.bossmaou.auction;

import com.bbw.god.game.award.Award;
import com.bbw.god.rd.RDCommon;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author suchaobin
 * @description 拍卖物品信息
 * @date 2020/7/23 11:30
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class RDMaouAuctionInfo extends RDCommon {
    private static final long serialVersionUID = 2432674575330594595L;
    // 拍卖物品
    private Award award;
    // 底价
    private Integer minPrice;
    // 每次最低加价
    private Integer minAddPrice;
    // 最高出价
    private Integer maxPrice;
    // 最高出价的玩家昵称
    private String nickname;
    // 剩余时间
    private Long remainTime;
    // 本次最大可允许的剩余时间
    private Long maxRemainTime;
    // 我已经出的最高价
    private Integer myLatestPrice;

    public static RDMaouAuctionInfo getInstance(CfgMaouAuction cfgMaouAuction, Award award, String nickname,
                                                int maxPrice, int myLatestPrice, long remainTime, long maxRemainTime) {
        RDMaouAuctionInfo info = new RDMaouAuctionInfo();
        info.setAward(award);
        info.setNickname(nickname);
        info.setMinPrice(cfgMaouAuction.getMinPrice());
        info.setMinAddPrice(cfgMaouAuction.getMinAddPrice());
        info.setMaxPrice(maxPrice);
        info.setMyLatestPrice(myLatestPrice);
        info.setRemainTime(remainTime);
        info.setMaxRemainTime(maxRemainTime);
        return info;
    }
}
