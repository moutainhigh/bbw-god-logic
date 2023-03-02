package com.bbw.god.activityrank.server.fuhao;

import com.bbw.god.activityrank.ActivityRankEnum;
import com.bbw.god.activityrank.ActivityRankService;
import com.bbw.god.activityrank.server.ServerActivityRank;
import com.bbw.god.activityrank.server.ServerActivityRankService;
import com.bbw.god.activityrank.server.fuhao.event.EPFuHaoRankUp;
import com.bbw.god.activityrank.server.fuhao.event.FuHaoRankEventPublisher;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.res.copper.CopperAddEvent;
import com.bbw.god.gameuser.res.copper.EPCopperAdd;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 财富榜监听器
 *
 * @author suhq
 * @date 2019年3月4日 下午4:15:07
 */
@Component
public class FuHaoRankListener {
    private ActivityRankEnum rankType = ActivityRankEnum.FUHAO_RANK;

    @Autowired
    private ActivityRankService activityRankService;
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private ServerActivityRankService serverActivityRankService;

    /**
     * 执行优先级高于铜钱增加处理
     *
     * @param event
     */
    @Async
    @EventListener
    @Order(1000)
    public void addCopper(CopperAddEvent event) {
        EPCopperAdd ep = event.getEP();
        WayEnum way = ep.getWay();
        long guId = ep.getGuId();
        // 富豪榜
        long fhbCopper = ep.getWeekCopper();
        // 满足添加条件
        if (shouldAdd(way, fhbCopper)) {
            Map<Long, Integer> oldRankMap = getOldRankMap(guId);
            int oldRank = activityRankService.getRank(guId, rankType);
            activityRankService.incrementRankValue(guId, fhbCopper, rankType);
            int newRank = activityRankService.getRank(guId, rankType);
            if (newRank < oldRank || oldRank == 0) {
                FuHaoRankEventPublisher.pubFuHaoRankUpEvent(EPFuHaoRankUp.instance(guId, way, oldRankMap));
            }
        }
    }

    /**
     * 获取玩家所在区服的变动前富豪榜排名的集合，key是玩家id，value是对应的变动前的排名
     *
     * @param guId
     * @return
     */
    private Map<Long, Integer> getOldRankMap(Long guId) {
        List<ServerActivityRank> activityRanks = serverActivityRankService.getServerActivityRanks(gameUserService.getActiveSid(guId));
        ServerActivityRank serverActivityRank = activityRanks.stream().filter(ar -> ar.getType().intValue() == ActivityRankEnum.FUHAO_RANK.getValue()).findFirst().orElse(null);
        Map<Long, Integer> oldRankMap = new HashMap<>();
        if (serverActivityRank != null) {
            Set<Long> rankers = activityRankService.rangeRankers(serverActivityRank, 1, 10);
            Iterator<Long> iterator = rankers.iterator();
            while (iterator.hasNext()) {
                Long uid = iterator.next();
                int rank = activityRankService.getRank(uid, rankType);
                oldRankMap.put(uid, rank);
            }
        }
        return oldRankMap;
    }

    // 是否算入富豪榜
    private boolean shouldAdd(WayEnum way, long fhbCopper) {
        if (fhbCopper <= 0) {
            return false;
        }
        switch (way) {
            case SALARY_COPPER:// 俸禄不算入富豪榜
            case EXCHANGE_FST:// 封神台兑换收入不算入富豪榜
            case Mail:// 邮件不纳入富豪榜
            case ACTIVITY_TODAY_RECHARGE://今日充值
            case TE_HUI_1_GOLD_GIFT_PACK://特惠1元元宝礼包
            case DAILY_1_GOLD_GIFT_PACK://每日1元元宝礼包
            case WEEKLY_1_GOLD_GIFT_PACK://每周1元元宝礼包
            case LT://鹿台
                return false;
            default:
                return true;
        }
    }
}
