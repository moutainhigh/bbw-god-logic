package com.bbw.god.server.maou.bossmaou.auction;

import com.bbw.common.DateUtil;
import com.bbw.db.redis.RedisHashUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.bbw.god.game.data.redis.RedisKeyConst.SPLIT;

/**
 * @author suchaobin
 * @description 魔王拍卖出价明细service
 * @date 2020/7/23 14:19
 **/
@Service
public class MaouAuctionBidDetailService {
    @Autowired
    private RedisHashUtil<String, MaouAuctionBidDetail> redisHashUtil;

    private static final long TIME_OUT = 3 * 24 * 60 * 60;

    /**
     * 获取存在redis中的key
     *
     * @param sid    区服id
     * @param maouId 魔王id
     * @return
     */
    private String getKey(int sid, long maouId) {
        return "server" + SPLIT + sid + SPLIT + "maouAuction" + SPLIT + "bidDetail" + SPLIT + maouId;
    }

    /**
     * 记录明细
     *
     * @param detail 明细对象
     */
    public void addDetail(MaouAuctionBidDetail detail) {
        Date bidTime = detail.getBidTime();
        String key = getKey(detail.getSid(), detail.getMaouId());
        redisHashUtil.putField(key, DateUtil.toDateTimeString(bidTime), detail, TIME_OUT);
    }

    /**
     * 获取最新的出价时间
     *
     * @param sid    区服id
     * @param maouId 魔王id
     * @return
     */
    public Date getLatestBidTime(int sid, long maouId) {
        String key = getKey(sid, maouId);
        Map<String, MaouAuctionBidDetail> map = redisHashUtil.get(key);
        List<Date> dateList = map.keySet().stream().map(DateUtil::fromDateTimeString).collect(Collectors.toList());
        return dateList.stream().max(Date::compareTo).orElse(null);
    }

    /**
     * 获取最新出价的明细
     *
     * @param sid    区服id
     * @param maouId 魔王id
     * @return
     */
    public MaouAuctionBidDetail getLatestBidDetail(int sid, long maouId) {
        String key = getKey(sid, maouId);
        Date latestBidTime = getLatestBidTime(sid, maouId);
        if (null == latestBidTime) {
            return null;
        }
        return redisHashUtil.getField(key, DateUtil.toDateTimeString(latestBidTime));
    }

    /**
     * 获取最新出价的明细
     *
     * @param uid    玩家id
     * @param sid    区服id
     * @param maouId 魔王id
     * @return
     */
    private MaouAuctionBidDetail getLatestBidDetail(long uid, int sid, long maouId) {
        List<MaouAuctionBidDetail> detailList = getDetailList(uid, sid, maouId);
        return detailList.stream().max(Comparator.comparing(MaouAuctionBidDetail::getBidTime)).orElse(null);
    }

    /**
     * 获取玩家最新出的价格
     *
     * @param uid    玩家id
     * @param sid    区服id
     * @param maouId 魔王id
     * @return
     */
    public int getLatestPrice(long uid, int sid, long maouId) {
        MaouAuctionBidDetail detail = getLatestBidDetail(uid, sid, maouId);
        if (null == detail) {
            return 0;
        }
        return detail.getPrice();
    }

    /**
     * 获取玩家最新出的价格
     *
     * @param sid    区服id
     * @param maouId 魔王id
     * @return
     */
    public int getLatestPrice(int sid, long maouId) {
        MaouAuctionBidDetail detail = getLatestBidDetail(sid, maouId);
        if (null == detail) {
            return 0;
        }
        return detail.getPrice();
    }

    /**
     * 获取明细集合
     *
     * @param sid    区服id
     * @param maouId 魔王id
     * @return
     */
    private List<MaouAuctionBidDetail> getDetailList(int sid, long maouId) {
        String redisKey = getKey(sid, maouId);
        Map<String, MaouAuctionBidDetail> map = redisHashUtil.get(redisKey);
        Set<String> keySet = map.keySet();
        List<MaouAuctionBidDetail> detailList = new ArrayList<>();
        for (String key : keySet) {
            MaouAuctionBidDetail detail = map.get(key);
            detailList.add(detail);
        }
        return detailList;
    }

    private List<MaouAuctionBidDetail> getDetailList(long uid, int sid, long maouId) {
        List<MaouAuctionBidDetail> detailList = getDetailList(sid, maouId);
        return detailList.stream().filter(detail -> detail.getUid().equals(uid)).collect(Collectors.toList());
    }

    /**
     * 获取所有出价的玩家id
     *
     * @param sid    区服id
     * @param maouId 魔王id
     * @return
     */
    public List<Long> getBidUids(int sid, long maouId) {
        return getDetailList(sid, maouId).stream().map(MaouAuctionBidDetail::getUid).distinct().collect(Collectors.toList());
    }
}
