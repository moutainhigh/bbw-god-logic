package com.bbw.god.city.miaoy.hexagram;

import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import org.springframework.stereotype.Service;

/**
 * 震为雷卦
 *
 * 获得10个三星灵石
 *
 * @author liuwenbin
 *
 */
@Service
public class Hexagram33Processor extends AbstractHexagram{
    @Override
    public int getHexagramId() {
        return 33;
    }

    @Override
    public HexagramLevelEnum getHexagramLevel() {
        return HexagramLevelEnum.MID_UP;
    }


    @Override
    public void effect(long uid, RDHexagram rd) {
        TreasureEventPublisher.pubTAddEvent(uid, TreasureEnum.WNLS3.getValue(),10,getWay(),rd);
    }


}
