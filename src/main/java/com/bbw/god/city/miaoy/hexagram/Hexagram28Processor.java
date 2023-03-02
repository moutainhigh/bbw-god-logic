package com.bbw.god.city.miaoy.hexagram;

import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import org.springframework.stereotype.Service;

/**
 * 雷天大壮卦
 *
 * 获得1个乾坤图
 *
 * @author liuwenbin
 *
 */
@Service
public class Hexagram28Processor extends AbstractHexagram{
    @Override
    public int getHexagramId() {
        return 28;
    }

    @Override
    public HexagramLevelEnum getHexagramLevel() {
        return HexagramLevelEnum.MID_UP;
    }


    @Override
    public void effect(long uid, RDHexagram rd) {
        TreasureEventPublisher.pubTAddEvent(uid, TreasureEnum.QKT.getValue(),1,getWay(),rd);
    }


}
