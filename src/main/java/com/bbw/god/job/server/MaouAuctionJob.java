package com.bbw.god.job.server;

import com.bbw.common.DateUtil;
import com.bbw.common.ListUtil;
import com.bbw.god.db.entity.CfgServerEntity;
import com.bbw.god.server.ServerDataService;
import com.bbw.god.server.maou.bossmaou.ServerBossMaou;
import com.bbw.god.server.maou.bossmaou.ServerBossMaouService;
import com.bbw.god.server.maou.bossmaou.auction.MaouAuctionBidDetailService;
import com.bbw.god.server.maou.bossmaou.auction.MaouAuctionService;
import com.bbw.god.server.maou.bossmaou.auction.ServerMaouAuction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author suchaobin
 * @description 魔王拍卖奖励发放定时器
 * @date 2020/7/23 16:00
 **/
@Component("maouAuctionJob")
public class MaouAuctionJob extends ServerJob {
    @Autowired
    private MaouAuctionBidDetailService bidDetailService;
    @Autowired
    private ServerBossMaouService bossMaouService;
    @Autowired
    private ServerDataService serverDataService;
    @Autowired
    private MaouAuctionService maouAuctionService;

    @Override
    public String getJobDesc() {
        return "魔王拍卖奖励发放定时器";
    }

    @Override
    public void job(CfgServerEntity server) {
//        int sid = server.getId();
//        List<ServerBossMaou> bossMaous = bossMaouService.getBossMaous(sid, DateUtil.now());
//        List<ServerMaouAuction> serverMaouAuctions = new ArrayList<>();
//        for (ServerBossMaou bossMaou : bossMaous) {
//            // 未击杀跳过
//            if (!bossMaou.isKilled()) {
//                continue;
//            }
//            Long maouId = bossMaou.getId();
//            // 或者区服拍卖对象
//            ServerMaouAuction serverMaouAuction = serverDataService.getServerDatas(sid, ServerMaouAuction.class)
//                    .stream().filter(sa -> sa.getMaouId().equals(maouId)).findFirst().orElse(null);
//            // 1.还没生成对应的拍卖对象 2.已经流拍或者发过奖励了
//            if (null == serverMaouAuction || null != serverMaouAuction.getEndTime()) {
//                continue;
//            }
//            Date latestBidTime = bidDetailService.getLatestBidTime(sid, bossMaou.getId());
//            // 还没有人下注
//            if (null == latestBidTime) {
//                Date beginTime = serverMaouAuction.getBeginTime();
//                long secondsBetween = DateUtil.getSecondsBetween(beginTime, DateUtil.now());
//                // 流拍了，设置结束时间处理状态
//                if (secondsBetween > 50) {
//                    serverMaouAuction.setEndTime(DateUtil.now());
//                    serverMaouAuctions.add(serverMaouAuction);
//                }
//            } else {
//                long secondsBetween = DateUtil.getSecondsBetween(latestBidTime, DateUtil.now());
//                // 结束拍卖环节了，发奖励
//                if (secondsBetween > 17) {
//                    maouAuctionService.sendAward(serverMaouAuction);
//                }
//            }
//        }
//        if (ListUtil.isNotEmpty(serverMaouAuctions)) {
//            serverDataService.updateServerData(serverMaouAuctions);
//        }
    }

    //必须重载，否则定时任务引擎认不到方法
    @Override
    public void doJob(String sendMail) {
        super.doJob(sendMail);
    }
}
