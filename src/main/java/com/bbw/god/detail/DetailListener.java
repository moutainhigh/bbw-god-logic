package com.bbw.god.detail;

import com.bbw.common.ListUtil;
import com.bbw.god.detail.disruptor.DetailEventHandler;
import com.bbw.god.game.config.TypeEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.card.UserCard;
import com.bbw.god.gameuser.card.UserCardService;
import com.bbw.god.gameuser.card.event.EPCardAdd;
import com.bbw.god.gameuser.card.event.UserCardAddEvent;
import com.bbw.god.gameuser.res.copper.CopperAddEvent;
import com.bbw.god.gameuser.res.copper.CopperDeductEvent;
import com.bbw.god.gameuser.res.copper.EPCopperAdd;
import com.bbw.god.gameuser.res.copper.EPCopperDeduct;
import com.bbw.god.gameuser.res.diamond.DiamondAddEvent;
import com.bbw.god.gameuser.res.diamond.DiamondDeductEvent;
import com.bbw.god.gameuser.res.diamond.EPDiamondAdd;
import com.bbw.god.gameuser.res.diamond.EPDiamondDeduct;
import com.bbw.god.gameuser.res.dice.DiceAddEvent;
import com.bbw.god.gameuser.res.dice.DiceDeductEvent;
import com.bbw.god.gameuser.res.dice.EPDiceAdd;
import com.bbw.god.gameuser.res.dice.EPDiceDeduct;
import com.bbw.god.gameuser.res.ele.EPEleAdd;
import com.bbw.god.gameuser.res.ele.EPEleDeduct;
import com.bbw.god.gameuser.res.ele.EleAddEvent;
import com.bbw.god.gameuser.res.ele.EleDeductEvent;
import com.bbw.god.gameuser.res.gold.EPGoldAdd;
import com.bbw.god.gameuser.res.gold.EPGoldDeduct;
import com.bbw.god.gameuser.res.gold.GoldAddEvent;
import com.bbw.god.gameuser.res.gold.GoldDeductEvent;
import com.bbw.god.gameuser.treasure.UserTreasure;
import com.bbw.god.gameuser.treasure.UserTreasureService;
import com.bbw.god.gameuser.treasure.event.*;
import com.bbw.god.gameuser.yuxg.UserFuTu;
import com.bbw.god.gameuser.yuxg.UserYuXGService;
import com.bbw.god.rd.RDCommon.RDCardInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 明细监听器
 *
 * @author suhq
 * @date 2019年3月13日 下午5:55:35
 */
@Async
@Slf4j
@Component
public class DetailListener {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private UserCardService userCardService;
    @Autowired
    private UserTreasureService userTreasureService;
    @Autowired
    private UserYuXGService userYuXGService;

    @EventListener
    @Order(1000)
    public void addCopper(CopperAddEvent event) {
        EPCopperAdd ep = event.getEP();
        GameUser gu = this.gameUserService.getGameUser(ep.getGuId());
        long addedCopper = ep.gainAddCopper();
        // 明细
        AwardDetail awardDetail = AwardDetail.fromCopper(addedCopper);
        DetailData detail = DetailData.instance(gu, ep.getWay(), awardDetail);
        detail.setAfterValue(gu.getCopper());
        DetailEventHandler.getInstance().log(detail);
    }

    @EventListener
    @Order(1000)
    public void deductCopper(CopperDeductEvent event) {
        EPCopperDeduct ep = event.getEP();
        GameUser gu = this.gameUserService.getGameUser(ep.getGuId());
        // 明细
        AwardDetail awardDetail = AwardDetail.fromCopper(-ep.getDeductCopper());
        DetailData detail = DetailData.instance(gu, ep.getWay(), awardDetail);
        detail.setAfterValue(gu.getCopper());
        DetailEventHandler.getInstance().log(detail);
    }

    @EventListener
    @Order(1000)
    public void addDice(DiceAddEvent event) {
        EPDiceAdd ep = event.getEP();
        GameUser gu = this.gameUserService.getGameUser(ep.getGuId());
        // 明细
        AwardDetail awardDetail = AwardDetail.fromDice(ep.getAddDice());
        DetailData detail = DetailData.instance(gu, ep.getWay(), awardDetail);
        detail.setAfterValue(gu.getDice().longValue());
        DetailEventHandler.getInstance().log(detail);
    }

