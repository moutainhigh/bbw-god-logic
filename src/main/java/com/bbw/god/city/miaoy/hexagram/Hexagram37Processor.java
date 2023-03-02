package com.bbw.god.city.miaoy.hexagram;

import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import org.springframework.stereotype.Service;

/**
 * 地天泰卦
 *
 * 获得2个分身经验丹
 *
 * @author liuwenbin
 *
 */
@Service
public class Hexagram37Processor extends AbstractHexagram{
    @Override
    public int getHexagramId() {
        return 37;
    }

    @Override
    public HexagramLevelEnum getHexagramLevel() {
        return HexagramLevelEnum.MID_MID;
    }


    @Override
    public void effect(long uid, RDHexagram rd) {
        TreasureEventPublisher.pubTAddEvent(uid, TreasureEnum.FEN_SHEN_JYD.getValue(),2,getWay(),rd);
    }


}
