package com.bbw.god.gameuser.guide.v1;

import com.bbw.god.db.entity.InsGuideDetail;
import com.bbw.god.db.service.InsGuideDetailService;
import com.bbw.god.fight.FightTypeEnum;
import com.bbw.god.game.combat.event.EPFightEnd;
import com.bbw.god.game.combat.event.CombatFightWinEvent;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.card.event.*;
import com.bbw.god.gameuser.guide.*;
import com.bbw.god.gameuser.res.copper.CopperAddEvent;
import com.bbw.god.gameuser.res.copper.EPCopperAdd;
import com.bbw.god.gameuser.special.event.EPSpecialAdd;
import com.bbw.god.gameuser.special.event.SpecialAddEvent;
import com.bbw.god.gameuser.treasure.event.EPTreasureAdd;
import com.bbw.god.gameuser.treasure.event.TreasureAddEvent;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class NewGuideListener {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private NewerGuideService newerGuideService;
    @Autowired
    private InsGuideDetailService insGuideDetailService;

    @EventListener
    public void addCard(UserCardAddEvent event) {
        EPCardAdd ep = event.getEP();
        long uid = ep.getGuId();
        WayEnum way = ep.getWay();
        switch (way) {
            case CZ:
                updateGuideStatus(uid, NewerGuideEnum.CUNZHUANG, ep.getRd());
                return;
            case KZ:
                updateGuideStatus(uid, NewerGuideEnum.KZ_BUY, ep.getRd());
                return;

        }
    }

    @EventListener
    public void addTreasure(TreasureAddEvent event) {
        EPTreasureAdd ep = event.getEP();
        WayEnum way = ep.getWay();
        switch (way) {
            case XRD:
                updateGuideStatus(ep.getGuId(), NewerGuideEnum.XIANRENDONG, ep.getRd());
                return;

        }
    }

    @EventListener
    public void grouping(UserCardGroupingEvent event) {
        EPCardGrouping ep = event.getEP();
        updateGuideStatus(ep.getGuId(), NewerGuideEnum.BIANZHU, ep.getRd());
    }

    @EventListener
    public void fightWin(CombatFightWinEvent event) {
        EPFightEnd ep = (EPFightEnd) event.getSource();
        if (FightTypeEnum.YG == ep.getFightType()) {
            updateGuideStatus(ep.getGuId(), NewerGuideEnum.YEGUAI, ep.getRd());
        } else if (FightTypeEnum.ATTACK == ep.getFightType()) {
            updateGuideStatus(ep.getGuId(), NewerGuideEnum.ATTACK, ep.getRd());
        }
    }

    @EventListener
    @Order(1000)
    public void addCopper(CopperAddEvent event) {
        EPCopperAdd ep = event.getEP();
        if (ep.getWay() == WayEnum.QZ_AWARD) {
            updateGuideStatus(ep.getGuId(), NewerGuideEnum.QIANZHUANG, ep.getRd());
        }

    }

    @EventListener
    public void addSpecials(SpecialAddEvent event) {
        EPSpecialAdd ep = event.getEP();
        if (ep.getWay() == WayEnum.TRADE) {
            updateGuideStatus(ep.getGuId(), NewerGuideEnum.JIAOYI, ep.getRd());
        }
    }

    @EventListener
    public void cardLevelUp(UserCardLevelUpEvent event) {
        EPCardLevelUp ep = event.getEP();
        if (ep.getWay() == WayEnum.CARD_UPDATE) {
            updateGuideStatus(ep.getGuId(), NewerGuideEnum.CARD_LEVEL_UP, ep.getRd());
        }
    }

    /**
     * 更新新手引导状态
     *
     * @param uid
     * @param guide
     */
    private void updateGuideStatus(long uid, NewerGuideEnum guide, RDCommon rd) {
        UserNewerGuide userNewerGuide = this.newerGuideService.getUserNewerGuide(uid);
        if (!userNewerGuide.getIsPassNewerGuide()) {
            userNewerGuide.updateNewerGuide(guide.getStep());
            // 发布记录新手引导进度变化事件
            GuideEventPublisher.pubLogNewerGuideEvent(uid, guide.getStep(), rd);
            if (NewerGuideEnum.CARD_LEVEL_UP == guide) {
                userNewerGuide.setIsPassNewerGuide(true);
            }
            this.gameUserService.updateItem(userNewerGuide);
            if (userNewerGuide.getIsPassNewerGuide()) {
                GuideEventPublisher.pubPassNewerGuideEvent(uid, rd);
            }
        }
    }

    @EventListener
    @Async
    @Order(1000)
    public void log(LogNewerGuideEvent event) {
        EPLogNewerGuide ep = event.getEP();
        long uid = ep.getGuId();
        Integer sid = gameUserService.getGameUser(uid).getServerId();
        Integer newerGuide = ep.getNewerGuide();
        String name = NewerGuideEnum.fromValue(newerGuide).getName();
        InsGuideDetail detail = InsGuideDetail.getInstance(ep.getGuId(), sid, newerGuide, name, "v1");
        insGuideDetailService.insert(detail);
    }
}
