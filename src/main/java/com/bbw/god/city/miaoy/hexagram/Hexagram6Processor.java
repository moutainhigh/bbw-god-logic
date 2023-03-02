package com.bbw.god.city.miaoy.hexagram;

import com.bbw.common.PowerRandom;
import com.bbw.god.gameuser.card.UserCard;
import com.bbw.god.gameuser.card.UserCardService;
import com.bbw.god.gameuser.card.event.CardEventPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 山雷颐卦
 *
 * 获得王者卡牌（已获得的）
 *
 * @author liuwenbin
 *
 */
@Service
public class Hexagram6Processor extends AbstractHexagram{
    @Autowired
    private UserCardService userCardService;

    private static final List<Integer> KING_CARDS= Arrays.asList(126,226,325,425,525);

    @Override
    public int getHexagramId() {
        return 6;
    }

    @Override
    public boolean canEffect(long uid) {
        for (int id : KING_CARDS) {
            UserCard card = userCardService.getUserNormalCardOrDeifyCard(uid, id);
            if (card!=null){
                return true;
            }
        }
        return false;
    }

    @Override
    public HexagramLevelEnum getHexagramLevel() {
        return HexagramLevelEnum.UP_UP;
    }


    @Override
    public void effect(long uid, RDHexagram rd) {
        List<UserCard> userCards = userCardService.getUserCards(uid);
        List<UserCard> cards = userCards.stream().filter(p -> KING_CARDS.contains(p.getBaseId()) || KING_CARDS.contains(Integer.valueOf(p.getBaseId() + 10000))).collect(Collectors.toList());
        UserCard userCard = PowerRandom.getRandomFromList(cards);
        int id=userCard.getBaseId()>10000?userCard.getBaseId()-10000:userCard.getBaseId();
        CardEventPublisher.pubCardAddEvent(uid,id,getWay(),getWay().getName(),rd);
    }


}