    @EventListener
    @Order(1000)
    public void deductDice(DiceDeductEvent event) {
        EPDiceDeduct ep = event.getEP();
        GameUser gu = this.gameUserService.getGameUser(ep.getGuId());
        // 明细
        AwardDetail awardDetail = AwardDetail.fromDice(-ep.getDeductDice());
        DetailData detail = DetailData.instance(gu, ep.getWay(), awardDetail);
        detail.setAfterValue(gu.getDice().longValue());
        DetailEventHandler.getInstance().log(detail);
    }

    @EventListener
    @Order(1000)
    public void addEle(EleAddEvent event) {
        EPEleAdd ep = event.getEP();
        GameUser gu = this.gameUserService.getGameUser(ep.getGuId());
        ep.getAddEles().stream().forEach(ele -> {
            // 明细
            TypeEnum eleType = TypeEnum.fromValue(ele.getType());
            AwardDetail awardDetail = AwardDetail.fromEle(eleType, ele.getNum());
            DetailData detail = DetailData.instance(gu, ep.getWay(), awardDetail);
            detail.setAfterValue((long) gu.getEleCount(eleType));
            DetailEventHandler.getInstance().log(detail);
        });
    }

    @EventListener
    @Order(1000)
    public void deductEle(EleDeductEvent event) {
        EPEleDeduct ep = event.getEP();
        GameUser gu = this.gameUserService.getGameUser(ep.getGuId());
        ep.getDeductEles().stream().forEach(ele -> {
            // 明细
            TypeEnum eleType = TypeEnum.fromValue(ele.getType());
            AwardDetail awardDetail = AwardDetail.fromEle(eleType, -ele.getNum());
            DetailData detail = DetailData.instance(gu, ep.getWay(), awardDetail);
            detail.setAfterValue((long) gu.getEleCount(eleType));
            DetailEventHandler.getInstance().log(detail);
        });
    }

    @EventListener
    @Order(1000)
    public void addGold(GoldAddEvent event) {
        EPGoldAdd ep = event.getEP();
        GameUser gu = this.gameUserService.getGameUser(ep.getGuId());
        // 明细
        AwardDetail awardDetail = AwardDetail.fromGold(ep.gainAddGold());
        DetailData detail = DetailData.instance(gu, ep.getWay(), awardDetail);
        detail.setAfterValue(gu.getGold().longValue());
        DetailEventHandler.getInstance().log(detail);
    }

    @EventListener
    @Order(1000)
    public void deductGold(GoldDeductEvent event) {
        EPGoldDeduct ep = event.getEP();
        GameUser gu = this.gameUserService.getGameUser(ep.getGuId());
        // 明细
        AwardDetail awardDetail = AwardDetail.fromGold(-ep.getDeductGold());
        DetailData detail = DetailData.instance(gu, ep.getWay(), awardDetail);
        detail.setAfterValue(gu.getGold().longValue());
        DetailEventHandler.getInstance().log(detail);
    }

    @EventListener
    @Order(1000)
    public void addDiamond(DiamondAddEvent event) {
        EPDiamondAdd ep = event.getEP();
        GameUser gu = this.gameUserService.getGameUser(ep.getGuId());
        // 明细
        AwardDetail awardDetail = AwardDetail.fromDiamond(ep.gainAddDiamond());
        DetailData detail = DetailData.instance(gu, ep.getWay(), awardDetail);
        detail.setAfterValue(gu.getDiamond().longValue());
        DetailEventHandler.getInstance().log(detail);
    }

    @EventListener
    @Order(1000)
    public void deductDiamond(DiamondDeductEvent event) {
        EPDiamondDeduct ep = event.getEP();
        GameUser gu = this.gameUserService.getGameUser(ep.getGuId());
        // 明细
        AwardDetail awardDetail = AwardDetail.fromDiamond(-ep.getDeductDiamond());
        DetailData detail = DetailData.instance(gu, ep.getWay(), awardDetail);
        detail.setAfterValue(gu.getDiamond().longValue());
        DetailEventHandler.getInstance().log(detail);
    }

