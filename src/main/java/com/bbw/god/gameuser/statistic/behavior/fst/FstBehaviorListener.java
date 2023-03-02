package com.bbw.god.gameuser.statistic.behavior.fst;

import com.bbw.common.DateUtil;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.game.config.mall.MallTool;
import com.bbw.god.gameuser.card.event.EPCardAdd;
import com.bbw.god.gameuser.card.event.UserCardAddEvent;
import com.bbw.god.gameuser.statistic.StatisticTypeEnum;
import com.bbw.god.gameuser.statistic.event.StatisticEventPublisher;
import com.bbw.god.server.fst.event.EPFstFightOver;
import com.bbw.god.server.fst.event.FstGuardWinEvent;
import com.bbw.god.server.fst.event.GameFstFightOverEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 封神台统计监听类
 *
 * @author lzc
 * @description
 * @date 2021/4/15 11:28
 */
@Component
@Slf4j
@Async
public class FstBehaviorListener {
    @Autowired
    private FstStatisticService statisticService;

    /**
     * 封神台战斗胜利
     *
     * @param event
     */
    @Order(2)
    @EventListener
    @SuppressWarnings("unchecked")
    public void fightWin(FstGuardWinEvent event) {
        try {
            BaseEventParam ep = (BaseEventParam) event.getSource();
            Long uid = ep.getGuId();
            //封神台守位成功
            statisticService.draw(uid, 0, true, false, false, false);
            FstStatistic statistic = statisticService.fromRedis(uid, StatisticTypeEnum.NONE, DateUtil.getTodayInt());
            StatisticEventPublisher.pubBehaviorStatisticEvent(uid, ep.getWay(), ep.getRd(), statistic);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 收集到13张封神台卡牌
     *
     * @param event
     */
    @Order(2)
    @EventListener
    @SuppressWarnings("unchecked")
    public void addCard(UserCardAddEvent event) {
        try {
            EPCardAdd ep = event.getEP();
            List<Integer> fstCardIds = MallTool.getMallConfig().getFstCardIds();
            long count = ep.getAddCards().stream().filter(tmp -> tmp.isNew() && fstCardIds.contains(tmp.getCardId())).count();
            Long uid = ep.getGuId();
            statisticService.draw(uid, (int) count, false, false, false, false);
            FstStatistic statistic = statisticService.fromRedis(uid, StatisticTypeEnum.NONE, DateUtil.getTodayInt());
            StatisticEventPublisher.pubBehaviorStatisticEvent(uid, ep.getWay(), ep.getRd(), statistic);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 跨服封神台的挑战结算
     *
     * @param event
     */
    @Order(2)
    @EventListener
    @SuppressWarnings("unchecked")
    public void GameFstFightOver(GameFstFightOverEvent event) {
        try {
            EPFstFightOver ep = (EPFstFightOver) event.getEP();
            Long uid = ep.getGuId();
            statisticService.draw(uid, 0, false, false, false, ep.isWin());
            FstStatistic statistic = statisticService.fromRedis(uid, StatisticTypeEnum.NONE, DateUtil.getTodayInt());
            StatisticEventPublisher.pubBehaviorStatisticEvent(uid, ep.getWay(), ep.getRd(), statistic);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
