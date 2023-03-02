package com.bbw.god.activity.processor.cardboost;

import com.bbw.common.DateUtil;
import com.bbw.common.ListUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.activity.processor.AbstractActivityProcessor;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.card.CardExpTool;
import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.card.UserCard;
import com.bbw.god.gameuser.card.UserCardService;
import com.bbw.god.gameuser.card.event.CardEventPublisher;
import com.bbw.god.rd.RDCommon;
import com.bbw.god.rd.RDSuccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 卡牌助力处理器
 *
 * @author: suhq
 * @date: 2021/8/3 3:14 下午
 */
@Service
public class CardBoostProcessor extends AbstractActivityProcessor {

    @Autowired
    private UserCardService userCardService;

    public CardBoostProcessor() {
        this.activityTypeList = Arrays.asList(ActivityEnum.NEWER_BOOST, ActivityEnum.CARD_LEVEL_BOOST, ActivityEnum.CARD_EXP_BOOST);
    }

    @Override
    public RDSuccess getActivities(long uid, int activityType) {
        RDCardBoost rd = new RDCardBoost();
        int sid = gameUserService.getActiveSid(uid);
        IActivity a = this.activityService.getActivity(sid, ActivityEnum.CARD_LEVEL_BOOST);
        if (a == null) {
            throw new ExceptionForClientTip("activity.not.exist");
        }
        long remainTime = this.getRemainTime(uid, sid, a);
        rd.setRemainTime(remainTime);
        UserBoostCards boostCards = gameUserService.getSingleItem(uid, UserBoostCards.class);
        if (null == boostCards) {
            rd.setCardIds(Arrays.asList(0, 0, 0));
        } else {
            rd.setCardIds(Arrays.asList(boostCards.getCardIds()));
        }
        return rd;
    }

    /**
     * 设置助力卡牌
     *
     * @param uid
     * @param cards
     * @return
     */
    public RDSuccess setBoostCard(long uid, String cards) {
        long remainTime = getRemainTime(uid);
        if (remainTime <= 0) {
            throw new ExceptionForClientTip("activity.is.timeout");
        }
        List<Integer> cardIdList = ListUtil.parseStrToInts(cards, ";");
        Integer[] cardIds = new Integer[cardIdList.size()];
        cardIdList.toArray(cardIds);
        cardIdList = cardIdList.stream().filter(tmp -> tmp != 0).collect(Collectors.toList());
        List<UserCard> userCards = userCardService.getUserCards(uid, cardIdList);
        if (cardIdList.size() != userCards.size()) {
            throw new ExceptionForClientTip("card.not.own");
        }
        boolean isValid = userCards.stream().anyMatch(tmp -> tmp.getLevel() >= 10);
        if (isValid) {
            throw new ExceptionForClientTip("activity.boost.card.outOfLevel");
        }
        UserBoostCards boostCards = gameUserService.getSingleItem(uid, UserBoostCards.class);
        if (null == boostCards) {
            boostCards = UserBoostCards.instance(uid, cardIds);
            gameUserService.addItem(uid, boostCards);
        } else {
            boostCards.setCardIds(cardIds);
            gameUserService.updateItem(boostCards);
        }
        return new RDSuccess();
    }

    /**
     * 使用封神令更新助力卡
     *
     * @param deifyCard
     */
    public void setBoostCardForDeifyCard(UserCard deifyCard) {
        if (deifyCard.getLevel() >= 10) {
            return;
        }
        long uid = deifyCard.getGameUserId();
        int deifyCardId = deifyCard.getBaseId();
        long remainTime = getRemainTime(uid);
        if (remainTime <= 0) {
            return;
        }
        UserBoostCards boostCards = gameUserService.getSingleItem(uid, UserBoostCards.class);
        if (null == boostCards) {
            return;
        }
        int index = boostCards.gainIndex(deifyCardId);
        if (index < 0) {
            return;
        }
        boostCards.setCard(index, deifyCardId);
        gameUserService.updateItem(boostCards);
    }

    /**
     * 从助力移除。
     * 1、玩家将卡牌升级到10级
     *
     * @param uid
     * @param cardId
     */
    public void removeBoostCard(long uid, int cardId) {
        long remainTime = getRemainTime(uid);
        if (remainTime <= 0) {
            return;
        }
        UserBoostCards boostCards = gameUserService.getSingleItem(uid, UserBoostCards.class);
        if (null == boostCards) {
            return;
        }
        boostCards.removeCard(cardId);
        gameUserService.updateItem(boostCards);
    }

    /**
     * 消耗元素升级卡牌时检查是否双倍经验
     *
     * @param uid
     * @return
     */
    public boolean isDoubleExp(long uid) {
        long remainTime = getRemainTime(uid);
        return remainTime > 0;
    }

    @Override
    public int getAbleAwardedNum(GameUser gu, IActivity a) {
        return 0;
    }

    @Override
    protected long getRemainTime(long uid, int sid, IActivity a) {
        if (null == a) {
            return 0;
        }
        GameUser gu = gameUserService.getGameUser(uid);
        Date regTime = gu.getRoleInfo().getRegTime();
        Date endDate = DateUtil.addSeconds(regTime, 5 * 24 * 60 * 60 - 1);
        return endDate.getTime() - DateUtil.now().getTime();
    }

    /**
     * 获取剩余时间
     *
     * @param uid
     * @return <0已过期 =0已过期但未升级卡牌 >0未结束
     */
    public long getRemainTime(long uid) {
        if (uid < 0) {
            return -1;
        }
        int sid = gameUserService.getActiveSid(uid);
        IActivity a = this.activityService.getActivity(sid, ActivityEnum.CARD_LEVEL_BOOST);
        long remainTime = getRemainTime(uid, sid, a);
        //活动已失效或未生效，如果设置过助力且为升级，则返回0，让客户端进行同步
        if (remainTime <= 0) {
            UserBoostCards boostCards = gameUserService.getSingleItem(uid, UserBoostCards.class);
            if (null == boostCards || boostCards.isUpdated()) {
                return -1;
            }
            return 0;
        }
        return remainTime;
    }

    /**
     * 客户端发起同步
     *
     * @param uid
     */
    public void updateCard(long uid) {
        long remainTime = getRemainTime(uid);
        if (remainTime > 0 || remainTime < 0) {
            return;
        }
        UserBoostCards boostCards = gameUserService.getSingleItem(uid, UserBoostCards.class);
        if (null == boostCards || boostCards.isUpdated()) {
            return;
        }
        List<Integer> cardIds = new ArrayList<>();
        for (int i = 0; i < boostCards.getCardIds().length; i++) {
            if (boostCards.getCardIds()[i] != 0) {
                cardIds.add(boostCards.getCardIds()[i]);
            }
        }
        if (ListUtil.isEmpty(cardIds)) {
            return;
        }
        List<UserCard> userCards = userCardService.getUserCards(uid, cardIds);
        for (UserCard userCard : userCards) {
            if (userCard.getLevel() >= 10) {
                continue;
            }
            long expAs10 = CardExpTool.getExpByLevel(CardTool.getCardById(userCard.getBaseId()), 10);
            int addExp = (int) (expAs10 - userCard.getExperience()) + 1;
            BaseEventParam bp = new BaseEventParam(uid, WayEnum.CARD_UPDATE_BY_BOOST, new RDCommon());
            CardEventPublisher.pubCardExpAddEvent(bp, userCard.getBaseId(), addExp);
        }
        boostCards.setUpdated(true);
        gameUserService.updateItem(boostCards);
    }


}
