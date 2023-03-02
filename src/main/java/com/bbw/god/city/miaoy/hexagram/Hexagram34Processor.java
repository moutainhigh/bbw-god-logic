package com.bbw.god.city.miaoy.hexagram;

import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import org.springframework.stereotype.Service;

/**
 * 巽为风卦
 *
 * 获得20个2星灵石
 *
 * @author liuwenbin
 *
 */
@Service
public class Hexagram34Processor extends AbstractHexagram{
    @Override
    public int getHexagramId() {
        return 34;
    }

    @Override
    public HexagramLevelEnum getHexagramLevel() {
        return HexagramLevelEnum.MID_UP;
    }


    @Override
    public void effect(long uid, RDHexagram rd) {
        TreasureEventPublisher.pubTAddEvent(uid, TreasureEnum.WNLS2.getValue(),20,getWay(),rd);
    }


}
