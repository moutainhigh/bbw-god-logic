package com.bbw.god.city.miaoy.hexagram;

import com.bbw.god.gameuser.res.ResEventPublisher;
import org.springframework.stereotype.Service;

/**
 * 坤为地卦
 * 获得188元宝
 * @author liuwenbin
 *
 */
@Service
public class Hexagram2Processor extends AbstractHexagram{

    @Override
    public int getHexagramId() {
        return 2;
    }

    @Override
    public HexagramLevelEnum getHexagramLevel() {
        return HexagramLevelEnum.UP_UP;
    }

    @Override
    public void effect(long uid,RDHexagram rd) {
        ResEventPublisher.pubGoldAddEvent(uid,188, getWay(),rd);
    }
}
