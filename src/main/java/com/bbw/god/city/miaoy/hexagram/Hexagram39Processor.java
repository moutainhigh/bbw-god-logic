package com.bbw.god.city.miaoy.hexagram;

import com.bbw.god.gameuser.res.ResEventPublisher;
import org.springframework.stereotype.Service;

/**
 * 地山谦卦
 *
 * 获得10元宝
 *
 * @author liuwenbin
 *
 */
@Service
public class Hexagram39Processor extends AbstractHexagram{
    @Override
    public int getHexagramId() {
        return 39;
    }

    @Override
    public HexagramLevelEnum getHexagramLevel() {
        return HexagramLevelEnum.MID_MID;
    }


    @Override
    public void effect(long uid, RDHexagram rd) {
        ResEventPublisher.pubGoldAddEvent(uid,10,getWay(),rd);
    }


}
