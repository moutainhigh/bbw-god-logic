package com.bbw.god.city.miaoy.hexagram;

import com.bbw.god.gameuser.res.ResEventPublisher;
import org.springframework.stereotype.Service;

/**
 * 火地晋卦
 *
 * 获得80万铜钱
 *
 * @author liuwenbin
 *
 */
@Service
public class Hexagram29Processor extends AbstractHexagram{
    @Override
    public int getHexagramId() {
        return 29;
    }

    @Override
    public HexagramLevelEnum getHexagramLevel() {
        return HexagramLevelEnum.MID_UP;
    }


    @Override
    public void effect(long uid, RDHexagram rd) {
        ResEventPublisher.pubCopperAddEvent(uid,800000,getWay(),rd);
    }


}
