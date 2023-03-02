package com.bbw.god.gameuser.card.event;

import com.bbw.common.ListUtil;
import com.bbw.common.SpringContextUtil;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.gameuser.card.UserCard;
import com.bbw.god.gameuser.card.UserCardService;
import com.bbw.god.gameuser.card.event.EPCardAdd.CardAddInfo;
import com.bbw.god.rd.RDCommon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 卡牌事件推送器
 *
 * @author suhq
 * @date 2018年11月24日 下午8:19:11
 */
public class CardEventPublisher {
    private static UserCardService userCardService = SpringContextUtil.getBean(UserCardService.class);

    public static void pubCardAddEvent(long guId, int cardId, WayEnum way, String broadcastWayInfo, RDCommon rd) {
        pubCardAddEvent(guId, Arrays.asList(cardId), way, broadcastWayInfo, rd);
    }

    public static void pubCardAddEvent(Long guId, int cardId, boolean isRandom, WayEnum way, String broadcastWayInfo, RDCommon rd) {
        pubCardAddEvent(guId, Arrays.asList(cardId), way, isRandom, broadcastWayInfo, rd);
    }

    public static void pubCardAddEvent(Long guId, List<Integer> cardIds, WayEnum way, String broadcastWayInfo, RDCommon rd) {
        if (ListUtil.isEmpty(cardIds)) {
            return;
        }
        List<CardAddInfo> cardAddInfos = getCardAddInfos(guId, cardIds);
        pubCardAddEvent(guId, cardAddInfos, way, broadcastWayInfo, rd);
    }

    public static void pubCardAddEvent(long guId, List<Integer> cardIds, WayEnum way, Boolean isRandom, String broadcastWayInfo, RDCommon rd) {
        List<CardAddInfo> cardAddInfos = getCardAddInfos(guId, cardIds);
        pubCardAddEvent(guId, cardAddInfos, way, isRandom, broadcastWayInfo, rd);
    }


    public static void pubCardAddEvent(long guId, List<CardAddInfo> cardAddInfos, WayEnum way, String broadcastWayInfo, RDCommon rd) {
        BaseEventParam bep = new BaseEventParam(guId, way, rd);
        EPCardAdd ep = new EPCardAdd(bep, cardAddInfos, broadcastWayInfo);
        SpringContextUtil.publishEvent(new UserCardAddEvent(ep));
    }

    public static void pubCardAddEvent(long guId, List<CardAddInfo> cardAddInfos, WayEnum way, boolean isRandom, String broadcastWayInfo, RDCommon rd) {
        BaseEventParam bep = new BaseEventParam(guId, way, rd);
        EPCardAdd ep = new EPCardAdd(isRandom, bep, cardAddInfos, broadcastWayInfo);
        SpringContextUtil.publishEvent(new UserCardAddEvent(ep));
    }

    public static List<CardAddInfo> getCardAddInfos(Long guId, List<Integer> cardIds) {
        List<CardAddInfo> cardAddInfos = new ArrayList<CardAddInfo>();
        List<UserCard> ucs = userCardService.getUserCards(guId, cardIds);
        List<Integer> ownCardIds = ucs.stream().map(UserCard::getBaseId).collect(Collectors.toList());
        for (Integer cardId : cardIds) {
            boolean isNew = !ownCardIds.contains(cardId);
            cardAddInfos.add(new CardAddInfo(cardId, isNew));
        }
        return cardAddInfos;
    }

    public static void pubCardExpAddEvent(BaseEventParam bp, Integer cardId, Integer addedExp) {
        SpringContextUtil.publishEvent(new UserCardExpAddEvent(new EPCardExpAdd(bp, cardId, addedExp)));
    }

    public static void pubCardHierarchyUpEvent(EPCardHierarchyUp ep) {
        SpringContextUtil.publishEvent(new UserCardHierarchyUpEvent(ep));
    }

    public static void pubCardLevelUpEvent(EPCardLevelUp ep) {
        SpringContextUtil.publishEvent(new UserCardLevelUpEvent(ep));
    }

    public static void pubCardGroupingEvent(long guId, String deckInfo, WayEnum way, RDCommon rd) {
        BaseEventParam bep = new BaseEventParam(guId, way, rd);
        EPCardGrouping ep = new EPCardGrouping(bep, deckInfo);
        SpringContextUtil.publishEvent(new UserCardGroupingEvent(ep));
    }

    public static void pubCardDelEvent(long guId, List<UserCard> ucs) {
        BaseEventParam bep = new BaseEventParam(guId);
        EPCardDel ep = new EPCardDel(bep, ucs);
        SpringContextUtil.publishEvent(new UserCardDelEvent(ep));
    }

    public static void pubCardSkillChangeEvent(EPCardSkillChange ep) {
        SpringContextUtil.publishEvent(new UserCardSkillChangeEvent(ep));
    }

    public static void pubCardSkillResetEvent(EPCardSkillReset ep) {
        SpringContextUtil.publishEvent(new UserCardSkillResetEvent(ep));
    }

    public static void pubCardLingShiDeductEvent(EPCardLingShi epCardLingShi) {
        SpringContextUtil.publishEvent(new UserCardLingshiDeductEvent(epCardLingShi));
    }
}
