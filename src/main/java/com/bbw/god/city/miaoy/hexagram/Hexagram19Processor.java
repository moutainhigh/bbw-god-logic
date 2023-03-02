package com.bbw.god.city.miaoy.hexagram;

import com.bbw.god.gameuser.card.event.CardEventPublisher;
import org.springframework.stereotype.Service;

/**
 * 天泽履卦
 *
 * 获得卡牌-金銮火凤
 *
 * @author liuwenbin
 *
 */
@Service
public class Hexagram19Processor extends AbstractHexagram{
    @Override
    public int getHexagramId() {
        return 19;
    }

    @Override
    public HexagramLevelEnum getHexagramLevel() {
        return HexagramLevelEnum.MID_UP;
    }


    @Override
    public void effect(long uid, RDHexagram rd) {
        CardEventPublisher.pubCardAddEvent(uid,401,getWay(),getWay().getName(),rd);
    }


}
