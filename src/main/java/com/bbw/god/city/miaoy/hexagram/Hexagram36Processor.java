package com.bbw.god.city.miaoy.hexagram;

import com.bbw.god.gameuser.card.event.CardEventPublisher;
import org.springframework.stereotype.Service;

/**
 * 水火即济卦
 *
 * 获得卡牌-鸿鹄
 *
 * @author liuwenbin
 *
 */
@Service
public class Hexagram36Processor extends AbstractHexagram{
    @Override
    public int getHexagramId() {
        return 36;
    }

    @Override
    public HexagramLevelEnum getHexagramLevel() {
        return HexagramLevelEnum.MID_UP;
    }


    @Override
    public void effect(long uid, RDHexagram rd) {
        CardEventPublisher.pubCardAddEvent(uid,254,getWay(),getWay().getName(),rd);
    }


}
