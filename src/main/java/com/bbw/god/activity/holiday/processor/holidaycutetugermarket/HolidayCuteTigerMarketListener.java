package com.bbw.god.activity.holiday.processor.holidaycutetugermarket;

import com.bbw.common.LM;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.mail.UserMail;
import com.bbw.god.gameuser.treasure.event.*;
import com.bbw.god.login.event.EPFirstLoginPerDay;
import com.bbw.god.login.event.FirstLoginPerDayEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 萌虎集市监听
 *
 * @author fzj
 * @date 2022/3/8 15:22
 */
@Component
@Slf4j
@Async
public class HolidayCuteTigerMarketListener {
    @Autowired
    HolidayCuteTigerMarketService holidayCuteTigerMarketService;
    @Autowired
    GameUserService gameUserService;
    @Autowired
    HolidayCuteTigerMarketProcessor holidayCuteTigerMarketProcessor;

    /**
     * 糕点添加
     *
     * @param event
     */
    @Order(1000)
    @EventListener
    public void addPastry(TreasureAddEvent event) {
        EPTreasureAdd ep = event.getEP();
        long uid = ep.getGuId();
        List<EVTreasure> activityOutput = ep.getAddTreasures().stream()
                .filter(t -> HolidayCuteTigerMarketProcessor.ACTIVITY_OUTPUT.contains(t.getId())).collect(Collectors.toList());
        if (activityOutput.isEmpty()) {
            return;
        }
        //同步当天糕点数据
        for (EVTreasure evTreasure : activityOutput) {
            holidayCuteTigerMarketService.addPastryNum(uid, evTreasure.getId(), evTreasure.getNum());
        }
    }

    /**
     * 糕点扣除
     *
     * @param event
     */
    @Order(1000)
    @EventListener
    public void delPastry(TreasureDeductEvent event) {
        EPTreasureDeduct ep = event.getEP();
        Long guId = ep.getGuId();
        if (ep.getWay() == WayEnum.CUTE_TIGER_MARKET_AUTOMATIC_DEL) {
            return;
        }
        EVTreasure deductTreasure = ep.getDeductTreasure();
        Integer deductTreasureId = deductTreasure.getId();
        if (!HolidayCuteTigerMarketProcessor.ACTIVITY_OUTPUT.contains(deductTreasureId)) {
            return;
        }
        //同步当天糕点数据
        holidayCuteTigerMarketService.delPastryNum(guId, deductTreasureId, deductTreasure.getNum());
    }

    /**
     * 活动最后一天发送通知邮件
     *
     * @param event
     */

    @Order(1000)
    @EventListener
    public void sendLastDayNotifyEmail(
            FirstLoginPerDayEvent event) {
        EPFirstLoginPerDay ep = event.getEP();
        long uid = ep.getUid();
        GameUser gameUser = gameUserService.getGameUser(uid);
        //活动是否开启
        if (!holidayCuteTigerMarketProcessor.isOpened(gameUser.getServerId())) {
            return;
        }
        //是否是活动最后一天
        if (!holidayCuteTigerMarketProcessor.isLastDay(uid)) {
            return;
        }
        //等级是否小于活动开放等级
        if (gameUser.getLevel() < HolidayCuteTigerMarketTool.getLevelToMailNotice()) {
            return;
        }
        //添加通知邮件
        String title = LM.I.getMsgByUid(uid, "activity.game.cuteTigerMarket.title");
        String content = LM.I.getMsgByUid(uid, "activity.game.cuteTigerMarket.content");
        UserMail userMail = UserMail.newSystemMail(title, content, uid);
        gameUserService.addItem(uid, userMail);
    }
}
