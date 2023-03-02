package com.bbw.god.city.miaoy.hexagram;

import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import org.springframework.stereotype.Service;

/**
 * 水天需卦
 *
 * 获得1个财神珠
 *
 * @author liuwenbin
 *
 */
@Service
public class Hexagram17Processor extends AbstractHexagram{
    @Override
    public int getHexagramId() {
        return 17;
    }

    @Override
    public HexagramLevelEnum getHexagramLevel() {
        return HexagramLevelEnum.MID_UP;
    }


    @Override
    public void effect(long uid, RDHexagram rd) {
        TreasureEventPublisher.pubTAddEvent(uid, TreasureEnum.CSZ.getValue(),1,getWay(),rd);
    }


}
