package com.bbw.god.city.miaoy.hexagram;

import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import org.springframework.stereotype.Service;

/**
 * 雷山小过卦
 *
 * 获得40个一星灵石
 *
 * @author liuwenbin
 *
 */
@Service
public class Hexagram35Processor extends AbstractHexagram{
    @Override
    public int getHexagramId() {
        return 35;
    }

    @Override
    public HexagramLevelEnum getHexagramLevel() {
        return HexagramLevelEnum.MID_UP;
    }


    @Override
    public void effect(long uid, RDHexagram rd) {
        TreasureEventPublisher.pubTAddEvent(uid, TreasureEnum.WNLS1.getValue(),40,getWay(),rd);
    }


}
