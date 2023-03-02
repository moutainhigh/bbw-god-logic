package com.bbw.god.gameuser.guide.v2;

import com.bbw.god.city.chengc.in.event.BuildingLevelUpEvent;
import com.bbw.god.city.chengc.in.event.EPBuildingLevelUp;
import com.bbw.god.event.EventParam;
import com.bbw.god.fight.FightTypeEnum;
import com.bbw.god.game.combat.event.EPFightEnd;
import com.bbw.god.game.combat.event.CombatFightWinEvent;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.card.event.*;
import com.bbw.god.gameuser.guide.GuideEventPublisher;
import com.bbw.god.gameuser.guide.NewerGuideService;
import com.bbw.god.gameuser.guide.UserNewerGuide;
import com.bbw.god.gameuser.redis.GameUserRedisUtil;
import com.bbw.god.gameuser.res.copper.CopperAddEvent;
import com.bbw.god.gameuser.res.copper.EPCopperAdd;
import com.bbw.god.gameuser.special.event.EPSpecialAdd;
import com.bbw.god.gameuser.special.event.SpecialAddEvent;
import com.bbw.god.gameuser.treasure.event.*;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;

//@Component
public class NewGuideListener {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private NewerGuideService newerGuideService;
    @Autowired
    private GameUserRedisUtil gameUserRedisUtil;

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
            case JXZ_AWARD:
                updateGuideStatus(uid, NewerGuideEnum.JXZ_BUY, ep.getRd());
                return;
            default:
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
            case ACHIEVEMENT:
                updateGuideStatus(ep.getGuId(), NewerGuideEnum.FIRST_FIGHT_ACHIEVEMENT, ep.getRd());
                return;
            default:
                return;
        }
    }

    @EventListener
    public void useTreasure(TreasureDeductEvent event) {
        EPTreasureDeduct ep = event.getEP();
        EVTreasure treasure = ep.getDeductTreasure();
        TreasureEnum treasureEnum = TreasureEnum.fromValue(treasure.getId());
        if (treasureEnum == null) {
            return;
        }
        switch (treasureEnum) {
            case QKT:
                updateGuideStatus(ep.getGuId(), NewerGuideEnum.QKT_USE, ep.getRd());
                return;
            default:
                return;
        }
    }

    @EventListener
    @Order(1000)
    public void addCopper(CopperAddEvent event) {
        EPCopperAdd ep = event.getEP();
        if (ep.getWay() == WayEnum.QZ_AWARD) {
            updateGuideStatus(ep.getGuId(), NewerGuideEnum.DFZ_USE, ep.getRd());
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
    public void addSpecials(SpecialAddEvent event) {
        EPSpecialAdd ep = event.getEP();
        if (ep.getWay() == WayEnum.TRADE) {
            updateGuideStatus(ep.getGuId(), NewerGuideEnum.SPECIAL_BUY, ep.getRd());
        }
    }

    @EventListener
    public void addCardExp(UserCardExpAddEvent event) {
        EPCardExpAdd ep = event.getEP();
        if (ep.getWay() == WayEnum.LDF_AWARD) {
            updateGuideStatus(ep.getGuId(), NewerGuideEnum.CARD_EXP, ep.getRd());
        }
    }

    @EventListener
    public void cardLevelUp(UserCardLevelUpEvent event) {
        EPCardLevelUp ep = event.getEP();
        if (ep.getWay() == WayEnum.CARD_UPDATE) {
            updateGuideStatus(ep.getGuId(), NewerGuideEnum.CARD_LEVEL_UP, ep.getRd());
        }
    }

    @EventListener
    public void levelUpLDF(BuildingLevelUpEvent event) {
        EventParam<EPBuildingLevelUp> ep = (EventParam<EPBuildingLevelUp>) event.getSource();
        WayEnum way = ep.getWay();
        switch (way) {
            case LDF_UPDATE:
                updateGuideStatus(ep.getGuId(), NewerGuideEnum.LDF_LEVEL_UP, ep.getRd());
                return;
            case TCP_UPDATE:
                updateGuideStatus(ep.getGuId(), NewerGuideEnum.TCP_LEVEL_UP, ep.getRd());
                return;
            default:
                return;
        }
    }

    @EventListener
    public void achieve(BuildingLevelUpEvent event) {
        EventParam<EPBuildingLevelUp> ep = (EventParam<EPBuildingLevelUp>) event.getSource();
        if (ep.getWay() == WayEnum.LDF_UPDATE) {
            updateGuideStatus(ep.getGuId(), NewerGuideEnum.LDF_LEVEL_UP, ep.getRd());
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
            if (guide.getStep() < userNewerGuide.getNewerGuide().intValue()) {
                return;
            }
            userNewerGuide.updateNewerGuide(guide.getStep());
            this.gameUserService.updateItem(userNewerGuide);
            GameUser.Status status = this.gameUserRedisUtil.getUserStatus(uid);
            status.setGuideStatus(guide.getStep().intValue());
            this.gameUserRedisUtil.updateStatus(uid, status);
            if (userNewerGuide.getIsPassNewerGuide()) {
                GuideEventPublisher.pubPassNewerGuideEvent(uid, rd);
            }
        }
    }
}
