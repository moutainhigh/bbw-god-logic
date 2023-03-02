package com.bbw.god.server.maou.bossmaou.auction;

import com.bbw.common.DateUtil;
import com.bbw.common.ID;
import com.bbw.god.game.award.Award;
import com.bbw.god.server.ServerData;
import com.bbw.god.server.ServerDataType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @author suchaobin
 * @description 区服拍卖对象
 * @date 2020/7/23 13:51
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class ServerMaouAuction extends ServerData {
    private Long maouId;
    private Award award;
    private Integer cfgAuctionId;
    private Date beginTime = DateUtil.now();
    private Date endTime;

    public static ServerMaouAuction getInstance(int sid, long maouId, int cfgAuctionId, Award award) {
        ServerMaouAuction auction = new ServerMaouAuction();
        auction.setId(ID.INSTANCE.nextId());
        auction.setSid(sid);
        auction.setAward(award);
        auction.setCfgAuctionId(cfgAuctionId);
        auction.setMaouId(maouId);
        return auction;
    }

    @Override
    public ServerDataType gainDataType() {
        return ServerDataType.MAOU_AUCTION;
    }
}
