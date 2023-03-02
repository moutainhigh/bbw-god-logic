package com.bbw.god.city.miaoy.hexagram;

import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import org.springframework.stereotype.Service;

/**
 * 地水师卦
 *
 * 获得2个震天箭
 *
 * @author liuwenbin
 *
 */
@Service
public class Hexagram18Processor extends AbstractHexagram{
    @Override
    public int getHexagramId() {
        return 18;
    }

    @Override
    public HexagramLevelEnum getHexagramLevel() {
        return HexagramLevelEnum.MID_UP;
    }


    @Override
    public void effect(long uid, RDHexagram rd) {
        TreasureEventPublisher.pubTAddEvent(uid, TreasureEnum.ZTJ.getValue(),2,getWay(),rd);
    }


}
