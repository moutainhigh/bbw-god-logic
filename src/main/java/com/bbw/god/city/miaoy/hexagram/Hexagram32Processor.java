package com.bbw.god.city.miaoy.hexagram;

import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import org.springframework.stereotype.Service;

/**
 * 泽水困卦
 *
 * 获得5个4星灵石
 *
 * @author liuwenbin
 *
 */
@Service
public class Hexagram32Processor extends AbstractHexagram{
    @Override
    public int getHexagramId() {
        return 32;
    }

    @Override
    public HexagramLevelEnum getHexagramLevel() {
        return HexagramLevelEnum.MID_UP;
    }


    @Override
    public void effect(long uid, RDHexagram rd) {
        TreasureEventPublisher.pubTAddEvent(uid, TreasureEnum.WNLS4.getValue(),5,getWay(),rd);
    }


}
