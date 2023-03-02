package com.bbw.god.gameuser.card.juling;

import com.bbw.common.PowerRandom;
import com.bbw.god.ConsumeType;
import com.bbw.god.detail.async.CardDrawDetailAsyncHandler;
import com.bbw.god.detail.async.CardDrawDetailEventParam;
import com.bbw.god.detail.async.MallDetailAsyncHandler;
import com.bbw.god.detail.async.MallDetailEventParam;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.card.event.CardEventPublisher;
import com.bbw.god.gameuser.card.event.EPCardAdd;
import com.bbw.god.gameuser.res.ResChecker;
import com.bbw.god.gameuser.res.ResEventPublisher;
import com.bbw.god.gameuser.treasure.TreasureChecker;
import com.bbw.god.gameuser.treasure.UserTreasureService;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * 聚灵逻辑
 *
 * @author suhq
 * @date 2019-07-29 16:10:03
 */
@Service
public class JuLingLogic {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private UserTreasureService userTreasureService;
    @Autowired
    private CardDrawDetailAsyncHandler cardDrawDetailAsyncHandler;
    @Autowired
    private MallDetailAsyncHandler mallDetailAsyncHandler;

    /**
     * 获得聚灵界信息
     *
     * @param guId
     * @return
     */
    public RdJuLJ getJLJInfo(long guId) {
        RdJuLJ rdJuLJ = new RdJuLJ();
        int jlqNum = userTreasureService.getTreasureNum(guId, TreasureEnum.JXQ.getValue());
        // 普通聚灵
        rdJuLJ.setJlCards(JuLingTool.getConfig().getJlCards());
        rdJuLJ.setNeedGold(JuLingTool.getConfig().getNeedGoldForJL());
        rdJuLJ.setNeedJxq(JuLingTool.getConfig().getNeedJlqForJL());
        rdJuLJ.setJxq(jlqNum);
        // 限定聚灵
        int hsfNum = userTreasureService.getTreasureNum(guId, TreasureEnum.HSF.getValue());
        rdJuLJ.setJlCardsXd(JuLingTool.getConfig().getJlXDCards());
        rdJuLJ.setNeedGoldXd(JuLingTool.getConfig().getNeedGoldForJLXD());
        rdJuLJ.setNeedHsf(JuLingTool.getConfig().getNeedHsfForJLXD());
        rdJuLJ.setHsf(hsfNum);
        return rdJuLJ;
    }

    /**
     * 聚灵
     *
     * @param guId
     * @param cardId 0表示随机，其他值表示特定卡牌
     */
    public RDJuling juLing(long guId, int cardId, boolean isXD) {
        List<Integer> jlCards = null;
        int needGold = 0;
        int needTreasure = 0;
        TreasureEnum treasureEnum = null;
        WayEnum way = null;
        if (isXD) {
            // 限定聚灵
            jlCards = JuLingTool.getConfig().getJlXDCards();
            needGold = JuLingTool.getConfig().getNeedGoldForJLXD();
            needTreasure = JuLingTool.getConfig().getNeedHsfForJLXD();
            treasureEnum = TreasureEnum.HSF;
            way = WayEnum.CARD_HS;
        } else {
            // 普通聚灵
            jlCards = JuLingTool.getConfig().getJlCards();
            needGold = JuLingTool.getConfig().getNeedGoldForJL();
            needTreasure = JuLingTool.getConfig().getNeedJlqForJL();
            treasureEnum = TreasureEnum.JXQ;
            way = WayEnum.CARD_JL;
        }
        // 聚灵道具是否足够
        TreasureChecker.checkIsEnough(treasureEnum.getValue(), needTreasure, guId);
        boolean isRandom = true;
        RDJuling rd = new RDJuling();
        if (cardId > 0) {
            isRandom = false;
            // 指定卡牌，消耗元宝
            GameUser gu = gameUserService.getGameUser(guId);
            ResChecker.checkGold(gu, needGold);
            ResEventPublisher.pubGoldDeductEvent(guId, needGold, way, rd);
            mallDetailAsyncHandler.log(new MallDetailEventParam(guId, CardTool.getCardById(cardId), needGold, 1, needGold, ConsumeType.GOLD, gu.getGold()));
        } else {
            // 未指定卡牌，随机一张卡牌
            cardId = PowerRandom.getRandomFromList(jlCards);
        }
        TreasureEventPublisher.pubTDeductEvent(guId, treasureEnum.getValue(), needTreasure, way, rd);
        List<EPCardAdd.CardAddInfo> cardAddInfos = CardEventPublisher.getCardAddInfos(guId, Arrays.asList(cardId));
        CardEventPublisher.pubCardAddEvent(guId, cardId, isRandom, way, "聚灵界", rd);

        rd.setSpend(-needTreasure, isXD);
        cardDrawDetailAsyncHandler.log(new CardDrawDetailEventParam(guId, cardAddInfos, way));

        return rd;
    }

}
