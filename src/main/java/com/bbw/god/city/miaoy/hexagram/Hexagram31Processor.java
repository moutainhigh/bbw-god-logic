package com.bbw.god.city.miaoy.hexagram;

import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import org.springframework.stereotype.Service;

/**
 * 泽地萃卦
 *
 * 获得2个五星灵石
 *
 * @author liuwenbin
 *
 */
@Service
public class Hexagram31Processor extends AbstractHexagram{
    @Override
    public int getHexagramId() {
        return 31;
    }

    @Override
    public HexagramLevelEnum getHexagramLevel() {
        return HexagramLevelEnum.MID_UP;
    }


    @Override
    public void effect(long uid, RDHexagram rd) {
        TreasureEventPublisher.pubTAddEvent(uid, TreasureEnum.WNLS5.getValue(),2,getWay(),rd);
    }


}
