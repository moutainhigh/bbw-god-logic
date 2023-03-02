package com.bbw.god.activity.processor.cardboost;

import com.bbw.common.ListUtil;
import com.bbw.god.activity.ActivityService;
import com.bbw.god.controller.AbstractController;
import com.bbw.god.game.CR;
import com.bbw.god.gameuser.card.UserCard;
import com.bbw.god.gameuser.card.UserCardService;
import com.bbw.god.gameuser.leadercard.UserLeaderCard;
import com.bbw.god.gameuser.leadercard.service.LeaderCardService;
import com.bbw.god.login.RDGameUser;
import com.bbw.god.rd.RDSuccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class BoostCardController extends AbstractController {
    @Autowired
    private ActivityService activityService;
    @Autowired
    private UserCardService userCardService;
    @Autowired
    private CardBoostProcessor cardBoostProcessor;
    @Autowired
    private LeaderCardService leaderCardService;

    /**
     * 设置助力卡牌
     *
     * @param cardIds
     * @return
     */
    @GetMapping(CR.Card.SET_BOOST)
    public RDSuccess setFightCards(String cardIds) {
        cardBoostProcessor.setBoostCard(getUserId(), cardIds);
        return new RDSuccess();
    }

    /**
     * 同步卡牌
     *
     * @return
     */
    @GetMapping(CR.Card.SYNC_BOOST_CARDS)
    public RDSyncBoostCards syncBoostCards() {
        RDSyncBoostCards rd = new RDSyncBoostCards();
        //执行卡牌升级操作
        cardBoostProcessor.updateCard(getUserId());
        // 玩家卡牌
        List<UserCard> uCards = this.userCardService.getUserCardsAsLogin(getUserId());
        List<RDGameUser.RDCard> rdCards = new ArrayList<>();
        //获取封装主角卡
        Optional<UserLeaderCard> leaderCardOp = leaderCardService.getUserLeaderCardOp(getUserId());
        if (leaderCardOp.isPresent()) {
            UserLeaderCard leaderCard = leaderCardOp.get();
            rdCards.add(RDGameUser.RDCard.instance(leaderCard));
        }
        //玩家自己的卡牌集合
        if (ListUtil.isNotEmpty(uCards)) {
            rdCards.addAll(uCards.stream().map(RDGameUser.RDCard::instance).collect(Collectors.toList()));
        }
        rd.setCards(rdCards);
        return rd;
    }
}