    @EventListener
    @Order(1000)
    public void addCard(UserCardAddEvent event) {
        EPCardAdd ep = event.getEP();
        GameUser gu = this.gameUserService.getGameUser(ep.getGuId());
        if (!ListUtil.isNotEmpty(ep.getRd().getCards())) {
            return;
        }
        //浅拷贝，极大减少java.util.ConcurrentModificationException: null
        List<RDCardInfo> cardInfos = new ArrayList<>();
        for (int i = 0; i < ep.getRd().getCards().size(); i++) {
            cardInfos.add(ep.getRd().getCards().get(i));
        }
        for (int i = 0; i < cardInfos.size(); i++) {
            RDCardInfo cardInfo = cardInfos.get(i);
            if (null == cardInfo) {
                continue;
            }
            UserCard uCard = userCardService.getUserCard(gu.getId(), cardInfo.getCard());
            if (uCard != null) {
                AwardDetail awardDetail = AwardDetail.fromUserCard(uCard, cardInfo.getSoulNum() == null ? 0 : cardInfo.getSoulNum());
                // 明细
                DetailData detail = DetailData.instance(gu, ep.getWay(), awardDetail);
                detail.setAfterValue(uCard.getLingshi().longValue());
                DetailEventHandler.getInstance().log(detail);
            } else {
                log.warn("明细卡牌时，卡牌尚未持久化到Redis");
            }

        }
    }

    @EventListener
    @Order(1000)
    public void addTreasure(TreasureFinishAddEvent event) {
        EPTreasureFinishAdd ep = event.getEP();
        GameUser gu = this.gameUserService.getGameUser(ep.getGuId());
        ep.getAddTreasures().stream().forEach(t -> {
            UserTreasure uTreasure = this.userTreasureService.getUserTreasure(gu.getId(), t.getId());
            if (uTreasure != null) {
                AwardDetail awardDetail = AwardDetail.fromTreasure(uTreasure.getBaseId(), t.getNum());
                // 明细
                DetailData detail = DetailData.instance(gu, ep.getWay(), awardDetail);
                detail.setAfterValue((long) uTreasure.gainTotalNum());
                DetailEventHandler.getInstance().log(detail);
                if (gu.getId() == 191122224300681L) {
                    log.info("uid=" + gu.getId() + ",detaillisterner,addTreausre:" + uTreasure.toString());
                }
            }
        });

    }

    @EventListener
    @Order(1000)
    public void deductTreasure(TreasureFinishDeductEvent event) {
        EPTreasureFinishDeduct ep = event.getEP();
        GameUser gu = this.gameUserService.getGameUser(ep.getGuId());
        EVTreasure ev = ep.getDeductTreasure();
        AwardDetail awardDetail = AwardDetail.fromTreasure(ev.getId(), -ev.getNum());
        int ownNum = 0;
        UserTreasure uTreasure = userTreasureService.getUserTreasure(gu.getId(), ev.getId());
        if (uTreasure != null) {
            ownNum = uTreasure.gainTotalNum();
        }
        // 明细
        DetailData detail = DetailData.instance(gu, ep.getWay(), awardDetail);
        detail.setAfterValue((long) ownNum);
        DetailEventHandler.getInstance().log(detail);
    }

    @EventListener
    @Order(1000)
    public void addFuTu(FuTuAddEvent event) {
        EPFuTuAdd ep = event.getEP();
        long uid = ep.getGuId();
        GameUser gu = this.gameUserService.getGameUser(uid);
        UserFuTu userFuTu = userYuXGService.getUserFuTuCache(uid, ep.getId());
        if (null == userFuTu){
            return;
        }
        AwardDetail awardDetail = AwardDetail.fromFuTu(userFuTu, ep.getNum());
        // 明细
        DetailData detail = DetailData.instance(gu, ep.getWay(), awardDetail);
        detail.setAfterValue((long) ep.getNum());
        DetailEventHandler.getInstance().log(detail);
    }

    @EventListener
    @Order(1000)
    public void deductFuTu(FuTuDeductEvent event) {
        EPFuTuDeduct ep = event.getEP();
        long uid = ep.getGuId();
        GameUser gu = this.gameUserService.getGameUser(uid);
        UserFuTu userFuTu = ep.getUserFuTu();
        AwardDetail awardDetail = AwardDetail.fromFuTu(userFuTu, -ep.getNum());
        // 明细
        DetailData detail = DetailData.instance(gu, ep.getWay(), awardDetail);
        detail.setAfterValue(0L);
        DetailEventHandler.getInstance().log(detail);
    }
}
