package com.bbw.god.city.miaoy.hexagram;

import com.bbw.god.gameuser.card.event.CardEventPublisher;
import org.springframework.stereotype.Service;

/**
 * 雷水解卦
 *
 * 获得卡牌-巫医
 *
 * @author liuwenbin
 *
 */
@Service
public class Hexagram30Processor extends AbstractHexagram{
    @Override
    public int getHexagramId() {
        return 30;
    }

    @Override
    public HexagramLevelEnum getHexagramLevel() {
        return HexagramLevelEnum.MID_UP;
    }


    @Override
    public void effect(long uid, RDHexagram rd) {
        CardEventPublisher.pubCardAddEvent(uid,251,getWay(),getWay().getName(),rd);
    }


}
