package com.bbw.god.city.miaoy.hexagram;

import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import org.springframework.stereotype.Service;

/**
 * 雷地豫卦
 *
 * 获得3个杏黄旗
 *
 * @author liuwenbin
 *
 */
@Service
public class Hexagram40Processor extends AbstractHexagram{
    @Override
    public int getHexagramId() {
        return 40;
    }

    @Override
    public HexagramLevelEnum getHexagramLevel() {
        return HexagramLevelEnum.MID_MID;
    }


    @Override
    public void effect(long uid, RDHexagram rd) {
        TreasureEventPublisher.pubTAddEvent(uid, TreasureEnum.XHQ.getValue(),3,getWay(),rd);
    }


}
