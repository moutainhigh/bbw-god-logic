package com.bbw.god.server.maou.bossmaou.auction;

import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.controller.AbstractController;
import com.bbw.god.game.CR;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author suchaobin
 * @description 魔王拍卖接口
 * @date 2020/7/23 11:37
 **/
@RestController
public class MaouAuctionCtrl extends AbstractController {
    @Autowired
    private MaouAuctionService maouAuctionService;

    /**
     * 获取拍卖信息
     *
     * @return
     */
    @RequestMapping(CR.MaouAuction.GET_AUCTION_INFO)
    public RDMaouAuctionInfo getAuctionInfo() {
        throw new ExceptionForClientTip("bug.close.this");
//        return maouAuctionService.getAuctionInfo(getUserId(), getServerId());
    }

    /**
     * 出价
     *
     * @param price 出的价格
     * @return
     */
    @RequestMapping(CR.MaouAuction.AUCTION_BID)
    public RDCommon bid(int price) {
        throw new ExceptionForClientTip("bug.close.this");
//        return maouAuctionService.bid(getUserId(), getServerId(), price);
    }
}
