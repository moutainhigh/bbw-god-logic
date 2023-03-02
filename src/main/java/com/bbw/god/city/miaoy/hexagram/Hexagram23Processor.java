package com.bbw.god.city.miaoy.hexagram;

import com.bbw.god.gameuser.card.event.CardEventPublisher;
import org.springframework.stereotype.Service;

/**
 * 山火贲卦
 *
 * 获得卡牌-九尾狐王
 *
 * @author liuwenbin
 *
 */
@Service
public class Hexagram23Processor extends AbstractHexagram{
    @Override
    public int getHexagramId() {
        return 23;
    }

    @Override
    public HexagramLevelEnum getHexagramLevel() {
        return HexagramLevelEnum.MID_UP;
    }


    @Override
    public void effect(long uid, RDHexagram rd) {
        CardEventPublisher.pubCardAddEvent(uid,326,getWay(),getWay().getName(),rd);
    }


}
