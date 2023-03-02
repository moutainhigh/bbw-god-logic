package com.bbw.god.server.maou.bossmaou.auction;

import com.bbw.common.DateUtil;
import com.bbw.common.LM;
import com.bbw.common.ListUtil;
import com.bbw.common.lock.RedisLockUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.game.config.server.ServerTool;
import com.bbw.god.game.config.treasure.TreasureTool;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.mail.UserMail;
import com.bbw.god.gameuser.res.ResEventPublisher;
import com.bbw.god.rd.RDCommon;
import com.bbw.god.server.ServerDataService;
import com.bbw.god.server.maou.BaseServerMaou;
import com.bbw.god.server.maou.bossmaou.ServerBossMaou;
import com.bbw.god.server.maou.bossmaou.ServerBossMaouService;
import com.bbw.god.server.maou.bossmaou.attackinfo.BossMaouAttackSummary;
import com.bbw.god.server.maou.bossmaou.attackinfo.BossMaouAttackSummaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.bbw.god.game.data.redis.RedisKeyConst.SPLIT;

/**
 * @author suchaobin
 * @description 拍卖service
 * @date 2020/7/23 11:37
 **/
@Service
public class MaouAuctionService {
    @Autowired
    private ServerDataService serverDataService;
    @Autowired
    private RedisLockUtil redisLockUtil;
    @Autowired
    private ServerBossMaouService bossMaouService;
    @Autowired
    private MaouAuctionBidDetailService bidDetailService;
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private BossMaouAttackSummaryService bossMaouAttackSummaryService;

    /**
     * 获取拍卖信息
     *
     * @param uid 玩家id
     * @param sid 区服id
     * @return
     */
    public RDMaouAuctionInfo getAuctionInfo(long uid, int sid) {
        // 检查数据
        ServerBossMaou bossMaou = checkMaou(uid, sid);
        // 封装返回对象
        ServerMaouAuction serverMaouAuction = getCurServerAuction(sid, bossMaou.getId());
        // 已经发过奖励了
        if (null != serverMaouAuction.getEndTime()) {
            throw new ExceptionForClientTip("maou.auction.already.end");
        }
        CfgMaouAuction cfgMaouAuction = MaouAuctionTool.getCfgAuction(serverMaouAuction.getCfgAuctionId());
        Award award = serverMaouAuction.getAward();
        int maxPrice = cfgMaouAuction.getMinPrice();
        String nickname = "拍卖底价";
        MaouAuctionBidDetail bidDetail = bidDetailService.getLatestBidDetail(sid, bossMaou.getId());
        // 如果没有人出价
        long remainTime;
        if (null == bidDetail) {
            remainTime = DateUtil.addSeconds(serverMaouAuction.getBeginTime(), 50).getTime() - DateUtil.now().getTime();
        } else {
            Long latestUid = bidDetail.getUid();
            nickname = gameUserService.getGameUser(latestUid).getRoleInfo().getNickname();
            maxPrice = bidDetail.getPrice();
            remainTime = DateUtil.addSeconds(bidDetail.getBidTime(), 17).getTime() - DateUtil.now().getTime();
        }
        long maxRemainTime = null == bidDetail ? 50000 : 17000;
        int myLatestPrice = bidDetailService.getLatestPrice(uid, sid, bossMaou.getId());
        return RDMaouAuctionInfo.getInstance(cfgMaouAuction, award, nickname, maxPrice, myLatestPrice,
                remainTime, maxRemainTime);
    }

    /**
     * 检查数据并返回当前的魔王
     *
     * @param uid 玩家id
     * @param sid 区服id
     * @return
     */
    private ServerBossMaou checkMaou(long uid, int sid) {
        ServerBossMaou bossMaou = null;
        List<ServerBossMaou> bossMaous = bossMaouService.getBossMaous(uid, sid, DateUtil.now()).stream()
                .filter(sbm -> sbm.getRemainBlood() == 0).collect(Collectors.toList());
        // 魔王不存在或者还没死
        if (ListUtil.isEmpty(bossMaous)) {
            throw new ExceptionForClientTip("maou.not.die");
        }
        // 今天已经死的魔王集合长度大于1取后面那个魔王
        if (bossMaous.size() > 1) {
            bossMaou = bossMaous.stream().max(Comparator.comparing(BaseServerMaou::getBeginTime)).orElse(null);
        } else {
            bossMaou = bossMaous.get(0);
        }
        List<BossMaouAttackSummary> ranker = bossMaouAttackSummaryService.getAttackInfoSorted(bossMaou);
        if (ranker.size() == 2) {
            throw new ExceptionForClientTip("maou.auction.join.not.valid");
        }
        List<Long> attackUids = ranker.stream().map(BossMaouAttackSummary::getGuId).collect(Collectors.toList());
        if (!attackUids.contains(uid)) {
            throw new ExceptionForClientTip("maou.auction.not.attack.maou");
        }
        return bossMaou;
    }

