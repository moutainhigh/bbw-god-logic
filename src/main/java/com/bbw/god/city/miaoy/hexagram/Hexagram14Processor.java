package com.bbw.god.city.miaoy.hexagram;

import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import org.springframework.stereotype.Service;

/**
 * 兑为泽卦
 *
 * 获得10个通天残卷
 *
 * @author liuwenbin
 *
 */
@Service
public class Hexagram14Processor extends AbstractHexagram{
    @Override
    public int getHexagramId() {
        return 14;
    }

    @Override
    public HexagramLevelEnum getHexagramLevel() {
        return HexagramLevelEnum.UP_UP;
    }


    @Override
    public void effect(long uid, RDHexagram rd) {
        TreasureEventPublisher.pubTAddEvent(uid, TreasureEnum.TongTCJ.getValue(),10,getWay(),rd);
    }


}
