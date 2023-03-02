package com.bbw.god.report.business;

import com.bbw.god.event.EventParam;
import com.bbw.god.game.config.mall.CfgMallEntity;
import com.bbw.god.game.config.mall.MallEnum;
import com.bbw.god.game.config.mall.MallTool;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.mall.cardshop.event.DrawEndEvent;
import com.bbw.god.mall.cardshop.event.EPDraw;
import com.bbw.god.mall.event.EPMallBuy;
import com.bbw.god.mall.event.MallBuyEvent;
import com.bbw.god.report.TapdbEventReporter;
import com.bbw.god.server.ServerUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * 上报监听
 *
 * @author: suhq
 * @date: 2021/8/17 6:14 下午
 */
@Async
@Component
public class BusinessReportListener {
    /** 需要上报的商店 */
    private static List<MallEnum> MALLS_TO_REPORT = Arrays.asList(
            MallEnum.NOT_SHOWED,
            MallEnum.DJ,
            MallEnum.EMOTICON,
            MallEnum.THLB,
            MallEnum.TTCJ_LB,
            MallEnum.NEWER_PACKAGE,
            MallEnum.HOLIDAY_MALL_LIMIT_PACK,
            MallEnum.MXLB,
            MallEnum.ZLLB,
            MallEnum.XJBK,
            MallEnum.GOLD_CONSUME,
            MallEnum.DAILY_RECHARGE_BAG,
            MallEnum.WEEK_RECHARGE_BAG,
            MallEnum.MONTH_RECHARGE_BAG,
            MallEnum.ACTIVITY_BAG,
            MallEnum.HOLIDAY_EXCHANGE,
            MallEnum.FST,
            MallEnum.ZXZ,
            MallEnum.MAOU,
            MallEnum.SXDH,
            MallEnum.SNATCH_TREASURE,
            MallEnum.TE_HUI_RECHARGE_BAG,
            MallEnum.ROLE_TIME_LIMIT_BAG,
            MallEnum.GOLD_RECHARGE_BAG,
            MallEnum.FIRST_RECHARGE_ITEM,
            MallEnum.WAR_TOKEN,
            MallEnum.JJLP_TOKEN,
            MallEnum.TRANSMIGRATION,
            MallEnum.SPECIAL_DISCOUNT,
            MallEnum.COMBINED_SERVICE_EXCHANGE,
            MallEnum.DIAMOND_GIFT_PACK,
            MallEnum.SM
    );

    @Autowired
    ServerUserService serverUserService;
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    TapdbEventReporter tapdbEventReporter;

    /**
     * 抽卡监听
     */
    @Order(1000)
    @EventListener
    public void drawCard(DrawEndEvent event) {
        EventParam<EPDraw> ep = (EventParam<EPDraw>) event.getSource();
        GameUser gu = gameUserService.getGameUser(ep.getGuId());
        BusinessFinishReportEvent reportEvent = BusinessFinishReportEvent.instanceAsCardDraw(gu, ep.getValue());
        report(reportEvent);
    }

    /**
     * 商城购买监听
     */
    @Order(1000)
    @EventListener
    public void mallBuy(MallBuyEvent event) {
        EPMallBuy ep = event.getEP();
        GameUser gu = gameUserService.getGameUser(ep.getGuId());
        MallEnum mallEnum = MallEnum.fromValue(ep.getMallType());
        if (!MALLS_TO_REPORT.contains(mallEnum)) {
            return;
        }
        CfgMallEntity Mall = MallTool.getMall(ep.getMallType(), ep.getGoodsId());
        BusinessFinishReportEvent reportEvent = BusinessFinishReportEvent.instanceAsMallBuy(gu, Mall, ep.getNum());
        report(reportEvent);
    }

    private void report(BusinessFinishReportEvent reportEvent) {
        TapdbBusinessFinishEventData data = new TapdbBusinessFinishEventData(reportEvent);
        tapdbEventReporter.report(data);
    }

}
