package com.bbw.god.city.miaoy.hexagram;

import com.bbw.god.gameuser.res.ResEventPublisher;
import org.springframework.stereotype.Service;

/**
 * 火水未济卦
 *
 * 遗失10元宝
 *
 * @author liuwenbin
 *
 */
@Service
public class Hexagram51Processor extends AbstractHexagram{
    @Override
    public int getHexagramId() {
        return 51;
    }

    @Override
    public HexagramLevelEnum getHexagramLevel() {
        return HexagramLevelEnum.DOWN_DOWN;
    }


    @Override
    public void effect(long uid, RDHexagram rd) {
        ResEventPublisher.pubGoldDeductEvent(uid,10,getWay(),rd);
    }


}