    /**
     * 获取当前的区服拍卖对象
     *
     * @param sid    区服id
     * @param maouId 魔王id
     * @return
     */
    public ServerMaouAuction getCurServerAuction(int sid, long maouId) {
        ServerMaouAuction serverMaouAuction = serverDataService.getServerDatas(sid, ServerMaouAuction.class).stream().
                filter(sa -> sa.getMaouId().equals(maouId)).findFirst().orElse(null);
        // 加锁双重检查，懒汉单例
        if (null == serverMaouAuction) {
            serverMaouAuction = (ServerMaouAuction) redisLockUtil.doSafe(getInstanceKey(sid, maouId),
                    obj -> {
                        ServerMaouAuction auction = serverDataService.getServerDatas(sid, ServerMaouAuction.class)
                                .stream().filter(sa -> sa.getMaouId().equals(maouId)).findFirst().orElse(null);
                        if (null == auction) {
                            CfgMaouAuction randomAuction = MaouAuctionTool.getRandomAuction(ServerTool.getServer(sid));
                            Award award = MaouAuctionTool.getAward(randomAuction);
                            auction = ServerMaouAuction.getInstance(sid, maouId, randomAuction.getId(), award);
                            serverDataService.addServerData(auction);
                        }
                        return auction;
                    });
        }
        return serverMaouAuction;
    }

    /**
     * 出价
     *
     * @param uid   玩家id
     * @param sid   区服id
     * @param price 出的价格
     * @return
     */
    public RDCommon bid(long uid, int sid, int price) {
        RDCommon rd = new RDCommon();
        ServerBossMaou bossMaou = checkMaou(uid, sid);
        Long maouId = bossMaou.getId();
        ServerMaouAuction serverMaouAuction = getCurServerAuction(sid, maouId);
        Integer cfgAuctionId = serverMaouAuction.getCfgAuctionId();
        CfgMaouAuction cfgMaouAuction = MaouAuctionTool.getCfgAuction(cfgAuctionId);
        // 加锁执行操作并返回
        return (RDCommon) redisLockUtil.doSafe(getBidKey(sid, maouId), obj -> {
            int latestPrice = Math.max(cfgMaouAuction.getMinPrice(), bidDetailService.getLatestPrice(sid, maouId));
            int addPrice = price - latestPrice;
            int costPrice = price - bidDetailService.getLatestPrice(uid, sid, maouId);
            checkBid(uid, sid, maouId, price, addPrice, costPrice, serverMaouAuction, cfgMaouAuction);
            bidDetailService.addDetail(MaouAuctionBidDetail.getInstance(uid, sid, price, maouId));
            ResEventPublisher.pubGoldDeductEvent(uid, costPrice, WayEnum.MAOU_AUCTION, rd);
            return rd;
        });
    }

    /**
     * 检查出价是否符合规则
     *
     * @param uid               玩家id
     * @param sid               区服id
     * @param maouId            魔王id
     * @param price             出的价格
     * @param addPrice          加价
     * @param costPrice         要扣除的元宝数
     * @param serverMaouAuction 区服魔王拍卖对象
     * @param cfgMaouAuction    魔王拍卖配置对象
     */
    private void checkBid(long uid, int sid, long maouId, int price, int addPrice, int costPrice,
                          ServerMaouAuction serverMaouAuction, CfgMaouAuction cfgMaouAuction) {
        // 检查元宝够不够
        GameUser gu = gameUserService.getGameUser(uid);
        if (gu.getGold() < costPrice) {
            throw new ExceptionForClientTip("gu.gold.not.enough");
        }
        MaouAuctionBidDetail bidDetail = bidDetailService.getLatestBidDetail(sid, maouId);
        // 出价低于商品底价
        if (price < cfgMaouAuction.getMinPrice()) {
            throw new ExceptionForClientTip("maou.auction.price.lower.than.minPrice");
        }
        // 当前价格小于最新价格
        if (null != bidDetail && price <= bidDetail.getPrice()) {
            throw new ExceptionForClientTip("maou.auction.addPrice.lower.than.minAddPrice");
        }
        // 单次加价小于最低加价
        if (price != cfgMaouAuction.getMinPrice() && addPrice < cfgMaouAuction.getMinAddPrice()) {
            throw new ExceptionForClientTip("maou.auction.addPrice.lower.than.minAddPrice");
        }
        // 每次加价不允许超过1000
        if (addPrice > 1000) {
            throw new ExceptionForClientTip("maou.auction.addPrice.out.of.limit");
        }
        // 没人出价
        if (null == bidDetail) {
            Date beginTime = serverMaouAuction.getBeginTime();
            long secondsBetween = DateUtil.getSecondsBetween(beginTime, DateUtil.now());
            // 没人出价的时候，流拍时间50秒
            if (secondsBetween > 50) {
                throw new ExceptionForClientTip("maou.auction.product.went.unsold");
            }
        } else {
            Date latestBidTime = bidDetailService.getLatestBidTime(sid, maouId);
            long secondsBetween = DateUtil.getSecondsBetween(latestBidTime, DateUtil.now());
            // 有人出价的时候，超过17秒竞拍结束
            if (secondsBetween > 17) {
                throw new ExceptionForClientTip("maou.auction.product.is.sold");
            }
        }
    }

