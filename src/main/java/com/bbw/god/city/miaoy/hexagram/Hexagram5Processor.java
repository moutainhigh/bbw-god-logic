package com.bbw.god.city.miaoy.hexagram;

import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.card.UserCardService;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 火雷噬嗑卦
 *
 * 获得2个仙之源
 *
 * @author liuwenbin
 *
 */
@Service
public class Hexagram5Processor extends AbstractHexagram{
    @Override
    public int getHexagramId() {
        return 5;
    }

    @Override
    public HexagramLevelEnum getHexagramLevel() {
        return HexagramLevelEnum.UP_UP;
    }


    @Override
    public void effect(long uid, RDHexagram rd) {
        TreasureEventPublisher.pubTAddEvent(uid, TreasureEnum.XZY.getValue(),2,getWay(),rd);
    }


}