    /**
     * 发奖励
     *
     * @param serverMaouAuction 区服拍卖对象
     */
    public void sendAward(ServerMaouAuction serverMaouAuction) {
        Integer sid = serverMaouAuction.getSid();
        Long maouId = serverMaouAuction.getMaouId();
        MaouAuctionBidDetail latestBidDetail = bidDetailService.getLatestBidDetail(sid, maouId);
        Long uid = latestBidDetail.getUid();
        Integer price = latestBidDetail.getPrice();
        // 发送中标人奖励
        sendWinnerBidderAward(uid, price, serverMaouAuction.getAward());
        // 拍卖价格的70%平均分给其他参与者（有打过魔王的就能分钱）
        ServerBossMaou bossMaou = bossMaouService.getBossMaous(sid, DateUtil.now()).stream().filter(s ->
                s.getId().equals(maouId)).findFirst().orElse(null);
        List<Long> attackUids = bossMaouAttackSummaryService.getAttackInfoSorted(bossMaou).stream()
                .map(BossMaouAttackSummary::getGuId).distinct().collect(Collectors.toList());
        attackUids.remove(uid);
        sendParticipateAward(price, attackUids);
        // 未拍到的玩家退款
        List<Long> uids = bidDetailService.getBidUids(sid, maouId);
        uids.remove(uid);
        sendRefundAward(sid, maouId, uids);
        // 修改状态并保存
        serverMaouAuction.setEndTime(DateUtil.now());
        serverDataService.updateServerData(serverMaouAuction);
    }

    /**
     * 发送中标人奖励
     *
     * @param uid   中标人的玩家id
     * @param price 出的价格
     * @param award 奖励
     */
    private void sendWinnerBidderAward(long uid, int price, Award award) {
        int num = award.getNum();
        String awardName = "";
        switch (award.getItem()) {
            case 40:
                awardName = CardTool.getCardById(award.getAwardId()).getName();
                break;
            case 60:
                awardName = TreasureTool.getTreasureById(award.getAwardId()).getName();
                break;
            default:
                break;
        }
        String title = LM.I.getMsgByUid(uid,"mail.maou.auction.win.award.title");
        String msg = LM.I.getMsgByUid(uid,"mail.maou.auction.win.award.content", price, awardName, num);
        UserMail userMail = UserMail.newAwardMail(title, msg, uid, Arrays.asList(award));
        gameUserService.updateItem(userMail);
    }

    /**
     * 发送参与奖
     *
     * @param price 最终拍卖价格
     * @param uids  玩家id集合
     */
    private void sendParticipateAward(int price, List<Long> uids) {
        int gold = (int) (price * 0.7 / uids.size());
        Award award = new Award(AwardEnum.YB, gold);
        List<UserMail> mailList = new ArrayList<>();
        for (Long uid : uids) {
            String title = LM.I.getMsgByUid(uid,"mail.maou.auction.participate.award.title");
            String msg =  LM.I.getMsgByUid(uid,"mail.maou.auction.participate.award.content");
            mailList.add(UserMail.newAwardMail(title, msg, uid, Arrays.asList(award)));
        }
        gameUserService.updateItems(mailList);
    }

    /**
     * 未拍到的玩家返还元宝
     *
     * @param sid    区服id
     * @param maouId 魔王id
     * @param uids   玩家id集合
     */
    private void sendRefundAward(int sid, long maouId, List<Long> uids) {
        List<UserMail> mailList = new ArrayList<>();
        for (Long uid : uids) {
            String title = LM.I.getMsgByUid(uid,"mail.maou.auction.return.title");
            String msg = LM.I.getMsgByUid(uid,"mail.maou.auction.return.content");
            int price = bidDetailService.getLatestPrice(uid, sid, maouId);
            Award award = new Award(AwardEnum.YB, price);
            mailList.add(UserMail.newAwardMail(title, msg, uid, Arrays.asList(award)));
        }
        gameUserService.updateItems(mailList);
    }

    /**
     * 获取用户初始化锁的key
     *
     * @param sid    区服id
     * @param maouId 魔王id
     * @return
     */
    private String getInstanceKey(int sid, long maouId) {
        return "server" + SPLIT + sid + SPLIT + "maouAuction" + SPLIT + maouId + SPLIT + "instance";
    }

    /**
     * 获取出价的锁的key
     *
     * @param sid    区服id
     * @param maouId 魔王id
     * @return
     */
    private String getBidKey(int sid, long maouId) {
        return "server" + SPLIT + sid + SPLIT + "maouAuction" + SPLIT + maouId + SPLIT + "bid";
    }
}
